package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagRestrict extends Flag
{
    private String message;
    
    public FlagRestrict()
    {
        type = FlagType.RESTRICT;
    }
    
    @Override
    public FlagRestrict clone()
    {
        FlagRestrict clone = new FlagRestrict();
        
        clone.message = message;
        
        return clone;
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
    public boolean onParse(String value)
    {
        setMessage(value);
        return true;
    }
    
    @Override
    public void onCheck(Arguments a)
    {
        a.addReason(Messages.CRAFT_FLAG_DISABLED, message);
    }
}