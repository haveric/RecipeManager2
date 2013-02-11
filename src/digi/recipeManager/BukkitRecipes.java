package digi.recipeManager;

import java.util.*;

import org.bukkit.*;
import org.bukkit.inventory.*;

import digi.recipeManager.data.*;
import digi.recipeManager.recipes.*;
import digi.recipeManager.recipes.RecipeInfo.RecipeOwner;

/**
 * Control for bukkit recipes to avoid confusion with RecipeManager's recipes
 */
public class BukkitRecipes
{
    protected static Map<RmRecipe, RecipeInfo> recipeIndex       = new HashMap<RmRecipe, RecipeInfo>();
    
    // Constants
    protected static final ItemStack           RECIPE_LEATHERDYE = new ItemStack(Material.LEATHER_HELMET, 0, (short)0);
    protected static final ItemStack           RECIPE_MAPCLONE   = new ItemStack(Material.MAP, 0, (short)-1);
    protected static final ItemStack           RECIPE_MAPEXTEND  = new ItemStack(Material.EMPTY_MAP, 0, (short)0);
    protected static final ItemStack           RECIPE_FIREWORKS  = new ItemStack(Material.FIREWORK, 0, (short)0);
    
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
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Removed recipe which made = " + sr.getResult());
                        
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
                
                if(recipe.getIngredient().isSimilar(fr.getInput()))
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
     * Clears server's recipes then adds the custom ones from the specified list then re-adds the initial recipes found when plugin first loaded.
     * 
     * @param recipes
     */
    /*
    public static void setServerRecipes(List<Recipe> recipes)
    {
        Bukkit.clearRecipes(); // discard of ALL recipes
        
        recipes.addAll(initialRecipes); // add all initial recipes
        
        for(Recipe recipe : recipes) // re-add recipes to server!
        {
            Bukkit.addRecipe(recipe);
        }
    }
    
    public static void restoreInitialRecipes()
    {
        Bukkit.clearRecipes();
        
        for(Recipe r : initialRecipes)
        {
            Bukkit.addRecipe(r);
        }
    }
    */
    
    public static void removeCustomRecipes()
    {
        Iterator<org.bukkit.inventory.Recipe> iterator = Bukkit.recipeIterator();
        
        while(iterator.hasNext())
        {
            if(RecipeManager.getRecipes().isCustomRecipe(iterator.next()))
                iterator.remove();
        }
    }
}
