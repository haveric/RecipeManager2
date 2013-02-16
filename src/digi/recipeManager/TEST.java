package digi.recipeManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TEST implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Bukkit.getScheduler().cancelTasks(RecipeManager.getPlugin());
        
        System.out.print("Executing cancel task...");
        
        return true;
    }
}