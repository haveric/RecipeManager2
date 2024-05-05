package haveric.recipeManager.api.events;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManager.recipes.fuel.FuelRecipe1_13;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

// TODO: Reimplement
/**
 * Event triggered when RecipeManager's custom furnace recipes are being smelted.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled to prevent the action.
 */
public class RecipeManagerSmeltEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private RMFurnaceRecipe1_13 recipe;
    private FuelRecipe1_13 fuelRecipe;
    private ItemResult result;
    private Block block;

    public RecipeManagerSmeltEvent(RMFurnaceRecipe1_13 newRecipe, FuelRecipe1_13 newFuelRecipe, ItemResult newResult, Block newBlock/*,, String newSmelter String newFueler*/) {
        recipe = newRecipe;
        fuelRecipe = newFuelRecipe;
        result = newResult;
        block = newBlock;
    }

    /**
     * @return RecipeManager's Smelt class recipe, never null
     */
    public RMFurnaceRecipe1_13 getRecipe() {
        return recipe;
    }

    /**
     * Gets the fuel recipe that powered the furnace.
     *
     * @return RecipeManager's Fuel class recipe or null if not found
     */
    public FuelRecipe1_13 getFuelRecipe() {
        return fuelRecipe;
    }

    /**
     * @return result item or NULL if chance of failure occurred
     */
    public ItemResult getResult() {
        return result;
    }

    /**
     * Sets the result of the recipe.<br>
     * Set to NULL to mark as failed.
     *
     * @param item
     *            ItemResult or ItemStack
     */
    public void setResult(ItemStack item) {
        if (item == null) {
            result = null;
        } else {
            result = new ItemResult(item);
        }
    }

    /**
     * @return furnace block of the involved event
     */
    public Block getBlock() {
        return block;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newCancelled) {
        cancelled = newCancelled;
    }
}
