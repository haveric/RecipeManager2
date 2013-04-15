package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;

public class FlagDescription extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
    private String description;
    
    public FlagDescription()
    {
        type = FlagType.DESCRIPTION;
    }
    
    @Override
    public FlagDescription clone()
    {
        FlagDescription clone = new FlagDescription();
        
        clone.description = description;
        
        return clone;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @Override
    protected boolean onValidate()
    {
        BaseRecipe recipe = getRecipe();
        
        if(recipe instanceof FuelRecipe)
        {
            RecipeErrorReporter.warning("Flag " + getType() + " does nothing on fuel recipes since fuels are listed in a single page in recipe books.");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        setDescription(value);
        return true;
    }
}
