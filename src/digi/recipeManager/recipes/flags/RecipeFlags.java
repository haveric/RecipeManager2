package digi.recipeManager.recipes.flags;

import org.bukkit.inventory.ItemStack;

public class RecipeFlags extends Flags
{
    private String    info          = null;
    private String    failMessage   = null;
    private ItemStack displayItem   = null;
    private boolean   hideUnallowed = false;
    private String    remove        = null;
    private boolean   override      = false;
    
    public RecipeFlags()
    {
    }
    
    public RecipeFlags(Flags flags)
    {
        super(flags);
        
        if(flags instanceof RecipeFlags)
        {
            RecipeFlags f = (RecipeFlags)flags;
            
            info = f.info;
            failMessage = f.failMessage;
            displayItem = f.displayItem;
            hideUnallowed = f.hideUnallowed;
            remove = f.remove;
            override = f.override;
        }
    }
    
    @Override
    public RecipeFlags clone()
    {
        return new RecipeFlags(this);
    }
    
    public String getInfo()
    {
        return info;
    }
    
    public void setInfo(String info)
    {
        this.info = info;
    }
    
    public String getFailMessage()
    {
        return failMessage;
    }
    
    public void setFailMessage(String failMessage)
    {
        this.failMessage = failMessage;
    }
    
    public ItemStack getDisplayItem()
    {
        return displayItem;
    }
    
    public void setDisplayItem(ItemStack displayItem)
    {
        this.displayItem = displayItem;
    }
    
    public boolean isHideUnallowed()
    {
        return hideUnallowed;
    }
    
    public void setHideUnallowed(boolean hideUnallowed)
    {
        this.hideUnallowed = hideUnallowed;
    }
    
    public boolean isOverride()
    {
        return override;
    }
    
    public void setOverride(boolean override)
    {
        this.override = override;
    }
    
    public boolean isRemove()
    {
        return remove != null;
    }
    
    public void setRemove(String message)
    {
        remove = message;
    }
}