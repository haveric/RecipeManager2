package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemName extends Flag
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
        D[0] = "Changes result's display name, supports colors (e.g. <red>, <blue>, &4, &F, etc)";
        D[1] = "Setting to false will remove custom display name.";
        
        E = null;
    }
    
    // Flag code
    
    public FlagItemName()
    {
        type = FlagType.ITEMNAME;
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