package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.data.RecipeBook;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class GetBookCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command can only be used by players.");
            return true;
        }

        int argsLength = args.length;
        if (argsLength <= 0) {
            Messages.getInstance().send(sender, "cmd.getbook.usage", "{command}", label);
            return true;
        }

        Player player = (Player) sender;

        StringBuilder s = new StringBuilder(argsLength * 10);

        s.append(args[0]);

        for (int i = 1; i < argsLength; i++) {
            s.append(' ').append(args[i]);
        }

        String bookName = s.toString();
        int volume = 1;
        int index = bookName.lastIndexOf('#');

        if (index > 0) { // found and not the first character
            try {
                volume = Integer.parseInt(bookName.substring(index + 1));
            } catch (Throwable e) {
                Messages.getInstance().send(sender, "cmd.getbook.invalidnumber");
            }

            bookName = bookName.substring(0, index).trim();
        }

        List<RecipeBook> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(bookName);

        if (books.isEmpty()) {
            Messages.getInstance().send(sender, "cmd.getbook.notexist", "{arg}", bookName);
            return true;
        } else if (books.size() > 1) {
            Messages.getInstance().send(sender, "cmd.getbook.manymatches", "{num}", books.size(), "{arg}", bookName);

            for (RecipeBook b : books) {
                MessageSender.getInstance().send(sender, "<red> - <reset>" + b.getTitle());
            }

            return true;
        }

        ItemStack item = books.get(0).getBookItem(volume);

        player.getInventory().addItem(item);

        BookMeta meta = (BookMeta) item.getItemMeta();

        Messages.getInstance().send(sender, "cmd.getbook.given", "{title}", meta.getTitle());

        return true;
    }
}
