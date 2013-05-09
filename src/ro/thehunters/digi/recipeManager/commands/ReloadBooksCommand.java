package ro.thehunters.digi.recipeManager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.RecipeManager;

public class ReloadBooksCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        RecipeManager.getRecipeBooks().reload(sender);
        
        return true;
    }
}
