package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagHeight extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.HEIGHT;
        
        A = new String[]
        {
            "{flag} <min or min-max> | [fail message]",
        };
        
        D = new String[]
        {
            "Checks if crafter or furnace is at least at 'min' height and optionally at most 'max' height.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
            "In the message the following variables can be used:",
            "  {height}  = height or height range",
        };
        
        E = new String[]
        {
            "{flag} 200 // must be high in the sky",
            "{flag} 0-30 | <red>You need to be deep underground!",
        };
    }
    
    // Flag code
    
    private int minHeight;
    private int maxHeight;
    private String failMessage;
    
    public FlagHeight()
    {
    }
    
    public FlagHeight(FlagHeight flag)
    {
        minHeight = flag.minHeight;
        maxHeight = flag.maxHeight;
        failMessage = flag.failMessage;
    }
    
    @Override
    public FlagHeight clone()
    {
        return new FlagHeight(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public int getMinHeight()
    {
        return minHeight;
    }
    
    public void setMinHeight(int minHeight)
    {
        this.minHeight = minHeight;
    }
    
    public int getMaxHeight()
    {
        return maxHeight;
    }
    
    public void setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight;
    }
    
    public String getHeightString()
    {
        return getMinHeight() + (getMaxHeight() > getMinHeight() ? " - " + getMaxHeight() : "");
    }
    
    public boolean checkHeight(int height)
    {
        return (height >= minHeight && height <= maxHeight);
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
            setMinHeight(Integer.valueOf(value));
            setMaxHeight(getMinHeight());
        }
        catch(NumberFormatException e)
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid min required height number: " + value);
            return false;
        }
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            try
            {
                setMaxHeight(Integer.valueOf(value));
            }
            catch(NumberFormatException e)
            {
                RecipeErrorReporter.error("The " + getType() + " flag has invalid max required height number: " + value);
                return false;
            }
        }
        
        if((getMinHeight() <= 0 && getMaxHeight() <= 0) || getMaxHeight() < getMinHeight())
        {
            RecipeErrorReporter.error("The " + getType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasLocation() || !checkHeight(a.location().getBlockY()))
        {
            a.addReason(Messages.FLAG_HEIGHT, failMessage, "{height}", getHeightString());
        }
    }
}
