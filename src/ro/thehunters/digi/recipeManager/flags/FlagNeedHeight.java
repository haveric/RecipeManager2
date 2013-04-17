package ro.thehunters.digi.recipeManager.flags;

public class FlagNeedHeight extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.NEEDHEIGHT;
        
        A = new String[]
        {
            "{flag} ...",
        };
        
        D = new String[]
        {
            "FLAG NOT IMPLEMENTED YET!",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    public FlagNeedHeight()
    {
    }
    
    public FlagNeedHeight(FlagNeedHeight flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagNeedHeight clone()
    {
        return new FlagNeedHeight(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return false;
    }
}
