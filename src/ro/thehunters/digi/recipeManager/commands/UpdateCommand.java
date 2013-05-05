package ro.thehunters.digi.recipeManager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.UpdateChecker;

public class UpdateCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Messages.sendAndLog(sender, ChatColor.GRAY + "Checking for updates...");
        
        new UpdateChecker(sender);
        
        return true;
    }
}
