package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagFireworkItem extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.FIREWORKITEM;
        
        A = new String[]
        {
            "{flag} ...",
        };
        
        D = new String[]
        {
            "FLAG NOT IMPLEMENTED",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    private FireworkMeta fwMeta; // TODO
    
    public FlagFireworkItem()
    {
    }
    
    public FlagFireworkItem(FlagFireworkItem flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagFireworkItem clone()
    {
        return new FlagFireworkItem(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof FireworkMeta == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs a FIREWORK item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        FireworkMeta firework = (FireworkMeta)result.getItemMeta();
        
        value = value.toLowerCase();
        
        if(value.startsWith("effect"))
        {
            String[] split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag " + getType() + " has no arguments for 'effect' !");
                return false;
            }
            
            FireworkEffect effect = Tools.parseFireworkEffect(split[1].trim(), getType());
            
            if(effect == null)
            {
                return false;
            }
            
            firework.addEffect(effect);
            result.setItemMeta(firework);
        }
        else if(value.startsWith("power"))
        {
            String[] split = value.split(" ", 2);
            
            if(split.length <= 1)
            {
                RecipeErrorReporter.error("Flag " + getType() + " has no arguments for 'power' !");
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
                RecipeErrorReporter.error("Flag " + getType() + " invalid 'power' argument, it must be a number from 0 to 128");
                return false;
            }
            
            firework.setPower(power);
            result.setItemMeta(firework);
        }
        else
        {
            RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + value);
        }
        
        return true;
    }
}
