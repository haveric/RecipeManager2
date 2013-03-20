package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.data.MutableInteger;

public class FlagCooldown extends Flag
{
    private static final Map<String, MutableInteger> playerNextUse = new HashMap<String, MutableInteger>();
    
    private int                                      cooldown;
    private String                                   failMessage;
    private String                                   craftMessage;
    
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
        
        MutableInteger get = playerNextUse.get(playerName);
        int time = (int)(System.currentTimeMillis() / 1000);
        
        if(get == null || time >= get.value)
            return 0;
        
        return get.value - time;
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
        
        MutableInteger get = playerNextUse.get(playerName);
        
        if(get == null)
            return true;
        
        return (System.currentTimeMillis() / 1000) >= get.value;
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
        
        MutableInteger get = playerNextUse.get(a.playerName());
        int time = (int)(System.currentTimeMillis() / 1000) + getCooldownTime();
        
        if(get == null)
        {
            get = new MutableInteger(time);
            playerNextUse.put(a.playerName(), get);
        }
        else
        {
            get.value = time;
        }
        
        a.addEffect(Messages.FLAG_COOLDOWN_CRAFT, getCraftMessage(), "{time}", diffTimeToString(time));
        
        return true;
    }
}