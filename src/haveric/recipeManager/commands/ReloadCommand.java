package haveric.recipeManager.commands;

import haveric.recipeManager.RecipeManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
         * boolean force = false;
         * 
         * if(args.length > 0) { for(String arg : args) { arg = arg.toLowerCase();
         * 
         * if(arg.equals("force")) { force = true; } else { Messages.send(sender, "<red>Unknown argument: " + arg); } } }
         */

        RecipeManager.getPlugin().reload(sender, false);

        return true;
    }
}
