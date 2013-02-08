package digi.recipeManager;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.data.*;
import digi.recipeManager.data.RecipeInfo.RecipeOwner;
import digi.recipeManager.data.RmRecipe.RecipeType;

/**
 * Processes all recipe files and updates main Recipes class once done.
 */
public class RecipeProcessor implements Runnable
{
    private final CommandSender           sender;
    private final boolean                 check;
    private Recipes                       recipes;
    
    private Set<String>                   foundFiles;
    private List<String>                  fileList;
    private HashMap<String, Long>         lastRead;
    private HashMap<String, List<String>> recipeErrors;
    
    private String                        currentFile;
    private BufferedReader                reader;
    private boolean                       commentBlock;
    private Flags                         fileFlags;
    private String                        line;
    private int                           lineNum;
    
    // Constants
    private final String                  DIR_PLUGIN    = RecipeManager.getPlugin().getDataFolder() + File.separator;
    private final String                  DIR_RECIPES   = DIR_PLUGIN + "recipes" + File.separator;
    private final String                  FILE_LASTREAD = DIR_RECIPES + "lastread.dat";
    private final String                  FILE_ERRORLOG = DIR_PLUGIN + "last recipe errors.log";
    private final String                  NL            = System.getProperty("line.separator");
    private final String[]                COMMENTS      = { "//", "#" };
    
    protected RecipeProcessor(CommandSender sender, boolean check)
    {
        this.sender = sender;
        this.check = check;
        
        run();
//        Bukkit.getScheduler().runTask(RecipeManager.getPlugin(), this);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void run()
    {
        long startTime = System.currentTimeMillis();
        
        Messages.send(sender, (check ? "Checking" : "Loading") + " recipes...");
        
        File dir = new File(DIR_RECIPES);
        
        if(!dir.exists() && !dir.mkdirs())
            Messages.send(sender, ChatColor.RED + "Error: couldn't create directories: " + dir.getPath());
        
        // Load last modified file list
        lastRead = (HashMap<String, Long>)Tools.loadObjectFromFile(FILE_LASTREAD);
        
        if(lastRead == null)
            lastRead = new HashMap<String, Long>();
        
        fileList = new ArrayList<String>();
        foundFiles = new HashSet<String>();
        recipeErrors = new HashMap<String, List<String>>();
        
        // Scan for files
        analyzeDirectory(dir);
        
        boolean noRecipeFiles = foundFiles.isEmpty();
        
        if(fileList.isEmpty())
        {
            if(noRecipeFiles)
                Messages.send(sender, "Done (" + (System.currentTimeMillis() - startTime) / 1000.0 + "s), no recipe files exist in the recipes folder!");
            else
                Messages.send(sender, "Done (" + (System.currentTimeMillis() - startTime) / 1000.0 + "s), no modified recipe files to " + (check ? "check" : "load") + ".");
        }
        else
        {
            recipes = new Recipes(); // Create new recipe storage
            
            long lastDisplay = System.currentTimeMillis();
            long time;
            int numFiles = fileList.size();
            int parsedFiles = 0;
            
            // Start reading files...
            for(String name : fileList)
            {
                try
                {
                    currentFile = name;
                    lineNum = 0;
                    parseFile(DIR_RECIPES + name);
                    parsedFiles++;
                    time = System.currentTimeMillis();
                    
                    // display progress each second
                    if(time > lastDisplay + 1000)
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
            
            if(!check) // replace recipes storage from server
                recipes.registerRecipesToServer();
            
            int errors = recipeErrors.size();
            int recipeNum = recipes.craftRecipes.size() + recipes.combineRecipes.size() + recipes.smeltRecipes.size() + recipes.fuels.size();
            
            if(errors > 0)
            {
                Messages.send(sender, ChatColor.RED + "Done " + (check ? "checking" : "loading") + " " + recipeNum + " recipes in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds, " + errors + " errors were found" + (sender == null ? ", see below:" : ", see console."));
                
                StringBuilder buffer = new StringBuilder().append(ChatColor.RED).append("There were ").append(errors).append(" errors while processing the files: ").append(NL).append(NL);
                
                for(Entry<String, List<String>> entry : recipeErrors.entrySet())
                {
                    buffer.append(ChatColor.BOLD).append(ChatColor.BLUE).append("File: ").append(entry.getKey()).append(NL);
                    
                    for(String error : entry.getValue())
                    {
                        buffer.append(ChatColor.WHITE).append(error).append(NL);
                    }
                    
                    buffer.append(NL);
                }
                
                Messages.info(buffer.append(NL).append(NL).toString());
                
                if(Tools.saveTextToFile(ChatColor.stripColor(buffer.toString()), FILE_ERRORLOG))
                    Messages.info(ChatColor.YELLOW + "Apart from server.log, these errors have been saved in '" + FILE_ERRORLOG + "' as well.");
            }
            else
                Messages.send(sender, "Done " + (check ? "checking" : "loading") + " " + recipeNum + " recipes without errors, elapsed time " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
            
            if(!lastRead.isEmpty())
            {
                // Clean up last modified file list of inexistent files
                Iterator<Entry<String, Long>> iterator = lastRead.entrySet().iterator();
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
            
            // Save last modified file list to file if we found at least 1 file
//            if(!noRecipeFiles)
//                Tools.saveObject(lastRead, FILE_LASTREAD); // TODO activate this cache once done testing
        }
    }
    
    private void analyzeDirectory(File dir)
    {
        String fileName;
        Long lastMod;
        long fileMod;
        
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
                
                // Check when was file last modified
                fileMod = (file.lastModified() / 1000);
                lastMod = lastRead.get(fileName);
                
                if(lastMod == null)
                    lastRead.put(fileName, fileMod);
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
    
    private void parseFile(String fileName) throws Exception
    {
        this.currentFile = fileName;
        reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));
        fileFlags = new Flags();
        lineNum = 0;
        String[] error = null;
        
        parseFlags(fileFlags, null, null); // parse file header flags that applies to all recipes
        
        while(searchRecipes())
        {
            debug("checking recipe type...");
            
            if(line.equalsIgnoreCase(RecipeType.CRAFT.getDirective()))
                error = parseCraftRecipe();
            else if(line.equalsIgnoreCase(RecipeType.COMBINE.getDirective()))
                error = parseCombineRecipe();
            else if(line.equalsIgnoreCase(RecipeType.SMELT.getDirective()))
                error = parseSmeltRecipe();
            else if(line.equalsIgnoreCase(RecipeType.FUEL.getDirective()))
                error = parseFuelRecipe();
            else if(line.equalsIgnoreCase("removeresult"))
                error = parseRemoveResult();
            else
                error = new String[] { ChatColor.YELLOW + "Unexpected directive: '" + line + "'", "This might be caused by previous errors. For more info read '" + InfoFiles.FILE_INFOERRORS + "'." };
            
            if(error != null)
                recipeError(error[0], (error.length > 1 ? error[1] : null));
        }
        
        if(lineNum == 0)
            recipeError(ChatColor.YELLOW + "Recipe file '" + fileName + "' is empty.", null);
        
        commentBlock = false;
        reader.close();
    }
    
    private boolean searchRecipes()
    {
        if(line != null)
        {
            for(RecipeType type : RecipeType.values())
            {
                if(line.equalsIgnoreCase(type.getDirective()))
                    return true;
            }
        }
        
        return nextLine();
    }
    
    private boolean readNextLine()
    {
        lineNum++;
        
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
        
        // now check for line comments
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
    
    private void parseFlags(Flags flags, RecipeType recipeType, ItemStack item) throws Exception
    {
        debug("parsing flags...");
        
        nextLine();
        
        if(line == null)
            return;
        
        while(line.charAt(0) == '@')
        {
            parseFlag(flags, recipeType, item);
            nextLine();
        }
    }
    
    private void parseFlag(Flags flags, RecipeType recipeType, ItemStack item)
    {
        debug("parsing found flag...");
        
        String[] split = line.split(":", 2);
        String flag = split[0].substring(1).trim();
        String value = (split.length > 1 ? split[1].trim() : null);
        
        if(flag.equalsIgnoreCase("log"))
        {
            flags.setLog(value == null ? true : !value.equalsIgnoreCase("false"));
            return;
        }
        
        if(flag.equalsIgnoreCase("override"))
        {
            flags.setOverride(value == null ? true : !value.equalsIgnoreCase("false"));
            return;
        }
        
        if(flag.equalsIgnoreCase("remove"))
        {
            flags.setRemove(value == null ? true : !value.equalsIgnoreCase("false"));
            return;
        }
        
        if(flag.equalsIgnoreCase("secret"))
        {
            flags.setSecret(value == null ? true : !value.equalsIgnoreCase("false"));
            return;
        }
        
        if(flag.equalsIgnoreCase("itemname"))
        {
            if(item == null)
                recipeError("The @" + flag + " can only be used on results!", null);
            else
                flags.setItemName(value);
            
            return;
        }
        
        recipeError("Unknown flag: " + line, "Flag name might be diferent, check " + InfoFiles.FILE_INFOFLAGS);
    }
    
    private String[] parseCraftRecipe() throws Exception
    {
        debug("parsing craft recipe...");
        
        CraftRecipe recipe = new CraftRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags(), RecipeType.CRAFT, null); // parse recipe's flags
        
        ItemStack[] ingredients = new ItemStack[9];
        String split[];
        ItemStack item;
        int rows = 0;
        boolean ingredientErrors = false;
        
        while(rows < 3) // loop until we find 3 rows of ingredients (or bump into the result along the way)
        {
            if(rows > 0)
                nextLine();
            
            debug("searching for ingredients...");
            
            if(line.charAt(0) == '=') // if we bump into the result prematurely (smaller recipes)
                break;
            
            split = line.split("\\+"); // split ingredients by the + sign
            int rowLen = split.length;
            
            if(rowLen > 3) // if we find more than 3 ingredients warn the user and limit it to 3
            {
                rowLen = 3;
                recipeError("You can't have more than 3 ingredients on a row, ingredient(s) ignored.", "Remove the extra ingredient(s).");
            }
            
            for(int i = 0; i < rowLen; i++) // go through each ingredient on the line
            {
                if((item = convertStringToItemStack(split[i], -1, true, false, false, true)) == null) // invalid item
                    ingredientErrors = true;
                
                if(ingredientErrors) // no point in adding more ingredients if there are errors
                    continue;
                
                if(item.getTypeId() != 0)
                    ingredients[(rows * 3) + i] = item;
            }
            
            rows++;
        }
        
        if(ingredientErrors) // invalid ingredients found
            return new String[] { "Recipe has some invalid ingredients, fix them!" };
        else if(rows == 0) // no ingredients were processed
            return new String[] { "Recipe doesn't have ingredients !", "Consult readme.txt for proper recipe syntax." };
        
        recipe.setIngredients(ingredients); // done with ingredients, set'em
        debug("set ingredients...");
        
        // get results
        List<ItemResult> results = new ArrayList<ItemResult>();
        String[] errors = parseResults(recipe, results, false, false);
        
        if(errors != null) // results have errors
            return errors;
        
        recipe.setResults(results); // done with results, set'em
        
        // check if the recipe already exists...
        errors = recipeCheckExists(recipe);
        
        if(errors != null)
            return errors;
        
        debug("done with recipe...");
        
        // add the recipe to the Recipes class and to the list for later adding to the server
        recipes.addCraftRecipe(recipe);
        
        return null; // no errors encountered
    }
    
    private String[] parseCombineRecipe() throws Exception
    {
        debug("parsing combine recipe...");
        
        CombineRecipe recipe = new CombineRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags(), RecipeType.COMBINE, null); // parse recipe's flags
        
        // get the ingredients
        String[] ingredientsRaw = line.split("\\+");
        
        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        ItemStack item;
        int items = 0;
        
        for(String str : ingredientsRaw)
        {
            item = convertStringToItemStack(str, -1, true, true, false, true);
            
            if(item == null)
                return new String[] { "Recipe has some invalid ingredients, fix them!" };
            
            if((items += item.getAmount()) > 9)
                return new String[] { "Combine recipes can't have more than 9 ingredients !", "If you're using stacks make sure they don't exceed 9 items in total." };
            
            ingredients.add(item);
        }
        
        recipe.setIngredients(ingredients);
        
        // get the results
        List<ItemResult> results = new ArrayList<ItemResult>();
        String[] resultErrors = parseResults(recipe, results, false, false);
        
        if(resultErrors != null)
            return resultErrors;
        
        recipe.setResults(results);
        
        // check if recipe already exists
        String[] errors = recipeCheckExists(recipe);
        
        if(errors != null)
            return errors;
        
        // add the recipe to the Recipes class and to the list for later adding to the server
        recipes.addCombineRecipe(recipe);
        
        return null; // no errors encountered
    }
    
    private String[] parseSmeltRecipe() throws Exception
    {
        debug("parsing smelting recipe...");
        
        SmeltRecipe recipe = new SmeltRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags(), RecipeType.SMELT, null); // check for @flags
        
        // get the ingredient and smelting time
        String[] split = line.split("%");
        
        if(split.length == 0)
            return new String[] { "Smeling recipe doesn't have an ingredient !" };
        
        ItemStack ingredient = convertStringToItemStack(split[0], -1, true, false, false, true);
        
        if(ingredient == null)
            return new String[] { "Invalid ingredient '" + split[0] + "'.", "Name could be diferent, look in readme.txt for links." };
        
        recipe.setIngredient(ingredient);
        
        // get min-max or fixed smelting time
        if(!recipe.getFlags().isRemove()) // if it's got @remove we don't care about burn time
        {
            float minTime = -1;
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
                        recipeError("Invalid burn time float number! Smelt time set to default.", null);
                        minTime = -1;
                        maxTime = -1;
                    }
                }
                else
                    minTime = 0;
                
                if(maxTime > -1.0 && minTime >= maxTime)
                    return new String[] { "Smelting recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value." };
            }
            
            recipe.setMinTime(minTime);
            recipe.setMaxTime(maxTime);
        }
        
        // get result or move current line after them if we got @remove and results
        List<ItemResult> results = new ArrayList<ItemResult>();
        String[] resultErrors = parseResults(recipe, results, true, true);
        
        if(!recipe.getFlags().isRemove()) // ignore results and results errors if we have @remove
        {
            if(resultErrors != null)
                return resultErrors;
            
            if(results.size() > 1)
                recipeError("Can't have more than 1 result in smelting recipes! Rest of results ignored.", null);
            
            recipe.setResult(results.get(0));
        }
        
        // check if the recipe already exists
        String[] errors = recipeCheckExists(recipe);
        
        if(errors != null)
            return errors;
        
        // add the recipe to the Recipes class and to the list for later adding to the server
        recipes.addSmeltRecipe(recipe);
        
        if(!recipes.customSmelt && recipe.getMinTime() >= 0.0)
            recipes.customSmelt = true;
        
        return null;
    }
    
    private String[] parseFuelRecipe() throws Exception
    {
        debug("parsing fuel recipe...");
        
        FuelRecipe recipe = new FuelRecipe(fileFlags); // create recipe and copy flags from file
        parseFlags(recipe.getFlags(), RecipeType.FUEL, null); // check for @flags
        
        // get the ingredient and burning time
        String[] split = line.split("%");
        
        if(!recipe.getFlags().isRemove()) // if it's got @remove we don't care about burn time
        {
            if(split.length < 2 || split[1] == null)
                return new String[] { "Burn time not set !", "It must be set after the ingredient like: ingredient % burntime" };
            
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
                return new String[] { "Invalid burn time float number!" };
            }
            
            if(minTime <= 0)
                return new String[] { "Fuels can't burn for negative or 0 seconds!" };
            
            if(maxTime > -1 && minTime >= maxTime)
                return new String[] { "Fuel has minimum time less or equal to maximum time!", "Use a single number if you want a fixed value" };
            
            recipe.setMinTime(minTime);
            recipe.setMaxTime(maxTime);
        }
        
        // set ingredient
        ItemStack ingredient = convertStringToItemStack(split[0], -1, true, false, false, true);
        
        if(ingredient == null || ingredient.getTypeId() == 0)
            return new String[] { "Invalid item: '" + ingredient + "'" };
        
        recipe.setIngredient(ingredient);
        
        // check if it already exists
        if(recipes.getFuelRecipe(ingredient) != null)
            return new String[] { "Fuel " + ingredient + " is already defined!", "You should remove this duplicate recipe." };
        
        // add recipe !
        recipes.addFuelRecipe(recipe);
        
        debug("done with fuel !");
        
        return null;
    }
    
    private String[] parseRemoveResult() throws Exception
    {
        ItemStack item = convertStringToItemStack(line, 1, true, true, true, true);
        
        if(item == null)
            return new String[] { "Invalid item!" };
        
        return null;
    }
    
    private String[] parseResults(RmRecipe recipe, List<ItemResult> results, boolean allowAir, boolean oneResult) throws Exception
    {
        int totalpercentage = 0;
        ItemResult noPercentItem = null;
        ItemResult result;
        
        debug("searching for results...");
        
        if(line.charAt(0) != '=')
            nextLine();
        
        while(line != null && line.charAt(0) == '=')
        {
            debug("parsing result...");
            
            result = convertStringToItemResult(line, 0, true, true, true, true);
            
            if(result == null || (!allowAir && result.getTypeId() == 0))
                return new String[] { "Invalid result !", "Result might be missing or just be incorectly typed, see previous errors if any." };
            
            if((totalpercentage += result.getChance()) > 100)
                return new String[] { "Total result items' chance exceeds 100% !", "Not defining percentage for one item will make its chance fit with the rest!" };
            
            if(result.getChance() == -1)
            {
                if(noPercentItem != null)
                    return new String[] { "Can't have more than 1 item without percentage to fill the rest!" };
                
                noPercentItem = result;
            }
            else
                results.add(result);
            
            parseFlags(result.getFlags(), null, result);
        }
        
        if(noPercentItem != null)
            results.add(new ItemResult(noPercentItem, (100 - totalpercentage)));
        
        else if(results.isEmpty())
            return new String[] { "Found = but no result !" };
        
        else if(!oneResult && totalpercentage < 100)
            results.add(new ItemResult(Material.AIR, 0, 0, (100 - totalpercentage)));
        
        debug("done with results...");
        
        return null;
    }
    
    private ItemResult convertStringToItemResult(String string, int defaultData, boolean allowData, boolean allowAmount, boolean allowEnchantments, boolean printErrors)
    {
        String[] split = string.substring(1).trim().split("%");
        ItemResult result = new ItemResult();
        
        if(split.length >= 2)
        {
            string = split[0].trim();
            
            try
            {
                result.setChance(Math.min(Math.max(Integer.valueOf(string), 0), 100));
            }
            catch(Exception e)
            {
                recipeError("Invalid percentage number: " + string, null);
            }
            
            string = split[1];
        }
        else
            string = split[0];
        
        ItemStack item = convertStringToItemStack(string, defaultData, allowData, allowAmount, allowEnchantments, printErrors);
        
        if(item == null)
            return null;
        
        result.setItemStack(item);
        
        return result;
    }
    
    private ItemStack convertStringToItemStack(String string, int defaultData, boolean allowData, boolean allowAmount, boolean allowEnchantments, boolean printErrors)
    {
        string = string.trim();
        
        if(string.length() == 0)
            return null;
        
        String[] itemString = string.split("\\|");
        String[] stringArray = itemString[0].trim().split(":");
        
        if(stringArray.length <= 0 || stringArray[0].isEmpty())
            return new ItemStack(0);
        
        stringArray[0] = stringArray[0].trim();
        
        /*
        String alias = RecipeManager.getPlugin().getAliases().get(stringArray[0]);
        
        if(alias != null)
        {
                if(stringArray.length > 2 && printErrors)
                        recipeError("'" + stringArray[0] + "' is an alias with data and amount.", "You can only set amount e.g.: alias:amount.");
                
                return stringToItemStack(string.replace(stringArray[0], alias), defaultData, allowData, allowAmount, allowEnchantments, printErrors);
        }
        */
        
        Material mat = Material.matchMaterial(stringArray[0]);
        
        if(mat == null)
        {
            if(printErrors)
                recipeError("Item '" + stringArray[0] + "' does not exist!", "Name could be different, look in readme.txt for links");
            
            return null;
        }
        
        int type = mat.getId();
        
        if(type <= 0)
            return new ItemStack(0);
        
        int data = defaultData;
        
        if(stringArray.length > 1)
        {
            if(allowData)
            {
                try
                {
                    stringArray[1] = stringArray[1].trim();
                    
                    if(stringArray[1].charAt(0) != '*')
                        data = Math.max(Integer.valueOf(stringArray[1]), data);
                }
                catch(Exception e)
                {
                    if(printErrors)
                        recipeError("Item '" + mat + " has data value that is not a number: '" + stringArray[1] + "', defaulting to " + defaultData, null);
                }
            }
            else if(printErrors)
                recipeError("Item '" + mat + "' can't have data value defined in this recipe's slot, data value ignored.", null);
        }
        
        int amount = 1;
        
        if(stringArray.length > 2)
        {
            if(allowAmount)
            {
                try
                {
                    amount = Math.max(Integer.valueOf(stringArray[2].trim()), 1);
                }
                catch(Exception e)
                {
                    if(printErrors)
                        recipeError("Item '" + mat + "' has amount value that is not a number: " + stringArray[2] + ", defaulting to 1", null);
                }
            }
            else if(printErrors)
                recipeError("Item '" + mat + "' can't have amount defined in this recipe's slot, amount ignored.", null);
        }
        
        ItemStack item = new ItemStack(type, amount, (short)data);
        
        if(itemString.length > 1)
        {
            if(allowEnchantments)
            {
                if(item.getAmount() > 1)
                {
                    if(printErrors)
                        recipeError("Item '" + mat + "' has enchantments and more than 1 amount, it can't have both, amount set to 1.", null);
                    
                    item.setAmount(1);
                }
                
                String[] enchants = itemString[1].split(",");
                String[] enchData;
                Enchantment ench;
                int level;
                
                for(String enchant : enchants)
                {
                    enchant = enchant.trim();
                    enchData = enchant.split(":");
                    
                    if(enchData.length != 2)
                    {
                        if(printErrors)
                            recipeError("Enchantments have to be 'ENCHANTMENT:LEVEL' format.", "Look in readme.txt for enchantment list link.");
                        
                        continue;
                    }
                    
                    ench = Enchantment.getByName(enchData[0]);
                    
                    if(ench == null)
                    {
                        try
                        {
                            ench = Enchantment.getById(Integer.valueOf(enchData[0]));
                        }
                        catch(Exception e)
                        {
                            ench = null;
                        }
                        
                        if(ench == null)
                        {
                            if(printErrors)
                                recipeError("Enchantment '" + enchData[0] + "' does not exist!", "Name or ID could be different, look in readme.txt for enchantments list links.");
                            
                            continue;
                        }
                    }
                    
                    if(enchData[1].equals("MAX"))
                        level = ench.getMaxLevel();
                    
                    else
                    {
                        try
                        {
                            level = Integer.valueOf(enchData[1]);
                        }
                        catch(Exception e)
                        {
                            if(printErrors)
                                recipeError("Invalid enchantment level: '" + enchData[1] + "' must be a valid number, positive, zero or negative.", null);
                            
                            continue;
                        }
                    }
                    
                    item.addUnsafeEnchantment(ench, level);
                }
            }
            else if(printErrors)
                recipeError("Item '" + mat + "' can't use enchantments in this recipe slot!", null);
        }
        
        return item;
    }
    
    private String[] recipeCheckExists(RmRecipe recipe) // TODO
    {
        boolean override = recipe.getFlags().isOverride();
        boolean remove = recipe.getFlags().isRemove();
        RecipeInfo info = recipes.recipeIndex.get(recipe);
        
        System.out.print("recipeCheckExists(" + recipe + ") :: " + info + " | " + override + " | " + remove);
        
        if(info == null)
        {
            if(remove || override)
                return new String[] { "Recipe was not found, can't " + (override ? "override" : "remove") + " it! Added as new recipe.", "Use rmextract command to see the exact ingredients needed" };
        }
        else
        {
            if(info.getOwner() == RecipeOwner.RECIPEMANAGER)
                return new String[] { "Recipe already defined in one of your recipe files.", (override || remove ? "You can't " + (override ? "override" : "remove") + " recipes that are already handled by this plugin because you can simply " + (override ? "edit" : "delete") + " them!" : null) };
        }
        
        return null;
    }
    
    private void recipeError(String error, String tip)
    {
        List<String> errors = recipeErrors.get(currentFile);
        
        if(errors == null)
            errors = new ArrayList<String>();
        
        errors.add("line " + String.format("%-5d", lineNum) + ChatColor.WHITE + error + (tip != null ? NL + ChatColor.DARK_GREEN + "          TIP: " + ChatColor.GRAY + tip : ""));
        
        recipeErrors.put(currentFile, errors);
    }
}