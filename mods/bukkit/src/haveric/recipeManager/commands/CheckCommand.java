package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CheckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RecipeManager.getPlugin().reload(sender, true, false);

        return true;
    }
}
