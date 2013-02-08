package digi.recipeManager.data;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import digi.recipeManager.Messages;
import digi.recipeManager.RecipeManager;

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
    
    private Flags flags;
    protected int hash;
    
    public RmRecipe()
    {
    }
    
    public RmRecipe(RmRecipe recipe)
    {
        flags = recipe.getFlags().clone();
    }
    
    public RmRecipe(Flags flags)
    {
        this.flags = flags.clone();
    }
    
    public boolean isUsableBy(Player player)
    {
        return true;
    }
    
    public Flags getFlags()
    {
        if(flags == null) // TODO
        {
            Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Flags were null!");
            
            flags = new Flags();
        }
        
        return flags;
    }
    
    public void setFlags(Flags flags)
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
}
