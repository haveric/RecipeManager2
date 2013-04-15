package ro.thehunters.digi.recipeManager.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;

public class HelpCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        PluginDescriptionFile desc = RecipeManager.getPlugin().getDescription();
        
        Messages.send(sender, ChatColor.YELLOW + "---------- " + ChatColor.WHITE + desc.getFullName() + ChatColor.GRAY + " by Digi " + ChatColor.YELLOW + "----------");
        
        Map<String, Map<String, Object>> cmds = desc.getCommands();
        Map<String, Object> data;
        
        for(Entry<String, Map<String, Object>> e : cmds.entrySet())
        {
            data = e.getValue();
            Object obj = data.get("permission");
            
            if(obj != null && obj instanceof String)
            {
                if(!sender.hasPermission((String)obj))
                {
                    continue;
                }
            }
            
            Messages.send(sender, "<gold>" + data.get("usage").toString().replace("<command>", e.getKey()) + ": " + ChatColor.RESET + data.get("description"));
        }
        
        return true;
    }
}
