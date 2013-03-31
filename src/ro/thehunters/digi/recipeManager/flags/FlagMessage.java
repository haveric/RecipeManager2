package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Tools;

public class FlagMessage extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} <text or false>",
        };
        
        D = new String[]
        {
            "Prints a message when recipe or item is succesfully crafted.",
            "The text can contain colors (<red>, &5, etc) and can also contain new lines, separated by | character.",
            "",
            "Setting to false will disable the flag.",
            "",
            "NOTE: Using this flag more than once will overwrite the previous one.",
        };
        
        E = new String[]
        {
            "{flag} <green>Good job ! |<gray>Now you can die happy that you crafted that.",
        };
    }
    
    // Flag code
    
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