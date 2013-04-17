package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemName extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.ITEMNAME;
        
        A = new String[]
        {
            "{flag} <text or false>",
        };
        
        D = new String[]
        {
            "Changes result's display name, supports colors (e.g. <red>, <blue>, &4, &F, etc)",
        };
        
        E = new String[]
        {
            "{flag} <yellow>Weird Item",
        };
    }
    
    // Flag code
    
    public FlagItemName()
    {
    }
    
    public FlagItemName(FlagItemName flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagItemName clone()
    {
        return new FlagItemName(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        ItemResult result = getResult();
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(value == null ? null : Tools.parseColors(value, false));
        result.setItemMeta(meta);
        return true;
    }
}
