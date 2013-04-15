package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe.RecipeType;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class ArgBuilder
{
    private Args a = new Args();
    
    public static void init()
    {
    }
    
    /**
     * Start building an argument class for flag events
     * 
     * @return linkable methods
     */
    public static ArgBuilder create()
    {
        return new ArgBuilder();
    }
    
    /**
     * Start building an argument class for flag events
     * 
     * @return linkable methods
     */
    public ArgBuilder()
    {
    }
    
    public ArgBuilder player(String player)
    {
        a.setPlayerName(player);
        return this;
    }
    
    public ArgBuilder player(Player player)
    {
        a.setPlayer(player);
        return this;
    }
    
    public ArgBuilder location(Location location)
    {
        a.setLocation(location);
        return this;
    }
    
    public ArgBuilder recipe(BaseRecipe recipe)
    {
        a.setRecipe(recipe);
        return this;
    }
    
    public ArgBuilder recipe(RecipeType type)
    {
        a.setRecipeType(type);
        return this;
    }
    
    public ArgBuilder inventory(Inventory inventory)
    {
        a.setInventory(inventory);
        return this;
    }
    
    public ArgBuilder result(ItemStack result)
    {
        if(result != null)
            a.setResult(result instanceof ItemResult ? (ItemResult)result : new ItemResult(result));
        
        return this;
    }
    
    public ArgBuilder result(ItemResult result)
    {
        a.setResult(result);
        return this;
    }
    
    /**
     * Compile the arguments and get them.
     * 
     * @return
     */
    public Args build()
    {
        if(a.player() == null && a.playerName() != null)
        {
            a.setPlayer(Bukkit.getPlayerExact(a.playerName()));
        }
        
        if(a.playerName() == null && a.player() != null)
        {
            a.setPlayerName(a.player().getName());
        }
        
        if(a.location() == null && a.player() != null)
        {
            a.setLocation(a.player().getLocation());
        }
        
        if(a.recipeType() == null && a.recipe() != null)
        {
            a.setRecipeType(a.recipe().getType());
        }
        
        return a;
    }
}
