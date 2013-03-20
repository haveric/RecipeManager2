package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModLevel extends Flag
{
    private int    level;
    private String message;
    
    public FlagModLevel()
    {
        type = FlagType.MODLEVEL;
    }
    
    public FlagModLevel(FlagModLevel flag)
    {
        this();
        
        level = flag.level;
        message = flag.message;
    }
    
    @Override
    public FlagModLevel clone()
    {
        return new FlagModLevel(this);
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public void setLevel(int level)
    {
        this.level = level;
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
        
        if(value.length() > String.valueOf(Integer.MAX_VALUE).length())
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has level value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
        }
        
        int level = 0;
        
        try
        {
            level = Integer.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
        }
        
        if(level == 0)
        {
            return RecipeErrorReporter.error("The " + getType() + " flag must not have 0 value !");
        }
        
        setLevel(level);
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(level == 0)
            return false;
        
        Player p = a.player();
        
        if(p == null)
            return false;
        
        p.giveExpLevels(level);
        
        a.addEffect(Messages.FLAG_MODLEVEL, message, "{color}", (level < 0 ? ChatColor.RED : ChatColor.GREEN) + "", "{level}", "" + level);
        
        return true;
    }
}