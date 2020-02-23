package haveric.recipeManager.commands;

import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.Version;
import haveric.recipeManager.common.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FindItemCommand implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            Messages.getInstance().send(sender, "cmd.finditem.usage", "{command}", label);
            return true;
        }

        List<Material> found = new ArrayList<>();
        String find = args[0].trim();

        if (find.equalsIgnoreCase("this")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use the 'this' argument.");
                return true;
            }
            Player player = (Player) sender;
            ItemStack item;
            if (Version.has1_12Support()) {
                item = player.getInventory().getItemInMainHand();
            } else {
                item = ((Player) sender).getItemInHand();
            }

            if (item == null || item.getType() == Material.AIR) {
                Messages.getInstance().send(sender, "cmd.finditem.invalidhelditem");
                return true;
            }

            found.add(item.getType());
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
                Messages.getInstance().send(sender, "cmd.finditem.list", "{material}", m.name().toLowerCase(), "{maxdata}", m.getMaxDurability(), "{maxstack}", m.getMaxStackSize());
            }

            if (foundSize > 10) {
                Messages.getInstance().send(sender, "cmd.finditem.foundmore", "{matches}", (foundSize - 10));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String currentInput = args[0].toLowerCase();
            if ("this".contains(currentInput)) {
                list.add("this");
            }

            for (Material mat : Material.values()) {
                String originalName = mat.name().toLowerCase();
                if (originalName.contains(currentInput) ) {
                    list.add(originalName);
                }
            }
        }
        return list;
    }
}
