package digi.recipeManager;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.inventory.Recipe;

public class TEST implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        int recipes = 0;
        
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        
        while(iterator.hasNext())
        {
            iterator.next();
            recipes++;
        }
        
        System.out.print("Recipes = " + recipes);
        
        return true;
    }
}