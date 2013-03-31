package ro.thehunters.digi.recipeManager.flags;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemColor extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} <R G B or false>";
        
        D = new String[4];
        D[0] = "Changes result's leather armor color, colors must be 3 numbers ranged from 0 to 255, the red, green and blue channels.";
        D[1] = "Setting it to false will reset leather color.";
        D[2] = null;
        D[3] = "Specific items: leather armor";
        
        E = new String[2];
        E[0] = "{flag} 255 100 50";
        E[1] = "{flag} false";
    }
    
    // Flag code
    
    public FlagItemColor()
    {
        type = FlagType.ITEMCOLOR;
    }
    
    public FlagItemColor(FlagItemColor flag)
    {
        this();
    }
    
    @Override
    public FlagItemColor clone()
    {
        return new FlagItemColor(this);
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