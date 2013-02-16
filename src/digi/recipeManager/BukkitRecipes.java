package digi.recipeManager;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import digi.recipeManager.recipes.*;
import digi.recipeManager.recipes.RecipeInfo.RecipeOwner;

/**
 * Control for bukkit recipes to avoid confusion with RecipeManager's recipes
 */
public class BukkitRecipes
{
    protected static Map<BaseRecipe, RecipeInfo> initialRecipes    = new HashMap<BaseRecipe, RecipeInfo>();
    
    // Constants
    
    /**
     * Leather dyeing's special recipe result, you can use it to identify vanilla firework recipes.
     */
    
    public static final ItemStack                RECIPE_LEATHERDYE = new ItemStack(Material.LEATHER_HELMET, 0, (short)0);
    /**
     * Map cloning's special recipe result, you can use it to identify vanilla firework recipes.
     */
    public static final ItemStack                RECIPE_MAPCLONE   = new ItemStack(Material.MAP, 0, (short)-1);
    
    /**
     * Map extending's special recipe result, you can use it to identify vanilla firework recipes.
     */
    public static final ItemStack                RECIPE_MAPEXTEND  = new ItemStack(Material.EMPTY_MAP, 0, (short)0);
    
    /**
     * Fireworks' special recipe result, you can use it to identify vanilla firework recipes.
     */
    public static final ItemStack                RECIPE_FIREWORKS  = new ItemStack(Material.FIREWORK, 0, (short)0);
    
    protected static void init()
    {
        clean();
        
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe r;
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof ShapedRecipe)
                initialRecipes.put(new CraftRecipe((ShapedRecipe)r), new RecipeInfo(RecipeOwner.MINECRAFT));
            else if(r instanceof ShapelessRecipe)
                initialRecipes.put(new CombineRecipe((ShapelessRecipe)r), new RecipeInfo(RecipeOwner.MINECRAFT));
            else if(r instanceof FurnaceRecipe)
                initialRecipes.put(new SmeltRecipe((FurnaceRecipe)r), new RecipeInfo(RecipeOwner.MINECRAFT));
        }
    }
    
    protected static void clean()
    {
        initialRecipes.clear();
    }
    
    /**
     * Removes a RecipeManager's craft recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager's recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeShapedRecipe(CraftRecipe recipe)
    {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        ShapedRecipe sr;
        Recipe r;
        String[] sh;
        
        ItemStack[] matrix = recipe.getIngredients().clone();
        Tools.trimItemMatrix(matrix);
        ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
        int height = recipe.getHeight();
        int width = recipe.getWidth();
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof ShapedRecipe)
            {
                sr = (ShapedRecipe)r;
                sh = sr.getShape();
                
                if(sh.length == height && sh[0].length() == width)
                {
                    if(Tools.compareShapedRecipeToMatrix(sr, matrix, matrixMirror))
                    {
                        iterator.remove();
                        return true;
                    }
                }
            }
        }
        
        iterator = null;
        return false;
    }
    
    /**
     * Removes a RecipeManager's combine recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager's recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeShapelessRecipe(CombineRecipe recipe)
    {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        ShapelessRecipe sr;
        Recipe r;
        
        List<ItemStack> items = recipe.getIngredients();
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof ShapelessRecipe)
            {
                sr = (ShapelessRecipe)r;
                
                if(Tools.compareIngredientList(items, sr.getIngredientList()))
                {
                    iterator.remove();
                    return true;
                }
            }
        }
        
        iterator = null;
        return false;
    }
    
    /**
     * Removes a RecipeManager's smelt recipe from the <b>server</b>
     * 
     * @param recipe
     *            RecipeManager's recipe
     * @return true if recipe was found and removed
     */
    public static boolean removeFurnaceRecipe(SmeltRecipe recipe)
    {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        FurnaceRecipe fr;
        Recipe r;
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof FurnaceRecipe)
            {
                fr = (FurnaceRecipe)r;
                
                // TODO maybe check data value ?
                if(recipe.getIngredient().getTypeId() == fr.getInput().getTypeId())
                {
                    iterator.remove();
                    return true;
                }
            }
        }
        
        iterator = null;
        return false;
    }
    
    /**
     * Remove all RecipeManager's recipes from the server.
     */
    public static void removeCustomRecipes()
    {
        if(RecipeManager.getRecipes() == null)
            return;
        
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe recipe;
        
        while(iterator.hasNext())
        {
            recipe = iterator.next();
            
            if(recipe != null && RecipeManager.getRecipes().isCustomRecipe(recipe))
            {
                iterator.remove();
            }
        }
    }
    
    /**
     * Adds all recipes that already existed when the plugin was enabled.
     */
    public static void restoreInitialRecipes()
    {
        for(Entry<BaseRecipe, RecipeInfo> entry : initialRecipes.entrySet())
        {
            // TODO maybe check if recipe is already in server ?
            Bukkit.addRecipe(entry.getKey().toBukkitRecipe());
        }
    }
}