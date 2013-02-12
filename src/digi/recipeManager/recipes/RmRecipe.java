package digi.recipeManager.recipes;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import digi.recipeManager.RecipeManager;
import digi.recipeManager.recipes.flags.Flags;
import digi.recipeManager.recipes.flags.RecipeFlags;

public class RmRecipe
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
    
    public RmRecipe()
    {
    }
    
    public RmRecipe(RmRecipe recipe)
    {
        this.flags = new RecipeFlags(recipe.getFlags());
    }
    
    public RmRecipe(Flags flags)
    {
        this.flags = new RecipeFlags(flags);
    }
    
    public String[] isCraftable(Player player, Location location)
    {
        return (RecipeManager.rand.nextBoolean() ? null : new String[] { "Recipe is refusing to cooperate :P" });
        
//        return null;
    }
    
    public RecipeFlags getFlags()
    {
        if(flags == null) // TODO
        {
//            Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Flags were null!");
            
            flags = new RecipeFlags();
        }
        
        return flags;
    }
    
    public void setFlags(RecipeFlags flags)
    {
        this.flags = flags;
    }
    
    /**
     * Adds the recipe! <br>
     * Alias of: <br>
     * <code>RecipeManager.getRecipes().addRecipe(this);</code> <br>
     * Note: you must use RecipeManager.getRecipes().registerRecipes() once done!
     */
    public void registerRecipe()
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
    
    public void applyFlags(Player player, Location location)
    {
        if(flags != null)
            flags.applyFlags(player, location);
    }
}
