package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagFireworkChargeItem extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.FIREWORKCHARGEITEM;
        
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
    
    private FireworkEffect effect; // TODO
    
    public FlagFireworkChargeItem()
    {
    }
    
    public FlagFireworkChargeItem(FlagFireworkChargeItem flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagFireworkChargeItem clone()
    {
        return new FlagFireworkChargeItem(this);
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
        
        if(result == null || result.getItemMeta() instanceof FireworkEffectMeta == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs a FIREWORK_CHARGE item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        FireworkEffect effect = Tools.parseFireworkEffect(value, getType());
        
        if(effect != null)
        {
            ItemResult result = getResult();
            FireworkEffectMeta firework = (FireworkEffectMeta)result.getItemMeta();
            firework.setEffect(effect);
            result.setItemMeta(firework);
        }
        
        return true;
    }
}
