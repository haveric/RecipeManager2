package haveric.recipeManager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import haveric.recipeManager.Messages;
import haveric.recipeManager.Updater;
import haveric.recipeManagerCommon.RMCChatColor;

public class UpdateCommand implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        Messages.sendAndLog(sender, RMCChatColor.GRAY + "Checking for updates...");

        Updater.updateOnce(sender);

        return true;
    }
}
