package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagEnchantBook extends Flag
{
    public FlagEnchantBook()
    {
        type = FlagType.ENCHANTBOOK;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof EnchantmentStorageMeta == false)
        {
            RecipeErrorReporter.error("Flag " + type + " needs an enchantable book!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onRemove()
    {
        ItemResult result = getResult();
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)result.getItemMeta();
        meta.getStoredEnchants().clear();
        result.setItemMeta(meta);
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)result.getItemMeta();
        
        String[] split = value.split(" ");
        value = split[0].trim();
        
        Enchantment ench = Enchantment.getByName(value);
        
        if(ench == null)
        {
            RecipeErrorReporter.error("Flag " + type + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
            return false;
        }
        
        int level = ench.getStartLevel();
        
        if(split.length > 1)
        {
            value = split[1].trim();
            
            if(!value.equalsIgnoreCase("max"))
            {
                try
                {
                    level = Integer.valueOf(value);
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + type + " has invalid enchantment level number!");
                    return false;
                }
            }
            else
            {
                level = ench.getMaxLevel();
            }
        }
        
        meta.addStoredEnchant(ench, level, true);
        result.setItemMeta(meta);
        return true;
    }
}