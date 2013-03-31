package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagRemove extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} < ??? >";
        
        D = new String[1];
        D[0] = "Flag not yet documented.";
        
        E = null;
    }
    
    // Flag code
    
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