package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagCooldown extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.COOLDOWN;
        
        A = new String[]
        {
            "{flag} <number>[suffix] | [arguments]",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Sets a cooldown time for crafting a recipe or result.",
            "Once a recipe/result is used, the crafter can not craft it again for the specified amount of time.",
            "If set on a result, the result will be unavailable to the crafter for the cooldown time but the rest of results and the recipe will work as before.",
            "NOTE: cooldown is reset when reloading/restarting server.",
            "",
            "The <number> argument must be a number, by default it's seconds.",
            "The [suffix] argument defines what the <number> value is scaled in, values for suffix can be:",
            "  s  = for seconds (default)",
            "  m  = for minutes",
            "  h  = for hours",
            "You can also use float values like '0.5m' to get 30 seconds.",
            "",
            "Optionally you can add some arguments separated by | character, those being:",
            "  global            = make the cooldown global instead of per-player.",
            "",
            "  msg <text>        = overwrites the information message; false to hide; supports colors; use {time} variable to display the new cooldown time.",
            "",
            "  failmsg <text>    = overwrites the failure message; false to hide; supports colors; use {time} variable to display the remaining time.",
            "",
        };
        
        E = new String[]
        {
            "{flag} 30",
            "{flag} 30s // exacly the same as the previous flag",
            "{flag} 1.75m | failmsg <red>Usable in: {time} // 1 minute and 45 seconds or 1 minute and 75% of a minute.",
            "{flag} .5h | global | failmsg <red>Someone used this recently, wait: {time} | msg <yellow>Cooldown time: {time} // half an hour",
        };
    }
    
    // Flag code
    
    private final Map<String, MutableInt> cooldownTime = new HashMap<String, MutableInt>();
    
    private int cooldown;
    private boolean global = false;
    private String failMessage;
    private String craftMessage;
    
    public FlagCooldown()
    {
    }
    
    public FlagCooldown(FlagCooldown flag)
    {
        cooldown = flag.cooldown;
        global = flag.global;
        failMessage = flag.failMessage;
        craftMessage = flag.craftMessage;
        
        // no cloning of cooldownTime Map.
    }
    
    @Override
    public FlagCooldown clone()
    {
        return new FlagCooldown(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    /**
     * @return cooldown time in seconds
     */
    public int getCooldownTime()
    {
        return cooldown;
    }
    
    /**
     * @param seconds
     *            Set the cooldown time in seconds
     */
    public void setCooldownTime(int seconds)
    {
        this.cooldown = seconds;
    }
    
    public boolean isGlobal()
    {
        return global;
    }
    
    public void setGlobal(boolean global)
    {
        this.global = global;
    }
    
    /**
     * Gets the cooldown time in seconds for specified player or for global if null is specified and global is enabled.
     * 
     * @param playerName
     *            if global is enabled this value is ignored, can be null.
     * @return -1 if there is a problem otherwise 0 or more specifies seconds left
     */
    public int getTimeLeftFor(String playerName)
    {
        if(global)
        {
            playerName = null;
        }
        else if(playerName == null)
        {
            return -1;
        }
        
        MutableInt get = cooldownTime.get(playerName);
        int time = (int)(System.currentTimeMillis() / 1000);
        
        if(get == null || time >= get.intValue())
        {
            return 0;
        }
        
        return get.intValue() - time;
    }
    
    /**
     * Gets the cooldown time as formatted string for specified player or for global if null is specified and global is enabled.
     * 
     * @param playerName
     *            if global is enabled this value is ignored, can be null.
     * @return '#h #m #s' format of remaining time.
     */
    public String getTimeLeftStringFor(String playerName)
    {
        return timeToString(getTimeLeftFor(playerName));
    }
    
    private String timeToString(int time)
    {
        Messages.debug("time = " + time);
        
        if(time < 1)
        {
            return "0s";
        }
        
        int seconds = time % 60;
        int minutes = time % 3600 / 60;
        int hours = time / 3600;
        
        return ((hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s" : "")).trim();
    }
    
    /**
     * Checks countdown time for player or globally if null is supplied and global is enabled.
     * 
     * @param playerName
     *            if global is enabled this value is ignored, can be null.
     * @return
     *         true if can be used, false otherwise.
     */
    public boolean hasCooldown(String playerName)
    {
        if(global)
        {
            playerName = null;
        }
        else if(playerName == null)
        {
            return false;
        }
        
        MutableInt get = cooldownTime.get(playerName);
        
        return (get == null ? true : (System.currentTimeMillis() / 1000) >= get.intValue());
    }
    
    public String getFailMessage()
    {
        return failMessage;
    }
    
    public void setFailMessage(String message)
    {
        this.failMessage = message;
    }
    
    public String getCraftMessage()
    {
        return craftMessage;
    }
    
    public void setCraftMessage(String message)
    {
        this.craftMessage = message;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        value = split[0].trim();
        float multiplier = 0;
        float time = 0.0f;
        
        switch(value.charAt(value.length() - 1))
        {
            case 'm':
                multiplier = 60.0f;
                break;
            
            case 'h':
                multiplier = 3600.0f;
                break;
            
            case 's':
                multiplier = 1;
                break;
        }
        
        if(multiplier > 0)
        {
            value = value.substring(0, value.length() - 1).trim();
        }
        
        if(value.length() > String.valueOf(Float.MAX_VALUE).length())
        {
            ErrorReporter.error("The " + getType() + " flag has cooldown value that is too long: " + value, "Value for float numbers can be between " + Tools.printNumber(Float.MIN_VALUE) + " and " + Tools.printNumber(Float.MAX_VALUE) + ".");
            return false;
        }
        
        try
        {
            time = Float.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            ErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
            return false;
        }
        
        cooldown = Math.round(multiplier > 0 ? multiplier * time : time);
        
        if(time <= 0.0f)
        {
            ErrorReporter.error("The " + getType() + " flag must have cooldown value more than 0 !");
            return false;
        }
        
        if(split.length > 1)
        {
            for(int i = 1; i < split.length; i++)
            {
                value = split[i].trim();
                
                if(value.equalsIgnoreCase("global"))
                {
                    global = true;
                }
                else if(value.toLowerCase().startsWith("msg"))
                {
                    craftMessage = value.substring("msg".length()).trim();
                }
                else if(value.toLowerCase().startsWith("failmsg"))
                {
                    failMessage = value.substring("failmsg".length()).trim();
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!hasCooldown(a.playerName()))
        {
            a.addReason((global ? Messages.FLAG_COOLDOWN_FAIL_GLOBAL : Messages.FLAG_COOLDOWN_FAIL_PERPLAYER), getFailMessage(), "{time}", getTimeLeftStringFor(a.playerName()));
        }
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(!global && !a.hasPlayerName())
        {
            return;
        }
        
        MutableInt get = cooldownTime.get(global ? null : a.playerName());
        int diff = (int)(System.currentTimeMillis() / 1000) + getCooldownTime();
        
        if(get == null)
        {
            get = new MutableInt(diff);
            cooldownTime.put(global ? null : a.playerName(), get);
        }
        else
        {
            get.setValue(diff);
        }
        
        Messages.debug("{time} = " + timeToString(getCooldownTime()) + " | cooldown = " + getCooldownTime());
        
        a.addEffect((global ? Messages.FLAG_COOLDOWN_SET_GLOBAL : Messages.FLAG_COOLDOWN_SET_PERPLAYER), getCraftMessage(), "{time}", timeToString(getCooldownTime()));
    }
    
    /*
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(1);
        
        list.add((global ? Messages.FLAG_COOLDOWN_SET_GLOBAL : Messages.FLAG_COOLDOWN_SET_PERPLAYER).get("{time}", timeToString(getCooldownTime())));
        
        return list;
    }
    */
}
