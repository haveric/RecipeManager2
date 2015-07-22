package haveric.recipeManager.commands;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManagerCommon.RMCChatColor;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class HelpCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PluginDescriptionFile desc = RecipeManager.getPlugin().getDescription();

        Messages.send(sender, RMCChatColor.YELLOW + "------ " + RMCChatColor.WHITE + desc.getFullName() + RMCChatColor.GRAY + " by haveric / Digi " + RMCChatColor.YELLOW + "------");

        Map<String, Map<String, Object>> cmds = desc.getCommands();
        Map<String, Object> data;

        for (Entry<String, Map<String, Object>> e : cmds.entrySet()) {
            data = e.getValue();
            Object obj = data.get("permission");

            if (obj instanceof String && !sender.hasPermission((String) obj)) {
                continue;
            }

            Messages.send(sender, "<gold>" + data.get("usage").toString().replace("<command>", e.getKey()) + ": " + RMCChatColor.RESET + data.get("description"));
        }

        return true;
    }
}
