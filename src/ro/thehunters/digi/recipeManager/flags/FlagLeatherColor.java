package ro.thehunters.digi.recipeManager.flags;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagLeatherColor extends Flag
{
    public FlagLeatherColor()
    {
        type = FlagType.LEATHERCOLOR;
    }
    
    public FlagLeatherColor(FlagLeatherColor flag)
    {
        this();
    }
    
    @Override
    public FlagLeatherColor clone()
    {
        return new FlagLeatherColor(this);
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof LeatherArmorMeta == false)
        {
            return RecipeErrorReporter.error("Flag " + type + " needs a leather armor item!");
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        Color color = Tools.parseColor(value);
        
        if(color == null)
        {
            return RecipeErrorReporter.error("Flag @" + type + " has invalid color numbers!", "Use 3 numbers ranging from 0 to 255, e.g. 255 128 0 for orange.");
        }
        
        return applyOnItem(getResult(), color);
    }
    
    private boolean applyOnItem(ItemStack item, Color color)
    {
        Validate.notNull(item);
        
        ItemMeta meta = item.getItemMeta();
        
        if(meta instanceof LeatherArmorMeta == false)
            return false;
        
        LeatherArmorMeta leather = (LeatherArmorMeta)meta;
        
        leather.setColor(color);
        
        item.setItemMeta(leather);
        
        return true;
    }
}