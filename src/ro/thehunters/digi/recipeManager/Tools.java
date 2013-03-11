package ro.thehunters.digi.recipeManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

/**
 * Collection of conversion and useful methods
 */
public class Tools
{
    public static String parseAliasName(String name)
    {
        return name.replaceAll("[\\s\\W]+", "").trim().toLowerCase();
    }
    
    public static String parseAliasPrint(String name)
    {
        return WordUtils.capitalize(name.toLowerCase().replace('_', ' ').trim());
    }
    
    public static String printItemStack(ItemStack item)
    {
        if(item == null || item.getTypeId() == 0)
            return "(nothing)";
        
        String name = null;
        String itemData = null;
        
        ItemMeta meta = item.getItemMeta();
        
        if(meta.hasDisplayName())
        {
            name = ChatColor.ITALIC + meta.getDisplayName();
        }
        else
        {
            name = RecipeManager.getSettings().printName.get(item.getType());
            
            if(name == null)
                name = parseAliasPrint(item.getType().toString());
        }
        
        Map<Short, String> dataMap = RecipeManager.getSettings().printData.get(item.getType());
        
        if(dataMap != null)
        {
            itemData = dataMap.get(item.getDurability());
            
            if(itemData != null)
                itemData = itemData + " " + name;
        }
        
        if(itemData == null)
            itemData = name + (item.getDurability() > 0 ? ":" + item.getDurability() : "");
        
        String amount = (item.getAmount() > 1 ? item.getAmount() + "x " : "");
        ChatColor color = (item.getEnchantments().size() > 0 ? ChatColor.AQUA : ChatColor.WHITE);
        
        return amount + color + itemData;
    }
    
    public static String replaceVariables(String msg, String... variables)
    {
        if(variables != null && variables.length > 0)
        {
            if(variables.length % 2 > 0)
                throw new IllegalArgumentException("Variables argument must have pairs of 2 arguments!");
            
            for(int i = 0; i < variables.length; i += 2) // loop 2 by 2
            {
                msg = msg.replace(variables[i], variables[i + 1]);
            }
        }
        
        return msg;
    }
    
    public static ItemResult convertStringToItemResult(String string, int defaultData, boolean allowData, boolean allowAmount, boolean allowEnchantments)
    {
        String[] split = string.substring(1).trim().split("%");
        ItemResult result = new ItemResult();
        result.setChance(-1);
        
        if(split.length >= 2)
        {
            string = split[0].trim();
            
            if(!string.equals("*") && !string.equalsIgnoreCase("calc"))
            {
                try
                {
                    result.setChance(Math.min(Math.max(Float.valueOf(string), 0), 100));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.warning("Invalid percentage number: " + string);
                }
            }
            
            string = split[1];
        }
        else
        {
            string = split[0];
        }
        
        ItemStack item = convertStringToItemStack(string, defaultData, allowData, allowAmount, allowEnchantments);
        
        if(item == null)
            return null;
        
        result.setItemStack(item);
        
        return result;
    }
    
    public static ItemStack convertStringToItemStack(String value, int defaultData, boolean allowData, boolean allowAmount, boolean allowEnchantments)
    {
        value = value.trim();
        
        if(value.length() == 0)
            return null;
        
        String[] enchantSplit = value.split("\\|");
        String[] split = enchantSplit[0].trim().split(":");
        
        if(split.length <= 0 || split[0].isEmpty())
            return new ItemStack(0);
        
        value = split[0].trim();
        
        /*
        String alias = RecipeManager.getPlugin().getAliases().get(stringArray[0]);
        
        if(alias != null)
        {
            if(stringArray.length > 2 && printErrors)
                RecipeErrorReporter.error("'" + stringArray[0] + "' is an alias with data and amount.", "You can only set amount e.g.: alias:amount.");
            
            return stringToItemStack(string.replace(stringArray[0], alias), defaultData, allowData, allowAmount, allowEnchantments, printErrors);
        }
        */
        
        Material material = RecipeManager.getSettings().nameAliases.get(Tools.parseAliasName(value));
        
        if(material == null)
            material = Material.matchMaterial(value);
        
        if(material == null)
        {
            RecipeErrorReporter.error("Item '" + value + "' does not exist!", "Name could be different, look in " + Files.FILE_INFO_NAMES + " or aliases.yml for material names.");
            return null;
        }
        
        int type = material.getId();
        
        if(type <= 0)
            return new ItemStack(0);
        
        int data = defaultData;
        
        if(split.length > 1)
        {
            if(allowData)
            {
                value = split[1].trim();
                
                if(value.charAt(0) == '*')
                {
                    data = -1;
                }
                else
                {
                    Map<String, Short> dataMap = RecipeManager.getSettings().dataAliases.get(material);
                    Short dataValue = (dataMap != null ? dataMap.get(Tools.parseAliasName(value)) : null);
                    
                    if(dataValue != null)
                    {
                        data = dataValue.shortValue();
                    }
                    else
                    {
                        try
                        {
                            data = Integer.valueOf(value);
                        }
                        catch(Exception e)
                        {
                            RecipeErrorReporter.warning("Item '" + material + " has data value that is not a number: '" + value + "', defaulting to " + defaultData);
                        }
                    }
                }
            }
            else
            {
                RecipeErrorReporter.warning("Item '" + material + "' can't have data value defined in this recipe's slot, data value ignored.");
            }
        }
        
        int amount = 1;
        
        if(split.length > 2)
        {
            if(allowAmount)
            {
                value = split[2].trim();
                
                try
                {
                    amount = Integer.valueOf(value);
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Item '" + material + "' has amount value that is not a number: " + value + ", defaulting to 1");
                }
            }
            else
            {
                RecipeErrorReporter.warning("Item '" + material + "' can't have amount defined in this recipe's slot, amount ignored.");
            }
        }
        
        ItemStack item = new ItemStack(type, amount, (short)data);
        
        if(enchantSplit.length > 1)
        {
            if(allowEnchantments)
            {
                /*
                if(item.getAmount() > 1)
                {
                    RecipeErrorReporter.warning("Item '" + material + "' has enchantments and more than 1 amount, it can't have both, amount set to 1.");
                    
                    item.setAmount(1);
                }
                */
                
                String[] enchants = enchantSplit[1].split(",");
                String[] enchData;
                Enchantment ench;
                int level;
                
                for(String enchant : enchants)
                {
                    enchant = enchant.trim();
                    enchData = enchant.split(":");
                    
                    if(enchData.length != 2)
                    {
                        RecipeErrorReporter.warning("Enchantments have to be 'ENCHANTMENT:LEVEL' format.", "Look in " + Files.FILE_INFO_NAMES + " for enchantments list.");
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
                            RecipeErrorReporter.warning("Enchantment '" + enchData[0] + "' does not exist!", "Name or ID could be different, look in " + Files.FILE_INFO_NAMES + " for enchantments list.");
                            continue;
                        }
                    }
                    
                    if(enchData[1].equals("MAX"))
                    {
                        level = ench.getMaxLevel();
                    }
                    else
                    {
                        try
                        {
                            level = Integer.valueOf(enchData[1]);
                        }
                        catch(Exception e)
                        {
                            RecipeErrorReporter.warning("Invalid enchantment level: '" + enchData[1] + "' must be a valid number, positive, zero or negative.");
                            continue;
                        }
                    }
                    
                    item.addUnsafeEnchantment(ench, level);
                }
            }
            else
            {
                RecipeErrorReporter.warning("Item '" + material + "' can't use enchantments in this recipe slot!");
            }
        }
        
        return item;
    }
    
    public static Potion parsePotion(String value, FlagType type)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        if(split.length == 0)
        {
            RecipeErrorReporter.error("Flag @" + type + " doesn't have any arguments!", "It must have at least 'type' argument, read '" + Files.FILE_INFO_NAMES + "' for potion types list.");
            return null;
        }
        
        Potion potion = new Potion(null);
        boolean splash = false;
        boolean extended = false;
        int level = 1;
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("splash"))
            {
                splash = true;
            }
            else if(s.equals("extended"))
            {
                extended = true;
            }
            else if(s.startsWith("type"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'type' argument with no type!", "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                    return null;
                }
                
                value = split[1].trim();
                
                try
                {
                    potion.setType(PotionType.valueOf(value.toUpperCase()));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'type' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                    return null;
                }
            }
            else if(s.startsWith("level"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'level' argument with no level!");
                    continue;
                }
                
                value = split[1].trim();
                
                if(value.equals("max"))
                {
                    level = 9999;
                }
                else
                {
                    try
                    {
                        level = Integer.valueOf(value);
                    }
                    catch(Exception e)
                    {
                        RecipeErrorReporter.error("Flag @" + type + " has invalid 'level' number: " + value);
                    }
                }
            }
            else
            {
                RecipeErrorReporter.error("Flag @" + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        if(potion.getType() == null)
        {
            RecipeErrorReporter.error("Flag @" + type + " is missing 'type' argument !", "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
            return null;
        }
        
        potion.setLevel(Math.min(Math.max(level, 1), potion.getType().getMaxLevel()));
        
        if(!potion.getType().isInstant())
            potion.setHasExtendedDuration(extended);
        
        potion.setSplash(splash);
        
        System.out.print("[debug] potion = " + potion.getLevel() + " | " + potion.getType() + " | " + potion.getEffects() + " | " + potion.isSplash() + " | " + potion.hasExtendedDuration());
        
        return potion;
    }
    
    public static PotionEffect parsePotionEffect(String value, FlagType type)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        if(split.length == 0)
        {
            RecipeErrorReporter.error("Flag @" + type + " doesn't have any arguments!", "It must have at least 'type' argument, read '" + Files.FILE_INFO_NAMES + "' for potion effect types list.");
            return null;
        }
        
        PotionEffectType effectType = null;
        boolean ambient = false;
        float duration = 1;
        int amplify = 0;
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("ambient"))
            {
                ambient = true;
            }
            else if(s.startsWith("type"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'type' argument with no type!", "Read '" + Files.FILE_INFO_NAMES + "' for potion effect types.");
                    return null;
                }
                
                value = split[1].trim();
                
                try
                {
                    effectType = PotionEffectType.getByName(value.toUpperCase());
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'type' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for potion effect types.");
                    return null;
                }
            }
            else if(s.startsWith("duration"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'duration' argument with no number!");
                    continue;
                }
                
                value = split[1].trim();
                
                try
                {
                    duration = Float.valueOf(value);
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'duration' number: " + value);
                }
            }
            else if(s.startsWith("amplify"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'amplify' argument with no number!");
                    continue;
                }
                
                value = split[1].trim();
                
                try
                {
                    amplify = Integer.parseInt(value);
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'amplify' number: " + value);
                }
            }
            else
            {
                RecipeErrorReporter.warning("Flag @" + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        if(effectType == null)
        {
            RecipeErrorReporter.error("Flag @" + type + " is missing 'type' argument !", "Read '" + Files.FILE_INFO_NAMES + "' for potion effect types.");
            return null;
        }
        
        if(duration != 1 && (effectType == PotionEffectType.HEAL || effectType == PotionEffectType.HARM))
            RecipeErrorReporter.warning("Flag @" + type + " can't have duration on HEAL or HARM because they're instant!");
        
        return new PotionEffect(effectType, Math.round(duration * 20), amplify, ambient);
    }
    
    public static FireworkEffect parseFireworkEffect(String value, FlagType type)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        if(split.length == 0)
        {
            RecipeErrorReporter.error("Flag @" + type + " doesn't have any arguments!", "It must have at least one 'color' argument, read '" + Files.FILE_INFO_FLAGS + "' for syntax.");
            return null;
        }
        
        Builder build = FireworkEffect.builder();
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("trail"))
            {
                build.withTrail();
            }
            else if(s.equals("flicker"))
            {
                build.withFlicker();
            }
            else if(s.startsWith("color"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'color' argument with no colors!", "Add colors separated by , in RGB format (3 numbers ranged 0-255)");
                    return null;
                }
                
                split = split[1].split(",");
                List<Color> colors = new ArrayList<Color>();
                Color color;
                
                for(String c : split)
                {
                    color = Tools.parseColor(c.trim());
                    
                    if(color == null)
                        RecipeErrorReporter.warning("Flag @" + type + " has an invalid color!");
                    else
                        colors.add(color);
                }
                
                if(colors.isEmpty())
                {
                    RecipeErrorReporter.error("Flag @" + type + " doesn't have any valid colors, they are required!");
                    return null;
                }
                
                build.withColor(colors);
            }
            else if(s.startsWith("fadecolor"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'fadecolor' argument with no colors!", "Add colors separated by , in RGB format (3 numbers ranged 0-255)");
                    return null;
                }
                
                split = split[1].split(",");
                List<Color> colors = new ArrayList<Color>();
                Color color;
                
                for(String c : split)
                {
                    color = Tools.parseColor(c.trim());
                    
                    if(color == null)
                        RecipeErrorReporter.warning("Flag @" + type + " has an invalid fade color! Moving on...");
                    else
                        colors.add(color);
                }
                
                if(colors.isEmpty())
                    RecipeErrorReporter.error("Flag @" + type + " doesn't have any valid fade colors! Moving on...");
                else
                    build.withFade(colors);
            }
            else if(s.startsWith("type"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'type' argument with no value!", "Read " + Files.FILE_INFO_NAMES + " for list of firework effect types.");
                    return null;
                }
                
                value = split[1].trim();
                
                try
                {
                    build.with(FireworkEffect.Type.valueOf(value.toUpperCase()));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'type' setting value: " + value, "Read " + Files.FILE_INFO_NAMES + " for list of firework effect types.");
                    return null;
                }
            }
            else
            {
                RecipeErrorReporter.warning("Flag @" + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        return build.build();
    }
    
    public static String convertListToString(List<?> list)
    {
        return convertListToString(list, ", ", "");
    }
    
    public static String convertListToString(List<?> list, String separator, String prefix)
    {
        if(list.isEmpty())
            return "";
        
        int size = list.size();
        
        if(size == 1)
            return list.get(0).toString();
        
        StringBuilder str = new StringBuilder(prefix).append(list.get(0).toString());
        
        for(int i = 1; i < size; i++)
        {
            str.append(separator).append(prefix).append(list.get(i).toString());
        }
        
        return str.toString();
    }
    
    public static ItemStack generateItemStackWithMeta(Material type, int data, int amount, String name, String... lore)
    {
        return generateItemStackWithMeta(type, data, amount, name, (lore != null && lore.length > 0 ? Arrays.asList(lore) : null));
    }
    
    public static ItemStack generateItemStackWithMeta(Material type, int data, int amount, String name, List<String> lore)
    {
        ItemStack item = new ItemStack(type, amount, (short)data);
        ItemMeta meta = item.getItemMeta();
        
        if(lore != null)
            meta.setLore(lore);
        
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static Color parseColor(String rgbString)
    {
        String[] split = rgbString.split(" ");
        
        if(split.length == 3)
        {
            try
            {
                int r = Integer.valueOf(split[0].trim());
                int g = Integer.valueOf(split[1].trim());
                int b = Integer.valueOf(split[2].trim());
                
                return Color.fromRGB(r, g, b);
            }
            catch(Exception e)
            {
            }
        }
        
        return null;
    }
    
    public static String parseColors(String message, boolean removeColors)
    {
        for(ChatColor color : ChatColor.values())
        {
            message = message.replaceAll("(?i)<" + color.name() + ">", (removeColors ? "" : "" + color));
        }
        
        return removeColors ? message : ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * For use in furnace smelting and fuel recipes hashmap
     */
    public static String convertItemToStringID(ItemStack item)
    {
        return item.getTypeId() + (item.getDurability() == -1 ? "" : ":" + item.getDurability());
    }
    
    /**
     * For use in shaped/shapeless recipe's result
     */
    public static ItemStack generateRecipeIdResult(ItemStack result, int id)
    {
        result = result.clone();
        ItemMeta meta = result.getItemMeta();
        List<String> lore = meta.getLore();
        
        if(lore == null)
            lore = new ArrayList<String>();
        
        lore.add(Recipes.RECIPE_ID_STRING + id);
        meta.setLore(lore);
        result.setItemMeta(meta);
        
        return result;
    }
    
    public static int getRecipeIdFromResult(ItemStack result)
    {
        List<String> desc = result.getItemMeta().getLore();
        
        if(desc == null)
            return -1;
        
        String id = desc.get(desc.size() - 1);
        int index = -1;
        
        if(id.startsWith(Recipes.RECIPE_ID_STRING))
        {
            try
            {
                index = Integer.valueOf(id.substring(Recipes.RECIPE_ID_STRING.length()));
            }
            catch(Exception e)
            {
            }
        }
        
        return index;
    }
    
    /* TODO not really needed, remove ?
    public static boolean compareShapedRecipeToCraftRecipe(ShapedRecipe bukkitRecipe, CraftRecipe recipe)
    {
        ItemStack[] matrix = recipe.getIngredients().clone();
        Tools.trimItemMatrix(matrix);
        ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
        int height = recipe.getHeight();
        int width = recipe.getWidth();
        String[] sh = bukkitRecipe.getShape();
        
        if(sh.length == height && sh[0].length() == width)
            return false;
        
        return Tools.compareShapedRecipeToMatrix(bukkitRecipe, matrix, matrixMirror);
    }
    */
    
    public static boolean compareShapedRecipeToMatrix(ShapedRecipe recipe, ItemStack[] matrix, ItemStack[] matrixMirror)
    {
        ItemStack[] ingredients = Tools.convertShapedRecipeToItemMatrix(recipe);
        
        boolean result = compareItemMatrix(ingredients, matrix);
        
        if(!result)
            result = compareItemMatrix(ingredients, matrixMirror);
        
        return result;
    }
    
    public static boolean compareItemMatrix(ItemStack[] ingredients, ItemStack[] matrix)
    {
        for(int i = 0; i < 9; i++)
        {
            if(matrix[i] == null && ingredients[i] == null)
                continue;
            
            if(matrix[i] == null || ingredients[i] == null || ingredients[i].getTypeId() != matrix[i].getTypeId() || (ingredients[i].getDurability() != -1 && ingredients[i].getDurability() != matrix[i].getDurability()))
                return false;
        }
        
        return true;
    }
    
    public static ItemStack[] convertShapedRecipeToItemMatrix(ShapedRecipe bukkitRecipe)
    {
        Map<Character, ItemStack> items = bukkitRecipe.getIngredientMap();
        ItemStack[] matrix = new ItemStack[9];
        String[] shape = bukkitRecipe.getShape();
        int slot = 0;
        
        for(int r = 0; r < shape.length; r++)
        {
            for(char col : shape[r].toCharArray())
            {
                matrix[slot] = items.get(col);
                slot++;
            }
            
            slot = ((r + 1) * 3);
        }
        
        trimItemMatrix(matrix);
        
        return matrix;
    }
    
    public static ItemStack[] mirrorItemMatrix(ItemStack[] matrix)
    {
        ItemStack[] m = new ItemStack[9];
        
        for(int r = 0; r < 3; r++)
        {
            m[(r * 3)] = matrix[(r * 3) + 2];
            m[(r * 3) + 1] = matrix[(r * 3) + 1];
            m[(r * 3) + 2] = matrix[(r * 3)];
        }
        
        trimItemMatrix(m);
        
        return m;
    }
    
    public static void trimItemMatrix(ItemStack[] matrix)
    {
        while(matrix[0] == null && matrix[1] == null && matrix[2] == null)
        {
            matrix[0] = matrix[3];
            matrix[1] = matrix[4];
            matrix[2] = matrix[5];
            
            matrix[3] = matrix[6];
            matrix[4] = matrix[7];
            matrix[5] = matrix[8];
            
            matrix[6] = null;
            matrix[7] = null;
            matrix[8] = null;
        }
        
        while(matrix[0] == null && matrix[3] == null && matrix[6] == null)
        {
            matrix[0] = matrix[1];
            matrix[3] = matrix[4];
            matrix[6] = matrix[7];
            
            matrix[1] = matrix[2];
            matrix[4] = matrix[5];
            matrix[7] = matrix[8];
            
            matrix[2] = null;
            matrix[5] = null;
            matrix[8] = null;
        }
    }
    
    public static boolean compareIngredientList(List<ItemStack> sortedIngr, List<ItemStack> ingredients)
    {
        int size = ingredients.size();
        
        if(size != sortedIngr.size())
            return false;
        
        sortIngredientList(ingredients);
        
        for(int i = 0; i < size; i++)
        {
            if(!sortedIngr.get(i).isSimilar(ingredients.get(i)))
                return false;
        }
        
        return true;
    }
    
    public static void sortIngredientList(List<ItemStack> ingredients)
    {
        Collections.sort(ingredients, new Comparator<ItemStack>()
        {
            int id1;
            int id2;
            
            @Override
            public int compare(ItemStack item1, ItemStack item2)
            {
                id1 = item1.getTypeId();
                id2 = item2.getTypeId();
                
                return (id1 == id2 ? (item1.getDurability() > item2.getDurability() ? -1 : 1) : (id1 > id2 ? -1 : 1));
            }
        });
    }
    
    public static String convertShapedRecipeToString(ShapedRecipe recipe)
    {
        StringBuilder str = new StringBuilder("s_");
        
        for(Entry<Character, ItemStack> entry : recipe.getIngredientMap().entrySet())
        {
            if(entry.getKey() != null && entry.getValue() != null)
                str.append(entry.getKey()).append("=").append(entry.getValue().getTypeId()).append(":").append(entry.getValue().getDurability()).append(";");
        }
        
        for(String row : recipe.getShape())
        {
            str.append(row).append(";");
        }
        
        return str.toString();
    }
    
    public static String convertShapelessRecipeToString(ShapelessRecipe recipe)
    {
        StringBuilder str = new StringBuilder("l_");
        
        for(ItemStack ingredient : recipe.getIngredientList())
        {
            if(ingredient == null)
                continue;
            
            str.append(ingredient.getTypeId()).append(":").append(ingredient.getDurability()).append(";");
        }
        
        return str.toString();
    }
    
    public static String convertFurnaceRecipeToString(FurnaceRecipe recipe)
    {
        return "f_" + recipe.getInput().getTypeId() + ":" + recipe.getInput().getDurability();
    }
    
    public static String convertLocationToString(Location location)
    {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }
    
    public static boolean saveTextToFile(String text, String filePath)
    {
        try
        {
            BufferedWriter stream = new BufferedWriter(new FileWriter(filePath, false));
            stream.write(text);
            stream.close();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean saveObjectToFile(Object object, String filePath)
    {
        try
        {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filePath));
            stream.writeObject(object);
            stream.flush();
            stream.close();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static Object loadObjectFromFile(String filePath)
    {
        File file = new File(filePath);
        
        if(file.exists())
        {
            try
            {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                Object result = stream.readObject();
                stream.close();
                return result;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
