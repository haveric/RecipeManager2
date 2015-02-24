package haveric.recipeManager.api.events;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * Triggered when RecipeManager is fully enabled.<br>
 * It is useful if you need to add recipes through this plugin.
 */
public class RecipeManagerEnabledEvent extends Event {
    private static HandlerList handlers = new HandlerList();

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
        return Settings.getInstance();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
