package ro.thehunters.digi.recipeManager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;

public class ReloadCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        boolean check = false;
        boolean force = false;
        
        if(args.length > 0)
        {
            for(String arg : args)
            {
                if(arg.equalsIgnoreCase("check"))
                {
                    check = true;
                }
                else if(arg.equalsIgnoreCase("force"))
                {
                    force = true;
                }
                else
                {
                    Messages.send(sender, "<red>Unknown argument: " + arg);
                }
            }
        }
        
        RecipeManager.getPlugin().reload(sender, check, force);
        return true;
    }
}