package digi.recipeManager;

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

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Collection of conversion and useful methods
 */
public class Tools
{
    public static String convertListToString(List<?> list)
    {
        if(list.isEmpty())
            return "";
        
        int size = list.size();
        
        if(size == 1)
            return list.get(0).toString();
        
        StringBuilder str = new StringBuilder(list.get(0).toString());
        
        for(int i = 1; i < size; i++)
        {
            str.append(", ").append(list.get(i).toString());
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
        
        // TODO remove color &1  ?
        return removeColors ? message : ChatColor.translateAlternateColorCodes(RecipeManager.getSettings().COLOR_CHAR, message);
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
