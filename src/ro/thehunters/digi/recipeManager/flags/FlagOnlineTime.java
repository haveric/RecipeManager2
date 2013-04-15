package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Players;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagOnlineTime extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        
        // TODO
        A = new String[]
        {
            "{flag} <min or min-max or false>",
        };
        
        D = new String[]
        {
            "",
        };
        
        E = new String[]
        {
            "",
        };
    }
    
    // Flag code
    
    private int minTime = -1;
    private int maxTime = -1;
    private String message;
    
    public FlagOnlineTime()
    {
        type = FlagType.ONLINETIME;
    }
    
    public FlagOnlineTime(FlagOnlineTime flag)
    {
        this();
        
        minTime = flag.minTime;
        maxTime = flag.maxTime;
        message = flag.message;
    }
    
    @Override
    public FlagOnlineTime clone()
    {
        return new FlagOnlineTime(this);
    }
    
    public int getMinTime()
    {
        return minTime;
    }
    
    public void setMinTime(int minTime)
    {
        this.minTime = minTime;
    }
    
    public int getMaxTime()
    {
        return maxTime;
    }
    
    public void setMaxTime(int maxTime)
    {
        this.maxTime = maxTime;
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
        
        split = split[0].split("-");
        
        value = split[0].trim();
        
        try
        {
            setMinTime(Integer.valueOf(value));
        }
        catch(NumberFormatException e)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " has invalid min time number: " + value);
        }
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            try
            {
                setMaxTime(Integer.valueOf(value));
            }
            catch(NumberFormatException e)
            {
                return RecipeErrorReporter.error("Flag " + getType() + " has invalid max time number: " + value);
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(a.hasPlayer())
        {
            Integer joined = Players.getJoinedTime(a.player());
            
            if(joined != null) // unlikely but just to be sure
            {
                int diff = (int)(System.currentTimeMillis() / 1000) - joined.intValue();
                
                if(diff >= minTime && (maxTime == -1 || maxTime >= diff))
                {
                    return;
                }
            }
        }
        
        // TODO
        
        long time = minTime;
        long s = time % 60;
        long m = time % 3600 / 60;
        long h = time / 3600 % 24;
        long d = time / 86400;
        
        a.addReason(Messages.FLAG_ONLINETIME, message, "{range}", "", "{min}", "", "{max}", "");
    }
}
