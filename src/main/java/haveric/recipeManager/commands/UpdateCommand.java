package haveric.recipeManager.commands;

import haveric.recipeManager.Messages;
import haveric.recipeManager.Updater;
import haveric.recipeManagerCommon.RMCChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages.sendAndLog(sender, RMCChatColor.GRAY + "Checking for updates...");

        Updater.query(sender);

        return true;
    }
}
