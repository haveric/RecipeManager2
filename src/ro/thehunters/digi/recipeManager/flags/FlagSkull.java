package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.SkullMeta;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagSkull extends Flag
{
    public FlagSkull()
    {
        type = FlagType.SKULL;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getItemMeta() instanceof SkullMeta == false || result.getDurability() != 3)
        {
            RecipeErrorReporter.error("Flag @" + type + " needs a SKULL_ITEM with data value 3 to work!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onRemove()
    {
        onParse(null);
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        SkullMeta skull = (SkullMeta)result.getItemMeta();
        skull.setOwner(value);
        result.setItemMeta(skull);
        return true;
    }
}