package digi.recipeManager.recipes;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import digi.recipeManager.RecipeManager;
import digi.recipeManager.recipes.flags.Flags;
import digi.recipeManager.recipes.flags.RecipeFlags;

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
    
    /**
     * Adds the recipe! <br>
     * Alias of: <br>
     * <code>RecipeManager.getRecipes().addRecipe(this);</code> <br>
     * Note: you must use RecipeManager.getRecipes().registerRecipes() once done!
     */
    public void addRecipe()
    {
        RecipeManager.getRecipes().addRecipe(this);
    }
    
    public boolean isValid()
    {
        return false; // empty recipe, invalid!
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
        
        if(obj == null)
            return false;
        
        // TODO
        return obj.hashCode() == hash;
        
        /*
        if(obj instanceof CraftRecipe)
        {
            if(hash == ((CraftRecipe)obj).hashCode())
                return true;
        }
        else if(obj instanceof CombineRecipe)
        {
            if(hash == ((CombineRecipe)obj).hashCode())
                return true;
        }
        else if(obj instanceof SmeltRecipe)
        {
            if(hash == ((SmeltRecipe)obj).hashCode())
                return true;
        }
        
        return false;
        */
    }
    
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
