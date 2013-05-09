package ro.thehunters.digi.recipeManager.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Triggered <b>before</b> Recipe Books are regenerated.<br>
 * Useful when editing/adding recipe books through plugins because reloading recipe books erases all previous recipe books.
 * 
 * @author Digi
 */
public class RMReloadBooksEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    private CommandSender sender;
    
    public RMReloadBooksEvent(CommandSender sender)
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
