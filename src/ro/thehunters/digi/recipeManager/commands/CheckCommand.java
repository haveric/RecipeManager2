package ro.thehunters.digi.recipeManager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.RecipeManager;

public class CheckCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        RecipeManager.getPlugin().reload(sender, true);
        
        return true;
    }
}
