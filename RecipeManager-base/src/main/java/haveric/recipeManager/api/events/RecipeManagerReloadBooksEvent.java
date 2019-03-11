package haveric.recipeManager.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Triggered before Recipe Books are regenerated.<br>
 * Useful when editing/adding recipe books through plugins because reloading recipe books erases all previous recipe books.
 */
public class RecipeManagerReloadBooksEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private CommandSender sender;

    public RecipeManagerReloadBooksEvent(CommandSender newSender) {
        sender = newSender;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSender getSender() {
        return sender;
    }
}
