package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Location;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

public class FlagProximity extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.PROXIMITY;
        
        A = new String[]
        {
            "{flag} <range> | [fail message]",
        };
        
        D = new String[]
        {
            "",
            "",
        };
        
        E = new String[]
        {
            "",
        };
    }
    
    // Flag code
    
    private float min;
    private float max;
    private String failMessage;
    
    public FlagProximity()
    {
    }
    
    public FlagProximity(FlagProximity flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagProximity clone()
    {
        return new FlagProximity(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onValidate()
    {
        BaseRecipe recipe = getRecipe();
        
        if(recipe instanceof SmeltRecipe == false && recipe instanceof FuelRecipe == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " only works for smelt and fuel recipes!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        // TODO
        
        RecipeErrorReporter.warning("Flag " + getType() + " is not yet coded.");
        
        return false;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasPlayer() || !a.hasLocation())
        {
            a.addCustomReason("Needs player and location!");
            return;
        }
        
        Location l = a.location();
        
        // TODO
    }
}
