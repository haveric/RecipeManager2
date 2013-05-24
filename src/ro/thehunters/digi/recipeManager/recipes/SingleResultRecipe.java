package ro.thehunters.digi.recipeManager.recipes;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class SingleResultRecipe extends BaseRecipe
{
    private ItemResult result;
    
    protected SingleResultRecipe()
    {
    }
    
    public SingleResultRecipe(BaseRecipe recipe)
    {
        super(recipe);
        
        if(recipe instanceof SingleResultRecipe)
        {
            SingleResultRecipe r = (SingleResultRecipe)recipe;
            
            result = r.getResult();
        }
    }
    
    public SingleResultRecipe(Flags flags)
    {
        super(flags);
    }
    
    /**
     * @return result as clone
     */
    public ItemResult getResult()
    {
        return result == null ? null : result.clone();
    }
    
    /**
     * @param a
     * @return result as clone or null if failed by chance or failed by flag check
     */
    public ItemResult getResult(Args a)
    {
        if(result == null)
        {
            return null;
        }
        
        float rand = RecipeManager.random.nextFloat() * 100f;
        ItemResult r = (result.getChance() >= rand ? result.clone() : null);
        
        return r;
    }
    
    public void setResult(ItemStack result)
    {
        Validate.notNull(result);
        
        if(result instanceof ItemResult)
        {
            this.result = ((ItemResult)result).setRecipe(this);
        }
        else
        {
            this.result = new ItemResult(result).setRecipe(this);
        }
    }
    
    public boolean hasResult()
    {
        return result != null;
    }
    
    public String getResultString()
    {
        StringBuilder s = new StringBuilder();
        
        if(result != null)
        {
            if(result.getAmount() > 1)
            {
                s.append("x").append(result.getAmount()).append(" ");
            }
            
            s.append(result.getType().toString().toLowerCase());
            
            if(result.getDurability() != 0)
            {
                s.append(":").append(result.getDurability());
            }
        }
        
        return s.toString();
    }
}
