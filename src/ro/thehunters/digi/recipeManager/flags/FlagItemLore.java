package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemLore extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[1];
        A[0] = "{flag} <text or false>";
        
        D = new String[2];
        D[0] = "Adds a line to result's lore (description), supports colors (e.g. <red>, <blue>, &4, &F, etc).";
        D[1] = "Setting to false will remove all lines.";
        
        E = new String[2];
        E[0] = "{flag} <red>Awesome item";
        E[1] = "{flag} <magic>some scrambled text on line 2";
    }
    
    // Flag code
    
    public FlagItemLore()
    {
        type = FlagType.ITEMLORE;
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
            {
                lore = new ArrayList<String>();
            }
            
            lore.add(Tools.parseColors(value, false));
            
            meta.setLore(lore);
        }
        
        result.setItemMeta(meta);
        
        return true;
    }
}