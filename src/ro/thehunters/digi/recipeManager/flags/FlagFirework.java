package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagFirework extends Flag
{
    public FlagFirework()
    {
        type = FlagType.FIREWORK;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof FireworkMeta == false)
        {
            RecipeErrorReporter.error("Flag " + type + " needs a FIREWORK item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        FireworkMeta firework = (FireworkMeta)result.getItemMeta();
        String[] split;
        
        if(value.startsWith("effect"))
        {
            split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag @" + type + " has no arguments for 'effect' !");
                return false;
            }
            
            FireworkEffect effect = Tools.parseFireworkEffect(split[1].trim(), type);
            
            if(effect == null)
                return false;
            
            firework.addEffect(effect);
            result.setItemMeta(firework);
        }
        else if(value.startsWith("power"))
        {
            split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag @" + type + " has no arguments for 'power' !");
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
                RecipeErrorReporter.error("Flag @" + type + " invalid 'power' argument, it must be a number from 0 to 128");
                return false;
            }
            
            firework.setPower(power);
            result.setItemMeta(firework);
        }
        else
        {
            RecipeErrorReporter.warning("Flag @" + type + " has unknown argument: " + value);
        }
        
        return true;
    }
}