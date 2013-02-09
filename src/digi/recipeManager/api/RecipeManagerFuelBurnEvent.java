package digi.recipeManager.api;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.data.FuelRecipe;

/**
 * Event triggered when RecipeManager's custom fuel recipes are burnt.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled to prevent the action.
 * 
 * @author Digi
 */
public class RecipeManagerFuelBurnEvent extends Event implements Cancellable
{
    private static final HandlerList handlers  = new HandlerList();
    private boolean                  cancelled = false;
    private FuelRecipe               recipe;
    private int                      burnTicks;
    private Block                    block;
    private String                   fueler;
    
    public RecipeManagerFuelBurnEvent(FuelRecipe recipe, int burnTicks, Block block, String fueler)
    {
        this.recipe = recipe;
        this.burnTicks = burnTicks;
        this.block = block;
        this.fueler = fueler;
    }
    
    /**
     * @return RecipeManager's Fuel class recipe
     */
    public FuelRecipe getRecipe()
    {
        return recipe;
    }
    
    /**
     * Shortcut from: event.getRecipe().getFuel().getItemStack();
     * 
     * @return fuel as ItemStack
     */
    public ItemStack getFuel()
    {
        return recipe.getIngredient();
    }
    
    /**
     * Get the ticks that the furnace will run for.<br>
     * Can be different every time depending on the recipe.
     * 
     * @return fuel burning ticks
     */
    public int getBurnTicks()
    {
        return burnTicks;
    }
    
    /**
     * Change the ticks that the furnace will burn for
     * 
     * @param burnTicks
     *            time in ticks
     */
    public void setBurnTicks(int burnTicks)
    {
        this.burnTicks = burnTicks;
    }
    
    /**
     * @return furnace block of the involved event
     */
    public Block getBlock()
    {
        return block;
    }
    
    /**
     * Get the player's name that initially placed the fuel.<br>
     * Can be null in certain situatinos!
     * 
     * @return fueler's name
     */
    public String getFuelerName()
    {
        return fueler;
    }
    
    /**
     * Get the Player object of the player that placed the fuel.<br>
     * NOTE: This returns null if player is not online or plugin couldn't get the player's name, use getFuelerName() to get his name only.<br>
     * Shortcut for: Bukkit.getPlayerExact(event.getFuelerName());
     * 
     * @return Player object of the fueler
     */
    public Player getFueler()
    {
        return (fueler == null ? null : Bukkit.getPlayerExact(fueler));
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }
}
