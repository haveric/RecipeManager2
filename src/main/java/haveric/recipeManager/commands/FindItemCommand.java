package haveric.recipeManager.commands;

import haveric.recipeManager.messages.Messages;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FindItemCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            Messages.getInstance().send(sender, "cmd.finditem.usage", "{command}", label);
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
                Messages.getInstance().send(sender, "cmd.finditem.invalidhelditem");
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
                    Messages.getInstance().send(sender, "cmd.finditem.notfound", "{argument}", id);
                } else {
                    found.add(mat);
                }
            }
        }

        if (found.isEmpty()) {
            find = RMCUtil.parseAliasName(find);

            for (Material mat : Material.values()) {
                String matName = RMCUtil.parseAliasName(mat.name());

                if (matName.contains(find)) {
                    found.add(mat);
                }
            }
        }

        if (found.isEmpty()) {
            Messages.getInstance().send(sender, "cmd.finditem.notfound", "{argument}", find);
        } else {
            int foundSize = found.size();
            Messages.getInstance().send(sender, "cmd.finditem.header", "{matches}", foundSize, "{argument}", find);

            for (int i = 0; i < Math.min(foundSize, 10); i++) {
                Material m = found.get(i);
                Messages.getInstance().send(sender, "cmd.finditem.list", "{id}", m.getId(), "{material}", m.name().toLowerCase(), "{maxdata}", m.getMaxDurability(), "{maxstack}", m.getMaxStackSize());
            }

            if (foundSize > 10) {
                Messages.getInstance().send(sender, "cmd.finditem.foundmore", "{matches}", (foundSize - 10));
            }
        }

        return true;
    }
}
