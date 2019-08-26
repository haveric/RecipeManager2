package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeBooks;
import haveric.recipeManager.data.RecipeBook;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetBookCommand implements TabExecutor {
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

        List<RecipeBook> books = RecipeBooks.getInstance().getBooksPartialMatch(bookName);

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        int argsLength = args.length;

        if (argsLength > 0) {
            StringBuilder s = new StringBuilder(argsLength * 10);

            s.append(args[0]);
            for (int i = 1; i < argsLength; i++) {
                s.append(' ').append(args[i]);
            }

            String searchString = s.toString().toLowerCase();

            int hashIndex = searchString.lastIndexOf('#');

            Map<String, RecipeBook> books = RecipeBooks.getInstance().getBooks();

            if (hashIndex > -1) {
                if (hashIndex == searchString.length() - 1) {
                    String bookName = searchString.substring(0, hashIndex).trim();
                    RecipeBook book = books.get(bookName);

                    if (book != null) {
                        for (int i = 1; i <= book.getVolumesNum(); i++) {
                            list.add("" + i);
                        }
                    }
                }
            } else {
                for (Map.Entry<String, RecipeBook> entry : books.entrySet()) {
                    String bookId = entry.getKey();

                    RecipeBook book = books.get(searchString.trim());
                    if (book == null) {
                        if (bookId.contains(searchString)) {
                            list.add(bookId);
                        }
                    } else if (searchString.endsWith(" ")) {
                        for (int i = 1; i <= book.getVolumesNum(); i++) {
                            list.add("#" + i);
                        }
                    }
                }
            }
        }

        return list;
    }
}
