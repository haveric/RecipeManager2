package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagReqExp extends Flag
{
    private int    minExp;
    private int    maxExp;
    private String message;
    
    public FlagReqExp()
    {
        type = FlagType.REQEXP;
    }
    
    public FlagReqExp(FlagReqExp flag)
    {
        this();
        
        minExp = flag.minExp;
        maxExp = flag.maxExp;
        message = flag.message;
    }
    
    @Override
    public FlagReqExp clone()
    {
        return new FlagReqExp(this);
    }
    
    public int getMinExp()
    {
        return minExp;
    }
    
    public void setMinExp(int minExp)
    {
        this.minExp = minExp;
    }
    
    public int getMaxExp()
    {
        return maxExp;
    }
    
    public void setMaxExp(int maxExp)
    {
        this.maxExp = maxExp;
    }
    
    public String getExp()
    {
        return getMinExp() + (getMaxExp() > 0 ? " - " + getMaxExp() : "");
    }
    
    public boolean checkExp(int exp)
    {
        return !((minExp > 0 && exp < minExp) || (maxExp > 0 && exp > maxExp));
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
    public boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setMessage(split[1].trim());
        }
        
        split = split[0].split("-", 2);
        value = split[0].trim();
        
        try
        {
            setMinExp(Integer.valueOf(value));
        }
        catch(NumberFormatException e)
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid min req exp number: " + value);
            return false;
        }
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            try
            {
                setMaxExp(Integer.valueOf(value));
            }
            catch(NumberFormatException e)
            {
                RecipeErrorReporter.error("The " + getType() + " flag has invalid max req exp number: " + value);
                return false;
            }
        }
        
        if(getMinExp() > 0 || getMaxExp() > 0)
        {
            return true;
        }
        else
        {
            RecipeErrorReporter.error("The " + type + " flag needs either min or max exp above 0 !");
            return false;
        }
    }
    
    @Override
    public void onCheck(Arguments a)
    {
        Player p = a.player();
        
        if(p == null || !checkExp(p.getTotalExperience()))
        {
            a.addReason(Messages.CRAFT_FLAG_REQEXP, message, "{exp}", getExp());
        }
    }
}
