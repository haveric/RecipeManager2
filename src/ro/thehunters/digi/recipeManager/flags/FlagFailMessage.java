package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Tools;

public class FlagFailMessage extends Flag
{
    private String message;
    
    public FlagFailMessage()
    {
        type = FlagType.FAILMESSAGE;
    }
    
    @Override
    public FlagFailMessage clone()
    {
        FlagFailMessage clone = new FlagFailMessage();
        
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
        setMessage(Tools.parseColors(value.replaceAll("\\n", "\n"), false));
        return true;
    }
    
    @Override
    public void onFailed(Arguments a)
    {
        a.addReason(message);
    }
}
