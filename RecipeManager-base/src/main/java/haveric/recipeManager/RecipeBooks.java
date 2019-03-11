package haveric.recipeManager;

import com.google.common.collect.Sets;
import haveric.recipeManager.api.events.RecipeManagerReloadBooksEvent;
import haveric.recipeManager.data.RecipeBook;
import haveric.recipeManager.flag.flags.FlagAddToBook;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.*;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeOwner;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeBooks {
    private static String DIR_PLUGIN;
    private static String DIR_BOOKS;
    private static String FILE_ERRORLOG;

    private final Map<String, RecipeBook> books = new HashMap<>();
    private final int generated = (int) (System.currentTimeMillis() / 1000);

    private static RecipeBooks instance;

    private boolean callEvents;

    protected RecipeBooks() {}

    public static RecipeBooks getInstance() {
        if (instance == null) {
            instance = new RecipeBooks();
        }

        return instance;
    }
    // Constants
    //public static final String BOOK_MARKER = "RecipeManager";

    public void init(File booksDir) {
        clean();

        DIR_BOOKS = booksDir.getPath() + File.separator;
        callEvents = false;
    }

    public void init() {
        clean();

        DIR_PLUGIN = RecipeManager.getPlugin().getDataFolder() + File.separator;
        DIR_BOOKS = DIR_PLUGIN + "books" + File.separator;
        callEvents = true;
    }

    public void clean() {
        books.clear();
    }

    public void reload(CommandSender sender) {
        FILE_ERRORLOG = DIR_BOOKS + "errors.log";
        File booksDir = new File(DIR_BOOKS);

        if (callEvents) {
            Bukkit.getPluginManager().callEvent(new RecipeManagerReloadBooksEvent(sender));
        }

        clean();

        ErrorReporter.getInstance().startCatching();

        if (!booksDir.exists() && !booksDir.mkdirs()) {
            MessageSender.getInstance().send(sender, RMCChatColor.RED + "Error: couldn't create directories: " + booksDir.getPath());
        }

        Map<String, File> files = new HashMap<>();

        File[] listOfFiles = booksDir.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    int i = file.getName().lastIndexOf('.');
                    String ext;
                    if (i > 0) {
                        ext = file.getName().substring(i).toLowerCase();
                    } else {
                        ext = file.getName();
                    }

                    if (ext.equals(".yml")) {
                        files.put(RMCUtil.removeExtensions(file.getName(), Sets.newHashSet(".yml")), file);
                    }
                }
            }
        }

        for (Entry<String, File> e : files.entrySet()) {
            initBook(sender, e.getValue());
        }

        int errors = ErrorReporter.getInstance().getCatchedAmount();

        if (errors > 0) {
            ErrorReporter.getInstance().print(FILE_ERRORLOG);
        } else {
            ErrorReporter.getInstance().stopCatching();

            File log = new File(FILE_ERRORLOG);

            if (log.exists()) {
                log.delete();
            }
        }

        String bookErrors = "";
        if (errors > 0) {
            bookErrors = " with " + errors + " errors/warnings";
        }
        MessageSender.getInstance().sendAndLog(sender, "Parsed " + books.size() + " recipe books" + bookErrors + ".");

        // TODO post event ?
        // Bukkit.getPluginManager().callEvent(new RecipeManagerReloadBooksEventPost(sender));
    }

    public void reloadAfterRecipes(CommandSender sender) {
        FILE_ERRORLOG = DIR_BOOKS + "errors.log";
        File booksDir = new File(DIR_BOOKS);

        Map<String, File> files = new HashMap<>();

        File[] listOfFiles = booksDir.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    int i = file.getName().lastIndexOf('.');
                    String ext;
                    if (i > 0) {
                        ext = file.getName().substring(i).toLowerCase();
                    } else {
                        ext = file.getName();
                    }

                    if (ext.equals(".yml")) {
                        files.put(RMCUtil.removeExtensions(file.getName(), Sets.newHashSet(".yml")), file);
                    }
                }
            }
        }

        for (Entry<String, File> e : files.entrySet()) {
            initBookRecipes(sender, e.getValue());
        }
    }

    /**
     * Parses a YAML file into a RecipeBook object.<br>
     * It can have any extension and its name will be the ID.<br>
     * Uses {@link ErrorReporter} to trigger errors.
     *
     * @param file
     *            the file.
     * @return RecipeBook object if successfully loaded or null if failed.
     * @throws IllegalArgumentException
     *             if File object does not point to a file.
     */
    public RecipeBook loadBook(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("The specified File object is not a file!");
        }

        return initBook(null, file);
    }

    private RecipeBook initBook(CommandSender sender, File file) {
        // Loading YML file
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String id = RMCUtil.removeExtensions(file.getName(), Sets.newHashSet(".yml")).toLowerCase(); // get filename without extension (book ID)

        // set defaults
        yml.addDefault("title", id);
        yml.addDefault("author", "RecipeManager");
        yml.addDefault("description", "");
        yml.addDefault("settings.pervolume", 50);
        yml.addDefault("settings.cover", true);
        yml.addDefault("settings.contents", true);
        yml.addDefault("settings.end", true);
        yml.addDefault("settings.customend", "");

        yml.options().header("Recipe book configuration (last loaded at " + DateFormat.getDateTimeInstance().format(new Date()) + ")" + Files.NL + "Read '" + Files.FILE_INFO_BOOKS + "' file to learn how to configure books." + Files.NL);
        yml.options().copyDefaults(true);

        // Create RecipeBook object and assign basic info
        RecipeBook book = new RecipeBook(id);

        book.setTitle(RMCUtil.parseColors(yml.getString("title"), false));
        book.setDescription(RMCUtil.parseColors(yml.getString("description"), false));
        book.setAuthor(RMCUtil.parseColors(yml.getString("author"), false));
        book.setRecipesPerVolume(yml.getInt("settings.pervolume"));
        book.setCoverPage(yml.getBoolean("settings.cover"));
        book.setContentsPage(yml.getBoolean("settings.contents"));
        book.setEndPage(yml.getBoolean("settings.end"));
        book.setCustomEndPage(RMCUtil.parseColors(yml.getString("settings.customend").replace("\\n", "\n"), false));

        try {
            yml.save(file);
        } catch (Throwable e) {
            MessageSender.getInstance().error(sender, e, "<red>Couldn't save '" + id + ".yml'!");
        }

        books.put(id.toLowerCase(), book);

        return book;
    }

    private void initBookRecipes(CommandSender sender, File file) {
        ErrorReporter.getInstance().setFile(file.getName());

        // Loading YML file
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String id = RMCUtil.removeExtensions(file.getName(), Sets.newHashSet(".yml")).toLowerCase(); // get filename without extension (book ID)

        RecipeBook book = getBook(id);

        // Loading recipes from volumes...
        Map<Integer, List<String>> volumesMap = new HashMap<>(); // need List for saving to YAML properly
        Set<String> allRecipes = new HashSet<>();
        int recipesNum = 0;

        for (String key : yml.getKeys(false)) {
            if (key.startsWith("volume")) {
                String volString = key.substring("volume".length()).trim();
                int volume;

                try {
                    volume = Integer.parseInt(volString);
                } catch (NumberFormatException e) {
                    volume = 0;
                }

                if (volume < 1) {
                    ErrorReporter.getInstance().error("Book '" + id + "' has invalid volume number: " + volString, "Must be a number starting from 1.");
                    continue;
                }

                List<String> recipes = volumesMap.get(volume);

                if (recipes == null) {
                    recipes = new ArrayList<>(book.getRecipesPerVolume());
                    volumesMap.put(volume, recipes);
                }

                for (String value : yml.getStringList(key)) {
                    parseRecipeName(id, value, recipes, allRecipes);
                }

                // Get all recipes that have @recipebook flag for this book with this volume
                for (BaseRecipe r : RecipeManager.getRecipes().index.keySet()) {
                    if (r.hasFlag(FlagType.ADD_TO_BOOK) && !allRecipes.contains(r.getName())) {
                        FlagAddToBook flag = (FlagAddToBook) r.getFlag(FlagType.ADD_TO_BOOK);

                        if (flag.getVolume() == volume && id.equals(flag.getBookName())) {
                            recipes.add(r.getName());
                            allRecipes.add(r.getName());
                        }
                    }
                }

                recipesNum += recipes.size();
            }
        }

        // Check volume numbers if any volume is missing
        if (!volumesMap.isEmpty()) {
            int maxVolume = 1;

            for (Entry<Integer, List<String>> e : volumesMap.entrySet()) {
                maxVolume = Math.max(e.getKey(), maxVolume);
            }

            for (int i = 1; i <= maxVolume; i++) {
                if (!volumesMap.containsKey(i)) {
                    ErrorReporter.getInstance().warning("Book '" + id + "' is missing 'volume " + i + "', volumes have been renamed.");

                    List<List<String>> list = new ArrayList<>();

                    for (int v = 1; v <= maxVolume; v++) {
                        List<String> l = volumesMap.get(v);

                        if (l != null) {
                            list.add(l);
                        }

                        yml.set("volume" + v, null);
                        yml.set("volume " + v, null);
                    }

                    volumesMap.clear();

                    for (int v = 0; v < list.size(); v++) {
                        volumesMap.put(v + 1, list.get(v));
                    }

                    break;
                }
            }
        }

        // Load unsorted recipes and recipes with @recipebook flag...
        List<String> unsorted = new ArrayList<>();

        // Get all recipes that have @recipebook flag for this book without defined volume
        for (BaseRecipe r : RecipeManager.getRecipes().index.keySet()) {
            if (r.hasFlag(FlagType.ADD_TO_BOOK) && !allRecipes.contains(r.getName())) {
                FlagAddToBook flag = (FlagAddToBook) r.getFlag(FlagType.ADD_TO_BOOK);

                if (id.equals(flag.getBookName()) && flag.getVolume() == 0) {
                    unsorted.add(r.getName());
                    allRecipes.add(r.getName());
                }
            }
        }

        // Check if 'recipes' node exists then grab its values and delete the node.
        if (yml.isSet("recipes")) {
            for (String value : yml.getStringList("recipes")) {
                parseRecipeName(id, value, unsorted, allRecipes);
            }

            yml.set("recipes", null);
        }

        // Add unsorted recipes to volumes
        if (!unsorted.isEmpty()) {
            int volume = Math.max(recipesNum / book.getRecipesPerVolume(), 1);
            int added = (recipesNum % book.getRecipesPerVolume());

            for (String name : unsorted) {
                List<String> recipes = volumesMap.get(volume);

                if (recipes == null) {
                    recipes = new ArrayList<>(book.getRecipesPerVolume());
                    volumesMap.put(volume, recipes);
                }

                recipes.add(name);

                if (++added >= book.getRecipesPerVolume()) {
                    volume++;
                    added = 0;
                }
            }
        }

        // Get all recipes that have @recipebook flag for this book with defined volume
        for (BaseRecipe r : RecipeManager.getRecipes().index.keySet()) {
            if (r.hasFlag(FlagType.ADD_TO_BOOK) && !allRecipes.contains(r.getName())) {
                FlagAddToBook flag = (FlagAddToBook) r.getFlag(FlagType.ADD_TO_BOOK);

                if (id.equals(flag.getBookName()) && flag.getVolume() > 0) {
                    List<String> recipes = volumesMap.get(flag.getVolume());

                    if (recipes != null) {
                        recipes.add(r.getName());
                        allRecipes.add(r.getName());
                    } else {
                        ErrorReporter.getInstance().warning("Flag " + FlagType.ADD_TO_BOOK + " is set for '" + id + "' book but with invalid volume: " + flag.getVolume());
                    }
                }
            }
        }

        // Save the data back to the YML file
        for (Entry<Integer, List<String>> e : volumesMap.entrySet()) {
            yml.set("volume" + e.getKey(), null);
            yml.set("volume " + e.getKey(), e.getValue());
        }

        try {
            yml.save(file);
        } catch (Throwable e) {
            MessageSender.getInstance().error(sender, e, "<red>Couldn't save '" + id + ".yml'!");
        }

        if (volumesMap.isEmpty()) {
            ErrorReporter.getInstance().error("Book '" + id + "' has no defined recipes!", "See '" + Files.FILE_INFO_BOOKS + "' file to learn about recipe books.");
            return;
        }

        for (List<String> recipes : volumesMap.values()) {
            book.addVolume(recipes);
        }

        books.put(id.toLowerCase(), book);
    }

    private void parseRecipeName(String id, String value, Collection<String> recipes, Set<String> allRecipes) {
        if (value.charAt(0) == '+') {
            value = value.substring(1).trim();
            int i = value.indexOf(' ');

            if (i < 0) {
                ErrorReporter.getInstance().warning("Book '" + id + "' has an argument without a value, removed.");
            }

            String arg = value.substring(0, i + 1).trim();
            value = value.substring(i).trim();

            if (arg.startsWith("existing")) {
                if (value.equals("all")) {
                    getExistingByType(recipes, allRecipes, CraftRecipe.class);
                    getExistingByType(recipes, allRecipes, CombineRecipe.class);
                    getExistingByType(recipes, allRecipes, SmeltRecipe.class);
                    getExistingByType(recipes, allRecipes, FuelRecipe.class);
                } else if (value.startsWith("work") || value.startsWith("craft")) {
                    getExistingByType(recipes, allRecipes, CraftRecipe.class);
                    getExistingByType(recipes, allRecipes, CombineRecipe.class);
                } else if (value.startsWith("smelt") || value.startsWith("furnace")) {
                    getExistingByType(recipes, allRecipes, SmeltRecipe.class);
                } else if (value.startsWith("fuel")) {
                    getExistingByType(recipes, allRecipes, FuelRecipe.class);
                } else {
                    ErrorReporter.getInstance().warning("Book '" + id + "' has 'existing' argument with unknown value: '" + value + "', removed");
                }
            } else if (arg.startsWith("custom")) {
                if (value.equals("all")) {
                    getCustomByType(recipes, allRecipes, CraftRecipe.class);
                    getCustomByType(recipes, allRecipes, CombineRecipe.class);
                    getCustomByType(recipes, allRecipes, SmeltRecipe.class);
                    getCustomByType(recipes, allRecipes, FuelRecipe.class);
                } else if (value.startsWith("work") || value.startsWith("craft")) {
                    getCustomByType(recipes, allRecipes, CraftRecipe.class);
                    getCustomByType(recipes, allRecipes, CombineRecipe.class);
                } else if (value.startsWith("smelt") || value.startsWith("furnace")) {
                    getCustomByType(recipes, allRecipes, SmeltRecipe.class);
                } else if (value.startsWith("fuel")) {
                    getCustomByType(recipes, allRecipes, FuelRecipe.class);
                } else {
                    ErrorReporter.getInstance().warning("Book '" + id + "' has 'custom' argument with unknown value: '" + value + "', removed");
                }
            } else if (arg.startsWith("file")) {
                if (value.charAt(0) == '/') {
                    value = value.substring(1).trim();
                }

                value = RMCUtil.removeExtensions(value, Files.FILE_RECIPE_EXTENSIONS);

                int added = 0;

                for (Entry<BaseRecipe, RMCRecipeInfo> e : RecipeManager.getRecipes().index.entrySet()) {
                    RMCRecipeInfo info = e.getValue();

                    if (info.getOwner() == RecipeOwner.RECIPEMANAGER && value.equals(info.getAdder())) {
                        recipes.add(e.getKey().getName());
                        added++;
                    }
                }

                if (added == 0) {
                    ErrorReporter.getInstance().warning("Book '" + id + "' could not find any recipes that were added by: '" + value + "', removed.");
                }
            } else if (arg.startsWith("folder")) {
                if (value.charAt(0) != '/') {
                    value = '/' + value;
                }

                value = value.replace('\\', '/');
                int added = 0;

                for (Entry<BaseRecipe, RMCRecipeInfo> e : RecipeManager.getRecipes().index.entrySet()) {
                    RMCRecipeInfo info = e.getValue();

                    if (info.getOwner() == RecipeOwner.RECIPEMANAGER && info.getAdder() != null) {
                        String adder = '/' + info.getAdder().replace('\\', '/');
                        i = adder.lastIndexOf('/');

                        if (i > -1) {
                            if (i == 0) {
                                adder = adder.substring(0, i + 1);
                            } else {
                                adder = adder.substring(0, i);
                            }
                        }

                        if (value.equals(adder)) {
                            recipes.add(e.getKey().getName());
                            added++;
                        }
                    }
                }

                if (added == 0) {
                    ErrorReporter.getInstance().warning("Book '" + id + "' could not find any recipes in folder: '" + value + "', removed.");
                }
            } else {
                ErrorReporter.getInstance().warning("Book '" + id + "' has unknown argument: '" + arg + "', removed.");
            }
        } else {
            BaseRecipe recipe = RecipeManager.getRecipes().getRecipeByName(value);

            if (recipe == null) {
                ErrorReporter.getInstance().warning("Book '" + id + "' has a recipe that does not exist any more: '" + value + "', removed.");
            } else {
                if (allRecipes.contains(value)) {
                    ErrorReporter.getInstance().warning("Book '" + id + "' already has recipe '" + value + "' added, removed.");
                } else {
                    recipes.add(value);
                    allRecipes.add(value);
                }
            }
        }
    }

    private void getExistingByType(Collection<String> recipes, Set<String> allRecipes, Class<? extends BaseRecipe> cls) {
        for (Entry<BaseRecipe, RMCRecipeInfo> e : Vanilla.initialRecipes.entrySet()) {
            BaseRecipe recipe = e.getKey();

            if (cls == null || cls.isInstance(recipe)) {
                if (!allRecipes.contains(recipe.getName())) {
                    recipes.add(recipe.getName());
                    allRecipes.add(recipe.getName());
                }
            }
        }
    }

    private void getCustomByType(Collection<String> recipes, Set<String> allRecipes, Class<? extends BaseRecipe> cls) {
        for (Entry<BaseRecipe, RMCRecipeInfo> e : RecipeManager.getRecipes().index.entrySet()) {
            if (e.getValue().getOwner() == RecipeOwner.RECIPEMANAGER) {
                BaseRecipe recipe = e.getKey();

                if (cls == null || cls.isInstance(recipe)) {
                    if (!allRecipes.contains(recipe.getName())) {
                        recipes.add(recipe.getName());
                        allRecipes.add(recipe.getName());
                    }
                }
            }
        }
    }

    /**
     * Updates (if available) the supplied book item with the latest changes
     *
     * @param player
     *            must not be null and must have 'recipemanager.updatebooks' permission.
     * @param item
     *            must be a written book generated by RecipeManager
     */
    public void updateBook(Player player, ItemStack item) {
        if (item == null || item.getType() != Material.WRITTEN_BOOK || !item.hasItemMeta() || !player.hasPermission("recipemanager.updatebooks")) {
            return;
        }

        BookMeta meta = (BookMeta) item.getItemMeta();

        if (!meta.hasAuthor()) {
            return;
        }

        Matcher match = Pattern.compile("(.*) ([\\d\\w]+) ([0-9]+) ([0-9]+)").matcher(RMCUtil.unhideString(meta.getAuthor()));

        if (match.find() && match.groupCount() >= 4) {
            String id = match.group(2);
            RecipeBook book = getBook(id);

            if (book == null) {
                Messages.getInstance().sendOnceCustom(player, "recipebook.update.extinct", "{title}", meta.getTitle());
                return;
            }

            int volume;
            int updated;

            try {
                volume = Integer.parseInt(match.group(3));
                updated = Integer.parseInt(match.group(4));
            } catch (NumberFormatException e) {
                MessageSender.getInstance().error(null, e, "Error while parsing " + player.getName() + "'s held book details.");
                return;
            }

            if (generated > updated) {
                if (volume > book.getVolumesNum()) {
                    Messages.getInstance().sendOnceCustom(player, "recipebook.update.novolume", "{title}", meta.getTitle(), "{volume}", volume);
                    return;
                }

                ItemStack bookItem = book.getBookItem(volume);
                BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();

                boolean titleDiff = !bookMeta.getTitle().equals(meta.getTitle());

                if (titleDiff || !bookMeta.getPages().equals(meta.getPages())) {
                    Messages.getInstance().send(player, "recipebook.update.done");

                    if (titleDiff) {
                        Messages.getInstance().send(player, "recipebook.update.changed.title", "{oldtitle}", meta.getTitle(), "{newtitle}", bookMeta.getTitle());
                    }

                    if (meta.getPageCount() != bookMeta.getPageCount()) {
                        Messages.getInstance().send(player, "recipebook.update.changed.pages", "{oldpages}", meta.getPageCount(), "{newpages}", bookMeta.getPageCount());
                    }
                }

                item.setItemMeta(bookMeta);
            }
        }
    }

    public Map<String, RecipeBook> getBooks() {
        return books;
    }

    public RecipeBook getBook(String id) {
        return books.get(id);
    }

    public ItemStack getBookItem(String id) {
        return getBookItem(id, 1);
    }

    public ItemStack getBookItem(String id, int volume) {
        RecipeBook book = getBook(id);

        ItemStack bookItem;
        if (book == null) {
            bookItem = null;
        } else {
            bookItem = book.getBookItem(volume);
        }
        return bookItem;
    }

    /**
     * Gets a recipe book by ID or partial ID string.
     *
     * @param id
     * @return list of found recipe books matching ID.
     */
    public List<RecipeBook> getBooksPartialMatch(String id) {
        id = id.toLowerCase();
        RecipeBook book = books.get(id); // full match first

        if (book != null) {
            return Collections.singletonList(book);
        }

        // partial match
        List<RecipeBook> found = new ArrayList<>(books.size());

        for (Entry<String, RecipeBook> e : books.entrySet()) {
            if (e.getKey().contains(id)) {
                found.add(e.getValue());
            }
        }

        return found;
    }
}
