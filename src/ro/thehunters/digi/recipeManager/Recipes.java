package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeStatus;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

import com.google.common.collect.ImmutableMap;

/**
 * RecipeManager's recipe storage
 */
public class Recipes
{
    // constants
    public static final String FURNACE_OWNER_STRING = ChatColor.GRAY + "Placed by: " + ChatColor.WHITE;
    public static final String RECIPE_ID_STRING = ChatColor.GRAY + "RecipeManager #";
    
    // Remember results for re-use on failure
    private static final Map<String, ItemResult> staticResults = new HashMap<String, ItemResult>();
    
    // Recipe index
    protected Map<BaseRecipe, RecipeInfo> index = new HashMap<BaseRecipe, RecipeInfo>();
    
    // Quick-find index
    protected Map<Integer, CraftRecipe> indexCraft = new HashMap<Integer, CraftRecipe>();
    protected Map<Integer, CombineRecipe> indexCombine = new HashMap<Integer, CombineRecipe>();
    protected Map<Integer, SmeltRecipe> indexSmelt = new HashMap<Integer, SmeltRecipe>();
    protected Map<String, SmeltRecipe> indexSmeltFuels = new HashMap<String, SmeltRecipe>();
    protected Map<String, FuelRecipe> indexFuels = new HashMap<String, FuelRecipe>();
    protected Map<String, BaseRecipe> indexName = new HashMap<String, BaseRecipe>();
    
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
        indexName.clear();
        
        staticResults.clear();
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
        {
            return false;
        }
        
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
        {
            return false;
        }
        
        if(recipe instanceof FurnaceRecipe)
        {
            return (getSmeltRecipe(((FurnaceRecipe)recipe).getInput()) != null);
        }
        
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
        {
            return getCraftRecipe(recipe.getResult());
        }
        
        if(recipe instanceof ShapelessRecipe)
        {
            return getCombineRecipe(recipe.getResult());
        }
        
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
        return (result == null ? null : indexCraft.get(Tools.getRecipeIdFromItem(result)));
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
        return (result == null ? null : indexCombine.get(Tools.getRecipeIdFromItem(result)));
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
        return (ingredient == null ? null : indexSmelt.get(ingredient.getTypeId()));
    }
    
    public SmeltRecipe getSmeltRecipeWithFuel(ItemStack fuel)
    {
        if(fuel == null)
        {
            return null;
        }
        
        SmeltRecipe recipe = indexSmeltFuels.get(String.valueOf(fuel.getTypeId()));
        
        if(recipe == null)
        {
            return indexSmeltFuels.get(fuel.getTypeId() + ":" + fuel.getDurability());
        }
        
        return recipe;
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
        if(fuel == null)
        {
            return null;
        }
        
        FuelRecipe recipe = indexFuels.get(String.valueOf(fuel.getTypeId()));
        
        if(recipe == null)
        {
            return indexFuels.get(fuel.getTypeId() + ":" + fuel.getDurability());
        }
        
        return recipe;
    }
    
    /**
     * Gets a recipe by its name.<br>
     * The name can be an auto-generated name or a custom name.
     * 
     * @param name
     *            recipe name
     * @return the recipe matching name
     */
    public BaseRecipe getRecipeByName(String name)
    {
        return indexName.get(name.toLowerCase());
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
        return ImmutableMap.copyOf(index);
    }
    
    /**
     * Register a recipe.
     * 
     * @param recipe
     */
    public void registerRecipe(BaseRecipe recipe)
    {
        String adder = "TODO"; // TODO
        
        registerRecipe(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }
    
    /**
     * Registers a recipe with custom recipe info object.<br>
     * NOTE: You should not use this if you don't know what the recipe info object REALLY does.
     * 
     * @param recipe
     * @param info
     */
    public void registerRecipe(BaseRecipe recipe, RecipeInfo info)
    {
        boolean queued = info.getStatus() == RecipeStatus.QUEUED;
        
        if(index.containsKey(recipe) && !queued)
        {
            Messages.debug("recipe already exists: " + recipe.getName());
            return;
        }
        
        if(queued)
        {
            info.setStatus(null);
        }
        
        /* replaced by sendRegistered()  TODO remove ?
        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);
        
        if(isRemove)
        {
            info.setStatus(RecipeStatus.REMOVED);
        }
        else if(recipe.hasFlag(FlagType.OVERRIDE))
        {
            info.setStatus(RecipeStatus.OVERRIDDEN);
        }
        */
        
        index.put(recipe, info); // Add to main index
        
        // Add to quickfind index if it's not removed
        if(!recipe.hasFlag(FlagType.REMOVE))
        {
            indexName.put(recipe.getName().toLowerCase(), recipe); // Add to name index
            
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
                SmeltRecipe r = (SmeltRecipe)recipe;
                
                indexSmelt.put(recipe.getIndex(), r);
                
                if(r.hasFuel())
                {
                    indexSmeltFuels.put(r.getFuelIndex(), r);
                }
                
                if(r.hasCustomTime())
                {
                    FurnaceWorker.start();
                }
            }
            else if(recipe instanceof FuelRecipe)
            {
                indexFuels.put(((FuelRecipe)recipe).getIndexString(), (FuelRecipe)recipe);
            }
        }
        
        // Add to server if appliable
        Recipe bukkitRecipe = recipe.getBukkitRecipe();
        
        if(bukkitRecipe != null)
        {
            Vanilla.removeCustomRecipe(recipe);
            
            Bukkit.addRecipe(bukkitRecipe);
        }
    }
    
    public boolean removeRecipe(BaseRecipe recipe)
    {
        index.remove(recipe); // Remove from main index
        indexName.remove(recipe.getName().toLowerCase()); // Remove from name index
        
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
        return Vanilla.removeCustomRecipe(recipe);
    }
    
    protected static ItemResult recipeGetResult(Args a, WorkbenchRecipe recipe)
    {
        ItemResult result = staticResults.get(a.playerName());
        
        if(result == null)
        {
            result = recipe.getResult(a);
            staticResults.put(a.playerName(), result);
        }
        
        return (result == null ? null : result.clone());
    }
    
    protected static void recipeResetResult(String name)
    {
        staticResults.remove(name);
    }
}
