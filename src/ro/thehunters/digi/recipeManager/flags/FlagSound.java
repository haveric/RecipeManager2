package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagSound extends Flag
{
    private boolean onlyPlayer = false;
    private Sound   sound      = null;
    private float   volume     = 1;
    private float   pitch      = 0;
    
    public FlagSound()
    {
        type = FlagType.SOUND;
    }
    
    public FlagSound(FlagSound flag)
    {
        this();
        
        onlyPlayer = flag.onlyPlayer;
        sound = flag.sound;
        volume = flag.volume;
        pitch = flag.pitch;
    }
    
    @Override
    public FlagSound clone()
    {
        return new FlagSound(this);
    }
    
    public boolean isOnlyPlayer()
    {
        return onlyPlayer;
    }
    
    public void setOnlyPlayer(boolean onlyPlayer)
    {
        this.onlyPlayer = onlyPlayer;
    }
    
    public Sound getSound()
    {
        return sound;
    }
    
    public void setSound(Sound sound)
    {
        this.sound = sound;
    }
    
    /**
     * @return volume from 0.0 to 1.0
     */
    public float getVolume()
    {
        return volume;
    }
    
    /**
     * @param volume
     *            from 0.0 to 1.0
     */
    public void setVolume(float volume)
    {
        if(volume < 0 || volume > 4)
        {
            RecipeErrorReporter.warning("Flag " + type + " has invalid 'volume' number range, must be between 0.0 and 1.0, trimmed.");
            this.volume = Math.min(Math.max(volume, 0.0f), 4.0f);
        }
        else
        {
            this.volume = volume;
        }
    }
    
    /**
     * @return pitch from 0.0 to 4.0
     */
    public float getPitch()
    {
        return pitch;
    }
    
    /**
     * @param pitch
     *            from 0.0 to 4.0
     */
    public void setPitch(float pitch)
    {
        if(pitch < 0 || pitch > 4)
        {
            RecipeErrorReporter.warning("Flag " + type + " has invalid 'pitch' number range, must be between 0.0 and 4.0, trimmed.");
            this.pitch = Math.min(Math.max(pitch, 0.0f), 4.0f);
        }
        else
        {
            this.pitch = pitch;
        }
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
                    RecipeErrorReporter.error("Flag " + type + " has 'play' argument with no sound!", "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
                    return false;
                }
                
                try
                {
                    setSound(Sound.valueOf(value.toUpperCase()));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid 'play' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
                    return false;
                }
            }
            else if(s.startsWith("volume"))
            {
                value = s.substring("volume".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag " + type + " has 'volume' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
                
                try
                {
                    setVolume(Float.valueOf(value));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid 'volume' argument float number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
            }
            else if(s.startsWith("pitch"))
            {
                value = s.substring("pitch".length()).trim();
                
                if(value.isEmpty())
                {
                    RecipeErrorReporter.error("Flag " + type + " has 'pitch' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
                
                try
                {
                    setPitch(Float.valueOf(value));
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid 'pitch' argument number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                    return false;
                }
            }
            else
            {
                RecipeErrorReporter.warning("Flag " + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }
        
        if(getSound() == null)
        {
            RecipeErrorReporter.error("Flag " + type + " doesn't have the 'play' argument!", "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
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
                p.playSound(a.hasLocation() ? a.location() : p.getLocation(), sound, volume, pitch);
            }
        }
        else
        {
            Location l = a.getLocation();
            
            if(l != null)
            {
                l.getWorld().playSound(l, sound, volume, pitch);
            }
        }
    }
}
