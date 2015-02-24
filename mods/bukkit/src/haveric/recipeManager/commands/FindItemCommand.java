package haveric.recipeManager.commands;

import haveric.recipeManager.Messages;
import haveric.recipeManager.tools.Tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class FindItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            Messages.CMD_FINDITEM_USAGE.print(sender, null, "{command}", label);
            return true;
        }

        List<Material> found = new ArrayList<Material>();
        String find = args[0].trim();

        if (find.equalsIgnoreCase("this")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use the 'this' argument.");
                return true;
            }

            ItemStack item = ((Player) sender).getItemInHand();

            if (item == null || item.getType() == Material.AIR) {
                Messages.CMD_FINDITEM_INVALIDHELDITEM.print(sender);
                return true;
            }

            found.add(item.getType());
        } else {
            int id = 0;

            try {
                id = Integer.parseInt(find);
            } catch (NumberFormatException e) {
                // TODO: Handle error
            }

            if (id > 0) {
                Material mat = Material.getMaterial(id);

                if (mat == null) {
                    Messages.CMD_FINDITEM_NOTFOUND.print(sender, null, "{argument}", id);
                } else {
                    found.add(mat);
                }
            }
        }

        if (found.isEmpty()) {
            find = Tools.parseAliasName(find);

            for (Material mat : Material.values()) {
                String matName = Tools.parseAliasName(mat.name());

                if (matName.contains(find)) {
                    found.add(mat);
                }
            }
        }

        if (found.isEmpty()) {
            Messages.CMD_FINDITEM_NOTFOUND.print(sender, null, "{argument}", find);
        } else {
            int foundSize = found.size();
            Messages.CMD_FINDITEM_HEADER.print(sender, null, "{matches}", foundSize, "{argument}", find);

            for (int i = 0; i < Math.min(foundSize, 10); i++) {
                Material m = found.get(i);
                Messages.CMD_FINDITEM_LIST.print(sender, null, "{id}", m.getId(), "{material}", m.name().toLowerCase(), "{maxdata}", m.getMaxDurability(), "{maxstack}", m.getMaxStackSize());
            }

            if (foundSize > 10) {
                Messages.CMD_FINDITEM_FOUNDMORE.print(sender, null, "{matches}", (foundSize - 10));
            }
        }

        return true;
    }
}
