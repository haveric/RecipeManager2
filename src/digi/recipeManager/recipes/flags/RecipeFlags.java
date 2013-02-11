package digi.recipeManager.recipes.flags;

import org.bukkit.inventory.ItemStack;

public class RecipeFlags extends Flags
{
    private String    failMessage = null;
    private String    info        = null;
    private ItemStack displayItem = null;
    
    public RecipeFlags()
    {
    }
    
    public RecipeFlags(Flags flags)
    {
        super(flags);
        
        if(flags instanceof RecipeFlags)
        {
            RecipeFlags f = (RecipeFlags)flags;
            
            failMessage = f.failMessage;
            info = f.info;
        }
    }
    
    @Override
    public RecipeFlags clone()
    {
        return new RecipeFlags(this);
    }
    
    public String getFailMessage()
    {
        return failMessage;
    }
    
    public void setFailMessage(String failMessage)
    {
        this.failMessage = failMessage;
    }
    
    public String getInfo()
    {
        return info;
    }
    
    public void setInfo(String info)
    {
        this.info = info;
    }
}