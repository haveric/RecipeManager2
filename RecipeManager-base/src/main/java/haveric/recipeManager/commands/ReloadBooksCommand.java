package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeBooks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadBooksCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RecipeBooks.getInstance().reload(sender);
        RecipeBooks.getInstance().reloadAfterRecipes(sender);

        return true;
    }
}
