package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.inventory.ItemStack;

public class RemoveResultRecipe extends BaseRecipe
{
    private ItemStack result;
    
    public RemoveResultRecipe()
    {
    }
    
    public RemoveResultRecipe(ItemStack result)
    {
        setResult(result);
    }
    
    public ItemStack getResult()
    {
        return result;
    }
    
    public void setResult(ItemStack result)
    {
        this.result = result;
    }
}
