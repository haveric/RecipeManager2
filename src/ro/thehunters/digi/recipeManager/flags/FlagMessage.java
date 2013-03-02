package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Tools;

public class FlagMessage extends Flag
{
    private String message;
    
    public FlagMessage()
    {
        type = FlagType.MESSAGE;
    }
    
    @Override
    public FlagMessage clone()
    {
        FlagMessage clone = new FlagMessage();
        
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
}