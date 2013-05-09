package ro.thehunters.digi.recipeManager.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

/**
 * Event triggered when RecipeManager's custom recipes are crafted/combined in the workbench.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled to prevent the action.<br>
 * Event is triggered when a result is processed, it won't guarantee that the player will get the result !
 * 
 * @author Digi
 */
public class RMCraftEventPost extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    private boolean shiftClick = false;
    private boolean rightClick = false;
    private ItemStack result;
    private ItemStack cursor;
    private WorkbenchRecipe recipe;
    private Player player;
    
    public RMCraftEventPost(WorkbenchRecipe recipe, ItemStack item, Player player, ItemStack cursor, boolean shiftClick, boolean rightClick)
    {
        this.recipe = recipe;
        this.result = item;
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
    public ItemStack getResult()
    {
        return result;
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
