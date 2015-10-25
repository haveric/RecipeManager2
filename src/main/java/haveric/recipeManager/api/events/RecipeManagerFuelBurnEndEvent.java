package haveric.recipeManager.api.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import haveric.recipeManager.recipes.FuelRecipe;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;

public class RecipeManagerFuelBurnEndEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private FuelRecipe recipe;
    private Furnace furnace;
    private String fueler;

    public RecipeManagerFuelBurnEndEvent(FuelRecipe newRecipe, Furnace newFurnace, String newFueler) {
        recipe = newRecipe;
        furnace = newFurnace;
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
     * @return furnace block of the involved event
     */
    public Furnace getFurnace() {
        return furnace;
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
            } catch (Exception e) { }
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
