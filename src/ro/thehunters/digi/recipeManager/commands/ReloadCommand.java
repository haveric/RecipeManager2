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
        boolean books = false;
        boolean check = false;
        boolean force = false;
        
        if(args.length > 0)
        {
            for(String arg : args)
            {
                arg = arg.toLowerCase();
                
                if(arg.equals("force"))
                {
                    force = true;
                }
                else if(arg.equals("check"))
                {
                    check = true;
                }
                else if(arg.equals("books"))
                {
                    books = true;
                }
                else
                {
                    Messages.send(sender, "<red>Unknown argument: " + arg);
                }
            }
        }
        
        if(books)
        {
            if(check || force)
            {
                Messages.send(sender, "<yellow>NOTE<reset> Using 'check' or 'force' arguments with 'books' does nothing.");
            }
            
            RecipeManager.getRecipeBooks().reload(sender);
        }
        else
        {
            RecipeManager.getPlugin().reload(sender, check, force);
        }
        
        return true;
    }
}
