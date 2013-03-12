package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Tools;

public class FlagMessage extends Flag
{
    private String message;
    
    public FlagMessage()
    {
        type = FlagType.MESSAGE;
    }
    
    public FlagMessage(FlagMessage flag)
    {
        this();
        
        message = flag.message;
    }
    
    @Override
    public FlagMessage clone()
    {
        return new FlagMessage(this);
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        if(message == null || message.equalsIgnoreCase("false") || message.equalsIgnoreCase("remove"))
        {
            this.remove();
        }
        else
        {
            this.message = message;
        }
    }
    
    @Override
    protected boolean onParse(String value)
    {
        setMessage(Tools.parseColors(value.replace('|', '\n'), false));
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        a.addCustomEffect(a.parseVariables(getMessage()));
        return true;
    }
}