package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeBooks;
import haveric.recipeManager.data.RecipeBook;
import haveric.recipeManager.messages.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class BooksCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Map<String, RecipeBook> books = RecipeBooks.getInstance().getBooks();

        if (books.isEmpty()) {
            Messages.getInstance().send(sender, "cmd.books.nobooks");
            return true;
        }

        Messages.getInstance().send(sender, "cmd.books.header", "{number}", books.size());

        for (RecipeBook book : books.values()) {
            Messages.getInstance().send(sender, "cmd.books.item", "{title}", book.getTitle(), "{volumes}", book.getVolumesNum());
        }

        return true;
    }
}
