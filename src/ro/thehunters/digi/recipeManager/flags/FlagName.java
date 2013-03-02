package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagName extends Flag
{
    public FlagName()
    {
        type = FlagType.NAME;
    }
    
    @Override
    public void onRemove()
    {
        onParse(null);
        
        // TODO REMOVE:
        System.out.print("FlagName :: onRemove() " + getResult());
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(value == null ? null : Tools.parseColors(value, false));
        result.setItemMeta(meta);
        return true;
    }
}