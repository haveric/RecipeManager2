package ro.thehunters.digi.recipeManager.apievents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;


/**
 * Event triggered when RecipeManager's custom recipes' ingredients are placed in the workbench and the result is displayed.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled by setting the result to null to prevent player from crafting the recipe.
 * 
 * @author Digi
 */
public class RecipeManagerPrepareCraftEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private ItemStack                result;
    private WorkbenchRecipe          recipe;
    private Player                   player;
    private Location                 workbench;
    
    public RecipeManagerPrepareCraftEvent(WorkbenchRecipe recipe, ItemStack result, Player player, Location workbench)
    {
        this.recipe = recipe;
        this.result = result;
        this.player = player;
        this.setWorkbench(workbench);
    }
    
    /**
     * @return player or null if crafted by automated plugins
     */
    public Player getPlayer()
    {
        return player;
    }
    
    /**
     * @return recipe or null if it's a repair recipe
     */
    public WorkbenchRecipe getRecipe()
    {
        return recipe;
    }
    
    /**
     * Use getRecipe().getResults().get(0) to get the result that would've been displayed.
     * 
     * @return result item or null if player doesn't have access to recipe
     */
    public ItemStack getResult()
    {
        return result;
    }
    
    /**
     * Shortcut for: (getRecipe() == null)
     * 
     * @return Repair recipe true/false
     */
    public boolean isRepair()
    {
        return recipe == null;
    }
    
    /**
     * Sets the display result.<br>
     * Setting this to null will prevent the player from crafting the recipe!
     * 
     * @param result
     *            ItemStack displayed result or null to 'cancel' event
     */
    public void setResult(ItemStack result)
    {
        this.result = result;
    }
    
    public Location getWorkbench()
    {
        return workbench;
    }
    
    public void setWorkbench(Location workbench)
    {
        this.workbench = workbench;
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
}