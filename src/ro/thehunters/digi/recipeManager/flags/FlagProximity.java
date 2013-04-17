package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

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
        
        A = null;
        D = null;
        E = null;
    }
    
    // Flag code
    
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
    }
}
