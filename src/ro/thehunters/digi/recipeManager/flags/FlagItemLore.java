package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagItemLore extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.ITEMLORE;
        
        A = new String[]
        {
            "{flag} <text or false>",
        };
        
        D = new String[]
        {
            "Adds a line to result's lore (description), supports colors (e.g. <red>, <blue>, &4, &F, etc).",
        };
        
        E = new String[]
        {
            "{flag} <red>Awesome item",
            "{flag} <magic>some scrambled text on line 2",
        };
    }
    
    // Flag code
    
    private List<String> lore = new ArrayList<String>(); // TODO
    
    public FlagItemLore()
    {
    }
    
    public FlagItemLore(FlagItemLore flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagItemLore clone()
    {
        return new FlagItemLore(this);
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
