package haveric.recipeManager.api.events;

import haveric.recipeManager.recipes.FuelRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SmeltRecipe;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;


/**
 * Event triggered when RecipeManager's custom furnace recipes are being smelted.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled to prevent the action.
 *
 * @author Digi
 */
public class RecipeManagerSmeltEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private SmeltRecipe recipe;
    private FuelRecipe fuelRecipe;
    private ItemResult result;
    private Block block;
    private String smelter;
    private String fueler;

    public RecipeManagerSmeltEvent(SmeltRecipe newRecipe, FuelRecipe newFuelRecipe, ItemResult newResult, Block newBlock, String newSmelter, String newFueler) {
        recipe = newRecipe;
        fuelRecipe = newFuelRecipe;
        result = newResult;
        block = newBlock;
        smelter = newSmelter;
        fueler = newFueler;
    }

    /**
     * @return RecipeManager's Smelt class recipe, never null
     */
    public SmeltRecipe getRecipe() {
        return recipe;
    }

    /**
     * Gets the fuel recipe that powered the furnace.
     *
     * @return RecipeManager's Fuel class recipe or null if not found
     */
    public FuelRecipe getFuelRecipe() {
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

    /**
     * Get the player's name that initially placed the ingredient for this recipe.<br>
     * Can be null in certain situations!
     *
     * @return smelter's name or null
     */
    public String getSmelterName() {
        return smelter;
    }

    /**
     * Get the Player object of the player that placed the ingredient.<br>
     * NOTE: This returns null if player is not online or plugin couldn't get the player's name, use getSmelterName() to get his name only.<br>
     * Shortcut for: Bukkit.getPlayerExact(event.getSmelterName());
     *
     * @return Player object of the smelter or null
     */
    public Player getSmelter() {
        Player player = null;

        if (smelter != null) {
            try {
                UUID uuid = UUIDFetcher.getUUIDOf(smelter);
                player = Bukkit.getPlayer(uuid);
            } catch (Exception e) {}
        }

        return player;
    }

    /**
     * Get the player's name that placed the fuel powering this recipe.<br>
     * Can be null in certain situations!
     *
     * @return fueler's name or null
     */
    public String getFuelerName() {
        return fueler;
    }

    /**
     * Get the Player object of the player that placed the fuel powering this recipe.<br>
     * NOTE: This returns null if player is not online or plugin couldn't get the player's name, use getFuelerName() to get his name only.<br>
     * Shortcut for: Bukkit.getPlayerExact(event.getSmelterName());
     *
     * @return Player object of the fueler or null
     */
    public Player getFueler() {
        Player player = null;

        if (fueler != null) {
            try {
                UUID uuid = UUIDFetcher.getUUIDOf(fueler);
                player = Bukkit.getPlayer(uuid);
            } catch (Exception e) {}
        }

        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean newCancelled) {
        cancelled = newCancelled;
    }
}
