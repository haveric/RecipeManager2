package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModLevel extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.MODLEVEL;
        
        A = new String[]
        {
            "{flag} [modifier]<number>",
            "{flag} [modifier]<number> | <message>",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Modifies crafter's level.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The '[modifier]' argument can be nothing at all or you can use + (which is the same as nothing, to add), - (to subtract) or = (to set).",
            "The '<number>' argument must be the amount of levels to modify.",
            "The '<message>' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.",
            "",
            "NOTE: This is for experience levels, for experience points use " + FlagType.MODEXP.toString(),
            "NOTE: This flag does not check if player has enough levels when subtracting! Use in combination with " + FlagType.NEEDLEVEL.toString() + " if you want to check.",
        };
        
        E = new String[]
        {
            "{flag} 1 // gives 1 level to crafter",
            "{flag} +1 // exacly the same as above",
            "{flag} -2 | <red>You lost {amount} levels.  // takes at most 2 levels from crafter, if he does not have that amount it will be set to 0.",
            "{flag} = 0 | <red>You've been set to level 0 !  // sets crafter's level to 0, that space is valid there too.",
        };
    }
    
    // Flag code
    
    private char mod = '+';
    private int amount = 0;
    private String failMessage;
    
    public FlagModLevel()
    {
    }
    
    public FlagModLevel(FlagModLevel flag)
    {
        mod = flag.mod;
        amount = flag.amount;
        failMessage = flag.failMessage;
    }
    
    @Override
    public FlagModLevel clone()
    {
        return new FlagModLevel(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public char getModifier()
    {
        return mod;
    }
    
    public int getAmount()
    {
        return amount;
    }
    
    /**
     * Set the amount, can be negative.
     * 
     * @param amount
     */
    public void setAmount(int amount)
    {
        setAmount(amount < 0 ? '-' : '+', amount);
    }
    
    /**
     * @param mod
     *            can be '+', '-', '='
     * @param amount
     *            the amount, forced as positive number
     */
    public void setAmount(char mod, int amount)
    {
        switch(mod)
        {
            case '-':
            case '=':
            case '+':
            {
                break;
            }
            
            default:
            {
                throw new IllegalArgumentException("mod can only be '+', '-', '=' !");
            }
        }
        
        if(mod != '=' && amount == 0)
        {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-' !");
        }
        
        this.mod = mod;
        this.amount = Math.abs(amount);
    }
    
    public String getFailMessage()
    {
        return failMessage;
    }
    
    public void setFailMessage(String failMessage)
    {
        this.failMessage = failMessage;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setFailMessage(split[1].trim());
        }
        
        value = split[0].trim();
        char mod = value.charAt(0);
        
        switch(mod)
        {
            case '-':
            case '=':
            case '+':
            {
                value = value.substring(1).trim(); // remove modifier from string
                break;
            }
            
            default:
            {
                mod = '+'; // set default modifier if it's not defined
            }
        }
        
        if(value.length() > String.valueOf(Integer.MAX_VALUE).length())
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has level value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
        }
        
        int amount = 0;
        
        try
        {
            amount = Integer.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
        }
        
        if(mod != '=' && amount == 0)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag can only have 0 amount for = modifier, not for + or -");
        }
        
        setAmount(mod, amount);
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(mod != '=' && amount == 0)
        {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-' !");
        }
        
        if(!a.hasPlayer())
        {
            a.addCustomReason("Need a player!");
            return;
        }
        
        Player p = a.player();
        
        switch(mod)
        {
            case '-':
            {
                p.giveExpLevels(-amount);
                
                a.addEffect(Messages.FLAG_MODLEVEL_SUB, failMessage, "{amount}", amount);
                
                break;
            }
            
            case '+':
            {
                p.giveExpLevels(amount);
                
                a.addEffect(Messages.FLAG_MODLEVEL_ADD, failMessage, "{amount}", amount);
                
                break;
            }
            
            case '=':
            {
                p.setLevel(amount);
                
                a.addEffect(Messages.FLAG_MODLEVEL_SET, failMessage, "{amount}", amount);
                
                break;
            }
        }
    }
}
