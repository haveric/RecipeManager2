package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Vanilla;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class SmeltRecipe extends BaseRecipe
{
    private ItemStack ingredient;
    private ItemStack fuel;
    private ItemStack result;
    private float     minTime = Vanilla.FURNACE_RECIPE_TIME;
    private float     maxTime = -1;
    private int       hash;
    
    public SmeltRecipe()
    {
    }
    
    public SmeltRecipe(BaseRecipe recipe)
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
        if(result instanceof ItemResult)
            this.result = ((ItemResult)result).setRecipe(this);
        else
            this.result = new ItemResult(result).setRecipe(this);
    }
    
    public ItemStack getFuel()
    {
        return fuel;
    }
    
    public void setFuel(ItemStack fuel)
    {
        this.fuel = fuel;
    }
    
    public boolean hasCustomTime()
    {
        return minTime != Vanilla.FURNACE_RECIPE_TIME;
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
        return (maxTime > minTime ? minTime + (maxTime - minTime) * RecipeManager.random.nextFloat() : minTime);
    }
    
    public int getCookTicks()
    {
        return Math.round(getCookTime() * 20);
    }
    
    public int getIndex()
    {
        return ingredient.getTypeId();
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
    
    public boolean hasFuel()
    {
        return fuel != null;
    }
    
    public boolean hasResult()
    {
        return result != null;
    }
    
    @Override
    public boolean isValid()
    {
        return hasIngredient() && (hasFlag(FlagType.REMOVE) ? true : hasResult());
    }
    
    @Override
    public RecipeType getRecipeType()
    {
        return RecipeType.SMELT;
    }
}
