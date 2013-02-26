package ro.thehunters.digi.recipeManager.recipes;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Recipes;
import ro.thehunters.digi.recipeManager.recipes.flags.Flags;
import ro.thehunters.digi.recipeManager.recipes.flags.RecipeFlags;


public class BaseRecipe
{
    public enum RecipeType
    {
        CRAFT("craft"),
        COMBINE("combine"),
        SMELT("smelt"),
        FUEL("fuel");
        
        private final String directive;
        
        private RecipeType(String directive)
        {
            this.directive = directive;
        }
        
        public String getDirective()
        {
            return directive;
        }
    }
    
    private RecipeFlags flags;
    protected int       hash;
    
    public BaseRecipe()
    {
    }
    
    public BaseRecipe(BaseRecipe recipe)
    {
        this.flags = new RecipeFlags(recipe.getFlags());
    }
    
    public BaseRecipe(Flags flags)
    {
        this.flags = new RecipeFlags(flags);
    }
    
    /**
     * See: {@link Recipes #getRecipeInfo(BaseRecipe)}
     * 
     * @return
     *         Recipe info or null if doesn't exist
     */
    public RecipeInfo getInfo()
    {
        return RecipeManager.getRecipes().getRecipeInfo(this);
    }
    
    public RecipeFlags getFlags()
    {
        if(flags == null)
            flags = new RecipeFlags();
        
        return flags;
    }
    
    public void setFlags(RecipeFlags flags)
    {
        this.flags = flags;
    }
    
    public boolean checkFlags(Player player, String playerName, Location location, List<String> reasons)
    {
        return (flags == null ? true : flags.checkFlags(player, playerName, location, getRecipeType(), null, reasons));
    }
    
    public boolean applyFlags(Player player, String playerName, Location location, List<String> reasons)
    {
        return (flags == null ? true : flags.applyFlags(player, playerName, location, getRecipeType(), null, reasons));
    }
    
    public RecipeType getRecipeType()
    {
        return null;
    }
    
    public boolean isValid()
    {
        return false; // empty recipe, invalid!
    }
    
    public int getIndex()
    {
        return hash;
    }
    
    @Override
    public int hashCode()
    {
        return hash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj == null || obj instanceof BaseRecipe == false)
            return false;
        
        return obj.hashCode() == hash;
    }
    
    /**
     * Register recipe with the server and RecipeManager.<br>
     * Alias for RecipeManager.getRecipes().registerRecipe(this);
     */
    public void register()
    {
        if(!isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Check ingredients and results.");
        
        RecipeManager.getRecipes().registerRecipe(this);
    }
    
    /**
     * Remove this recipe from the server and from RecipeManager.<br>
     * Alias for: RecipeManager.getRecipes().removeRecipe(this);
     * 
     * @return if recipe was succesfully removed
     */
    public boolean remove()
    {
        return RecipeManager.getRecipes().removeRecipe(this);
    }
    
    /**
     * You usually won't need this, but just in case you do, here it is.
     * 
     * @return
     *         Bukkit API version of the recipe
     */
    public Recipe toBukkitRecipe()
    {
        if(this instanceof CraftRecipe)
        {
            return ((CraftRecipe)this).toShapedRecipe();
        }
        else if(this instanceof CombineRecipe)
        {
            return ((CombineRecipe)this).toShapelessRecipe();
        }
        else if(this instanceof SmeltRecipe)
        {
            return ((SmeltRecipe)this).toFurnaceRecipe();
        }
        
        return null;
    }
}
