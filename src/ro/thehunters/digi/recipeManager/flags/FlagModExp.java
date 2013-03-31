package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModExp extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} [modifier]<number>",
            "{flag} [modifier]<number> | <message>",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Modifies crafter's experience points.",
            "",
            "The '[modifier]' argument can be nothing at all or you can use + (which is the same as nothing, to add), - (to subtract) or = (to set).",
            "The '<number>' argument must be the amount of experience to modify.",
            "The '<message>' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.",
            "",
            "NOTE: Using this flag more than once will overwrite the previous one.",
            "NOTE: This is for total experience points, for experience levels use " + FlagType.MODLEVEL.toString(),
            "NOTE: This flag does not check if player has enough experience when subtracting! Use in combination with " + FlagType.REQEXP.toString() + " if you want to check.",
        };
        
        E = new String[]
        {
            "{flag} 25 // gives 25 experience to crafter",
            "{flag} +25 // exacly the same as above",
            "{flag} -50 | <red>You lost {amount} exp!  // takes at most 50 experience from crafter, if he does not have that amount it will be set to 0.",
            "{flag} = 0 | <red>You lost all your experience!  // sets crafter experience to 0, that space is valid there too.",
        };
    }
    
    // Flag code
    
    private char mod = '+';
    private int amount = 0;
    private String message = null;
    
    public FlagModExp()
    {
        type = FlagType.MODEXP;
    }
    
    public FlagModExp(FlagModExp flag)
    {
        this();
        
        mod = flag.mod;
        amount = flag.amount;
        message = flag.message;
    }
    
    @Override
    public FlagModExp clone()
    {
        return new FlagModExp(this);
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
        
        this.amount = Math.abs(amount);
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
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setMessage(split[1].trim());
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
            return RecipeErrorReporter.error("The " + getType() + " flag has exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
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
    protected boolean onCrafted(Args a)
    {
        if(amount == 0 || !a.hasPlayer())
        {
            return false;
        }
        
        Player p = a.player();
        
        switch(mod)
        {
            case '-':
            {
                int diff = p.getTotalExperience() - amount;
                
                p.setTotalExperience(0);
                p.setLevel(0);
                
                if(diff > 0)
                {
                    p.giveExp(diff);
                }
                
                a.addEffect(Messages.FLAG_MODEXP_SUB, message, "{amount}", amount);
                
                break;
            }
            
            case '+':
            {
                p.giveExp(amount);
                
                a.addEffect(Messages.FLAG_MODEXP_ADD, message, "{amount}", amount);
                
                break;
            }
            
            case '=':
            {
                p.setTotalExperience(0);
                p.setLevel(0);
                p.giveExp(amount);
                
                a.addEffect(Messages.FLAG_MODEXP_SET, message, "{amount}", amount);
                
                break;
            }
        }
        
        return true;
    }
}