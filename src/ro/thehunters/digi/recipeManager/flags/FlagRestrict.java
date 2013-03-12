package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagRestrict extends Flag
{
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
        a.addReason(Messages.CRAFT_FLAG_DISABLED, message);
    }
}