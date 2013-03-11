package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Color;
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
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof LeatherArmorMeta == false)
        {
            RecipeErrorReporter.error("Flag " + type + " needs a leather armor item!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        Color color = Tools.parseColor(value);
        
        if(color == null)
        {
            RecipeErrorReporter.error("Flag @" + type + " has invalid color numbers!", "Use 3 numbers ranging from 0 to 255, e.g. 255 128 0 for orange.");
            return false;
        }
        
        ItemResult result = getResult();
        LeatherArmorMeta meta = (LeatherArmorMeta)result.getItemMeta();
        meta.setColor(color);
        result.setItemMeta(meta);
        return true;
    }
}