package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModExp extends Flag
{
    private char   mod;
    private int    exp;
    private String message;
    
    public FlagModExp()
    {
        type = FlagType.MODEXP;
    }
    
    public FlagModExp(FlagModExp flag)
    {
        this();
        
        exp = flag.exp;
        message = flag.message;
    }
    
    @Override
    public FlagModExp clone()
    {
        return new FlagModExp(this);
    }
    
    public int getExp()
    {
        return exp;
    }
    
    public void setExp(int exp)
    {
        this.exp = exp;
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
                mod = '+';
        }
        
        // TODO finish
        Messages.debug("value: " + value);
        
        if(value.length() > String.valueOf(Integer.MAX_VALUE).length())
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
        }
        
        int exp = 0;
        
        try
        {
            exp = Integer.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
        }
        
        if(exp == 0)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag must not have 0 exp !");
        }
        
        setExp(exp);
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(exp == 0)
            return false;
        
        Player p = a.player();
        
        if(p == null)
            return false;
        
        if(exp < 0)
        {
            int diff = p.getTotalExperience() - exp;
            
            p.setTotalExperience(0);
            p.setLevel(0);
            
            if(diff > 0)
                p.giveExp(diff);
            
            a.addEffect(Messages.CRAFT_FLAG_MODEXP, message, "{color}", "" + ChatColor.RED, "{exp}", "" + exp);
        }
        else
        {
            p.giveExp(exp);
            
            a.addEffect(Messages.CRAFT_FLAG_MODEXP, message, "{color}", "" + ChatColor.GREEN, "{exp}", "+" + exp);
        }
        
        return true;
    }
}