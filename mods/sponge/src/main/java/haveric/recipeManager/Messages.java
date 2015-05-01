package haveric.recipeManager;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;

public class Messages {

    public static void send(CommandSource sender, String message) {
        send(sender, Texts.of(message));
    }
    
    public static void send(CommandSource sender, Text text) {
        if (sender == null) {
            sender = RecipeManager.getGame().getServer().getConsole();
        }
        
        if (sender instanceof ConsoleSource) {
            text = Texts.of("[RecipeManager] ", text);
        }
    
        sender.sendMessage(text);
    }
    
    public static void sendAndLog(CommandSource sender, String message) {
        if (sender instanceof Player) {
            send(sender, message);
        }
        
        send(null, message);
    }
}
