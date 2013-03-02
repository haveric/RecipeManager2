package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagLore extends Flag
{
    public FlagLore()
    {
        type = FlagType.LORE;
    }
    
    @Override
    public void onRemove()
    {
        onParse(null);
        
        // TODO REMOVE:
        System.out.print("FlagLore :: onRemove() " + getResult());
    }
    
    @Override
    public boolean onParse(String value)
    {
        ItemResult result = getResult();
        ItemMeta meta = result.getItemMeta();
        
        if(value == null)
        {
            meta.setLore(null);
        }
        else
        {
            List<String> lore = meta.getLore();
            
            if(lore == null || lore.isEmpty())
                lore = new ArrayList<String>();
            
            lore.add(Tools.parseColors(value, false));
            
            meta.setLore(lore);
        }
        
        result.setItemMeta(meta);
        
        return true;
    }
}