package digi.recipeManager.data;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.RecipeManager;

public class SmeltRecipe extends RmRecipe
{
    private ItemStack ingredient;
    private ItemStack result;
    private float     minTime;
    private float     maxTime;
    private int       hash;
    
    public SmeltRecipe(RmRecipe recipe)
    {
        super(recipe);
    }
    
    public SmeltRecipe(Flags flags)
    {
        super(flags);
    }
    
    public SmeltRecipe(FurnaceRecipe recipe)
    {
        setIngredient(recipe.getInput());
        setResult(recipe.getResult());
    }
    
    public ItemStack getIngredient()
    {
        return ingredient;
    }
    
    public void setIngredient(ItemStack ingredient)
    {
        this.ingredient = ingredient;
        
        hash = ("smelt" + ingredient.getTypeId() + ":" + ingredient.getDurability()).hashCode();
    }
    
    public ItemStack getResult()
    {
        return result;
    }
    
    public void setResult(ItemStack result)
    {
        this.result = result;
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
    
    public float getCookTime()
    {
        return (float)(maxTime > minTime ? minTime + (maxTime - minTime) * RecipeManager.rand.nextFloat() : minTime);
    }
    
    public float getCookTicks()
    {
        return Math.round(20.0 / getCookTime());
    }
    
    @Override
    public int hashCode()
    {
        return hash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj == null || obj instanceof SmeltRecipe == false)
            return false;
        
        if(hash != ((SmeltRecipe)obj).hashCode())
            return false;
        
        return true;
    }
    
    public FurnaceRecipe toFurnaceRecipe()
    {
        return new FurnaceRecipe(result, ingredient.getType(), ingredient.getDurability());
    }
    
    public boolean hasIngredient()
    {
        return ingredient != null;
    }
    
    public boolean hasResult()
    {
        return result != null;
    }
    
    @Override
    public boolean isValid()
    {
        return hasIngredient() && (getFlags().isRemove() ? true : hasResult());
    }
}
