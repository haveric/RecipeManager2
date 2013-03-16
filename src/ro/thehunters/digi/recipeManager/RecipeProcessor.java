package ro.thehunters.digi.recipeManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe.RecipeType;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

/**
 * Processes all recipe files and updates main Recipes class once done.
 */
public class RecipeProcessor implements Runnable
{
    @Override
    protected void finalize() throws Throwable // TODO REMOVE
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + getClass().getName() + " :: finalize()");
        
        super.finalize();
    }
    
    private final CommandSender               sender;
    private final boolean                     check;
    private final boolean                     force;
    
    private String                            currentFile;
    private BufferedReader                    reader;
    private boolean                           commentBlock;
    private Flags                             fileFlags;
    private String                            line;
    private int                               lineNum;
    private int                               loaded;
    
    // Storage
    private volatile RecipeRegistrator        registrator   = null;
    private final Set<String>                 foundFiles    = new HashSet<String>();
    private final List<String>                fileList      = new ArrayList<String>();
    
    // Static storage
    private final static Map<String, Integer> lastModified  = new HashMap<String, Integer>();
    
    // Constants
    private final String                      DIR_PLUGIN    = RecipeManager.getPlugin().getDataFolder() + File.separator;
    private final String                      DIR_RECIPES   = DIR_PLUGIN + "recipes" + File.separator;
//    private final String                      FILE_LASTREAD = DIR_RECIPES + "lastread.dat";
    private final String                      FILE_ERRORLOG = DIR_PLUGIN + "last recipe errors.log";
    private final String[]                    COMMENTS      = { "//", "#" };
    
    private static BukkitTask                 task;
    
    protected static void reload(CommandSender sender, boolean check, boolean force)
    {
        new RecipeProcessor(sender, check, force);
    }
    
    private RecipeProcessor(CommandSender sender, boolean check, boolean force)
    {
        this.sender = sender;
        this.check = check;
        this.force = force;
        
        if(task != null)
            task.cancel();
        
        RecipeErrorReporter.startCatching();
        
        if(RecipeManager.getSettings().MULTITHREADING)
            task = Bukkit.getScheduler().runTaskAsynchronously(RecipeManager.getPlugin(), this);
        else
            run();
    }
    
    @Override
    public void run()
    {
        final long start = System.currentTimeMillis();
        
        try
        {
            Messages.send(sender, (check ? "Checking" : "Loading") + " " + (force ? "all" : "changed") + " recipes...");
            
            File dir = new File(DIR_RECIPES);
            
            if(!dir.exists() && !dir.mkdirs())
                Messages.send(sender, ChatColor.RED + "Error: couldn't create directories: " + dir.getPath());
            
            if(force)
                lastModified.clear();
            
            // Scan for files
            analyzeDirectory(dir);
            
            boolean noRecipeFiles = foundFiles.isEmpty();
            
            if(fileList.isEmpty())
            {
                if(noRecipeFiles)
                {
                    Messages.send(sender, "Done (" + (System.currentTimeMillis() - start) / 1000.0 + "s), no recipe files exist in the recipes folder!");
                }
                else
                {
                    Messages.send(sender, "Done (" + (System.currentTimeMillis() - start) / 1000.0 + "s), no modified recipe files to " + (check ? "check" : "load") + ".");
                    
                    if(!force)
                        Messages.send(sender, "You can use 'force' argument for 'rmreload' command to re-check all files regardless of modified state.");
                }
            }
            else
            {
                registrator = new RecipeRegistrator(sender);
                
                long lastDisplay = System.currentTimeMillis();
                long time;
                int numFiles = fileList.size();
                int parsedFiles = 0;
                loaded = 0;
                
                // Start reading files...
                for(String name : fileList)
                {
                    try
                    {
                        parseFile(DIR_RECIPES, name);
                        parsedFiles++;
                        time = System.currentTimeMillis();
                        
                        // display progress each second
                        if(time > lastDisplay + 500)
                        {
                            Messages.send(sender, "Recipes processed " + ((parsedFiles * 100) / numFiles) + "%...");
                            lastDisplay = time;
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                
                // TODO detect when loaded files were deleted
                
                int errors = RecipeErrorReporter.getCatchedAmount();
                
                if(errors > 0)
                {
                    Messages.send(sender, ChatColor.YELLOW + (check ? "Checked" : "Parsed") + " " + loaded + " recipes from " + fileList.size() + " files in " + (System.currentTimeMillis() - start) / 1000.0 + " seconds, " + errors + " errors were found" + (sender == null ? ", see below:" : ", see console."));
                    RecipeErrorReporter.print(FILE_ERRORLOG);
                }
                else
                {
                    Messages.send(sender, (check ? "Checked" : "Parsed") + " " + loaded + " recipes from " + fileList.size() + " files without errors, elapsed time " + (System.currentTimeMillis() - start) / 1000.0 + " seconds.");
                }
                
                if(!lastModified.isEmpty())
                {
                    // Clean up last modified list of inexistent files
                    Iterator<Entry<String, Integer>> iterator = lastModified.entrySet().iterator();
                    String fileName;
                    
                    while(iterator.hasNext())
                    {
                        fileName = iterator.next().getKey();
                        
                        if(!foundFiles.contains(fileName))
                        {
                            foundFiles.remove(fileName);
                            iterator.remove();
                        }
                    }
                }
            }
        }
        finally
        {
            task = null;
            
            if(check || registrator == null)
                return;
            
            // Calling registerRecipesToServer() in main thread...
            if(RecipeManager.getSettings().MULTITHREADING)
            {
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        registrator.registerRecipesToServer(sender, start, (fileList.size() == foundFiles.size() ? null : new HashSet<String>(fileList)));
                    }
                }.runTask(RecipeManager.getPlugin());
            }
            else
            {
                registrator.registerRecipesToServer(sender, start, (fileList.size() == foundFiles.size() ? null : new HashSet<String>(fileList)));
            }
        }
    }
    
    private void analyzeDirectory(File dir)
    {
        String fileName;
        Integer lastMod;
        int fileMod;
        
        for(File file : dir.listFiles())
        {
            if(file.isDirectory())
            {
                if(!file.getName().equalsIgnoreCase("disabled"))
                    analyzeDirectory(file);
            }
            else if(file.getName().endsWith(".txt"))
            {
                fileName = file.getPath().replace(DIR_RECIPES, ""); // get the relative path+filename
                foundFiles.add(fileName); // add to found files list to clean the lastmodified file later
                
                fileMod = Math.round(file.lastModified() / 1000);
                lastMod = lastModified.get(fileName);
                
                if(lastMod == null)
                    lastModified.put(fileName, fileMod);
                else if(lastMod == fileMod)
                    continue;
                
                fileList.add(fileName); // add to the processing file list
            }
        }
    }
    
    // TODO remove this
    private void debug(String message)
    {
//        StringBuilder msg = new StringBuilder().append(ChatColor.RED).append("[debug] ").append(ChatColor.LIGHT_PURPLE).append(message).append(" | ").append(ChatColor.GOLD).append(lineNum).append(" | ").append(line);
//        Bukkit.getConsoleSender().sendMessage(msg.toString());
    }
    
    private void parseFile(String root, String fileName) throws Exception
    {
        reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(root + fileName))));
        currentFile = fileName;
        lineNum = 0;
        RecipeErrorReporter.setFile(currentFile);
        fileFlags = new Flags();
        commentBlock = false;
        boolean added = false;
        
        parseFlags(fileFlags); // parse file header flags that applies to all recipes
        
        while(searchRecipes()) // search for recipes...
        {
            debug("checking recipe type...");
            
            if(line.equalsIgnoreCase(RecipeType.CRAFT.getDirective()))
            {
                added = parseCraftRecipe();
            }
            else if(line.equalsIgnoreCase(RecipeType.COMBINE.getDirective()))
            {
                added = parseCombineRecipe();
            }
            else if(line.equalsIgnoreCase(RecipeType.SMELT.getDirective()))
            {
                added = parseSmeltRecipe();
            }
            else if(line.equalsIgnoreCase(RecipeType.FUEL.getDirective()))
            {
                added = parseFuelRecipe();
            }
            else if(line.equalsIgnoreCase("removeresult"))
            {
                added = parseRemoveResult();
            }
            else
            {
                RecipeErrorReporter.warning("Unexpected directive: '" + line + "'", "This might be caused by previous errors. For more info read '" + Files.FILE_INFO_ERRORS + "'.");
            }
            
            if(!added)
            {
                RecipeErrorReporter.error("Recipe was not added! Review previous errors and fix them.", "Warnings do not prevent recipe creation but they should be fixed as well!");
            }
        }
        
        if(lineNum == 0)
            RecipeErrorReporter.warning("Recipe file '" + fileName + "' is empty.");
        
        reader.close();
    }
    
    private boolean searchRecipes()
    {
        if(line != null)
        {
            for(RecipeType type : RecipeType.values())
            {
                if(type.getDirective() != null && line.equalsIgnoreCase(type.getDirective()))
                    return true;
            }
        }
        
        return nextLine();
    }
    
    private boolean readNextLine()
    {
        lineNum++;
        RecipeErrorReporter.setLine(lineNum);
        
        try
        {
            line = reader.readLine();
            return line != null;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean nextLine()
    {
        do
        {
            if(!readNextLine())
                return false;
            
            line = parseComments();
        }
        while(line == null);
        
        return true;
    }
    
    private String parseComments()
    {
//        debug("parsing comments...");
        
        line = (line == null ? null : line.trim());
        
        if(line == null || line.isEmpty())
            return null;
        
        int index;
        
        // if we are in a comment block check for exit character
        if(commentBlock)
        {
            index = line.indexOf("*/");
            
            if(index >= 0)
            {
                commentBlock = false;
                return (index == 0 ? null : line.substring(0, index).trim());
            }
            
            return null;
        }
        
        index = line.indexOf("/*"); // check for comment block start
        
        if(index >= 0)
        {
            int end = line.indexOf("*/"); // check for comment block end chars on the same line
            
            if(end > 0)
            {
                return line.substring(0, index) + line.substring(end + 2);
            }
            else
            {
                commentBlock = true;
                return (index == 0 ? null : line.substring(0, index).trim());
            }
        }
        
        // now check for in-line comments
        for(String comment : COMMENTS)
        {
            index = line.indexOf(comment);
            
            if(index == 0)
                return null;
            
            if(index > -1)
                return line.substring(0, index).trim(); // partial comment, return filtered data
        }
        
        return line;
    }
    
    private void parseFlags(Flags flags) throws Exception
    {
        debug("parsing flags...");
        
        nextLine();
        
        while(line != null && line.charAt(0) == '@')
        {
            flags.parseFlag(line);
            nextLine();
        }
    }
    
    private boolean parseCraftRecipe() throws Exception
    {
        debug("parsing craft recipe...");
        
        CraftRecipe recipe = new CraftRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // parse recipe's flags
        
        ItemStack[] ingredients = new ItemStack[9];
        String split[];
        ItemStack item;
        int rows = 0;
        boolean ingredientErrors = false;
        
        while(rows < 3) // loop until we find 3 rows of ingredients (or bump into the result along the way)
        {
            if(rows > 0)
                nextLine();
            
            if(line == null)
                return RecipeErrorReporter.error("No ingredients defined!");
            
            debug("searching for ingredients...");
            
            if(line.charAt(0) == '=') // if we bump into the result prematurely (smaller recipes)
                break;
            
            split = line.split("\\+"); // split ingredients by the + sign
            int rowLen = split.length;
            
            if(rowLen > 3) // if we find more than 3 ingredients warn the user and limit it to 3
            {
                rowLen = 3;
                RecipeErrorReporter.warning("You can't have more than 3 ingredients on a row, ingredient(s) ignored.", "Remove the extra ingredient(s).");
            }
            
            for(int i = 0; i < rowLen; i++) // go through each ingredient on the line
            {
                if((item = Tools.convertStringToItemStack(split[i], Vanilla.DATA_WILDCARD, true, false, false)) == null) // invalid item
                    ingredientErrors = true;
                
                if(ingredientErrors) // no point in adding more ingredients if there are errors
                    continue;
                
                if(item.getTypeId() != 0)
                    ingredients[(rows * 3) + i] = item;
            }
            
            rows++;
        }
        
        if(ingredientErrors) // invalid ingredients found
        {
            RecipeErrorReporter.error("Recipe has some invalid ingredients, fix them!");
            return false;
        }
        else if(rows == 0) // no ingredients were processed
        {
            RecipeErrorReporter.error("Recipe doesn't have ingredients !", "Consult readme.txt for proper recipe syntax.");
            return false;
        }
        
        recipe.setIngredients(ingredients); // done with ingredients, set'em
        debug("set ingredients...");
        
        // get results
        List<ItemResult> results = new ArrayList<ItemResult>();
        
        if(!parseResults(recipe, results, true, false)) // results have errors
            return false;
        
        recipe.setResults(results); // done with results, set'em
        
        // check if the recipe already exists...
        if(!recipeCheckExists(recipe))
            return false;
        
        debug("done with recipe...");
        
        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueCraftRecipe(recipe, currentFile);
        loaded++;
        return true; // succesfully added
    }
    
    private boolean parseCombineRecipe() throws Exception
    {
        debug("parsing combine recipe...");
        
        CombineRecipe recipe = new CombineRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // parse recipe's flags
        
        // get the ingredients
        String[] ingredientsRaw = line.split("\\+");
        
        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        ItemStack item;
        int items = 0;
        
        for(String str : ingredientsRaw)
        {
            item = Tools.convertStringToItemStack(str, Vanilla.DATA_WILDCARD, true, true, false);
            
            if(item == null)
                return false;
            
            if((items += item.getAmount()) > 9)
            {
                RecipeErrorReporter.error("Combine recipes can't have more than 9 ingredients !", "If you're using stacks make sure they don't exceed 9 items in total.");
                return false;
            }
            
            ingredients.add(item);
        }
        
        recipe.setIngredients(ingredients);
        
        // get the results
        List<ItemResult> results = new ArrayList<ItemResult>();
        
        if(!parseResults(recipe, results, true, false))
            return false;
        
        recipe.setResults(results);
        
        // check if recipe already exists
        if(!recipeCheckExists(recipe))
            return false;
        
        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueCombineRecipe(recipe, currentFile);
        loaded++;
        return true; // no errors encountered
    }
    
    private boolean parseSmeltRecipe() throws Exception
    {
        debug("parsing smelting recipe...");
        
        SmeltRecipe recipe = new SmeltRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // check for @flags
        
        // get the ingredient and smelting time
        String[] split = line.split("%");
        
        if(split.length == 0)
        {
            return RecipeErrorReporter.error("Smeling recipe doesn't have an ingredient !");
        }
        
        ItemStack ingredient = Tools.convertStringToItemStack(split[0], Vanilla.DATA_WILDCARD, true, false, false);
        
        if(ingredient == null)
            return false;
        
        if(ingredient.getTypeId() == 0)
        {
            return RecipeErrorReporter.error("Recipe does not accept AIR as ingredients !");
        }
        
        recipe.setIngredient(ingredient);
        
        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);
        
        // get min-max or fixed smelting time
        if(!isRemove) // if it's got @remove we don't care about burn time or fuel
        {
            float minTime = Vanilla.FURNACE_RECIPE_TIME;
            float maxTime = -1;
            
            if(split.length >= 2)
            {
                String[] timeSplit = split[1].trim().split("-");
                
                if(!timeSplit[0].equals("INSTANT"))
                {
                    try
                    {
                        minTime = Float.valueOf(timeSplit[0]);
                        
                        if(timeSplit.length >= 2)
                            maxTime = Float.valueOf(timeSplit[1]);
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Invalid burn time float number! Smelt time set to default.");
                        minTime = Vanilla.FURNACE_RECIPE_TIME;
                        maxTime = -1;
                    }
                }
                else
                {
                    minTime = 0;
                }
                
                if(maxTime > -1.0 && minTime >= maxTime)
                {
                    return RecipeErrorReporter.error("Smelting recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }
            }
            
            recipe.setMinTime(minTime);
            recipe.setMaxTime(maxTime);
            
            nextLine();
            
            if(line.charAt(0) == '&') // check if we have a fuel
            {
                ItemStack fuelItem = Tools.convertStringToItemStack(line.substring(1).trim(), 0, true, true, true);
                
                if(fuelItem == null)
                    return false;
                
                if(fuelItem.getTypeId() == 0)
                {
                    return RecipeErrorReporter.error("Fuel can not be air!");
                }
                
                recipe.setFuel(fuelItem);
                parseFlags(recipe.getFuel().getFlags());
            }
        }
        
        // get result or move current line after them if we got @remove and results
        List<ItemResult> results = new ArrayList<ItemResult>();
        
        if(isRemove) // ignore result errors if we have @remove
            RecipeErrorReporter.setIgnoreErrors(true);
        
        boolean hasResults = parseResults(recipe, results, false, true);
        
        if(!isRemove) // ignore results if we have @remove
        {
            if(!hasResults)
                return false;
            
            recipe.setResult(results.get(0));
        }
        
        if(isRemove) // un-ignore result errors
            RecipeErrorReporter.setIgnoreErrors(false);
        
        // check if the recipe already exists
        if(!recipeCheckExists(recipe))
            return false;
        
        // add the recipe to the Recipes class and to the list for later adding to the server
        registrator.queueSmeltRecipe(recipe, currentFile);
        loaded++;
        return true;
    }
    
    private boolean parseFuelRecipe() throws Exception
    {
        debug("parsing fuel recipe...");
        
        FuelRecipe recipe = new FuelRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags()); // check for @flags
        
        // get the ingredient and burning time
        String[] split = line.split("%");
        
        if(!recipe.hasFlag(FlagType.REMOVE)) // if it's got @remove we don't care about burn time
        {
            if(split.length < 2 || split[1] == null)
            {
                return RecipeErrorReporter.error("Burn time not set !", "It must be set after the ingredient like: ingredient % burntime");
            }
            
            // set the burn time
            String[] timeSplit = split[1].trim().split("-");
            float minTime = -1;
            float maxTime = -1;
            
            try
            {
                minTime = Math.max(Float.valueOf(timeSplit[0]), 1);
                
                if(timeSplit.length >= 2)
                    maxTime = (float)Math.max(Float.valueOf(timeSplit[1]), 0.0);
            }
            catch(NumberFormatException e)
            {
                return RecipeErrorReporter.error("Invalid burn time float number!");
            }
            
            if(minTime <= 0)
            {
                return RecipeErrorReporter.error("Fuels can't burn for negative or 0 seconds!");
            }
            
            if(maxTime > -1 && minTime >= maxTime)
            {
                maxTime = -1;
                RecipeErrorReporter.warning("Fuel has minimum time less or equal to maximum time!", "Use a single number if you want a fixed value");
            }
            
            recipe.setMinTime(minTime);
            recipe.setMaxTime(maxTime);
        }
        
        // set ingredient
        ItemStack ingredient = Tools.convertStringToItemStack(split[0], Vanilla.DATA_WILDCARD, true, false, false);
        
        if(ingredient == null)
            return false;
        
        if(ingredient.getTypeId() == 0)
        {
            RecipeErrorReporter.error("Can not use AIR as ingredient!");
            return false;
        }
        
        recipe.setIngredient(ingredient);
        
        // check if the recipe already exists
        if(!recipeCheckExists(recipe))
            return false;
        
        registrator.queuFuelRecipe(recipe, currentFile);
        
        debug("done with fuel !");
        
        loaded++;
        return true;
    }
    
    private boolean parseRemoveResult() throws Exception
    {
        /*
        ItemStack item = Tools.convertStringToItemStack(line, 1, true, true, true);
        
        if(item == null)
            return new String[] { "Invalid item!" };
        
        loaded++;
        */
        return false;
    }
    
    private boolean parseResults(BaseRecipe recipe, List<ItemResult> results, boolean allowAir, boolean oneResult) throws Exception
    {
        if(line.charAt(0) != '=') // check if current line is a result, if not move on
            nextLine();
        
        ItemResult result;
        float totalPercentage = 0;
        int splitChanceBy = 0;
        
        while(line != null && line.charAt(0) == '=')
        {
            result = Tools.convertStringToItemResult(line, 0, true, true, true); // convert result to ItemResult, grabbing chance and whatother stuff
            
            if(result == null)
            {
                nextLine();
                continue;
            }
            
            if(!allowAir && result.getTypeId() == 0)
            {
                RecipeErrorReporter.error("Result can not be AIR in this recipe!");
                return false;
            }
            
            results.add(result);
            result.setRecipe(recipe);
            
            if(result.getChance() < 0)
                splitChanceBy++;
            else
                totalPercentage += result.getChance();
            
            parseFlags(result.getFlags()); // check for result flags and keeps the line flow going too
        }
        
        if(results.isEmpty())
        {
            return RecipeErrorReporter.error("Found the '=' character but with no result!");
        }
        
        if(totalPercentage > 100)
        {
            return RecipeErrorReporter.error("Total result items' chance exceeds 100% !", "If you want some results to be split evenly automatically you can avoid the chance number.");
        }
        
        // Spread remaining chance to results that have undefined chance
        if(splitChanceBy > 0)
        {
            float remainingChance = (100.0f - totalPercentage);
            float chance = remainingChance / splitChanceBy;
            
            for(ItemResult r : results)
            {
                if(r.getChance() < 0)
                {
                    totalPercentage -= r.getChance();
                    r.setChance(chance);
                    totalPercentage += chance;
                }
            }
        }
        
        if(!oneResult && totalPercentage < 100)
        {
            boolean foundAir = false;
            
            for(ItemResult r : results)
            {
                if(r.getTypeId() == 0)
                {
                    r.setChance(100.0f - totalPercentage);
                    foundAir = true;
                    break;
                }
            }
            
            if(foundAir)
            {
                RecipeErrorReporter.warning("All results are set but they do not stack up to 100% chance, extended fail chance to " + (100.0f - totalPercentage) + " !", "You can remove the chance for AIR to auto-calculate it");
            }
            else
            {
                RecipeErrorReporter.warning("Results do not stack up to 100% and no fail chance defined, recipe now has " + (100.0f - totalPercentage) + "% chance to fail.", "You should extend or remove the chance for other results if you do not want fail chance instead!");
                
                results.add(new ItemResult(Material.AIR, 0, 0, (100.0f - totalPercentage)));
            }
        }
        
        if(oneResult && results.size() > 1)
        {
            RecipeErrorReporter.warning("Can't have more than 1 result! The rest were ignored.");
        }
        
        debug("done with results...");
        
        return true; // valid results
    }
    
    private boolean recipeCheckExists(BaseRecipe recipe) // TODO
    {
        RecipeInfo registered = RecipeManager.recipes.index.get(recipe);
        boolean isOverride = recipe.hasFlag(FlagType.OVERRIDE);
        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);
        
        if(isOverride || isRemove)
        {
            if(registered == null)
            {
                recipe.getFlags().removeFlag(FlagType.REMOVE);
                recipe.getFlags().removeFlag(FlagType.OVERRIDE);
                RecipeErrorReporter.warning("Recipe was not found, can't override/remove it! Added as new recipe.", "Use 'rmextract' command to see the exact ingredients needed");
                return true; // allow recipe to be added
            }
            else if(registered.getOwner() == RecipeOwner.RECIPEMANAGER && registered.getStatus() == null)
            {
                RecipeErrorReporter.warning("Can't override/remove RecipeManager's recipes - just edit the recipe files!");
                return false;
            }
            
            return true;
        }
        
        if(registered != null && registered.getOwner() == RecipeOwner.RECIPEMANAGER && !currentFile.equals(registered.getAdder()))
        {
            RecipeErrorReporter.warning("Recipe already created with this plugin, file: " + registered.getAdder());
            return false;
        }
        
        RecipeInfo queued = registrator.queuedRecipes.get(recipe);
        
        if(queued != null)
        {
            RecipeErrorReporter.warning("Recipe already created with this plugin, file: " + queued.getAdder());
            return false;
        }
        
        /*
        RecipeInfo infoExisting = RecipeManager.getRecipes() == null ? null : RecipeManager.getRecipes().getRecipeInfo(recipe);
        RecipeInfo infoProcessed = registrator.queuedRecipes.get(recipe);
        boolean override = recipe.getFlags().isOverride();
        boolean remove = recipe.getFlags().isRemove();
        
        if(infoProcessed == null && infoExisting == null)
        {
            if(remove || override)
            {
                return new String[] { "Recipe was not found, can't " + (override ? "override" : "remove") + " it! Added as new recipe.", "Use 'rmextract' command to see the exact ingredients needed" };
            }
            
            return null;
        }
        
        if((infoProcessed != null && infoProcessed.getOwner() == RecipeOwner.RECIPEMANAGER) || (infoExisting != null && infoExisting.getOwner() == RecipeOwner.RECIPEMANAGER))
        {
            return new String[] { "Recipe already created with this plugin, file: " + (infoProcessed == null ? infoExisting.getAdder() : infoProcessed.getAdder()), (override || remove ? "You can't @override or @remove recipes that you added using this plugin because you can just edit or delete them from the files." : null) };
        }
        else if(!(remove || override))
        {
            return new String[] { "Recipe already exists, added by " + (infoProcessed == null ? infoExisting.getAdder() : infoProcessed.getAdder()), "You can use @override flag to change recipe's result(s)." };
        }
        */
        
        return true;
    }
}