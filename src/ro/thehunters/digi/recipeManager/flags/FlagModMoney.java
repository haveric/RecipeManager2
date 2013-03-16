package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.ChatColor;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModMoney extends Flag
{
    private float  money;
    private String message;
    
    public FlagModMoney()
    {
        type = FlagType.MODLEVEL;
    }
    
    public FlagModMoney(FlagModMoney flag)
    {
        this();
        
        money = flag.money;
        message = flag.message;
    }
    
    @Override
    public FlagModMoney clone()
    {
        return new FlagModMoney(this);
    }
    
    public double getMoney()
    {
        return money;
    }
    
    public void setMoney(float money)
    {
        this.money = money;
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
        
        if(value.length() > String.valueOf(Float.MAX_VALUE).length())
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has money value that is too long: " + value, "Value for floats can be between " + Tools.printNumber(Float.MIN_VALUE) + " and " + Tools.printNumber(Float.MAX_VALUE) + ".");
        }
        
        float money = 0;
        
        try
        {
            money = Float.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
        }
        
        if(money == 0)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag must not have 0 value !");
        }
        
        setMoney(money);
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(money == 0 || !a.hasPlayerName())
            return false;
        
        if(money < 0)
        {
            double amount = RecipeManager.getEconomy().getMoney(a.playerName());
            
            if((amount - money) < 0)
            {
                amount = -amount;
            }
            else
            {
                amount = money;
            }
            
            RecipeManager.getEconomy().modMoney(a.playerName(), amount);
        }
        else
        {
            RecipeManager.getEconomy().modMoney(a.playerName(), money);
        }
        
        a.addEffect(Messages.CRAFT_FLAG_MODMONEY, message, "{color}", (money < 0 ? ChatColor.RED : ChatColor.GREEN) + "", "{money}", RecipeManager.getEconomy().getFormat(money));
        
        return true;
    }
}