package ro.thehunters.digi.recipeManager.flags;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagLaunchFirework extends Flag
{
    private FireworkMeta firework;
    private float        chance = 100;
    
    public FlagLaunchFirework()
    {
        type = FlagType.LAUNCHFIREWORK;
    }
    
    public FlagLaunchFirework(FlagLaunchFirework flag)
    {
        this();
        
        firework = flag.firework.clone();
    }
    
    @Override
    public FlagLaunchFirework clone()
    {
        return new FlagLaunchFirework(this);
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
            firework = (FireworkMeta)Bukkit.getItemFactory().getItemMeta(Material.FIREWORK);
        
        String[] split;
        
        if(value.startsWith("effect"))
        {
            split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag " + type + " has no arguments for 'effect' !");
                return false;
            }
            
            FireworkEffect effect = Tools.parseFireworkEffect(split[1].trim(), type);
            
            if(effect != null)
                firework.addEffect(effect);
        }
        else if(value.startsWith("power"))
        {
            split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag " + type + " has no arguments for 'power' !");
                return false;
            }
            
            int power = -1;
            
            try
            {
                power = Integer.valueOf(split[1].trim());
            }
            catch(Exception e)
            {
            }
            
            if(power < 0 || power > 128)
            {
                RecipeErrorReporter.error("Flag " + type + " invalid 'power' argument, it must be a number from 0 to 128");
                return false;
            }
            
            firework.setPower(power);
        }
        else if(value.startsWith("chance"))
        {
            split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag " + type + " has no arguments for 'chance' !");
                return false;
            }
            
            value = split[1].replace('%', ' ').trim();
            
            try
            {
                setChance(Float.valueOf(value));
            }
            catch(Exception e)
            {
            }
            
            if(getChance() < 0 || getChance() > 100)
            {
                RecipeErrorReporter.error("Flag " + type + " invalid 'chance' argument, it must be a number from 0 to 100");
                return false;
            }
        }
        else
        {
            RecipeErrorReporter.warning("Flag " + type + " has unknown argument: " + value);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        Validate.notNull(firework);
        
        if(a.hasLocation())
            return false;
        
        if(chance >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance)
        {
            Firework ent = (Firework)a.location().getWorld().spawnEntity(a.location(), EntityType.FIREWORK);
            ent.setFireworkMeta(firework);
        }
        
        return true;
    }
}