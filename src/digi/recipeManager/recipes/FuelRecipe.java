package digi.recipeManager.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.RecipeManager;
import digi.recipeManager.recipes.flags.Flags;

public class FuelRecipe extends RmRecipe
{
    private ItemStack ingredient;
    private float     minTime;
    private float     maxTime;
    
    public FuelRecipe(Material type, int burnTime)
    {
        setIngredient(new ItemStack(type));
        setMinTime(burnTime);
    }
    
    public FuelRecipe(Material type, short data, int burnTime)
    {
        setIngredient(new ItemStack(type, 1, data));
        setMinTime(burnTime);
    }
    
    public FuelRecipe(RmRecipe recipe)
    {
        super(recipe);
    }
    
    public FuelRecipe(Flags flags)
    {
        super(flags);
    }
    
    public ItemStack getIngredient()
    {
        return ingredient;
    }
    
    public void setIngredient(ItemStack ingredient)
    {
        this.ingredient = ingredient;
        
        hash = ("fuel" + ingredient.getTypeId() + ":" + ingredient.getDurability()).hashCode();
    }
    
    public float getMinTime()
    {
        return minTime;
    }
    
    public void setMinTime(float minTime)
    {
        this.minTime = minTime;
    }
    
    public float getMaxTime()
    {
        return maxTime;
    }
    
    public void setMaxTime(float maxTime)
    {
        this.maxTime = maxTime;
    }
    
    public int getBurnTicks()
    {
        return (int)Math.round(20.0 * (maxTime > minTime ? minTime + (maxTime - minTime) * RecipeManager.rand.nextFloat() : minTime));
    }
    
    public boolean hasIngredient()
    {
        return ingredient != null;
    }
    
    @Override
    public boolean isValid()
    {
        return hasIngredient();
    }
}
