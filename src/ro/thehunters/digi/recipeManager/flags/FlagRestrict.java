package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagRestrict extends Flag
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
    
    private String message;
    
    public FlagRestrict()
    {
        type = FlagType.RESTRICT;
    }
    
    public FlagRestrict(FlagRestrict flag)
    {
        this();
        
        message = flag.message;
    }
    
    @Override
    public FlagRestrict clone()
    {
        return new FlagRestrict(this);
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
        a.addReason(Messages.FLAG_DISABLED, message);
    }
}