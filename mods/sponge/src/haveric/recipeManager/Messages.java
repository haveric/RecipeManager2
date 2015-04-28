package haveric.recipeManager;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.mod.SpongeMod;

public class Messages {

    public static void send(CommandSource sender, Text... messages) {
        if (sender == null) {
            SpongeMod.instance.getLogger().info(messages.toString());
            //SpongeMod.instance.getLogger().info(message);
        } else {
            sender.sendMessage(messages);
            //sender.sendMessage(message);
        }
    }
}
