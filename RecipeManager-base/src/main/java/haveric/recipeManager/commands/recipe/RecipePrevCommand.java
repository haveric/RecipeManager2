package haveric.recipeManager.commands.recipe;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RecipePrevCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UUID playerUUID = null;
        if (sender instanceof Player) {
            playerUUID = ((Player) sender).getUniqueId();
        }

        Pages pages = RecipePagination.get(playerUUID);
        if (pages == null) {
            Messages.getInstance().send(sender, "cmd.recipes.needquery","{command}", "/rmrecipes");
        } else {
            if (pages.hasPrev()) {
                String page = pages.prev();
                Messages.getInstance().send(sender, "cmd.recipes.header", "{item}", ToolsItem.print(pages.getItem()), "{num}", (pages.getPage() + 1), "{total}", pages.getNumPages());
                MessageSender.getInstance().send(sender, page);

                if (pages.hasNext()) {
                    Messages.getInstance().send(sender, "cmd.recipes.more", "{cmdnext}", "/rmnext", "{cmdprev}", "/rmprev");
                } else {
                    Messages.getInstance().send(sender, "cmd.recipes.end");
                }
            } else {
                Messages.getInstance().send(sender, "cmd.recipes.noprev", "{command}", "/rmnext");
            }
        }

        return true;
    }
}
