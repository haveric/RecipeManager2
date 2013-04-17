package ro.thehunters.digi.recipeManager.recipes;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Vanilla;
import ro.thehunters.digi.recipeManager.flags.FlagDescription;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class SmeltRecipe extends MultiResultRecipe
{
    private ItemStack ingredient;
    private ItemResult fuel;
//    private ItemResult result;
    private float minTime = Vanilla.FURNACE_RECIPE_TIME;
    private float maxTime = -1;
    private int hash;
    
    private FurnaceRecipe bukkitRecipe;
    
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
    
    /*
    public ItemResult getResult()
    {
        return result;
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
    */
    
    public ItemResult getFuel()
    {
        return fuel;
    }
    
    public void setFuel(ItemStack fuel)
    {
        Validate.notNull(fuel);
        
        if(fuel instanceof ItemResult)
        {
            this.fuel = ((ItemResult)fuel).setRecipe(this);
        }
        else
        {
            this.fuel = new ItemResult(fuel).setRecipe(this);
        }
    }
    
    public boolean hasCustomTime()
    {
        return minTime != Vanilla.FURNACE_RECIPE_TIME;
    }
    
    public float getMinTime()
    {
        return minTime;
    }
    
    /**
     * @param minTime
     *            min random time range (seconds)
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
     * @param maxTime
     *            max random time range (seconds) or set to -1 to disable
     */
    public void setMaxTime(float maxTime)
    {
        this.maxTime = maxTime;
    }
    
    /**
     * @return if recipe has random time range
     */
    public boolean hasRandomTime()
    {
        return (maxTime > minTime);
    }
    
    /**
     * @return min time or if hasRandomTime() gets a random between min and max time.
     */
    public float getCookTime()
    {
        return (hasRandomTime() ? minTime + ((maxTime - minTime) * RecipeManager.random.nextFloat()) : minTime);
    }
    
    /**
     * @return getCookTime() multiplied by 20.0 and rounded
     */
    public int getCookTicks()
    {
        return Math.round(getCookTime() * 20.0f);
    }
    
    @Override
    public int getIndex()
    {
        return ingredient.getTypeId();
    }
    
    public String getFuelIndex()
    {
        return fuel.getTypeId() + (fuel.getDurability() == Vanilla.DATA_WILDCARD ? "" : ":" + fuel.getDurability());
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
        {
            return true;
        }
        
        if(obj == null || obj instanceof SmeltRecipe == false)
        {
            return false;
        }
        
        if(hash != ((SmeltRecipe)obj).hashCode())
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    public FurnaceRecipe getBukkitRecipe()
    {
        return bukkitRecipe == null ? toFurnaceRecipe() : bukkitRecipe;
    }
    
    public FurnaceRecipe toFurnaceRecipe()
    {
        return new FurnaceRecipe(getFirstResult(), ingredient.getType(), ingredient.getDurability());
    }
    
    public boolean hasIngredient()
    {
        return ingredient != null;
    }
    
    public boolean hasFuel()
    {
        return fuel != null;
    }
    
    /*
    public boolean hasResult()
    {
        return result != null;
    }
    */
    
    @Override
    public boolean isValid()
    {
        return hasIngredient() && (hasFlag(FlagType.REMOVE) ? true : hasResults());
    }
    
    @Override
    public RecipeType getType()
    {
        return RecipeType.SMELT;
    }
    
    @Override
    public String printBookIndex()
    {
        return Tools.getItemName(getFirstResult());
    }
    
    @Override
    public String printBook()
    {
        StringBuilder s = new StringBuilder(256);
        
        s.append(Messages.RECIPEBOOK_HEADER_SMELT.get());
        s.append('\n').append(Tools.printItem(getFirstResult(), ChatColor.DARK_GREEN, null, true));
        
        if(isMultiResult())
        {
            s.append('\n').append(Messages.RECIPEBOOK_MORERESULTS.get("{amount}", (getResults().size() - 1)));
        }
        
        if(hasFlag(FlagType.DESCRIPTION))
        {
            s.append('\n').append(ChatColor.DARK_BLUE).append(Tools.parseColors(getFlag(FlagDescription.class).getDescription(), false));
        }
        
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_INGREDIENT.get()).append(ChatColor.BLACK);
        s.append('\n').append(Tools.printItem(getIngredient(), ChatColor.RED, ChatColor.BLACK, true));
        
        if(hasFuel())
        {
            s.append('\n');
            s.append('\n').append(Messages.RECIPEBOOK_HEADER_REQUIREFUEL.get()).append(ChatColor.BLACK);
            s.append('\n').append(Tools.printItem(getFuel(), ChatColor.RED, ChatColor.BLACK, true));
        }
        
        return s.toString();
    }
}
