package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagRemove extends Flag
{
    public FlagRemove()
    {
        type = FlagType.REMOVE;
    }
    
    @Override
    public FlagRemove clone()
    {
        return new FlagRemove();
    }
    
    @Override
    protected boolean onValidate()
    {
        if(getFlagsContainer().hasFlag(FlagType.OVERRIDE))
        {
            return RecipeErrorReporter.error("Flag " + getType() + " can't work with @override flag!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
}