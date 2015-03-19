package haveric.recipeManager;

import haveric.recipeManager.flags.FlagIngredientCondition;
import haveric.recipeManager.flags.FlagIngredientCondition.Conditions;
import haveric.recipeManager.flags.FlagOverride;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipe.RecipeType;
import haveric.recipeManager.recipes.BrewRecipe;
import haveric.recipeManager.recipes.CombineRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.FuelRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeInfo;
import haveric.recipeManager.recipes.RecipeInfo.RecipeOwner;
import haveric.recipeManager.recipes.RemoveResultRecipe;
import haveric.recipeManager.recipes.SmeltRecipe;
import haveric.recipeManager.tools.ParseBit;
import haveric.recipeManager.tools.Tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


/**
 * Processes all recipe files and updates main Recipes class once done.
 */
public class RecipeProcessor implements Runnable {
    private final CommandSender sender;
    private final boolean check;

    private String currentFile;
    private BufferedReader reader;
    private boolean commentBlock;
    private Flags fileFlags;
    private String line;
    private int lineNum;
    private int directiveLine;
    private int loaded;
    private String recipeName;

    // Storage
    private volatile RecipeRegistrator registrator = null;
    private final List<String> fileList = new ArrayList<String>();

    // Constants
    private static final String DIR_PLUGIN = RecipeManager.getPlugin().getDataFolder() + File.separator;
    private static final String DIR_RECIPES = DIR_PLUGIN + "recipes" + File.separator;
    private static final String FILE_ERRORLOG = DIR_RECIPES + "errors.log";
    private static final String[] COMMENTS = { "//", "#" };

    private static BukkitTask task;

    protected static void reload(CommandSender sender, boolean check) {
        new RecipeProcessor(sender, check);
    }

    private RecipeProcessor(CommandSender newSender, boolean newCheck) {
        sender = newSender;
        check = newCheck;

        if (task != null) {
            task.cancel();
        }

        ErrorReporter.startCatching();

        if (Settings.getInstance().getMultithreading()) {
            task = Bukkit.getScheduler().runTaskAsynchronously(RecipeManager.getPlugin(), this);
        } else {
            run();
        }
    }

    @Override
    public void run() {
        final long start = System.currentTimeMillis();

        try {
            String message;
            if (check) {
                message = "Checking";
            } else {
                message = "Loading";
            }
            Messages.sendAndLog(sender, message + " all recipes...");

            File dir = new File(DIR_RECIPES);

            if (!dir.exists() && !dir.mkdirs()) {
                Messages.sendAndLog(sender, ChatColor.RED + "Error: couldn't create directories: " + dir.getPath());
            }

            // Scan for files
            analyzeDirectory(dir);

            if (fileList.isEmpty()) {
                Messages.sendAndLog(sender, "<yellow>No recipe files exist in the recipes folder.");
            } else {
                registrator = new RecipeRegistrator();

                long lastDisplay = System.currentTimeMillis();
                long time;
                int numFiles = fileList.size();
                int parsedFiles = 0;
                loaded = 0;

                // Start reading files...
                for (String name : fileList) {
                    try {
                        parseFile(DIR_RECIPES, name);
                        parsedFiles++;
                        time = System.currentTimeMillis();

                        // display progress each second
                        if (time > lastDisplay + 500) {
                            Messages.sendAndLog(sender, "Recipes processed " + ((parsedFiles * 100) / numFiles) + "%...");
                            lastDisplay = time;
                        }
                    } catch (Throwable e) {
                        Messages.error(sender, e, "Error while reading recipe files!");
                    }
                }

                int errors = ErrorReporter.getCatchedAmount();

                String parsed;
                if (check) {
                    parsed = "Checked";
                } else {
                    parsed = "Parsed";
                }
                if (errors > 0) {
                    String senderMessage;
                    if (errors == 1) {
                        senderMessage = " error was found";
                    } else {
                        senderMessage = " errors were found";
                    }

                    if (sender == null) {
                        senderMessage += ", see below:";
                    } else {
                        senderMessage += ", see console.";
                    }

                    Messages.sendAndLog(sender, ChatColor.YELLOW + parsed + " " + loaded + " recipes from " + fileList.size() + " files in " + (System.currentTimeMillis() - start) / 1000.0 + " seconds, " + errors + senderMessage);

                    ErrorReporter.print(FILE_ERRORLOG);
                } else {
                    Messages.sendAndLog(sender, parsed + " " + loaded + " recipes from " + fileList.size() + " files without errors, elapsed time " + (System.currentTimeMillis() - start) / 1000.0 + " seconds.");

                    File log = new File(FILE_ERRORLOG);

                    if (log.exists()) {
                        log.delete();
                    }
                }

                ErrorReporter.stopCatching();
            }
        } catch (Throwable e) {
            Messages.error(sender, e, "Code error while processing recipes");
        } finally {
            task = null;

            if (check || registrator == null) {
                return;
            }

            // Calling registerRecipesToServer() in main thread...
            if (Settings.getInstance().getMultithreading()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        registrator.registerRecipesToServer(sender, start);
                    }
                }.runTask(RecipeManager.getPlugin());
            } else {
                registrator.registerRecipesToServer(sender, start);
            }
        }
    }

    private void analyzeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                if (!file.getName().equalsIgnoreCase("disabled")) {
                    analyzeDirectory(file);
                }
            } else {
                int i = file.getName().lastIndexOf('.');
                String ext;
                if (i > 0) {
                    ext = file.getName().substring(i).toLowerCase();
                } else {
                    ext = file.getName();
                }

                if (!Files.FILE_RECIPE_EXTENSIONS.contains(ext)) {
                    continue;
                }

                String fileName = file.getPath().replace(DIR_RECIPES, ""); // get the relative path+filename
                fileList.add(fileName); // add to the processing file list
            }
        }
    }

    private void parseFile(String root, String fileName) throws Throwable {
        reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(root + fileName))));
        currentFile = Tools.removeExtensions(fileName, Files.FILE_RECIPE_EXTENSIONS);
        lineNum = 0;
        ErrorReporter.setFile(currentFile);
        fileFlags = new Flags();
        commentBlock = false;
        boolean added = false;

        parseFlags(fileFlags); // parse file header flags that applies to all recipes

        while (searchRecipes()) { // search for recipes...
            directiveLine = lineNum;
            String directive = line.toLowerCase();
            recipeName = null;
            int i = directive.indexOf(' ');

            if (i > 0) {
                recipeName = line.substring(i + 1).trim();
                directive = directive.substring(0, i);
            }

            if (directive.equals(RecipeType.CRAFT.getDirective())) {
                added = parseCraftRecipe();
            } else if (directive.equals(RecipeType.COMBINE.getDirective())) {
                added = parseCombineRecipe();
            } else if (directive.equals(RecipeType.SMELT.getDirective())) {
                added = parseSmeltRecipe();
            } else if (directive.equals(RecipeType.FUEL.getDirective())) {
                added = parseFuelRecipe();
            } else if (directive.equals(RecipeType.BREW.getDirective())) {
                added = parseBrewRecipe();
            } else if (directive.equals(RecipeType.SPECIAL.getDirective())) {
                added = parseRemoveResults();
            } else {
                ErrorReporter.warning("Unexpected directive: '" + line + "'", "This might be caused by previous errors.");
            }

            if (!added) {
                ErrorReporter.error("Recipe was not added! Review previous errors and fix them.", "Warnings do not prevent recipe creation but they should be fixed as well!");
            }
        }

        if (lineNum == 0) {
            ErrorReporter.warning("Recipe file '" + fileName + "' is empty.");
        }

        reader.close();
    }

    private boolean searchRecipes() {
        if (line != null && lineIsRecipe()) {
            return true;
        }

        return nextLine();
    }

    private boolean lineIsRecipe() {
        for (RecipeType type : RecipeType.values()) {
            if (type.getDirective() != null && line.toLowerCase().startsWith(type.getDirective())) {
                return true;
            }
        }

        return false;
    }

    private boolean lineIsFlag() {
        return line.trim().charAt(0) == '@';
    }

    private boolean readNextLine() {
        lineNum++;
        ErrorReporter.setLine(lineNum);

        try {
            line = reader.readLine();
            return line != null;
        } catch (Throwable e) {
            Messages.error(null, e, null);
            return false;
        }
    }

    private boolean nextLine() {
        do {
            if (!readNextLine()) {
                return false;
            }

            line = parseComments();
        } while (line == null);

        return true;
    }

    private String parseComments() {
        if (line != null) {
            line = line.trim();
        }

        if (line == null || line.isEmpty()) {
            return null;
        }

        int index;

        // if we are in a comment block check for exit character
        if (commentBlock) {
            index = line.indexOf("*/");

            if (index >= 0) {
                commentBlock = false;

                String comment;
                if (index == 0) {
                    comment = null;
                } else {
                    comment = line.substring(0, index);
                }
                return comment;
            }

            return null;
        }

        index = line.indexOf("/*"); // check for comment block start

        if (index >= 0) {
            int end = line.indexOf("*/"); // check for comment block end chars on the same line

            if (end > 0) {
                return line.substring(0, index) + line.substring(end + 2);
            }

            commentBlock = true;
            String comment;
            if (index == 0) {
                comment = null;
            } else {
                comment = line.substring(0, index);
            }
            return comment;
        }

        // now check for in-line comments
        for (String comment : COMMENTS) {
            index = line.indexOf(comment);

            if (index == 0) {
                return null;
            }

            if (index > -1) {
                return line.substring(0, index); // partial comment, return filtered data
            }
        }

        return line;
    }

    private void parseFlags(Flags flags) throws Throwable {
        nextLine();

        while (line != null && line.trim().length() > 0 && line.trim().charAt(0) == '@') {
            flags.parseFlag(line);
            nextLine();
        }
    }

    private boolean parseCraftRecipe() throws Throwable {
        CraftRecipe recipe = new CraftRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // parse recipe's flags

        ItemStack[] ingredients = new ItemStack[9];
        String[] split;
        ItemStack item;
        int rows = 0;
        int ingredientsNum = 0;
        boolean ingredientErrors = false;

        while (rows < 3) { // loop until we find 3 rows of ingredients (or bump into the result along the way)
            if (rows > 0) {
                nextLine();
            }

            if (line == null) {
                if (rows == 0) {
                    return ErrorReporter.error("No ingredients defined!");
                }

                break;
            }

            if (line.charAt(0) == '=') { // if we bump into the result prematurely (smaller recipes)
                break;
            }

            split = line.split("\\+"); // split ingredients by the + sign
            int rowLen = split.length;

            if (rowLen > 3) { // if we find more than 3 ingredients warn the user and limit it to 3
                rowLen = 3;
                ErrorReporter.warning("You can't have more than 3 ingredients on a row, ingredient(s) ignored.", "Remove the extra ingredient(s).");
            }

            for (int i = 0; i < rowLen; i++) { // go through each ingredient on the line
                item = Tools.parseItem(split[i], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
                if (item == null) { // invalid item
                    ingredientErrors = true;
                }

                // no point in adding more ingredients if there are errors
                if (!ingredientErrors && item.getType() != Material.AIR) {
                    item = parseConditions(recipe, item);

                    ingredients[(rows * 3) + i] = item;
                    ingredientsNum++;
                }
            }

            rows++;
        }

        if (ingredientErrors) { // invalid ingredients found
            ErrorReporter.error("Recipe has some invalid ingredients, fix them!");
            return false;
        } else if (ingredientsNum == 0) { // no ingredients were processed
            return ErrorReporter.error("Recipe doesn't have ingredients!", "Consult readme.txt for proper recipe syntax.");
        } else if (ingredientsNum == 2 && !checkIngredients(ingredients)) {
            return false;
        }

        recipe.setIngredients(ingredients); // done with ingredients, set 'em

        if (recipe.hasFlag(FlagType.REMOVE)) {
            nextLine(); // Skip the results line, if it exists
        } else {
            // get results
            List<ItemResult> results = new ArrayList<ItemResult>();

            if (!parseResults(recipe, results, true, false)) { // results have errors
                return false;
            }

            recipe.setResults(results); // done with results, set 'em

            if (recipe.getFirstResult() == null) {
                return ErrorReporter.error("Recipe must have at least one non-air result!");
            }
        }

        // check if the recipe already exists...
        if (!recipeExists(recipe)) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueCraftRecipe(recipe, currentFile);
        loaded++;

        return true; // successfully added
    }

    private boolean parseCombineRecipe() throws Throwable {
        CombineRecipe recipe = new CombineRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredients
        String[] ingredientsRaw = line.split("\\+");

        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        ItemStack item;
        int items = 0;

        for (String str : ingredientsRaw) {
            item = Tools.parseItem(str, Vanilla.DATA_WILDCARD, ParseBit.NO_META);

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            items += item.getAmount();
            if (items > 9) {
                ErrorReporter.error("Combine recipes can't have more than 9 ingredients!", "If you're using stacks make sure they don't exceed 9 items in total.");
                return false;
            }

            item = parseConditions(recipe, item);

            ingredients.add(item);
        }

        if (ingredients.size() == 2 && !checkIngredients(ingredients.get(0), ingredients.get(1))) {
            return false;
        }

        recipe.setIngredients(ingredients);

        // get the results
        List<ItemResult> results = new ArrayList<ItemResult>();

        if (!parseResults(recipe, results, true, false)) {
            return false;
        }

        recipe.setResults(results);

        if (recipe.getFirstResult() == null) {
            return ErrorReporter.error("Recipe must have at least one non-air result!");
        }

        // check if recipe already exists
        if (!recipeExists(recipe)) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueCombineRecipe(recipe, currentFile);
        loaded++;

        return true; // no errors encountered
    }

    private boolean checkIngredients(ItemStack... ingredients) {
        Material toolType = null;

        for (ItemStack i : ingredients) {
            if (i != null && i.getType().getMaxDurability() > 0) {
                if (toolType == i.getType()) {
                    ErrorReporter.error("Recipes can't have exactly 2 ingredients that are identical repairable items!", "Add another ingredient to make it work or even another tool and use " + FlagType.KEEPITEM + " flag to keep it.");
                    return false;
                }

                toolType = i.getType();
            }
        }

        return true;
    }

    private boolean parseSmeltRecipe() throws Throwable {
        SmeltRecipe recipe = new SmeltRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // check for @flags

        // get the ingredient and smelting time
        String[] split = line.split("%");

        if (split.length == 0) {
            return ErrorReporter.error("Smelting recipe doesn't have an ingredient !");
        }

        ItemStack ingredient = Tools.parseItem(split[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (ingredient == null) {
            return false;
        }

        if (ingredient.getType() == Material.AIR) {
            return ErrorReporter.error("Recipe does not accept AIR as ingredients!");
        }

        ingredient = parseConditions(recipe, ingredient);

        recipe.setIngredient(ingredient);

        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);

        // get min-max or fixed smelting time
        if (!isRemove) { // if it's got @remove we don't care about burn time or fuel
            float minTime = Vanilla.FURNACE_RECIPE_TIME;
            float maxTime = -1;

            if (split.length >= 2) {
                String[] timeSplit = split[1].trim().toLowerCase().split("-");

                if (timeSplit[0].equals("instant")) {
                    minTime = 0;
                } else {
                    try {
                        minTime = Float.valueOf(timeSplit[0]);

                        if (timeSplit.length >= 2) {
                            maxTime = Float.valueOf(timeSplit[1]);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.warning("Invalid burn time float number! Smelt time left as default.");
                        minTime = Vanilla.FURNACE_RECIPE_TIME;
                        maxTime = -1;
                    }
                }

                if (maxTime > -1.0 && minTime >= maxTime) {
                    return ErrorReporter.error("Smelting recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }
            }

            recipe.setMinTime(minTime);
            recipe.setMaxTime(maxTime);

            nextLine();

            if (line.charAt(0) == '&') { // check if we have a fuel
                ItemStack fuelItem = Tools.parseItem(line.substring(1), 0, ParseBit.NO_AMOUNT);

                if (fuelItem == null) {
                    return false;
                }

                if (fuelItem.getType() == Material.AIR) {
                    return ErrorReporter.error("Fuel can not be air!");
                }

                recipe.setFuel(fuelItem);
                parseFlags(recipe.getFuel().getFlags());
            }
        }

        // get result or move current line after them if we got @remove and results
        List<ItemResult> results = new ArrayList<ItemResult>();

        if (isRemove) { // ignore result errors if we have @remove
            ErrorReporter.setIgnoreErrors(true);
        }

        boolean hasResults = parseResults(recipe, results, false, true);

        if (!isRemove) { // ignore results if we have @remove
            if (!hasResults) {
                return false;
            }

            ItemResult result = results.get(0);

            recipe.setResult(result);
        }

        if (isRemove) { // un-ignore result errors
            ErrorReporter.setIgnoreErrors(false);
        }

        // check if the recipe already exists
        if (!recipeExists(recipe)) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueSmeltRecipe(recipe, currentFile);
        loaded++;

        return true;
    }

    private boolean parseFuelRecipe() throws Throwable {
        FuelRecipe recipe = new FuelRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // check for @flags
        int added = 0;

        do {
            if (lineIsRecipe() || lineIsFlag()) {
                break;
            }

            recipe = new FuelRecipe(recipe);

            String[] split = line.split("%");

            if (!recipe.hasFlag(FlagType.REMOVE)) { // if it's got @remove we don't care about burn time
                if (split.length < 2 || split[1] == null) {
                    ErrorReporter.error("Burn time not set!", "It must be set after the ingredient like: ingredient % burntime");
                    continue;
                }

                // set the burn time
                String[] timeSplit = split[1].trim().split("-");
                float minTime = -1;
                float maxTime = -1;

                try {
                    minTime = Math.max(Float.valueOf(timeSplit[0]), 1);

                    if (timeSplit.length >= 2) {
                        maxTime = (float) Math.max(Float.valueOf(timeSplit[1]), 0.0);
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.error("Invalid burn time float number!");
                    continue;
                }

                if (minTime <= 0) {
                    ErrorReporter.error("Fuels can't burn for negative or 0 seconds!");
                    continue;
                }

                if (maxTime > -1 && minTime >= maxTime) {
                    maxTime = -1;
                    ErrorReporter.warning("Fuel has minimum time less or equal to maximum time!", "Use a single number if you want a fixed value");
                }

                recipe.setMinTime(minTime);
                recipe.setMaxTime(maxTime);
            }

            // set ingredient
            ItemStack ingredient = Tools.parseItem(split[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

            if (ingredient == null) {
                continue;
            }

            if (ingredient.getType() == Material.AIR) {
                ErrorReporter.error("Can not use AIR as ingredient!");
                continue;
            }

            recipe.setIngredient(ingredient);

            // check if the recipe already exists
            if (!recipeExists(recipe)) {
                continue;
            }

            if (recipeName != null && !recipeName.equals("")) {
                String name = recipeName;
                if (added > 1) {
                    name += " (" + added + ")";
                }
                recipe.setName(name); // set recipe's name if defined
            }

            registrator.queueFuelRecipe(recipe, currentFile);
            loaded++;
            added++;
        } while (nextLine());

        return added > 0;
    }

    private boolean parseBrewRecipe() throws Throwable {
        BrewRecipe recipe = new BrewRecipe();
        parseFlags(recipe.getFlags());

        if (line == null || line.charAt(0) == '=') {
            return ErrorReporter.error("No ingredient defined!");
        }

        ItemStack ingredient = Tools.parseItem(line, Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
        if (ingredient == null) {
            return ErrorReporter.error("Recipe has an invalid ingredient, needs fixing!");
        }
        Messages.send(null, "ParseBrew Ingredient: " + ingredient);
        recipe.setIngredient(ingredient);

        nextLine();

        if (line == null || line.charAt(0) == '=') {
            return ErrorReporter.error("No ingredient defined!");
        }

        ItemStack potion = Tools.parseItem(line, Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
        if (potion == null) {
            return ErrorReporter.error("Recipe has an invalid potion, needs fixing!");
        }
        Messages.send(null, "ParseBrew Potion: " + potion);
        recipe.setPotion(potion);

        List<ItemResult> results = new ArrayList<ItemResult>();

        if (!parseResults(recipe, results, true, false)) { // results have errors
            return false;
        }

        ItemResult result = results.get(0);
        Messages.send(null, "ParseBrew Result: " + result);
        recipe.setResult(result);

        // check if the recipe already exists
        if (!recipeExists(recipe)) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueBrewRecipe(recipe, currentFile);
        loaded++;

        return true;
    }

    // TODO - maybe
    private boolean parseRemoveResults() throws Throwable {
        RemoveResultRecipe recipe;
        int added = 0;

        do {
            if (lineIsRecipe()) {
                break;
            }

            ItemStack result = Tools.parseItem(line, 0);

            if (result == null) {
                continue;
            }

            if (result.getType() == Material.AIR) {
                ErrorReporter.error("Recipe has invalid item to remove!");
                continue;
            }

            recipe = new RemoveResultRecipe(result);

            // check if the recipe already exists
            if (!recipeExists(recipe)) {
                continue;
            }

            if (recipeName != null && !recipeName.equals("")) {
                String name = recipeName;
                if (added > 1) {
                    name += " (" + added + ")";
                }
                recipe.setName(name); // set recipe's name if defined
            }

            // registrator.queueRemoveResultRecipe(recipe, currentFile);
            loaded++;
            added++;
        } while (nextLine());

        return added > 0;
    }

    private boolean parseResults(BaseRecipe recipe, List<ItemResult> results, boolean allowAir, boolean oneResult) throws Throwable {
        if (line == null) {
            return false;
        }

        if (line.charAt(0) != '=') { // check if current line is a result, if not move on
            nextLine();
        }

        ItemResult result;
        float totalPercentage = 0;
        int splitChanceBy = 0;

        while (line != null && line.charAt(0) == '=') {
            result = Tools.parseItemResult(line, 0); // convert result to ItemResult, grabbing chance and what other stuff

            if (result == null) {
                nextLine();
                continue;
            }

            if (!allowAir && result.getType() == Material.AIR) {
                ErrorReporter.error("Result can not be AIR in this recipe!");
                return false;
            }

            results.add(result);
            result.setRecipe(recipe);

            if (result.getChance() < 0) {
                splitChanceBy++;
            } else {
                totalPercentage += result.getChance();
            }

            parseFlags(result.getFlags()); // check for result flags and keeps the line flow going too
        }

        if (results.isEmpty()) {
            return ErrorReporter.error("Found the '=' character but with no result!");
        }

        if (totalPercentage > 100) {
            return ErrorReporter.error("Total result items' chance exceeds 100%!", "If you want some results to be split evenly automatically you can avoid the chance number.");
        }

        // Spread remaining chance to results that have undefined chance
        if (splitChanceBy > 0) {
            float remainingChance = (100.0f - totalPercentage);
            float chance = remainingChance / splitChanceBy;

            for (ItemResult r : results) {
                if (r.getChance() < 0) {
                    totalPercentage -= r.getChance();
                    r.setChance(chance);
                    totalPercentage += chance;
                }
            }
        }

        if (!oneResult && totalPercentage < 100) {
            boolean foundAir = false;

            for (ItemResult r : results) {
                if (r.getType() == Material.AIR) {
                    r.setChance(100.0f - totalPercentage);
                    foundAir = true;
                    break;
                }
            }

            if (foundAir) {
                ErrorReporter.warning("All results are set but they do not stack up to 100% chance, extended fail chance to " + (100.0f - totalPercentage) + "!", "You can remove the chance for AIR to auto-calculate it");
            } else {
                ErrorReporter.warning("Results do not stack up to 100% and no fail chance defined, recipe now has " + (100.0f - totalPercentage) + "% chance to fail.", "You should extend or remove the chance for other results if you do not want fail chance instead!");

                results.add(new ItemResult(Material.AIR, 0, 0, (100.0f - totalPercentage)));
            }
        }

        if (oneResult && results.size() > 1) {
            ErrorReporter.warning("Can't have more than 1 result! The rest were ignored.");
        }

        return true; // valid results
    }

    private boolean recipeExists(BaseRecipe recipe) {
        ErrorReporter.setLine(directiveLine); // set the line to point to the directive rather than the last read line!
        RecipeInfo registered = getRecipeFromMap(recipe, RecipeManager.getRecipes().index);

        if (recipe.hasFlag(FlagType.OVERRIDE) || recipe.hasFlag(FlagType.REMOVE)) {
            if (registered == null) {
                recipe.getFlags().removeFlag(FlagType.REMOVE);
                recipe.getFlags().removeFlag(FlagType.OVERRIDE);

                ErrorReporter.warning("Recipe was not found, can't override/remove it! Added as new recipe.", "Use 'rmextract' command to see the exact ingredients needed");

                return true; // allow recipe to be added
            } else if (registered.getOwner() == RecipeOwner.RECIPEMANAGER && registered.getStatus() == null) {
                ErrorReporter.error("Can't override/remove RecipeManager's recipes - just edit the recipe files!");

                return false; // can't re-add recipes
            }

            return true; // all ok, allow it to be added
        }

        if (registered != null) {
            if (registered.getOwner() == RecipeOwner.RECIPEMANAGER) {
                if (!currentFile.equals(registered.getAdder())) {
                    ErrorReporter.error("Recipe already created with this plugin, file: " + registered.getAdder());

                    return false; // can't re-add recipes
                }
            } else {
                recipe.getFlags().addFlag(new FlagOverride());

                if (!Settings.getInstance().getDisableOverrideWarnings()) {
                    ErrorReporter.warning("Recipe already created by " + registered.getOwner() + ", recipe overwritten!", "You can use @override flag to overwrite the recipe or @remove to just remove it.");
                }

                return true; // allow to be added since we're overwriting it
            }
        }

        RecipeInfo queued = getRecipeFromMap(recipe, registrator.queuedRecipes);

        if (queued != null) {
            ErrorReporter.error("Recipe already created with this plugin, file: " + queued.getAdder());

            return false; // can't re-add recipes
        }

        return true; // nothing found, let it be added
    }

    private RecipeInfo getRecipeFromMap(BaseRecipe recipe, Map<BaseRecipe, RecipeInfo> map) {
        RecipeInfo info = map.get(recipe);

        if (info == null && recipe instanceof CraftRecipe) {
            CraftRecipe cr = (CraftRecipe) recipe;
            cr.setMirrorShape(true);

            info = map.get(recipe);
        }

        return info;
    }

    private ItemStack parseConditions(BaseRecipe recipe, ItemStack item) {
        if (recipe.hasFlag(FlagType.INGREDIENTCONDITION)) {
            Conditions conditions = recipe.getFlag(FlagIngredientCondition.class).getIngredientConditions(item);

            if (conditions != null) {
                ItemMeta meta = item.getItemMeta();

                if (conditions.hasName()) {
                    meta.setDisplayName(conditions.getName());
                }

                if (conditions.hasEnchants()) {
                    Map<Enchantment, Map<Short, Boolean>> enchants = conditions.getEnchants();
                    for (Entry<Enchantment, Map<Short, Boolean>> entry : enchants.entrySet()) {
                        Enchantment enchant = entry.getKey();
                        Map<Short, Boolean> value = entry.getValue();

                        for (Entry<Short, Boolean> enchantEntry : value.entrySet()) {
                            short level = enchantEntry.getKey();
                            boolean ignore = enchantEntry.getValue();
                            meta.addEnchant(enchant, level, ignore);
                        }
                    }
                }

                if (conditions.hasLore()) {
                    meta.setLore(conditions.getLores());
                }

                item.setItemMeta(meta);
            }
        }

        return item;
    }
}
