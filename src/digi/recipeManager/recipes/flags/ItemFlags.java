package digi.recipeManager.recipes.flags;

public class ItemFlags extends Flags
{
/*
    private String             itemName      = null;
    private String[]           itemLore      = null;
    private Color              itemColor     = null;
    private List<String>       itemMeta      = null;
    private String             itemBookTitle = null;
    private List<String>       itemBookPages = null;
    private String             itemFireworks = null;
    private String             itemMapSize   = null;
    private String             itemEnchants  = null;
*/  
    
    public ItemFlags()
    {
    }
    
    public ItemFlags(Flags flags)
    {
        super(flags);
        
        if(flags instanceof ItemFlags)
        {
            ItemFlags f = (ItemFlags)flags;
        }
    }
    
    @Override
    public ItemFlags clone()
    {
        return new ItemFlags(this);
    }
}