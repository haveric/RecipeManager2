package digi.recipeManager;

import java.util.*;

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
    protected static Map<RmRecipe, RecipeInfo> recipeIndex       = new HashMap<RmRecipe, RecipeInfo>();
    
    // Constants
    public static final ItemStack              RECIPE_LEATHERDYE = new ItemStack(Material.LEATHER_HELMET, 0, (short)0);
    public static final ItemStack              RECIPE_MAPCLONE   = new ItemStack(Material.MAP, 0, (short)-1);
    public static final ItemStack              RECIPE_MAPEXTEND  = new ItemStack(Material.EMPTY_MAP, 0, (short)0);
    public static final ItemStack              RECIPE_FIREWORKS  = new ItemStack(Material.FIREWORK, 0, (short)0);
    
    protected static void init()
    {
        clean();
        
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe r;
        
        while(iterator.hasNext())
        {
            r = iterator.next();
            
            if(r instanceof ShapedRecipe)
                recipeIndex.put(new CraftRecipe((ShapedRecipe)r), new RecipeInfo(RecipeOwner.MINECRAFT));
            else if(r instanceof ShapelessRecipe)
                recipeIndex.put(new CombineRecipe((ShapelessRecipe)r), new RecipeInfo(RecipeOwner.MINECRAFT));
            else if(r instanceof FurnaceRecipe)
                recipeIndex.put(new SmeltRecipe((FurnaceRecipe)r), new RecipeInfo(RecipeOwner.MINECRAFT));
        }
    }
    
    protected static void clean()
    {
        recipeIndex.clear();
    }
    
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
}