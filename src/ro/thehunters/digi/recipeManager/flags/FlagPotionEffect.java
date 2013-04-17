package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;

public class FlagPotionEffect extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.POTIONEFFECT;
        
        A = new String[]
        {
            "{flag} <effect type> | [arguments]",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Adds potion effects to crafter.",
            "This flag can be used more than once to add more effects.",
            "",
            "The <effect type> argument must be an effect type, names for them can be found in '" + Files.FILE_INFO_NAMES + "' file at 'POTION EFFECT TYPE'.",
            "",
            "Optionally you can add more arguments separated by | character in any order:",
            "  duration <float>    = (default 3.0) potion effect duration in seconds, only works on non-instant effect types.",
            "  amplifier <num>     = (default 0) potion effect amplifier.",
            "  chance <0.01-100>%  = (default 100%) chance that the effect will be applied, this chance is individual for this effect.",
            "  morefx              = (default not set) more ambient particle effects, more screen intrusive.",
            "",
            "Setting to 'false' will remove the flag.",
        };
        
        E = new String[]
        {
            "{flag} heal",
            "{flag} blindness | duration 60 | amplifier 5",
            "{flag} poison | chance 6.66% | morefx | amplifier 666 | duration 6.66",
        };
    }
    
    // Flag code
    
    private Map<PotionEffect, Float> effects = new HashMap<PotionEffect, Float>();
    
    public FlagPotionEffect()
    {
    }
    
    public FlagPotionEffect(FlagPotionEffect flag)
    {
        effects.putAll(flag.effects);
    }
    
    @Override
    public FlagPotionEffect clone()
    {
        return new FlagPotionEffect(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public Map<PotionEffect, Float> getEffects()
    {
        return effects;
    }
    
    public void setEffects(Map<PotionEffect, Float> effects)
    {
        if(effects == null)
        {
            this.remove();
        }
        else
        {
            this.effects = effects;
        }
    }
    
    public void addEffect(PotionEffect effect)
    {
        addEffect(effect, 100);
    }
    
    public void addEffect(PotionEffect effect, float chance)
    {
        effects.put(effect, chance);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        value = split[0].trim();
        PotionEffectType type = PotionEffectType.getByName(value);
        int amplifier = 0;
        float chance = 100.0f;
        float duration = 3.0f;
        boolean morefx = false;
        
        if(type == null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " has invalid effect type: " + value);
            return false;
        }
        
        if(split.length > 1)
        {
            for(int i = 1; i < split.length; i++)
            {
                value = split[i].toLowerCase().trim();
                
                if(value.equals("morefx"))
                {
                    morefx = true;
                }
                else if(value.startsWith("chance"))
                {
                    value = value.substring("chance".length()).trim();
                    
                    if(value.charAt(value.length() - 1) == '%')
                    {
                        value = value.substring(0, value.length() - 1);
                    }
                    
                    try
                    {
                        chance = Float.valueOf(value);
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has invalid chance value number: " + value);
                        continue;
                    }
                    
                    if(chance < 0.01f || chance > 100.0f)
                    {
                        chance = Math.min(Math.max(chance, 0.01f), 100.0f);
                        
                        RecipeErrorReporter.warning("Flag " + getType() + " has chance value less than 0.01 or higher than 100.0, value trimmed.");
                    }
                }
                else if(value.startsWith("amplifier"))
                {
                    value = value.substring("amplifier".length()).trim();
                    
                    try
                    {
                        amplifier = Integer.valueOf(value);
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has invalid amplifier value number: " + value);
                    }
                }
                else if(value.startsWith("duration"))
                {
                    if(type.isInstant())
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has effect type '" + type.toString() + "' which is instant, it can't have duration, ignored.");
                        continue;
                    }
                    
                    value = value.substring("duration".length()).trim();
                    
                    // TODO type.getDurationModifier() !!!
                    
                    try
                    {
                        duration = Float.valueOf(value);
                    }
                    catch(NumberFormatException e)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has invalid duration value number: " + value);
                    }
                }
            }
        }
        
        PotionEffect effect = new PotionEffect(type, (int)Math.ceil(duration * 20.0), amplifier, morefx);
        
        addEffect(effect, chance);
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        if(!a.hasPlayer())
        {
            a.addCustomReason("Need player!");
            return;
        }
        
        for(Entry<PotionEffect, Float> e : effects.entrySet())
        {
            if(e.getValue() == 100 || e.getValue() >= (RecipeManager.random.nextFloat() * 100))
            {
                e.getKey().apply(a.player());
            }
        }
    }
}
