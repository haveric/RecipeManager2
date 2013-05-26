package ro.thehunters.digi.recipeManager.flags;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagLaunchFirework extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.LAUNCHFIREWORK;
        
        A = new String[]
        {
            "{flag} effect <effects>",
            "{flag} power <number 0-128>",
        };
        
        D = new String[]
        {
            "Launches a firework from workbench/player/furnace when recipe or result item is crafted.",
            "This flag can be defined multiple times add effects and set power to the same rocket.",
            "",
            "The 'effect' setting adds an effect to the rocket.",
            "Replace <effects> with the effects separated by | character.",
            "Effects can be:",
            "  color <red> <green> <blue>, ...           = (Required at least 1 color) Sets the primary explosion color(s), you can define more colors separated by comma.",
            "  fadecolor <red> <green> <blue>, ...       = (Optional) Color(s) of the explosion fading, you can define more colors separated by comma.",
            "  type <explode type>                       = (Optional) Shape/size of explosion, can be: BALL, BALL_LARGE, BURST, CREEPER or STAR... or see " + Files.FILE_INFO_NAMES + " file.",
            "  trail                                     = (Optional) Adds a trail to the explosion",
            "  flicker                                   = (Optional) Adds a flicker to explosion",
            "",
            "Effects can be listed in any order.",
            "Colors must be 3 numbers ranging from 0 to 255, basic RGB format.",
            "",
            "The 'power <number 0-128>' value sets how long rocket will fly, each number is 0.5 seconds, values above 4 are NOT recommended because it heavily affects client performance.",
        };
        
        E = new String[]
        {
            "{flag} effect color 0 255 0",
            "{flag} effect trail | color 255 0 0 | type burst",
            "{flag} effect color 255 0 200, 0 255 0, 255 128 0 | trail | type ball_large | fadecolor 255 0 0, 0 0 255, 0 255 0",
            "{flag} power 2",
        };
    }
    
    // Flag code
    
    private FireworkMeta firework;
    private float chance = 100;
    
    public FlagLaunchFirework()
    {
    }
    
    public FlagLaunchFirework(FlagLaunchFirework flag)
    {
        firework = flag.firework.clone();
        chance = flag.chance;
    }
    
    @Override
    public FlagLaunchFirework clone()
    {
        return new FlagLaunchFirework(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public FireworkMeta getFirework()
    {
        return firework;
    }
    
    public void setFirework(FireworkMeta firework)
    {
        Validate.notNull(firework);
        
        this.firework = firework;
    }
    
    public float getChance()
    {
        return chance;
    }
    
    public void setChance(float chance)
    {
        this.chance = chance;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        if(firework == null)
        {
            firework = (FireworkMeta)Bukkit.getItemFactory().getItemMeta(Material.FIREWORK);
        }
        
        if(value.startsWith("effect"))
        {
            value = value.substring("effect".length()).trim();
            
            FireworkEffect effect = Tools.parseFireworkEffect(value, getType());
            
            if(effect != null)
            {
                firework.addEffect(effect);
            }
        }
        else if(value.startsWith("power"))
        {
            value = value.substring("power".length()).trim();
            int power = -1;
            
            try
            {
                power = Integer.valueOf(value);
            }
            catch(NumberFormatException e)
            {
            }
            
            if(power < 0 || power > 128)
            {
                ErrorReporter.error("Flag " + getType() + " invalid 'power' argument value: '" + value + "', it must be a number from 0 to 128");
                return false;
            }
            
            firework.setPower(power);
        }
        else if(value.startsWith("chance"))
        {
            value = value.substring("chance".length()).replace('%', ' ').trim();
            
            try
            {
                setChance(Float.valueOf(value));
            }
            catch(NumberFormatException e)
            {
            }
            
            if(getChance() < 0 || getChance() > 100)
            {
                ErrorReporter.error("Flag " + getType() + " invalid 'chance' argument value: '" + value + "', it must be a number from 0 to 100");
                return false;
            }
        }
        else
        {
            ErrorReporter.warning("Flag " + getType() + " has unknown argument: " + value);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCrafted(Args a)
    {
        Validate.notNull(firework);
        
        if(!a.hasLocation())
        {
            return;
        }
        
        if(chance >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance)
        {
            Firework ent = (Firework)a.location().getWorld().spawnEntity(a.location(), EntityType.FIREWORK);
            
            ent.setFireworkMeta(firework);
        }
    }
}
