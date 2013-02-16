package digi.recipeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scheduler.BukkitTask;

import digi.recipeManager.recipes.BaseRecipe;
import digi.recipeManager.recipes.CombineRecipe;
import digi.recipeManager.recipes.CraftRecipe;
import digi.recipeManager.recipes.FuelRecipe;
import digi.recipeManager.recipes.RecipeInfo;
import digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import digi.recipeManager.recipes.SmeltRecipe;
import digi.recipeManager.recipes.WorkbenchRecipe;
import digi.recipeManager.recipes.flags.RecipeFlags;

/**
 * RecipeManager's recipe storage
 */
public class Recipes
{
    // Basic recipes
    protected List<CraftRecipe>           craftRecipes         = new ArrayList<CraftRecipe>();
    protected int                         craftIndex           = 0;
    protected List<CombineRecipe>         combineRecipes       = new ArrayList<CombineRecipe>();
    protected int                         combineIndex         = 0;
    protected Map<Integer, SmeltRecipe>   smeltRecipes         = new HashMap<Integer, SmeltRecipe>();
    protected Map<String, FuelRecipe>     fuels                = new HashMap<String, FuelRecipe>();
    
    protected Set<ItemStack>              removedResults       = new HashSet<ItemStack>();
    
    protected Map<BaseRecipe, RecipeInfo> recipeIndex          = new HashMap<BaseRecipe, RecipeInfo>();
    
    private boolean                       needFurnaceWorker    = false;
    private boolean                       registered           = false;
    
    private static BukkitTask             furnaceWorkerTask    = null;
    
    public static final String            FURNACE_OWNER_STRING = ChatColor.GRAY + "Placed by: " + ChatColor.WHITE;
    public static final String            RECIPE_ID_STRING     = ChatColor.GRAY + "RecipeManager #";
    
    protected Recipes()
    {
        recipeIndex.clear();
        recipeIndex.putAll(BukkitRecipes.initialRecipes);
    }
    
    public void registerRecipesToServer()
    {
        registerRecipesToServer(null);
    }
    
    protected void registerRecipesToServer(Set<String> reloadedFiles)
    {
        if(registered)
            throw new IllegalAccessError("This class is already registered!");
        
        /*
        if(needFurnaceWorker)
        {
            furnaceWorkerTask.cancel();
            furnaceWorkerTask = null;

            furnaceWorkerTask = Bukkit.getScheduler().runTaskTimer(RecipeManager.getPlugin(), new FurnaceWorker(RecipeManager.getSettings().FURNACE_TICKS), 0, RecipeManager.getSettings().FURNACE_TICKS);
        }
        else if(furnaceWorkerTask != null)
        {
            furnaceWorkerTask.cancel();
            furnaceWorkerTask = null;
        }
        */
        
        /* TODO !
        if(reloadedFiles != null && RecipeManager.recipes != null)
        {
            Iterator<Entry<BaseRecipe, RecipeInfo>> iterator = RecipeManager.recipes.recipeIndex.entrySet().iterator();
            Entry<BaseRecipe, RecipeInfo> entry;
            
            while(iterator.hasNext())
            {
                entry = iterator.next();
                
                if(!reloadedFiles.contains(entry.getValue().getFile()))
                {
                    // copy unchanged recipes from old storage...
                    recipeIndex.put(entry.getKey(), entry.getValue());
                }
                else
                {
                    // remove changed recipes from server
                    
                }
            }
        }
        */
        
        RecipeManager.recipes = this;
        
        Iterator<Entry<BaseRecipe, RecipeInfo>> iterator = recipeIndex.entrySet().iterator();
        Entry<BaseRecipe, RecipeInfo> entry;
        RecipeInfo info;
        BaseRecipe recipe;
        RecipeFlags flags;
        boolean add;
        
        while(iterator.hasNext())
        {
            entry = iterator.next();
            info = entry.getValue();
            
            if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
                continue;
            
            recipe = entry.getKey();
            flags = recipe.getFlags();
            add = true;
            
            if(recipe instanceof CraftRecipe)
            {
                if(flags.isOverride() || flags.isRemove())
                {
                    add = flags.isOverride();
                    
                    // TODO remove debug ?
                    if(!BukkitRecipes.removeShapedRecipe((CraftRecipe)recipe))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Couldn't find shaped recipe to remove!");
                }
                
                if(add)
                    Bukkit.addRecipe(((CraftRecipe)recipe).toShapedRecipe(info.getIndex()));
            }
            else if(recipe instanceof CombineRecipe)
            {
                if(flags.isOverride() || flags.isRemove())
                {
                    add = flags.isOverride();
                    
                    // TODO remove debug ?
                    if(!BukkitRecipes.removeShapelessRecipe((CombineRecipe)recipe))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Couldn't find shapeless recipe to remove!");
                }
                
                if(add)
                    Bukkit.addRecipe(((CombineRecipe)recipe).toShapelessRecipe(info.getIndex()));
            }
            else if(recipe instanceof SmeltRecipe)
            {
                if(flags.isOverride() || flags.isRemove())
                {
                    add = flags.isOverride();
                    
                    // TODO remove debug ?
                    if(!BukkitRecipes.removeFurnaceRecipe((SmeltRecipe)recipe))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Couldn't find furnace recipe to remove!");
                }
                
                if(add)
                    Bukkit.addRecipe(((SmeltRecipe)recipe).toFurnaceRecipe());
            }
        }
        
        registered = true;
        
        if(needFurnaceWorker)
            FurnaceWorker.start();
        else
            FurnaceWorker.stop();
    }
    
    public void addRecipe(BaseRecipe recipe)
    {
        if(recipe instanceof CraftRecipe)
            addCraftRecipe((CraftRecipe)recipe);
        
        else if(recipe instanceof CombineRecipe)
            addCombineRecipe((CombineRecipe)recipe);
        
        else if(recipe instanceof SmeltRecipe)
            addSmeltRecipe((SmeltRecipe)recipe);
        
        else if(recipe instanceof FuelRecipe)
            addFuelRecipe((FuelRecipe)recipe);
        
        else
            throw new IllegalArgumentException("Unknown recipe!");
    }
    
    public void addCraftRecipe(CraftRecipe recipe)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and exacly 9 ingredient slots, empty ones can be null.");
        
        craftRecipes.add(craftIndex, recipe); // add to list findable by index
        
        recipeIndex.remove(recipe); // if exists, update key too !
        recipeIndex.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, craftIndex));
        
        craftIndex++;
    }
    
    public void addCombineRecipe(CombineRecipe recipe)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and ingredient!");
        
        combineRecipes.add(combineIndex, recipe);
        
        recipeIndex.remove(recipe);
        recipeIndex.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, combineIndex));
        
        combineIndex++;
    }
    
    public void addSmeltRecipe(SmeltRecipe recipe)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs a result and ingredient!");
        
        int index = recipe.getIngredient().getTypeId();
        smeltRecipes.put(index, recipe);
        
        if(!needFurnaceWorker && recipe.getMinTime() >= 0)
            needFurnaceWorker = true;
        
        recipeIndex.remove(recipe);
        recipeIndex.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, index));
    }
    
    public void addFuelRecipe(FuelRecipe recipe)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs an ingredient!");
        
        fuels.put(Tools.convertItemToStringID(recipe.getIngredient()), recipe);
        
        recipeIndex.remove(recipe);
        recipeIndex.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER));
    }
    
    public boolean isCustomWorkbenchRecipe(ItemStack result)
    {
        if(result == null)
            return false;
        
        List<String> desc = result.getItemMeta().getLore();
        
        return (desc == null ? false : desc.get(desc.size() - 1).startsWith(RECIPE_ID_STRING));
    }
    
    public boolean isCustomRecipe(Recipe recipe)
    {
        if(recipe == null)
            return false;
        
        if(recipe instanceof FurnaceRecipe)
            return (getSmeltRecipe(((FurnaceRecipe)recipe).getInput()) != null);
        
        return isCustomWorkbenchRecipe(recipe.getResult());
    }
    
    public CraftRecipe getCraftRecipe(ItemStack result)
    {
        int index = Tools.getRecipeIdFromResult(result);
        
        if(index == -1 || index >= craftRecipes.size())
            return null;
        
        return craftRecipes.get(index);
    }
    
    public CombineRecipe getCombineRecipe(ItemStack result)
    {
        int index = Tools.getRecipeIdFromResult(result);
        
        if(index == -1 || index >= combineRecipes.size())
            return null;
        
        return combineRecipes.get(index);
    }
    
    public WorkbenchRecipe getWorkbenchRecipe(Recipe bukkitRecipe, ItemStack recipeResult)
    {
        if(bukkitRecipe instanceof ShapedRecipe)
            return RecipeManager.recipes.getCraftRecipe(recipeResult);
        
        if(bukkitRecipe instanceof ShapelessRecipe)
            return RecipeManager.recipes.getCombineRecipe(recipeResult);
        
        // TODO remove debug
        System.out.print("[debug] new recipe???!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return null;
    }
    
    public SmeltRecipe getSmeltRecipe(ItemStack ingredient)
    {
        return smeltRecipes.get(ingredient.getTypeId());
    }
    
    public FuelRecipe getFuelRecipe(ItemStack fuel)
    {
        FuelRecipe recipe = fuels.get(fuel.getTypeId() + "");
        
        if(recipe == null)
            return fuels.get(fuel.getTypeId() + ":" + fuel.getDurability());
        
        return recipe;
    }
    
    public boolean isResultTakeable(Player player, ItemStack result, ItemStack cursor, boolean shiftClick)
    {
        return false; // TODO Auto-generated method stub
    }
}