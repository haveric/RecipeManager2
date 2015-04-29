package haveric.recipeManager;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;

public class Messages {

    public static void send(CommandSource sender, String message) {
        send(sender, Texts.of(message));
    }
    
    public static void send(CommandSource sender, Text text) {
        if (sender == null) {
            RecipeManager.getGame().getServer().getConsole().sendMessage(text);
        } else {
            sender.sendMessage(text);
        }
    }
}
