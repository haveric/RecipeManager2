package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModExp extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.MODEXP;
        
        A = new String[]
        {
            "{flag} [modifier]<amount> | [fail message]",
        };
        
        D = new String[]
        {
            "Modifies crafter's experience points.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The '[modifier]' argument can be nothing at all or you can use + (which is the same as nothing, to add), - (to subtract) or = (to set).",
            "The '<amount>' argument must be the amount of experience to modify.",
            "The '[fail message]' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.",
            "For the fail message you can use the following arguments:",
            "  {amount}       = amount defined in the flag, never has modifier prefix.",
            "  {modifier}     = the modifier prefix.",
            "  {actualamount} = (only works for - modifier) the actual amount lost.",
            "",
            "NOTE: This is for total experience points, for experience levels use " + FlagType.MODLEVEL.toString(),
            "NOTE: This flag does not check if player has enough experience when subtracting! Use in combination with " + FlagType.NEEDEXP.toString() + " if you want to check.",
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
    private String failMessage = null;
    
    public FlagModExp()
    {
    }
    
    public FlagModExp(FlagModExp flag)
    {
        mod = flag.mod;
        amount = flag.amount;
        failMessage = flag.failMessage;
    }
    
    @Override
    public FlagModExp clone()
    {
        return new FlagModExp(this);
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
    
    public void setFailMessage(String message)
    {
        this.failMessage = message;
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
            return ErrorReporter.error("The " + getType() + " flag has exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
        }
        
        int amount = 0;
        
        try
        {
            amount = Integer.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            return ErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
        }
        
        if(mod != '=' && amount == 0)
        {
            return ErrorReporter.error("The " + getType() + " flag can only have 0 amount for = modifier, not for + or -");
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
        int exp = 0;
        
        switch(mod)
        {
            case '+':
            {
                exp = Tools.Exp.getTotalExperience(p) + amount;
                
                a.addEffect(Messages.FLAG_MODEXP_ADD, failMessage, "{amount}", amount, "{modifier}", mod);
                
                break;
            }
            
            case '-':
            {
                exp = Math.max(Tools.Exp.getTotalExperience(p) - amount, 0);
                
                a.addEffect(Messages.FLAG_MODEXP_SUB, failMessage, "{amount}", amount, "{modifier}", mod, "{actualamount}", exp);
                
                break;
            }
            
            case '=':
            {
                exp = Math.max(amount, 0);
                
                a.addEffect(Messages.FLAG_MODEXP_SET, failMessage, "{amount}", amount, "{modifier}", mod);
                
                break;
            }
        }
        
        Tools.Exp.setTotalExperience(p, exp);
    }
    
    /*
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(1);
        
        switch(mod)
        {
            case '+':
                list.add(Messages.FLAG_MODEXP_ADD.get("{amount}", amount, "{modifier}", mod));
                break;
            case '-':
                list.add(Messages.FLAG_MODEXP_SUB.get("{amount}", amount, "{modifier}", mod, "{actualamount}", amount));
                break;
            case '=':
                list.add(Messages.FLAG_MODEXP_SET.get("{amount}", amount, "{modifier}", mod));
                break;
        }
        
        return list;
    }
    */
}
