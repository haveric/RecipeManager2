package ro.thehunters.digi.recipeManager.commands;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;

public class FindItemCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length <= 0)
        {
            Messages.CMD_RMFINDITEM_USAGE.print(sender, null, "{command}", label);
            return true;
        }
        
        ArrayList<Material> found = new ArrayList<Material>();
        String find = args[0].trim();
        Material match = null;
        
        if(find.equalsIgnoreCase("this"))
        {
            if(sender instanceof Player == false)
            {
                sender.sendMessage("Only players can use the 'this' argument.");
                return true;
            }
            
            ItemStack item = ((Player)sender).getItemInHand();
            
            if(item == null || item.getTypeId() == 0)
            {
                Messages.CMD_RMFINDITEM_INVALIDHELDITEM.print(sender);
                return true;
            }
            
            match = item.getType();
        }
        else
        {
            match = Material.matchMaterial(find);
        }
        
        if(match == null)
        {
            find = find.toUpperCase();
            
            for(Material material : Material.values())
            {
                if(material.name().contains(find))
                    found.add(material);
            }
        }
        else
        {
            found.add(match);
        }
        
        int foundSize = found.size();
        
        if(foundSize > 0)
        {
            Messages.CMD_RMFINDITEM_HEADER.print(sender, null, "{matches}", foundSize, "{argument}", find);
            
            for(Material material : found)
            {
                Messages.CMD_RMFINDITEM_LIST.print(sender, null, "{id}", material.getId(), "{material}", material.name().toLowerCase(), "{maxdata}", material.getMaxDurability(), "{maxstack}", material.getMaxStackSize());
            }
        }
        else
        {
            Messages.CMD_RMFINDITEM_NOTFOUND.print(sender, null, "{argument}", find);
        }
        
        return true;
    }
}
