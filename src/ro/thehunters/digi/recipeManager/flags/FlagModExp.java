package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModExp extends Flag
{
    private char   mod     = '+';
    private int    amount  = 0;
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
                value = value.substring(1);
                break;
            }
            
            default:
            {
                mod = '+';
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
            return RecipeErrorReporter.error("The " + getType() + " flag must not have 0 exp !");
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
                
                a.addEffect(Messages.FLAG_MODEXP_SUB, message, "{exp}", String.valueOf(Math.abs(amount)));
                
                break;
            }
            
            case '+':
            {
                p.giveExp(amount);
                
                a.addEffect(Messages.FLAG_MODEXP_ADD, message, "{exp}", String.valueOf(amount));
                
                break;
            }
            
            case '=':
            {
                p.setTotalExperience(0);
                p.setLevel(0);
                p.giveExp(amount);
                
                a.addEffect(Messages.FLAG_MODEXP_SET, message, "{exp}", String.valueOf(amount));
                
                break;
            }
        }
        
        return true;
    }
}