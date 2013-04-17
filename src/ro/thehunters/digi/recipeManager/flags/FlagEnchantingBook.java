package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagEnchantingBook extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.ENCHANTENGBOOK;
        
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
    
    private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>(); // TODO
    
    public FlagEnchantingBook()
    {
    }
    
    public FlagEnchantingBook(FlagEnchantingBook flag)
    {
        // TODO clone
    }
    
    @Override
    public Flag clone()
    {
        return new FlagEnchantingBook(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof EnchantmentStorageMeta == false)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs an enchantable book!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onRemove()
    {
        ItemResult result = getResult();
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)result.getItemMeta();
        meta.getStoredEnchants().clear();
        result.setItemMeta(meta);
    }
    
    @Override
    protected boolean onParse(String value)
    {
        ItemResult result = getResult();
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)result.getItemMeta();
        
        String[] split = value.split(" ");
        value = split[0].trim();
        
        Enchantment ench = Enchantment.getByName(value);
        
        if(ench == null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " has invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
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
                    RecipeErrorReporter.error("Flag " + getType() + " has invalid enchantment level number!");
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
