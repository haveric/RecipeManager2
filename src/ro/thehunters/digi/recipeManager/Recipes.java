package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeStatus;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

/**
 * RecipeManager's recipe storage
 */
public class Recipes
{
    @Override
    protected void finalize() throws Throwable // TODO REMOVE
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + getClass().getName() + " :: finalize()");
        
        super.finalize();
    }
    
    // Recipe index
    protected Map<BaseRecipe, RecipeInfo> index                = new HashMap<BaseRecipe, RecipeInfo>();
    
    // Quick-find index
    protected Map<Integer, CraftRecipe>   indexCraft           = new HashMap<Integer, CraftRecipe>();
    protected Map<Integer, CombineRecipe> indexCombine         = new HashMap<Integer, CombineRecipe>();
    protected Map<Integer, SmeltRecipe>   indexSmelt           = new HashMap<Integer, SmeltRecipe>();
    protected Map<String, FuelRecipe>     indexFuels           = new HashMap<String, FuelRecipe>();
    
    // constants
    public static final String            FURNACE_OWNER_STRING = ChatColor.GRAY + "Placed by: " + ChatColor.WHITE;
    public static final String            RECIPE_ID_STRING     = ChatColor.GRAY + "RecipeManager #";
    
    protected Recipes()
    {
    }
    
    protected void clean()
    {
        index.clear();
        indexCraft.clear();
        indexCombine.clear();
        indexSmelt.clear();
        indexFuels.clear();
    }
    
    /**
     * Alias for RecipeManager.getRecipes()
     * 
     * @return
     */
    public static Recipes getInstance()
    {
        return RecipeManager.getRecipes();
    }
    
    /**
     * Checks if result is part of a recipe added by RecipeManager by checking item's lore.
     * 
     * @param result
     *            must be the actual result from recipe.
     * @return
     */
    public boolean isCustomWorkbenchRecipe(ItemStack result)
    {
        if(result == null)
            return false;
        
        List<String> desc = result.getItemMeta().getLore();
        
        return (desc == null ? false : desc.get(desc.size() - 1).startsWith(RECIPE_ID_STRING));
    }
    
    /**
     * Checks if recipe is added by RecipeManager, works for workbench and furnace recipes.<br>
     * Does not work for fuels because they do not exist in Bukkit API, they're a custom system by RecipeManager.
     * 
     * @param recipe
     *            must be the actual recipe, because it checks for result's lore.
     * @return
     */
    public boolean isCustomRecipe(Recipe recipe)
    {
        if(recipe == null)
            return false;
        
        if(recipe instanceof FurnaceRecipe)
            return (getSmeltRecipe(((FurnaceRecipe)recipe).getInput()) != null);
        
        return isCustomWorkbenchRecipe(recipe.getResult());
    }
    
    /**
     * Checks if item can be used as a fuel.
     * Alias for getFuelRecipe(item) != null
     * 
     * @param item
     * @return
     */
    public boolean isFuel(ItemStack item)
    {
        return getFuelRecipe(item) != null;
    }
    
    /**
     * Get the RecipeManager workbench recipe for the bukkit recipe inputted.<br>
     * Can be either craft or combine recipe.<br>
     * If you know the specific type you can use {@link #getCraftRecipe(ItemStack)} or {@link #getCombineRecipe(ItemStack)}
     * 
     * @param recipe
     * @return Workbench recipe, otherwise it can be null if doesn't exist or you inputted a furnace recipe
     */
    public WorkbenchRecipe getWorkbenchRecipe(Recipe recipe)
    {
        if(recipe instanceof ShapedRecipe)
            return getCraftRecipe(recipe.getResult());
        
        if(recipe instanceof ShapelessRecipe)
            return getCombineRecipe(recipe.getResult());
        
        return null;
    }
    
    /**
     * Get the RecipeManager craft recipe for the result inputted.<br>
     * The result must be the one from the bukkit recipe retrieved as it needs to check for result lore for the ID.<br>
     * 
     * @param result
     * @return Craft recipe or null if doesn't exist
     */
    public CraftRecipe getCraftRecipe(ItemStack result)
    {
        return indexCraft.get(Tools.getRecipeIdFromResult(result));
    }
    
    /**
     * Get the RecipeManager combine recipe for the result inputted.<br>
     * The result must be the one from the bukkit recipe retrieved as it needs to check for result lore for the ID.<br>
     * 
     * @param result
     * @return Combine recipe or null if doesn't exist
     */
    public CombineRecipe getCombineRecipe(ItemStack result)
    {
        return indexCombine.get(Tools.getRecipeIdFromResult(result));
    }
    
    /**
     * Get RecipeManager's furnace smelt recipe for the specified ingredient
     * 
     * @param ingredient
     * @return
     *         Smelt recipe or null if doesn't exist
     */
    public SmeltRecipe getSmeltRecipe(ItemStack ingredient)
    {
        return indexSmelt.get(ingredient.getTypeId());
    }
    
    /**
     * Get RecipeManager's furnace fuel recipe for the specified item.
     * 
     * @param fuel
     *            fuel
     * @return
     *         Fuel recipe or null if doesn't exist
     */
    public FuelRecipe getFuelRecipe(ItemStack fuel)
    {
        FuelRecipe recipe = indexFuels.get(fuel.getTypeId() + "");
        
        if(recipe == null)
            return indexFuels.get(fuel.getTypeId() + ":" + fuel.getDurability());
        
        return recipe;
    }
    
    /**
     * Gets the recipe's information (owner, adder, status, etc).<br>
     * You can use this on bukkit recipes by converting them to RecipeManager format using:<br>
     * <code>new BaseRecipe(bukkitRecipe);</code>
     * 
     * @param recipe
     *            a RecipeManager recipe
     * @return Recipe's info or null if doesn't exist
     */
    public RecipeInfo getRecipeInfo(BaseRecipe recipe)
    {
        return index.get(recipe);
    }
    
    /**
     * Gets a copy of RecipeManager's recipe list.<br>
     * Returned values are mutable so you can edit individual recipes.<br>
     * Removing from this list does nothing, see {@link BaseRecipe #remove()} method instead.
     * 
     * @return copy of hashmap
     */
    public Map<BaseRecipe, RecipeInfo> getRecipeList()
    {
        return new HashMap<BaseRecipe, RecipeInfo>(index);
    }
    
    public boolean isResultTakeable(Player player, ItemStack result, ItemStack cursor, boolean shiftClick)
    {
        return true; // TODO Auto-generated method stub
    }
    
    public void registerRecipe(BaseRecipe recipe)
    {
        String adder = "TODO"; // TODO
        
        registerRecipe(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }
    
    protected void registerRecipe(BaseRecipe recipe, RecipeInfo info)
    {
        boolean queued = info.getStatus() == RecipeStatus.QUEUED;
        
        if(index.containsKey(recipe) && !queued)
        {
            Messages.info(ChatColor.RED + "[debug] " + ChatColor.GREEN + "RECIPE EXISTS !!!!!!!!!!!!!!!!!!!!");
            return;
        }
        
        if(queued)
            info.setStatus(null);
        
        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);
        
        if(isRemove)
            info.setStatus(RecipeStatus.REMOVED);
        
        else if(recipe.hasFlag(FlagType.OVERRIDE))
            info.setStatus(RecipeStatus.OVERRIDEN);
        
        // Add to main index
        index.put(recipe, info);
        
        // Add to quickfind index if it's not removed
        
        if(!isRemove)
        {
            if(recipe instanceof CraftRecipe)
            {
                indexCraft.put(recipe.getIndex(), (CraftRecipe)recipe);
            }
            else if(recipe instanceof CombineRecipe)
            {
                indexCombine.put(recipe.getIndex(), (CombineRecipe)recipe);
            }
            else if(recipe instanceof SmeltRecipe)
            {
                indexSmelt.put(recipe.getIndex(), (SmeltRecipe)recipe);
            }
            else if(recipe instanceof FuelRecipe)
            {
                indexFuels.put(((FuelRecipe)recipe).getIndexString(), (FuelRecipe)recipe);
            }
        }
        
        // Add to server if appliable
        Recipe bukkitRecipe = recipe.toBukkitRecipe();
        
        if(bukkitRecipe != null)
        {
            BukkitRecipes.removeRecipeManagerRecipe(recipe);
            
            Bukkit.addRecipe(bukkitRecipe);
        }
    }
    
    public boolean removeRecipe(BaseRecipe recipe)
    {
        // Remove from main index
        index.remove(recipe);
        
        /*
        RecipeFlags flags = recipe.getFlags();
        
        BukkitRecipes.removeBukkitRecipe(recipe);
        
        Bukkit.addRecipe(bukkitRecipe);
        */
        
        // Remove from quickfind index
        if(recipe instanceof CraftRecipe)
        {
            indexCraft.remove(recipe.getIndex());
        }
        else if(recipe instanceof CombineRecipe)
        {
            indexCombine.remove(recipe.getIndex());
        }
        else if(recipe instanceof SmeltRecipe)
        {
            indexSmelt.remove(recipe.getIndex());
        }
        else if(recipe instanceof FuelRecipe)
        {
            indexFuels.remove(((FuelRecipe)recipe).getIndexString());
        }
        
        // Remove from server
        return BukkitRecipes.removeRecipeManagerRecipe(recipe);
    }
}