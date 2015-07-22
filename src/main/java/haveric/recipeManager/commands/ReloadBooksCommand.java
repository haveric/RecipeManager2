package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadBooksCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RecipeManager.getRecipeBooks().reload(sender);

        return true;
    }
}
