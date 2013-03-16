package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagPotion extends Flag
{
    public FlagPotion()
    {
        type = FlagType.POTION;
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