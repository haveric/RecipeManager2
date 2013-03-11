package ro.thehunters.digi.recipeManager.commands;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Recipes;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;

public class RecipeCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        RecipeOwner filterOwner = null;
        String filterAdder = null;
        boolean filterRemoved = false;
        
        if(args.length > 0)
        {
            for(String arg : args)
            {
                arg = arg.toLowerCase();
                
                if(arg.equals("recipemanager") || arg.equals("minecraft") || arg.equals("unknown"))
                {
                    filterOwner = RecipeOwner.valueOf(arg.toUpperCase());
                }
                else if(arg.startsWith("remove"))
                {
                    filterRemoved = true;
                }
                else
                {
                    filterAdder = arg;
                }
            }
        }
        
        String format = "%-12s%-15s%-10s%s";
        Messages.send(sender, String.format(format, "RecipeType", "Owner", "Status", "Adder"));
        
        BaseRecipe recipe;
        RecipeInfo info;
        
        for(Entry<BaseRecipe, RecipeInfo> e : Recipes.getInstance().getRecipeList().entrySet())
        {
            recipe = e.getKey();
            info = e.getValue();
            
            /*
            if(filterAdder != null && (info.getAdder() == null || !info.getAdder().toLowerCase().equals(filterAdder)))
                continue;
            
            if(filterOwner != null && info.getOwner() != filterOwner)
                continue;
            */
            
            if(info.getAdder() == null)
                continue;
            
            Messages.send(sender, String.format(format, recipe.getRecipeType(), info.getOwner(), info.getStatus(), info.getAdder()));
        }
        
        Messages.send(sender, ChatColor.GREEN + "END OF LIST");
        
        return true;
    }
}