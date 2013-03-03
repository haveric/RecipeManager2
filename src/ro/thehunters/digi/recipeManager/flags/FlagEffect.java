package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagEffect extends Flag
{
    private boolean onlyPlayer = false;
    private Effect  effect     = null;
    private Effect  data       = null;
    private int     radius     = 10;
    
    // TODO finish this
    public FlagEffect()
    {
        type = FlagType.EFFECT;
    }
    
    public FlagEffect(FlagEffect flag)
    {
        this();
        
        // TODO CLONE
    }
    
    @Override
    public FlagEffect clone()
    {
        return new FlagEffect(this);
    }
    
    @Override
    public boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("player"))
            {
                onlyPlayer = true;
            }
            else if(s.startsWith("play"))
            {
                value = s.substring("play".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'play' argument with no sound!", "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
                    return false;
                }
                
                try
                {
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'play' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
                    return false;
                }
            }
            else if(s.startsWith("radius"))
            {
                value = s.substring("radius".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'radius' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
                
                try
                {
                    radius = Integer.valueOf(value);
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has invalid 'radius' argument number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
            }
            else
            {
                RecipeErrorReporter.warning("Flag @" + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        if(effect == null)
        {
            RecipeErrorReporter.error("Flag @" + type + " doesn't have the 'play' argument!", "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onApply(Arguments a)
    {
        if(onlyPlayer)
        {
            Player p = a.getPlayer();
            
            if(p != null)
            {
                p.playEffect(EntityEffect.WOLF_HEARTS);
                
//                p.playSound(a.hasLocation() ? a.location() : p.getLocation(), sound, volume, pitch);
            }
        }
        else
        {
            Location l = a.getLocation();
            
            if(l != null)
            {
                l.getWorld().playEffect(l, effect, data, radius);
            }
        }
    }
}
