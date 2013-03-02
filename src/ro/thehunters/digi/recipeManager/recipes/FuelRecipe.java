package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.flags.Flags;


public class FuelRecipe extends BaseRecipe
{
    private ItemStack ingredient;
    private float     minTime;
    private float     maxTime;
    
    public FuelRecipe(Material type, float burnTime)
    {
        setIngredient(new ItemStack(type, 1, (short)-1));
        setMinTime(burnTime);
    }
    
    public FuelRecipe(Material type, float minTime, float maxTime)
    {
        setIngredient(new ItemStack(type, 1, (short)-1));
        setMinTime(minTime);
        setMaxTime(maxTime);
    }
    
    public FuelRecipe(Material type, short data, float burnTime)
    {
        setIngredient(new ItemStack(type, 1, data));
        setMinTime(burnTime);
    }
    
    public FuelRecipe(Material type, short data, float minTime, float maxTime)
    {
        setIngredient(new ItemStack(type, 1, data));
        setMinTime(minTime);
        setMaxTime(maxTime);
    }
    
    public FuelRecipe(BaseRecipe recipe)
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
    
    /**
     * Set minimum time it can burn (or fixed if max not defined).
     * 
     * @param minTime
     *            float value in seconds
     */
    public void setMinTime(float minTime)
    {
        this.minTime = minTime;
    }
    
    public float getMaxTime()
    {
        return maxTime;
    }
    
    /**
     * Set maximum time it can burn.<br>
     * NOTE: minimum time must be smaller than this and higher than -1
     * 
     * @param maxTime
     *            float value in seconds
     */
    public void setMaxTime(float maxTime)
    {
        this.maxTime = maxTime;
    }
    
    /**
     * Get the burn time value, randomized if supported, in ticks (multiplied by 20).
     * 
     * @return burn time in ticks
     */
    public int getBurnTicks()
    {
        return (int)Math.round(20.0 * (maxTime > minTime ? minTime + (maxTime - minTime) * RecipeManager.random.nextFloat() : minTime));
    }
    
    public String getIndexString()
    {
        return ingredient.getTypeId() + (ingredient.getDurability() == -1 ? "" : ":" + ingredient.getDurability());
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
    
    @Override
    public RecipeType getRecipeType()
    {
        return RecipeType.FUEL;
    }
}
