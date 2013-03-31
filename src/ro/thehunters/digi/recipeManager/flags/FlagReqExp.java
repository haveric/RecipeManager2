package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagReqExp extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} <min or min-max>",
            "{flag} <min or min-max> | <message>",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Checks if crafter has at least 'min' experience and optionally at most 'max' experience.",
            "",
            "The '<message>' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in result's lore, should be as short as possible.",
            "",
            "NOTE: Using this flag more than once will overwrite the previous one!",
            "NOTE: This is for total experience points, for experience levels use " + FlagType.REQLEVEL.toString(),
        };
        
        E = new String[]
        {
            "{flag} 100 // player needs to have at least 100 experience to craft",
            "{flag} 0-500 // player can only craft if he has between 0 and 500 experience",
            "{flag} 1000 | <red>Need {exp} exp!",
        };
    }
    
    // Flag code
    
    private int minExp;
    private int maxExp;
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
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setMessage(split[1].trim());
        }
        
        split = split[0].split("-", 2);
        value = split[0].trim();
        
        if(value.length() > String.valueOf(Integer.MAX_VALUE).length())
        {
            RecipeErrorReporter.error("The " + getType() + " flag has min exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
            return false;
        }
        
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
            
            if(value.length() > String.valueOf(Integer.MAX_VALUE).length())
            {
                RecipeErrorReporter.error("The " + getType() + " flag has max exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
                return false;
            }
            
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
        
        if(getMinExp() <= 0 && getMaxExp() <= 0)
        {
            RecipeErrorReporter.error("The " + getType() + " flag needs either min or max exp above 0 !");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        Player p = a.player();
        
        if(p == null || !checkExp(p.getTotalExperience()))
        {
            a.addReason(Messages.FLAG_REQEXP, message, "{exp}", getExp());
        }
    }
}
