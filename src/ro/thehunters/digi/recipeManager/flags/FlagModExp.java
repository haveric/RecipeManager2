package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagModExp extends Flag
{
    private int    exp;
    private String message;
    
    @Override
    public FlagModExp clone()
    {
        FlagModExp clone = new FlagModExp();
        
        clone.exp = exp;
        clone.message = message;
        
        return clone;
    }
    
    public int getExp()
    {
        return exp;
    }
    
    public void setExp(int exp)
    {
        this.exp = exp;
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
        
        setExp(exp);
        return true;
    }
    
    @Override
    protected void onApply(Arguments a)
    {
        Player p = a.player();
        
        if(p == null)
            return;
        
        if(exp < 0)
        {
            int diff = p.getTotalExperience() - exp;
            
            p.setTotalExperience(0);
            p.setLevel(0);
            
            if(diff > 0)
                p.giveExp(diff);
            
            // TODO !
            a.addEffect(Messages.CRAFT_FLAG_MODEXP, message, "{color}", "" + ChatColor.RED, "{exp}", "" + exp);
            
            Messages.CRAFT_FLAG_MODEXP.print(p, message, "{color}", "" + ChatColor.RED, "{exp}", "" + exp);
        }
        else
        {
            p.giveExp(exp);
            
            // TODO !
            a.addEffect(Messages.CRAFT_FLAG_MODEXP, message, "{color}", "" + ChatColor.GREEN, "{exp}", "" + exp);
            
            Messages.CRAFT_FLAG_MODEXP.print(p, message, "{color}", "" + ChatColor.GREEN, "{exp}", "+" + exp);
        }
    }
}