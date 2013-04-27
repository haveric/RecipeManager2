package ro.thehunters.digi.recipeManager;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.data.Book;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

import com.google.common.collect.Sets;

public class RecipeBooks
{
    private final String DIR_PLUGIN = RecipeManager.getPlugin().getDataFolder() + File.separator;
    private final String DIR_BOOKS = DIR_PLUGIN + "books" + File.separator;
    private final String FILE_ERRORLOG = DIR_BOOKS + "errors.log";
    
    private final Map<String, Book> books = new HashMap<String, Book>();
    private final int generated = (int)(System.currentTimeMillis() / 1000);
    
    // Constants
    public static final String BOOK_MARKER = "RecipeManager";
    
    protected static void init()
    {
        if(RecipeManager.recipeBooks != null)
        {
            RecipeManager.recipeBooks.clean();
        }
        
        RecipeManager.recipeBooks = new RecipeBooks();
    }
    
    private RecipeBooks()
    {
    }
    
    public void clean()
    {
        books.clear();
    }
    
    public void reload(CommandSender sender)
    {
        clean();
        
        File dir = new File(DIR_BOOKS);
        
        RecipeErrorReporter.startCatching();
        
        if(!dir.exists() && !dir.mkdirs())
        {
            Messages.send(sender, ChatColor.RED + "Error: couldn't create directories: " + dir.getPath());
        }
        
        for(File file : dir.listFiles())
        {
            if(file.isFile())
            {
                int i = file.getName().lastIndexOf('.');
                String ext = (i > 0 ? file.getName().substring(i).toLowerCase() : file.getName());
                
                if(ext.equals(".yml"))
                {
                    parseBook(sender, file);
                }
            }
        }
        
        if(RecipeErrorReporter.getCatchedAmount() > 0)
        {
            RecipeErrorReporter.print(FILE_ERRORLOG);
        }
        else
        {
            RecipeErrorReporter.stopCatching();
            
            File log = new File(FILE_ERRORLOG);
            
            if(log.exists())
            {
                log.delete();
            }
        }
        
        Messages.info("Loaded " + books.size() + " recipe books."); // TODO remove ?
    }
    
    private void parseBook(CommandSender sender, File file)
    {
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        
        String id = Tools.removeExtensions(file.getName(), Sets.newHashSet(".yml"));
        
        yml.addDefault("title", id);
        yml.addDefault("description", "");
        
        String title = Tools.parseColors(yml.getString("title"), false);
        String description = Tools.parseColors(yml.getString("description"), false);
        int recipesPerVolume = yml.getInt("recipes-per-volume", 50);
        
        Set<String> allRecipes = new HashSet<String>();
        Map<Integer, List<String>> volumesMap = new HashMap<Integer, List<String>>();
        int recipesNum = 0;
        
        for(String key : yml.getKeys(false))
        {
            if(key.startsWith("volume"))
            {
                String volString = key.substring("volume".length()).trim();
                int volume = 0;
                
                try
                {
                    volume = Integer.valueOf(volString);
                }
                catch(NumberFormatException e)
                {
                }
                
                if(volume < 1)
                {
                    RecipeErrorReporter.error("Book '" + id + "' has invalid volume number: " + volString, "Must be a number starting from 1.");
                    continue;
                }
                
                List<String> recipes = volumesMap.get(volume);
                
                if(recipes == null)
                {
                    recipes = new ArrayList<String>(recipesPerVolume);
                    volumesMap.put(volume, recipes);
                }
                
                for(String value : yml.getStringList(key))
                {
                    parseRecipeName(id, value, recipes, allRecipes);
                }
                
                recipesNum += recipes.size();
            }
        }
        
        if(yml.isSet("recipes"))
        {
            List<String> unsorted = new ArrayList<String>();
            
            for(String value : yml.getStringList("recipes"))
            {
                parseRecipeName(id, value, unsorted, allRecipes);
            }
            
            Messages.info("Transfered all recipes from 'recipes' to individual volumes.");
            yml.set("recipes", null);
            
            int volume = Math.max((int)Math.floor((recipesNum + 0.0) / recipesPerVolume), 1);
            int added = (recipesNum % recipesPerVolume);
            
            Messages.debug("recipesNum = " + recipesNum);
            Messages.debug("volume = " + volume);
            Messages.debug("added = " + added);
            Messages.debug("allRecipes.size() = " + allRecipes.size());
            
            for(int i = 0; i < unsorted.size(); i++)
            {
                List<String> recipes = volumesMap.get(volume);
                
                if(recipes == null)
                {
                    recipes = new ArrayList<String>(recipesPerVolume);
                    volumesMap.put(volume, recipes);
                }
                
                Messages.debug("Added " + unsorted.get(i) + " to volume " + volume);
                
                recipes.add(unsorted.get(i));
                
                if(++added >= recipesPerVolume)
                {
                    volume++;
                    added = 0;
                }
            }
        }
        
        int maxVolume = 1;
        
        for(Entry<Integer, List<String>> e : volumesMap.entrySet())
        {
            maxVolume = Math.max(e.getKey(), maxVolume);
        }
        
        for(int i = 1; i <= maxVolume; i++)
        {
            if(!volumesMap.containsKey(i))
            {
                RecipeErrorReporter.warning("Book '" + id + "' is missing volume number " + i + ", volumes have been renamed!");
                
                List<List<String>> list = new ArrayList<List<String>>();
                
                for(int v = 1; v <= maxVolume; v++)
                {
                    List<String> l = volumesMap.get(v);
                    
                    if(l != null)
                    {
                        list.add(l);
                    }
                    
                    yml.set("volume" + v, null);
                    yml.set("volume " + v, null);
                }
                
                volumesMap.clear();
                
                for(int v = 0; v < list.size(); v++)
                {
                    volumesMap.put(v + 1, list.get(v));
                }
                
                break;
            }
        }
        
        for(Entry<Integer, List<String>> e : volumesMap.entrySet())
        {
            yml.set("volume" + e.getKey(), null);
            yml.set("volume " + e.getKey(), e.getValue());
        }
        
        yml.options().header("Recipe book configuration (last loaded at " + DateFormat.getDateTimeInstance().format(new Date()) + ")" + Files.NL + "Read '" + Files.FILE_INFO_BOOKS + "' file to learn how to configure books." + Files.NL);
        yml.options().copyDefaults(true);
        
        try
        {
            yml.save(file);
        }
        catch(Throwable e)
        {
            Messages.error(sender, e, "<red>Couldn't save '" + id + ".yml' !");
        }
        
        if(volumesMap.size() == 0)
        {
            RecipeErrorReporter.error("Book '" + id + "' has no defined recipes!", "See '" + Files.FILE_INFO_BOOKS + "' file to learn about recipe books.");
            return;
        }
        
        BookMeta[] metaArray = new BookMeta[volumesMap.size()];
        
        for(Entry<Integer, List<String>> v : volumesMap.entrySet())
        {
            int vol = v.getKey() - 1;
            BookMeta meta = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
            metaArray[vol] = meta;
            
            meta.setTitle(title + (metaArray.length > 1 ? " - " + Messages.RECIPEBOOK_VOLUME.get("{volume}", (vol + 1)) : ""));
            meta.setAuthor(BOOK_MARKER + Tools.hideString(" " + id + " " + vol + " " + (System.currentTimeMillis() / 1000)));
            
            // Cover page
            
            StringBuilder cover = new StringBuilder(256);
            
            cover.append('\n').append(ChatColor.BLACK).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append(title);
            
            if(metaArray.length > 1)
            {
                cover.append('\n').append(ChatColor.BLACK).append("        ").append(Messages.RECIPEBOOK_VOLUMEOFVOLUMES.get("{volume}", (vol + 1), "{volumes}", metaArray.length));
            }
            
            cover.append('\n').append(ChatColor.GRAY).append("        Published by\n          RecipeManager");
            
            if(description != null)
            {
                cover.append('\n').append(ChatColor.DARK_BLUE).append(description);
            }
            
            meta.addPage(cover.toString());
            
            List<StringBuilder> index = new ArrayList<StringBuilder>();
            List<String> pages = new ArrayList<String>();
//            List<FuelRecipe> fuels = new ArrayList<FuelRecipe>();
            int i = 0;
            int r = 2;
            int p = (int)Math.ceil(v.getValue().size() / 13.0) + 2;
            
            index.add(new StringBuilder(256).append(ChatColor.BLACK).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append("CONTENTS INDEX").append("\n\n").append(ChatColor.BLACK));
            
            for(String name : v.getValue())
            {
                BaseRecipe recipe = RecipeManager.getRecipes().getRecipeByName(name);
                
                /*
                if(recipe instanceof FuelRecipe)
                {
                    fuels.add((FuelRecipe)recipe);
                    
                    if(fuels.size() == 10)
                    {
                        StringBuilder s = new StringBuilder(256);
                        s.append(ChatColor.BLACK).append(ChatColor.BOLD).append("FURNACE FUELS"); // TODO messages.yml
                        s.append('\n');
                        
                        for(FuelRecipe fuelRecipe : fuels)
                        {
                            s.append('\n').append(Tools.Item.print(fuelRecipe.getIngredient(), ChatColor.BLACK, null, false));
                        }
                        
                        meta.addPage(s.toString());
                        
                        fuels.clear();
                    }
                    
                    continue;
                }
                */
                
                index.get(i).append(p++).append(". ").append(recipe.printBookIndex()).append(ChatColor.BLACK).append('\n');
                
                if(++r >= 13)
                {
                    r = 0;
                    i++;
                    index.add(new StringBuilder(256).append(ChatColor.BLACK));
                }
                
                String page = recipe.printBook();
                
                if(page.length() >= 255)
                {
                    int x = page.indexOf('\n', 220);
                    
                    if(x < 0 || x > 255)
                    {
                        x = 255;
                    }
                    
                    pages.add(page.substring(0, x));
                    pages.add(page.substring(x + 1));
                    p++;
                }
                else
                {
                    pages.add(page);
                }
            }
            
            for(StringBuilder s : index)
            {
                meta.addPage(s.toString());
            }
            
            for(String s : pages)
            {
                meta.addPage(s);
            }
            
            /*
            List<StringBuilder> index = new ArrayList<StringBuilder>();
            List<String> pages = new ArrayList<String>();
            int r = 2;
            int i = 0;
            int p = (int)Math.ceil(v.getValue().size() / 13.0) + 2;
            
            index.add(new StringBuilder(256).append(ChatColor.BLACK).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append("CONTENTS INDEX").append("\n\n").append(ChatColor.BLACK));
            
            List<FuelRecipe> fuels = new ArrayList<FuelRecipe>();
            
            for(String name : v.getValue())
            {
                BaseRecipe recipe = RecipeManager.getRecipes().getRecipeByName(name);
                
                if(recipe instanceof FuelRecipe)
                {
                    fuels.add((FuelRecipe)recipe);
                    continue;
                }
                
                index.get(i).append(p++).append(". ").append(recipe.printBookIndex()).append(ChatColor.BLACK).append('\n');
                
                if(++r >= 13)
                {
                    r = 0;
                    i++;
                    index.add(new StringBuilder(256).append(ChatColor.BLACK));
                }
                
                String page = recipe.printBook();
                
                if(page.length() >= 255)
                {
                    int x = page.indexOf('\n', 220);
                    
                    if(x < 0 || x > 255)
                    {
                        x = 255;
                    }
                    
                    pages.add(page.substring(0, x));
                    pages.add(page.substring(x + 1));
                    p++;
                }
                else
                {
                    pages.add(page);
                }
            }
            
            boolean hasFuels = !fuels.isEmpty();
            
            if(hasFuels)
            {
                index.get(i).append(p++).append(". ").append("Furnace fuels").append(ChatColor.BLACK).append('\n');
            }
            
            for(StringBuilder s : index)
            {
                meta.addPage(s.toString());
            }
            
            for(String s : pages)
            {
                meta.addPage(s);
            }
            
            if(hasFuels)
            {
                StringBuilder s = null;
                int f = 0;
                
                for(FuelRecipe recipe : fuels)
                {
                    if(f == 0)
                    {
                        s = new StringBuilder(256);
                        s.append(ChatColor.BLACK).append(ChatColor.BOLD).append("FURNACE FUELS"); // TODO messages.yml
                        s.append('\n');
                    }
                    
                    s.append('\n').append(Tools.Item.print(recipe.getIngredient(), ChatColor.BLACK, null, false));
                    
                    if(++f > 10)
                    {
                        meta.addPage(s.toString());
                        f = 0;
                    }
                }
                
                if(f > 0)
                {
                    meta.addPage(s.toString());
                }
            }
            */
        }
        
        books.put(id, new Book(title, description, metaArray));
    }
    
    private void parseRecipeName(String fileName, String value, List<String> recipes, Set<String> allRecipes)
    {
        if(value.charAt(0) == '+')
        {
            value = value.substring(1).trim();
            int i = value.indexOf(' ');
            
            if(i < 0)
            {
                RecipeErrorReporter.warning("Book '" + fileName + "' has an argument without a value, removed.");
            }
            
            String arg = value.substring(0, i + 1).trim();
            value = value.substring(i).trim();
            
            if(arg.startsWith("existing"))
            {
                if(value.equals("all"))
                {
                    getExistingByType(recipes, allRecipes, WorkbenchRecipe.class);
                    getExistingByType(recipes, allRecipes, SmeltRecipe.class);
                    getExistingByType(recipes, allRecipes, FuelRecipe.class);
                }
                else if(value.startsWith("work") || value.startsWith("craft"))
                {
                    getExistingByType(recipes, allRecipes, WorkbenchRecipe.class);
                }
                else if(value.startsWith("smelt") || value.startsWith("furnace"))
                {
                    getExistingByType(recipes, allRecipes, SmeltRecipe.class);
                }
                else if(value.startsWith("fuel"))
                {
                    getExistingByType(recipes, allRecipes, FuelRecipe.class);
                }
                else
                {
                    RecipeErrorReporter.warning("Book '" + fileName + "' has 'existing' argument with unknown value: '" + value + "', removed");
                }
            }
            else if(arg.startsWith("custom"))
            {
                if(value.equals("all"))
                {
                    getExistingByType(recipes, allRecipes, WorkbenchRecipe.class);
                    getExistingByType(recipes, allRecipes, SmeltRecipe.class);
                    getExistingByType(recipes, allRecipes, FuelRecipe.class);
                }
                else if(value.startsWith("work") || value.startsWith("craft"))
                {
                    getExistingByType(recipes, allRecipes, WorkbenchRecipe.class);
                }
                else if(value.startsWith("smelt") || value.startsWith("furnace"))
                {
                    getExistingByType(recipes, allRecipes, SmeltRecipe.class);
                }
                else if(value.startsWith("fuel"))
                {
                    getExistingByType(recipes, allRecipes, FuelRecipe.class);
                }
                else
                {
                    RecipeErrorReporter.warning("Book '" + fileName + "' has 'existing' argument with unknown value: '" + value + "', removed");
                }
            }
            else if(arg.startsWith("file"))
            {
                if(value.charAt(0) == '/')
                {
                    value = value.substring(1).trim();
                }
                
                value = Tools.removeExtensions(value, Files.FILE_RECIPE_EXTENSIONS);
                
                int added = 0;
                
                for(Entry<BaseRecipe, RecipeInfo> e : RecipeManager.getRecipes().index.entrySet())
                {
                    RecipeInfo info = e.getValue();
                    
                    if(info.getOwner() == RecipeOwner.RECIPEMANAGER && value.equals(info.getAdder()))
                    {
                        recipes.add(e.getKey().getName());
                        added++;
                    }
                }
                
                if(added == 0)
                {
                    RecipeErrorReporter.warning("Book '" + fileName + "' could not find any recipes that were added by: '" + value + "', removed.");
                }
            }
            else if(arg.startsWith("folder"))
            {
                if(value.charAt(0) != '/')
                {
                    value = '/' + value;
                }
                
                value = value.replace('\\', '/');
                int added = 0;
                
                for(Entry<BaseRecipe, RecipeInfo> e : RecipeManager.getRecipes().index.entrySet())
                {
                    RecipeInfo info = e.getValue();
                    
                    if(info.getOwner() == RecipeOwner.RECIPEMANAGER && info.getAdder() != null)
                    {
                        String adder = '/' + info.getAdder().replace('\\', '/');
                        i = adder.lastIndexOf('/');
                        
                        if(i > -1)
                        {
                            adder = adder.substring(0, (i == 0 ? i + 1 : i));
                        }
                        
                        if(value.equals(adder))
                        {
                            recipes.add(e.getKey().getName());
                            added++;
                        }
                    }
                }
                
                if(added == 0)
                {
                    RecipeErrorReporter.warning("Book '" + fileName + "' could not find any recipes in folder: '" + value + "', removed.");
                }
            }
            else
            {
                RecipeErrorReporter.warning("Book '" + fileName + "' has unknown argument: '" + arg + "', removed.");
            }
        }
        else
        {
            BaseRecipe recipe = RecipeManager.getRecipes().getRecipeByName(value);
            
            if(recipe == null)
            {
                RecipeErrorReporter.warning("Book '" + fileName + "' has a recipe that does not exist anymore: '" + value + "', removed.");
            }
            else
            {
                if(allRecipes.contains(value))
                {
                    RecipeErrorReporter.warning("Book '" + fileName + " already has recipe '" + value + "' added, ignored.");
                }
                else
                {
                    recipes.add(value);
                    allRecipes.add(value);
                }
            }
        }
        
        return;
    }
    
    private void getExistingByType(List<String> recipes, Set<String> allRecipes, Class<? extends BaseRecipe> cls)
    {
        for(Entry<BaseRecipe, RecipeInfo> e : Vanilla.initialRecipes.entrySet())
        {
            BaseRecipe recipe = e.getKey();
            
            if(cls == null || cls.isInstance(recipe))
            {
                if(!allRecipes.contains(recipe.getName()))
                {
                    recipes.add(recipe.getName());
                    allRecipes.add(recipe.getName());
                }
            }
        }
    }
    
    private void getCustomByType(List<String> recipes, Set<String> allRecipes, Class<? extends BaseRecipe> cls)
    {
        for(Entry<BaseRecipe, RecipeInfo> e : RecipeManager.getRecipes().index.entrySet())
        {
            if(e.getValue().getOwner() == RecipeOwner.RECIPEMANAGER)
            {
                BaseRecipe recipe = e.getKey();
                
                if(cls == null || cls.isInstance(recipe))
                {
                    if(!allRecipes.contains(recipe.getName()))
                    {
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
     *            must not be null
     * @param item
     *            must be a written book generated by RecipeManager
     */
    public void updateBook(Player player, ItemStack item)
    {
        if(item == null || item.getType() != Material.WRITTEN_BOOK || !item.hasItemMeta() || !player.hasPermission("recipemanager.updatebooks"))
        {
            return;
        }
        
        BookMeta meta = (BookMeta)item.getItemMeta();
        
        if(!meta.hasAuthor())
        {
            return;
        }
        
        Matcher match = Pattern.compile(BOOK_MARKER + " ([\\d\\w]+) ([0-9]+) ([0-9]+)").matcher(Tools.unhideString(meta.getAuthor()));
        
        if(match.find() && match.groupCount() >= 3)
        {
            try
            {
                String id = match.group(1);
                Book book = getBook(id);
                
                if(book == null)
                {
                    Messages.EVENTS_UPDATEBOOK_EXTINCT.printOnce(player, null, "{title}", meta.getTitle());
                    return;
                }
                
                Integer volume = Integer.valueOf(match.group(2));
                Integer lastUpdate = Integer.valueOf(match.group(3));
                
                if(generated > lastUpdate)
                {
                    if(volume >= book.getVolumes().length)
                    {
                        Messages.EVENTS_UPDATEBOOK_NOVOLUME.printOnce(player, null, "{title}", meta.getTitle(), "{volume}", volume);
                        return;
                    }
                    
                    ItemStack bookItem = book.getBookItem(volume);
                    BookMeta bookMeta = (BookMeta)bookItem.getItemMeta();
                    
                    boolean titleDiff = !bookMeta.getTitle().equals(meta.getTitle());
                    
                    if(titleDiff || !bookMeta.getPages().equals(meta.getPages()))
                    {
                        Messages.EVENTS_UPDATEBOOK_DONE.print(player);
                        
                        if(titleDiff)
                        {
                            Messages.EVENTS_UPDATEBOOK_CHANGED_TITLE.print(player, null, "{oldtitle}", meta.getTitle(), "{newtitle}", book.getTitle());
                        }
                        
                        if(meta.getPageCount() != bookMeta.getPageCount())
                        {
                            Messages.EVENTS_UPDATEBOOK_CHANGED_PAGES.print(player, null, "{oldpages}", meta.getPageCount(), "{newpages}", bookMeta.getPageCount());
                        }
                    }
                    
                    item.setItemMeta(bookMeta);
                }
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public Map<String, Book> getBooks()
    {
        return books;
    }
    
    public Book getBook(String id)
    {
        return books.get(id);
    }
    
    public ItemStack getBookItem(String id)
    {
        return getBookItem(id, 1);
    }
    
    public ItemStack getBookItem(String id, int volume)
    {
        Book book = getBook(id);
        
        return (book == null ? null : book.getBookItem(volume));
    }
    
    public List<Book> getBooksPartialMatch(String id)
    {
        Book book = books.get(id); // full match first
        
        if(book != null)
        {
            return Arrays.asList(book);
        }
        else
        {
            // partial match
            List<Book> found = new ArrayList<Book>(books.size());
            
            for(Entry<String, Book> e : books.entrySet())
            {
                if(e.getKey().contains(id))
                {
                    found.add(e.getValue());
                }
            }
            
            return found;
        }
    }
}
