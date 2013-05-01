package ro.thehunters.digi.recipeManager.recipes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Vanilla;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class FuelRecipe extends BaseRecipe
{
    private ItemStack ingredient;
    private float minTime;
    private float maxTime;
    
    public FuelRecipe(Material type, float burnTime)
    {
        setIngredient(new ItemStack(type, 1, Vanilla.DATA_WILDCARD));
        setMinTime(burnTime);
    }
    
    public FuelRecipe(Material type, float minTime, float maxTime)
    {
        setIngredient(new ItemStack(type, 1, Vanilla.DATA_WILDCARD));
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
        
        // TODO clone this extension
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
        return ingredient.getTypeId() + (ingredient.getDurability() == Vanilla.DATA_WILDCARD ? "" : ":" + ingredient.getDurability());
    }
    
    @Override
    public void resetName()
    {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);
        
        s.append("fuel ");
        
        s.append(ingredient.getType().toString().toLowerCase());
        
        if(ingredient.getDurability() != Vanilla.DATA_WILDCARD)
        {
            s.append(":").append(ingredient.getDurability());
        }
        
        if(removed)
        {
            s.append(" / removed recipe");
        }
        
        name = s.toString();
        customName = false;
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
    public RecipeType getType()
    {
        return RecipeType.FUEL;
    }
    
    @Override
    public String printBookIndex()
    {
        return hasCustomName() ? ChatColor.ITALIC + getName() : Tools.Item.getName(getIngredient());
    }
    
    @Override
    public String printBook()
    {
        StringBuilder s = new StringBuilder(256);
        
        s.append(Messages.RECIPEBOOK_HEADER_FUEL.get());
        
        if(hasCustomName())
        {
            s.append('\n').append(ChatColor.DARK_BLUE).append(getName()).append(ChatColor.BLACK);
        }
        
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_INGREDIENT.get()).append(ChatColor.BLACK);
        s.append('\n').append(Tools.Item.print(getIngredient(), ChatColor.RED, ChatColor.BLACK, true));
        
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_BURNTIME.get()).append(ChatColor.BLACK);
        s.append('\n');
        
        if(maxTime > minTime)
        {
            s.append(Messages.RECIPEBOOK_FUEL_TIME_RANDOM.get("{min}", Tools.printNumber(minTime), "{max}", Tools.printNumber(maxTime)));
        }
        else
        {
            s.append(Messages.RECIPEBOOK_FUEL_TIME_FIXED.get("{time}", Tools.printNumber(minTime)));
        }
        
        return s.toString();
    }
}
