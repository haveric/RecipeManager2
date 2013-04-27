package ro.thehunters.digi.recipeManager.commands;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.data.Book;

public class BooksCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Map<String, Book> books = RecipeManager.getRecipeBooks().getBooks();
        
        if(books.isEmpty())
        {
            Messages.CMD_BOOKS_NOBOOKS.print(sender);
            return true;
        }
        
        Messages.CMD_BOOKS_HEADER.print(sender, null, "{number}", books.size());
        
        for(Book book : books.values())
        {
            Messages.CMD_BOOKS_ITEM.print(sender, null, "{title}", book.getTitle(), "{volumes}", book.getVolumes().length);
        }
        
        return true;
    }
}
