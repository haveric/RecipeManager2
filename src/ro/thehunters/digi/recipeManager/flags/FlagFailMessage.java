package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.MultiResultRecipe;

public class FlagFailMessage extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} [message or false]",
        };
        
        D = new String[]
        {
            "Changes the message when recipe fails due to failure chance.",
            "Using this flag more than once will overwrite the previous message.",
            "",
            "The message allows colors (<red>, &3, etc) and new lines (with \n ).",
            "You can also use the following variables inside the message:",
            "  {failchance}    = the chance of failure as a number",
            "  {successchance} = the chance of success as a number",
            "",
            "The same effect can be achieved by using " + FlagType.MESSAGE + " on the fail result.",
            "",
            "Setting to 'false' will disable this flag, setting to blank will disable the message.",
        };
        
        E = new String[]
        {
            "{flag} <red>YOU FAILED, MWaHahahah!\n<gray>Now be gone.",
        };
    }
    
    // Flag code
    
    private String message;
    
    public FlagFailMessage()
    {
        type = FlagType.FAILMESSAGE;
    }
    
    public FlagFailMessage(FlagFailMessage flag)
    {
        this();
        
        message = flag.message;
    }
    
    @Override
    public FlagFailMessage clone()
    {
        return new FlagFailMessage(this);
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        if(message == null)
        {
            remove();
        }
        else
        {
            this.message = Tools.parseColors(message.replaceAll("\\n", "\n"), false);
        }
    }
    
    @Override
    public boolean onParse(String value)
    {
        setMessage(value);
        return true;
    }
    
    @Override
    public void onFailed(Args a)
    {
        if(a.hasRecipe() && a.recipe() instanceof MultiResultRecipe)
        {
            MultiResultRecipe recipe = (MultiResultRecipe)a.recipe();
            
            // TODO {failchance} & {successchance} vars
            
            a.addCustomReason(message);
        }
    }
}
