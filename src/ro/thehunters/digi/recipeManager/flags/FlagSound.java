package ro.thehunters.digi.recipeManager.flags;

import org.apache.commons.lang.Validate;
import org.bukkit.Sound;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;

public class FlagSound extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.SOUND;
        
        A = new String[]
        {
            "{flag} <sound> | [arguments]",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Plays a sound at crafting location.",
            "Using this flag more than once will overwrite the previous flag.",
            "",
            "The <sound> argument must be a sound name, you can find them in '" + Files.FILE_INFO_NAMES + "' file at 'SOUND LIST' section.",
            "",
            "Optionally you can specify some arguments separated by | character:",
            "  volume <0.0 to 100.0> = (default 1.0) sound volume, if exeeeds 1.0 it extends range, each 1.0 extends range by about 10 blocks.",
            "  pitch <0.0 to 4.0>    = (default 0.0) sound pitch value.",
            "  player                = (default not set) if set it will only play the sound to the crafter.",
            "You can specify these arguments in any order and they're completly optional.",
            "",
            "Setting to 'false' will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} level_up",
            "{flag} wolf_howl | volume 5 // can be heard loudly at 50 blocks away",
            "{flag} portal_travel | player | volume 0.65 | pitch 3.33",
        };
    }
    
    // Flag code
    
    private Sound sound = null;
    private float volume = 1;
    private float pitch = 0;
    private boolean onlyPlayer = false;
    
    public FlagSound()
    {
    }
    
    public FlagSound(FlagSound flag)
    {
        sound = flag.sound;
        volume = flag.volume;
        pitch = flag.pitch;
        onlyPlayer = flag.onlyPlayer;
    }
    
    @Override
    public FlagSound clone()
    {
        return new FlagSound(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public Sound getSound()
    {
        return sound;
    }
    
    public void setSound(Sound sound)
    {
        Validate.notNull(sound, "The sound argument can not be null!");
        
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
            RecipeErrorReporter.warning("Flag " + getType() + " has invalid 'volume' number range, must be between 0.0 and 1.0, trimmed.");
            
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
            RecipeErrorReporter.warning("Flag " + getType() + " has invalid 'pitch' number range, must be between 0.0 and 4.0, trimmed.");
            
            this.pitch = Math.min(Math.max(pitch, 0.0f), 4.0f);
        }
        else
        {
            this.pitch = pitch;
        }
    }
    
    public boolean isOnlyPlayer()
    {
        return onlyPlayer;
    }
    
    public void setOnlyPlayer(boolean onlyPlayer)
    {
        this.onlyPlayer = onlyPlayer;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        value = split[0].trim().toUpperCase();
        
        try
        {
            setSound(Sound.valueOf(value));
        }
        catch(IllegalArgumentException e)
        {
            RecipeErrorReporter.error("Flag " + getType() + " has invalid sound name: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for sounds list.");
            return false;
        }
        
        if(split.length > 1)
        {
            for(int i = 1; i < split.length; i++)
            {
                value = split[i].trim();
                
                if(value.equals("player"))
                {
                    onlyPlayer = true;
                }
                else if(value.startsWith("volume"))
                {
                    value = value.substring("volume".length()).trim();
                    
                    if(value.isEmpty())
                    {
                        RecipeErrorReporter.error("Flag " + getType() + " has 'volume' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }
                    
                    try
                    {
                        setVolume(Float.valueOf(value));
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.error("Flag " + getType() + " has invalid 'volume' argument float number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }
                }
                else if(value.startsWith("pitch"))
                {
                    value = value.substring("pitch".length()).trim();
                    
                    if(value.isEmpty())
                    {
                        RecipeErrorReporter.error("Flag " + getType() + " has 'pitch' argument with number!", "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }
                    
                    try
                    {
                        setPitch(Float.valueOf(value));
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.error("Flag " + getType() + " has invalid 'pitch' argument number: " + value, "Read '" + Files.FILE_INFO_FLAGS + "' for argument info.");
                        return false;
                    }
                }
                else
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + value, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(onlyPlayer)
        {
            if(!a.hasPlayer())
            {
                a.addCustomReason("Needs player!");
                return;
            }
            
            a.player().playSound(a.hasLocation() ? a.location() : a.player().getLocation(), sound, volume, pitch);
        }
        else
        {
            if(!a.hasLocation())
            {
                a.addCustomReason("Needs location!");
                return;
            }
            
            a.location().getWorld().playSound(a.location(), sound, volume, pitch);
        }
    }
}
