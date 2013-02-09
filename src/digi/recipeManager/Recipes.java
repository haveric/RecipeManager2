package digi.recipeManager;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import digi.recipeManager.data.*;
import digi.recipeManager.data.RecipeInfo.RecipeOwner;

/**
 * RecipeManager's recipe storage
 */
public class Recipes
{
    // Basic recipes
    protected List<CraftRecipe>         craftRecipes     = new ArrayList<CraftRecipe>();
    protected int                       craftIndex       = 0;
    protected List<CombineRecipe>       combineRecipes   = new ArrayList<CombineRecipe>();
    protected int                       combineIndex     = 0;
    protected Map<Integer, SmeltRecipe> smeltRecipes     = new HashMap<Integer, SmeltRecipe>();
    protected Map<String, FuelRecipe>   fuels            = new HashMap<String, FuelRecipe>();
    
    protected Set<ItemStack>            removedResults   = new HashSet<ItemStack>();
    
    protected Map<RmRecipe, RecipeInfo> recipeIndex      = new HashMap<RmRecipe, RecipeInfo>();
    
    protected boolean                   customSmelt      = false;
    private boolean                     registered       = false;
    
    public static final String          RECIPE_ID_STRING = ChatColor.BLACK + "RecipeManager #";
    
    protected Recipes()
    {
        recipeIndex.clear();
        recipeIndex.putAll(BukkitRecipes.recipeIndex);
    }
    
    public void registerRecipesToServer()
    {
        if(registered)
            throw new IllegalAccessError("This class is already registered!");
        
        RecipeManager.updatingRecipes = true;
        RecipeManager.recipes = this;
        
        BukkitRecipes.removeCustomRecipes();
        
        Iterator<Entry<RmRecipe, RecipeInfo>> iterator = recipeIndex.entrySet().iterator();
        Entry<RmRecipe, RecipeInfo> entry;
        RecipeInfo info;
        RmRecipe recipe;
        Flags flags;
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
            
            System.out.print("Custom recipe... id=" + info.getIndex());
            
            if(recipe instanceof CraftRecipe)
            {
                System.out.print("Craft recipe...");
                
                if(flags.isOverride() || flags.isRemove())
                {
                    System.out.print("override/remove...");
                    
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
                    
                    if(!BukkitRecipes.removeFurnaceRecipe((SmeltRecipe)recipe))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Couldn't find furnace recipe to remove!");
                }
                
                if(add)
                    Bukkit.addRecipe(((SmeltRecipe)recipe).toFurnaceRecipe());
            }
        }
        
        /*
        if(bukkitRecipes.isEmpty())
            return;
        
        BukkitRecipes.setServerRecipes(bukkitRecipes);
        bukkitRecipes.clear();
        */
        
        RecipeManager.updatingRecipes = false;
        registered = true;
    }
    
    public void addRecipe(RmRecipe recipe)
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