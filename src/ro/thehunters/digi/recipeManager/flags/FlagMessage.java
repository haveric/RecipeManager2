package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

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
            "This flag can be used more than once to add more messages.",
            "The text can contain colors (<red>, &5, etc)",
            "",
            "Setting to false will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} <green>Good job !",
            "{flag} <gray>Now you can die happy that you crafted that.",
        };
    }
    
    // Flag code
    
    private List<String> messages = new ArrayList<String>();
    
    public FlagMessage()
    {
        type = FlagType.MESSAGE;
    }
    
    public FlagMessage(FlagMessage flag)
    {
        this();
        
        messages.addAll(flag.messages);
    }
    
    @Override
    public FlagMessage clone()
    {
        return new FlagMessage(this);
    }
    
    public List<String> getMessages()
    {
        return messages;
    }
    
    public void setMessages(List<String> messages)
    {
        if(messages == null)
        {
            this.remove();
        }
        else
        {
            this.messages = messages;
        }
    }
    
    public void addMessage(String message)
    {
        if(message == null || message.equalsIgnoreCase("false") || message.equalsIgnoreCase("remove"))
        {
            this.remove();
        }
        else
        {
            if(messages == null)
            {
                messages = new ArrayList<String>();
            }
            
            messages.add(message);
        }
    }
    
    @Override
    protected boolean onParse(String value)
    {
        addMessage(Tools.parseColors(value, false));
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        Validate.notNull(messages);
        
        for(String s : messages)
        {
            a.addCustomEffect(a.parseVariables(s));
        }
    }
}