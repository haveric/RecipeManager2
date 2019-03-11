package haveric.recipeManager.api.events;

import haveric.recipeManager.recipes.FuelRecipe;
import org.bukkit.Bukkit;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RecipeManagerFuelBurnRandomEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private FuelRecipe recipe;
    private Furnace furnace;
    private UUID fuelerUUID;

    public RecipeManagerFuelBurnRandomEvent(FuelRecipe newRecipe, Furnace newFurnace, UUID newFuelerUUID) {
        recipe = newRecipe;
        furnace = newFurnace;
        fuelerUUID = newFuelerUUID;
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
     * @return furnace block of the involved event
     */
    public Furnace getFurnace() {
        return furnace;
    }

    /**
     * Get the player's UUID that initially placed the fuel.<br>
     * Can be null in certain situations!
     *
     * @return fueler's UUID
     */
    public UUID getFuelerUUID() {
        return fuelerUUID;
    }

    /**
     * Get the Player object of the player that placed the fuel.<br>
     * NOTE: This returns null if player is not online or plugin couldn't get the player's UUID, use getFuelerUUID() to get his UUID only.
     *
     * @return Player object of the fueler
     */
    public Player getFueler() {
        Player player = null;
        if (fuelerUUID != null) {
            player = Bukkit.getPlayer(fuelerUUID);
        }

        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean newCancelled) {
        cancelled = newCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
