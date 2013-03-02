package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagReqExp extends Flag
{
    private int    minExp;
    private int    maxExp;
    private String message;
    
    public FlagReqExp()
    {
        type = FlagType.REQEXP;
    }
    
    @Override
    public FlagReqExp clone()
    {
        FlagReqExp clone = new FlagReqExp();
        
        clone.minExp = minExp;
        clone.maxExp = maxExp;
        clone.message = message;
        
        return clone;
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
        
        split = split[0].split("-");
        value = split[0].trim();
        Integer xp = Tools.parseInteger(value, "The @" + type + " flag has invalid min req exp number: " + value);
        
        if(xp == null)
            return false;
        
        if(xp > 0)
            setMinExp(xp);
        
        if(split.length > 1)
        {
            value = split[1].trim();
            xp = Tools.parseInteger(value, "The @" + type + " flag has invalid max req exp number: " + value);
            
            if(xp == null)
                return false;
            
            if(xp > 0)
                setMaxExp(xp);
        }
        
        if(getMinExp() > 0 || getMaxExp() > 0)
        {
            return true;
        }
        else
        {
            RecipeErrorReporter.error("The @" + type + " flag needs either min or max exp above 0 !");
            return false;
        }
    }
    
    @Override
    public void onCheck(Arguments a)
    {
        Player p = a.getPlayer();
        
        if(p == null || !checkExp(p.getTotalExperience()))
        {
            a.addReason(Messages.CRAFT_FLAG_REQEXP, message, "{exp}", getExp());
        }
    }
}
