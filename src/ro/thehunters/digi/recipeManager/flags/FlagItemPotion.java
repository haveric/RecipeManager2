package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemPotion extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} < ??? >";
        
        D = new String[1];
        D[0] = "Flag not yet documented.";
        
        E = null;
    }
    
    // Flag code
    
    public FlagItemPotion()
    {
        type = FlagType.ITEMPOTION;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof PotionMeta == false)
        {
            return RecipeErrorReporter.error("Flag " + type + " needs a POTION item!");
        }
        
        return true;
    }
    
    @Override
    public void onRemove()
    {
        ItemResult result = getResult();
        PotionMeta potion = (PotionMeta)result.getItemMeta();
        potion.setMainEffect(null);
        potion.clearCustomEffects();
        result.setItemMeta(potion);
        result.setDurability((short)0);
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        PotionMeta potion = (PotionMeta)result.getItemMeta();
        
        if(value.startsWith("custom"))
        {
            String[] split = value.split(" ", 2);
            
            if(split.length != 2)
            {
                return RecipeErrorReporter.error("Flag " + type + " has 'custom' argument with no values!");
            }
            
            value = split[1].trim();
            PotionEffect effect = Tools.parsePotionEffect(value, type);
            
            if(effect != null)
            {
                potion.addCustomEffect(effect, true);
                result.setItemMeta(potion);
            }
        }
        else
        {
            Potion p = Tools.parsePotion(value, type);
            
            if(p != null)
            {
                result.setDurability(p.toDamageValue());
            }
        }
        
        return true;
    }
}