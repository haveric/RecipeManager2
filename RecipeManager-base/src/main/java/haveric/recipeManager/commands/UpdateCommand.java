package haveric.recipeManager.commands;

import haveric.recipeManager.Updater;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManagerCommon.RMCChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateCommand implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        MessageSender.getInstance().sendAndLog(sender, RMCChatColor.GRAY + "Checking for updates...");

        Updater.updateOnce(sender);

        return true;
    }
}
