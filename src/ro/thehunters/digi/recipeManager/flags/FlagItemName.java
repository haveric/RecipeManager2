package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.Tools;

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
    
    private String name;
    
    public FlagItemName()
    {
    }
    
    public FlagItemName(FlagItemName flag)
    {
        name = flag.name;
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
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        setName(value);
        return true;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        if(!a.hasResult())
        {
            a.addCustomReason("Needs result!");
            return;
        }
        
        ItemMeta meta = a.result().getItemMeta();
        
        meta.setDisplayName(getName() == null ? null : Tools.parseColors(getName(), false));
        
        a.result().setItemMeta(meta);
    }
}
