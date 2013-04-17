package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagNeedLevel extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.NEEDLEVEL;
        
        A = new String[]
        {
            "{flag} <min or min-max> | [fail message]",
        };
        
        D = new String[]
        {
            "Checks if crafter has at least 'min' levels and optionally at most 'max' levels.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
            "In the message the following variables can be used:",
            "  {level}  = level or level range",
            "",
            "NOTE: This is for experience levels, for experience points use " + FlagType.NEEDEXP.toString() + " or for world height use " + FlagType.NEEDHEIGHT + ".",
        };
        
        E = new String[]
        {
            "{flag} 1",
            "{flag} 25-100 | <red>Need level 25 to 100!",
        };
    }
    
    // Flag code
    
    private int minLevel;
    private int maxLevel;
    private String failMessage;
    
    public FlagNeedLevel()
    {
    }
    
    public FlagNeedLevel(FlagNeedLevel flag)
    {
        minLevel = flag.minLevel;
        maxLevel = flag.maxLevel;
        failMessage = flag.failMessage;
    }
    
    @Override
    public FlagNeedLevel clone()
    {
        return new FlagNeedLevel(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public int getMinLevel()
    {
        return minLevel;
    }
    
    public void setMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
    }
    
    public int getMaxLevel()
    {
        return maxLevel;
    }
    
    public void setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;
    }
    
    public String getLevel()
    {
        return getMinLevel() + (getMaxLevel() > 0 ? " - " + getMaxLevel() : "");
    }
    
    public boolean checkLevel(int level)
    {
        return !((minLevel > 0 && level < minLevel) || (maxLevel > 0 && level > maxLevel));
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
        
        split = split[0].split("-", 2);
        value = split[0].trim();
        
        try
        {
            setMinLevel(Integer.valueOf(value));
        }
        catch(NumberFormatException e)
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid min required level number: " + value);
            return false;
        }
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            try
            {
                setMaxLevel(Integer.valueOf(value));
            }
            catch(NumberFormatException e)
            {
                RecipeErrorReporter.error("The " + getType() + " flag has invalid max required level number: " + value);
                return false;
            }
        }
        
        if(getMinLevel() <= 0 && getMaxLevel() <= 0)
        {
            RecipeErrorReporter.error("The " + getType() + " flag needs either min or max level above 0 !");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        Player p = a.player();
        
        if(p == null || !checkLevel(p.getTotalExperience()))
        {
            a.addReason(Messages.FLAG_REQLEVEL, failMessage, "{level}", getLevel());
        }
    }
}
