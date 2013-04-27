package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;

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
            "{flag} <text>",
        };
        
        D = new String[]
        {
            "Adds a line to result's lore (description)",
            "Supports colors (e.g. <red>, <blue>, &4, &F, etc).",
        };
        
        E = new String[]
        {
            "{flag} <red>Awesome item",
            "{flag} <magic>some scrambled text on line 2",
        };
    }
    
    // Flag code
    
    private List<String> lore = new ArrayList<String>();
    
    public FlagItemLore()
    {
    }
    
    public FlagItemLore(FlagItemLore flag)
    {
        lore.addAll(flag.lore);
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
    
    public List<String> getLore()
    {
        return lore;
    }
    
    public void setLore(List<String> lore)
    {
        Validate.notNull(lore, "The 'lore' argument must not be null!");
        
        this.lore.clear();
        
        for(String value : lore)
        {
            addLore(value);
        }
    }
    
    public void addLore(String value)
    {
        lore.add(Tools.parseColors(value, false));
    }
    
    @Override
    protected boolean onParse(String value)
    {
        addLore(value);
        
        return true;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(!a.hasResult())
        {
            a.addCustomReason("Need result!");
            return;
        }
        
        ItemMeta meta = a.result().getItemMeta();
        
        if(meta.hasLore())
        {
            meta.getLore().addAll(this.lore);
        }
        else
        {
            meta.setLore(this.lore);
        }
        
        a.result().setItemMeta(meta);
    }
}
