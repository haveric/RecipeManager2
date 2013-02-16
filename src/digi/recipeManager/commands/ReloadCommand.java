package digi.recipeManager.commands;

import org.bukkit.command.*;

import digi.recipeManager.Messages;
import digi.recipeManager.RecipeManager;

public class ReloadCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        boolean check = false;
        
        if(args.length > 0)
        {
            for(String arg : args)
            {
                if(arg.equalsIgnoreCase("check"))
                {
                    check = false;
                }
                else
                {
                    Messages.send(sender, "<red>Unknown argument: " + arg);
                }
            }
        }
        
        RecipeManager.getPlugin().reload(sender, check);
        return true;
    }
}