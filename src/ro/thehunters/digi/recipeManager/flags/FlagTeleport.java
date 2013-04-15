package ro.thehunters.digi.recipeManager.flags;

public class FlagTeleport extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag}",
        };
        
        D = new String[]
        {
            "Flag not yet documented.",
        };
        
        E = null;
    }
    
    // Flag code
    
    public FlagTeleport()
    {
        type = FlagType.TELEPORT;
    }
    
    public FlagTeleport(FlagTeleport flag)
    {
        this();
        
        // TODO clone
    }
    
    @Override
    public FlagTeleport clone()
    {
        return new FlagTeleport(this);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        // TODO
        
        // @teleport relative block | y + 2 | x - 2
        // @teleport relative player | y + 10
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
    }
}
