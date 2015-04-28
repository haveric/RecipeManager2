package haveric.recipeManager;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;

public class Messages {

    public static void send(CommandSource sender, String message) {
        if (sender == null) {
            //SpongeMod.instance.getLogger().info(message);
        } else {
            sender.sendMessage(Texts.of(message));
            //sender.sendMessage(message);
        }
    }
}
