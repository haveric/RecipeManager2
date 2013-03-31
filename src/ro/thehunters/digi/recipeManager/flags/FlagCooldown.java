package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagCooldown extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} <seconds> | [fail message or blank or false] | [craft message or false]";
        
        D = new String[17];
        D[0] = "Sets a cooldown time for recipe or result.";
        D[1] = "Once a recipe/result is used, it can not be used for the specified amount of time.";
        D[2] = "If set on a result, the result will be unavailable for the cooldown time, the recipe will work just like before.";
        D[3] = "NOTE: the cooldown is not saved between full server shutdowns!";
        D[4] = null;
        D[5] = "The <seconds> argument must be a number in seconds.";
        D[6] = null;
        D[7] = "The [fail message or false] argument is used when the result/recipe is still in cooldown.";
        D[8] = "Using 'false' as value will hide the message.";
        D[9] = "You can also not write anything (leave it blank) to skip it if you want to only set the craft message";
        D[10] = "It can have the following variables:";
        D[11] = "  {time}    = the remaining cooldown time for current crafter in format '#h #m #s'.";
        D[12] = null;
        D[13] = "The [craft message or false] is triggered when the recipe/result was crafted and cooldown was set.";
        D[14] = "Using 'false' as value will hide the message.";
        D[15] = "It can have the following variables:";
        D[16] = "  {time}    = the new cooldown time in format '#h #m #s'.";
        
        E = new String[3];
        E[0] = "{flag} 30";
        E[1] = "{flag} 5 | <red>Cooldown: {time}";
        E[2] = "{flag} 120 | <red>Wait {time}! | <yellow>You can craft this again after {time}...";
    }
    
    // Flag code
    
    private static final Map<String, MutableInt> playerNextUse = new HashMap<String, MutableInt>();
    
    private int cooldown;
    private String failMessage;
    private String craftMessage;
    
    public FlagCooldown()
    {
        type = FlagType.COOLDOWN;
    }
    
    public FlagCooldown(FlagCooldown flag)
    {
        this();
        
        cooldown = flag.cooldown;
        failMessage = flag.failMessage;
        craftMessage = flag.craftMessage;
    }
    
    @Override
    public FlagCooldown clone()
    {
        return new FlagCooldown(this);
    }
    
    public int getCooldownTime()
    {
        return cooldown;
    }
    
    public void setCooldownTime(int time)
    {
        this.cooldown = time;
    }
    
    public int getCooldownTimeFor(String playerName)
    {
        if(playerName == null)
            return -1;
        
        MutableInt get = playerNextUse.get(playerName);
        int time = (int)(System.currentTimeMillis() / 1000);
        
        if(get == null || time >= get.intValue())
            return 0;
        
        return get.intValue() - time;
    }
    
    public String getCooldownStringFor(String playerName)
    {
        int diff = getCooldownTimeFor(playerName);
        
        if(diff < 1)
            return "0s";
        
        return diffTimeToString(diff);
    }
    
    private String diffTimeToString(int diff)
    {
        int seconds = diff % 60;
        int minutes = diff % 3600 / 60;
        int hours = diff / 3600;
        
        return ((hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s" : "")).trim();
    }
    
    public boolean checkTime(String playerName)
    {
        if(playerName == null)
            return false;
        
        MutableInt get = playerNextUse.get(playerName);
        
        if(get == null)
            return true;
        
        return (System.currentTimeMillis() / 1000) >= get.intValue();
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
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            if(!value.isEmpty())
                setFailMessage(value);
            
            if(split.length > 2)
            {
                setCraftMessage(split[2].trim());
            }
        }
        
        value = split[0].trim();
        
        if(value.length() > String.valueOf(Integer.MAX_VALUE).length())
        {
            RecipeErrorReporter.error("The " + getType() + " flag has exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
            return false;
        }
        
        int exp = 0;
        
        try
        {
            exp = Integer.valueOf(value);
        }
        catch(NumberFormatException e)
        {
            RecipeErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
            return false;
        }
        
        if(exp == 0)
        {
            RecipeErrorReporter.error("The " + getType() + " flag must not have 0 exp !");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!checkTime(a.playerName()))
        {
            a.addReason(Messages.FLAG_COOLDOWN_FAIL, getFailMessage(), "{time}", getCooldownStringFor(a.playerName()));
        }
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(!a.hasPlayerName())
            return false;
        
        MutableInt get = playerNextUse.get(a.playerName());
        int time = (int)(System.currentTimeMillis() / 1000) + getCooldownTime();
        
        if(get == null)
        {
            get = new MutableInt(time);
            playerNextUse.put(a.playerName(), get);
        }
        else
        {
            get.setValue(time);
        }
        
        a.addEffect(Messages.FLAG_COOLDOWN_CRAFT, getCraftMessage(), "{time}", diffTimeToString(time));
        
        return true;
    }
}