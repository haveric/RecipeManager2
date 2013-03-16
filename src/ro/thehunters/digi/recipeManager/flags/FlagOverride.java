package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagOverride extends Flag
{
    public FlagOverride()
    {
        type = FlagType.OVERRIDE;
    }
    
    @Override
    public Flag clone()
    {
        return new FlagOverride();
    }
    
    @Override
    protected boolean onValidate()
    {
        if(getFlagsContainer().hasFlag(FlagType.REMOVE))
        {
            return RecipeErrorReporter.error("Flag " + getType() + " can't work with @remove flag!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
}