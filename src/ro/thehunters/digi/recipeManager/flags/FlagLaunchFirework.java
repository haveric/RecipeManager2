package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagLaunchFirework extends Flag
{
    private FireworkMeta firework;
    
    public FlagLaunchFirework()
    {
        type = FlagType.LAUNCHFIREWORK;
    }
    
    @Override
    public FlagLaunchFirework clone()
    {
        FlagLaunchFirework clone = new FlagLaunchFirework();
        
        clone.firework = firework.clone();
        
        return clone;
    }
    
    public FireworkMeta getFirework()
    {
        return firework;
    }
    
    public void setFirework(FireworkMeta firework)
    {
        this.firework = firework;
    }
    
    @Override
    public void onApply(Arguments a)
    {
        if(a.location() != null && firework != null)
        {
            Firework ent = (Firework)a.location().getWorld().spawnEntity(a.location(), EntityType.FIREWORK);
            ent.setFireworkMeta(firework);
        }
    }
    
    @Override
    public boolean onParse(String value)
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
        else
        {
            RecipeErrorReporter.warning("Flag " + type + " has unknown value: " + value);
            return false;
        }
        
        return true;
    }
}