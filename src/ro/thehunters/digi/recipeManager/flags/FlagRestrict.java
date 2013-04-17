package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagRestrict extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.RESTRICT;
        
        A = new String[]
        {
            "{flag} ...",
        };
        
        D = new String[]
        {
            "FLAG NOT IMPLEMENTED",
        /*
        "Restricts the recipe to everybody.",
        "This is the crafter friendly version of @remove because crafter gets a message when trying to craft the recipe.",
        "Optionally you can overwrite the default restrict message.",
        "",
        "Setting it to false will lift the restriction.",
         */
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    private String message;
    
    public FlagRestrict()
    {
    }
    
    public FlagRestrict(FlagRestrict flag)
    {
        message = flag.message;
    }
    
    @Override
    public FlagRestrict clone()
    {
        return new FlagRestrict(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        setMessage(value);
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        a.addReason(Messages.FLAG_RESTRICT, message);
    }
}
