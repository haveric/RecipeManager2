package ro.thehunters.digi.recipeManager.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Triggered before Recipe Books are regenerated.<br>
 * Useful when editing/adding recipe books through plugins because reloading recipe books erases all previous recipe books.
 * 
 * @author Digi
 */
public class RecipeManagerReloadBooksEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    private CommandSender sender;
    
    public RecipeManagerReloadBooksEvent(CommandSender sender)
    {
        this.sender = sender;
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
    
    public CommandSender getSender()
    {
        return sender;
    }
}
