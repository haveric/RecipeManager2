package haveric.recipeManager.api.events;

import haveric.recipeManager.recipes.FuelRecipe;
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
 * Event triggered when RecipeManager's custom fuel recipes are burned.<br>
 * Player can return null in certain situations, so be sure to prepare for that situation.<br>
 * Event can be cancelled to prevent the action.
 *
 * @author Digi
 */
public class RecipeManagerFuelBurnEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private FuelRecipe recipe;
    private int burnTicks;
    private Block block;
    private String fueler;

    public RecipeManagerFuelBurnEvent(FuelRecipe newRecipe, int newBurnTicks, Block newBlock, String newFueler) {
        recipe = newRecipe;
        burnTicks = newBurnTicks;
        block = newBlock;
        fueler = newFueler;
    }

    /**
     * @return RecipeManager's Fuel class recipe
     */
    public FuelRecipe getRecipe() {
        return recipe;
    }

    /**
     * Shortcut from: event.getRecipe().getFuel().getItemStack();
     *
     * @return fuel as ItemStack
     */
    public ItemStack getFuel() {
        return recipe.getIngredient();
    }

    /**
     * Get the ticks that the furnace will run for.<br>
     * Can be different every time depending on the recipe.
     *
     * @return fuel burning ticks
     */
    public int getBurnTicks() {
        return burnTicks;
    }

    /**
     * Change the ticks that the furnace will burn for
     *
     * @param burnTicks
     *            time in ticks
     */
    public void setBurnTicks(int newBurnTicks) {
        burnTicks = newBurnTicks;
    }

    /**
     * @return furnace block of the involved event
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Get the player's name that initially placed the fuel.<br>
     * Can be null in certain situations!
     *
     * @return fueler's name
     */
    public String getFuelerName() {
        return fueler;
    }

    /**
     * Get the Player object of the player that placed the fuel.<br>
     * NOTE: This returns null if player is not online or plugin couldn't get the player's name, use getFuelerName() to get his name only.
     *
     * @return Player object of the fueler
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
