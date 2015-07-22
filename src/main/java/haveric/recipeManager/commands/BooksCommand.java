package haveric.recipeManager.commands;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.data.RecipeBook;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BooksCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Map<String, RecipeBook> books = RecipeManager.getRecipeBooks().getBooks();

        if (books.isEmpty()) {
            Messages.CMD_BOOKS_NOBOOKS.print(sender);
            return true;
        }

        Messages.CMD_BOOKS_HEADER.print(sender, null, "{number}", books.size());

        for (RecipeBook book : books.values()) {
            Messages.CMD_BOOKS_ITEM.print(sender, null, "{title}", book.getTitle(), "{volumes}", book.getVolumesNum());
        }

        return true;
    }
}
