package ro.thehunters.digi.recipeManager.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

/**
 * Event triggered when RecipeManager's custom recipes are crafted/combined in the workbench.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled to prevent the action.<br>
 * Event is triggered when a result is processed, it won't guarantee that the player will get the result !
 * 
 * @author Digi
 */
public class RMCraftEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    
    private boolean cancelled = false;
    private boolean shiftClick = false;
    private boolean rightClick = false;
    private ItemResult result;
    private ItemStack cursor;
    private WorkbenchRecipe recipe;
    private Player player;
    
    public RMCraftEvent(WorkbenchRecipe recipe, ItemResult result, Player player, ItemStack cursor, boolean shiftClick, boolean rightClick)
    {
        this.recipe = recipe;
        this.result = result;
        this.player = player;
        this.cursor = cursor;
        this.shiftClick = shiftClick;
        this.rightClick = rightClick;
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
    
    /**
     * @return player or null if crafted by automated plugins
     */
    public Player getPlayer()
    {
        return player;
    }
    
    /**
     * @return the recipe
     */
    public WorkbenchRecipe getRecipe()
    {
        return recipe;
    }
    
    /**
     * @return result item or AIR if chance of failure occured
     */
    public ItemResult getResult()
    {
        return result;
    }
    
    /**
     * Sets the result to the specified item.<br>
     * Set to AIR or NULL to force the recipe to fail.
     * 
     * @param result
     *            the new result or null
     */
    public void setResult(ItemStack result)
    {
        this.result = (result == null ? null : (result instanceof ItemResult ? (ItemResult)result : new ItemResult(result)));
    }
    
    /**
     * @return The item in the player's cursor or null
     */
    public ItemStack getCursorItem()
    {
        return cursor;
    }
    
    /**
     * @return Was it a Shift+Click ?
     */
    public boolean isShiftClick()
    {
        return shiftClick;
    }
    
    /**
     * Shortcut for: !isRightClick()
     * 
     * @return Was the click a LeftClick ?
     */
    public boolean isLeftClick()
    {
        return !rightClick;
    }
    
    /**
     * @return Was the click a RightClick ?
     */
    public boolean isRightClick()
    {
        return rightClick;
    }
}
