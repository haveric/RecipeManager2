package ro.thehunters.digi.recipeManager.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Recipes;
import ro.thehunters.digi.recipeManager.Settings;

/**
 * Triggered when RecipeManager is fully enabled.<br> It is useful if you need to add recipes through this plugin.
 */
public class RecipeManagerEnabledEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public RecipeManagerEnabledEvent() {
    }

    /**
     * @return RecipeManager.getPlugin();
     */
    public RecipeManager getPlugin() {
        return RecipeManager.getPlugin();
    }

    /**
     * @return RecipeManager.getRecipes();
     */
    public Recipes getRecipes() {
        return RecipeManager.getRecipes();
    }

    /**
     * @return RecipeManager.getSettings();
     */
    public Settings getSettings() {
        return RecipeManager.getSettings();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
