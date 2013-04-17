package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagResultChance extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.RESULTCHANCE;
        
        A = new String[]
        {
            "{flag} [modifier]<value>",
        };
        
        D = new String[]
        {
            "FLAG NOT IMPLEMENTED YET !",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    private char mod;
    private float chance;
    
    public FlagResultChance()
    {
    }
    
    public FlagResultChance(FlagResultChance flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagResultChance clone()
    {
        return new FlagResultChance(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    public boolean onParse(String value)
    {
        // TODO
        
        return false;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(!a.hasResult())
        {
            a.addCustomReason("Needs result!");
            return;
        }
        
        // TODO test
        
        Messages.debug("chance set to " + chance);
        
        a.result().setChance(chance);
    }
}
