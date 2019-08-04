package haveric.recipeManager.api.events;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.WorkbenchRecipe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when RecipeManager's custom recipes' ingredients are placed in the workbench and the result is displayed.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled by setting the result to null to prevent player from crafting the recipe.
 */
public class RecipeManagerPrepareCraftEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private WorkbenchRecipe recipe;
    private ItemResult result;
    private Player player;
    private Location workbench;

    public RecipeManagerPrepareCraftEvent(WorkbenchRecipe newRecipe, ItemResult newResult, Player newPlayer, Location newWorkbench) {
        recipe = newRecipe;
        result = newResult;
        player = newPlayer;
        setWorkbenchLocation(newWorkbench);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return player or null if crafted by automated plugins
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the recipe
     */
    public WorkbenchRecipe getRecipe() {
        return recipe;
    }

    /**
     * The recipe result.<br>
     * This may also return a special item that is used to print multi-results into one item's lore!
     *
     * @return result item or null if player doesn't have access to recipe
     */
    public ItemResult getResult() {
        return result;
    }

    /**
     * Sets the display result.<br>
     * Setting this to null will prevent the player from crafting the recipe.
     *
     * @param newResult
     *            ItemResult displayed result or null to 'cancel' event
     */
    public void setResult(ItemResult newResult) {
        result = newResult;
    }

    /**
     * @return Get the workbench location or crafter location
     */
    public Location getWorkbenchLocation() {
        return workbench;
    }

    /**
     * The workbench location is used by the recipe flags.
     *
     * @param newWorkbench
     *            the new workbench location
     */
    public void setWorkbenchLocation(Location newWorkbench) {
        workbench = newWorkbench;
    }
}
