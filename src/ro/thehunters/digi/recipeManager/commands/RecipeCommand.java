package ro.thehunters.digi.recipeManager.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;

public class RecipeCommand implements CommandExecutor
{
    // TODO needs full rewrite
    
    private class PageData
    {
        public int page;
        public String[] args;
        
        public PageData(int page, String[] args)
        {
            this.page = page;
            this.args = args;
        }
    }
    
    private static final Map<String, PageData> pagination = new HashMap<String, PageData>();
    
    public static void clean(String name)
    {
        pagination.remove(name);
    }
    
    public static void clean()
    {
        pagination.clear();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (Player)sender;
        
        /*
        if(args.length == 0)
        {
            int mc = 0;
            int rm = 0;
            int other = 0;
            
            for(RecipeInfo info : RecipeManager.getRecipes().getRecipeList().values())
            {
                switch(info.getOwner())
                {
                    case MINECRAFT:
                        mc++;
                        break;
                    
                    case RECIPEMANAGER:
                        rm++;
                        break;
                    
                    default:
                        other++;
                }
            }
            
            // TODO to messages.yml
            Messages.send(sender, "<yellow>========== Recipes =========");
            Messages.send(sender, "Minecraft: <green>" + mc);
            Messages.send(sender, "RecipeManager: <green>" + rm);
            Messages.send(sender, "Other plugins: <green>" + other);
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("next"))
        {
            PageData page = pagination.get(sender.getName());
            
            if(page == null)
            {
                Messages.send(sender, "First you need to make a query.");
            }
            else
            {
                Messages.send(sender, queryRecipePage(sender, page.page, page.args));
            }
        }
        else
        {
            queryRecipePage(sender, 0, args);
        }
        */
        
        return true;
    }
    
    private String[] queryRecipePage(CommandSender sender, int page, String[] args)
    {
        ItemStack filterItem = null;
        
        if(args.length > 0)
        {
        }
        
        String format = "%-12s%-15s%-10s%s";
        Messages.send(sender, String.format(format, "RecipeType", "Owner", "Status", "Adder"));
        List<String> lines = new ArrayList<String>();
        BaseRecipe recipe;
        RecipeInfo info;
        
        for(Entry<BaseRecipe, RecipeInfo> e : RecipeManager.getRecipes().getRecipeList().entrySet())
        {
            recipe = e.getKey();
            info = e.getValue();
            
            /*
            if(filterAdder != null && (info.getAdder() == null || !info.getAdder().toLowerCase().equals(filterAdder)))
                continue;
            
            if(filterOwner != null && info.getOwner() != filterOwner)
                continue;
            
            if(info.getAdder() == null)
                continue;
            
            if(filterRemoved && info.getStatus() == RecipeStatus.REMOVED)
                continue;
            */
            
            String status = "<gray>Unknown";
            switch(info.getStatus())
            {
                case OVERRIDDEN:
                    status = "<yellow>overridden";
                    break;
                case QUEUED:
                    status = "<red>queued";
                    break;
                case REMOVED:
                    status = "<red>removed";
                    break;
            }
            
            lines.add(String.format(format, recipe.getType(), info.getOwner(), status, info.getAdder()));
        }
        
        int maxPages = (int)Math.ceil(lines.size() / 10.0);
        
        if(page > maxPages)
        {
            pagination.remove(sender.getName());
            
            return new String[]
            {
                "<gray>No more pages."
            };
        }
        
        String[] pageLines = new String[Math.min(lines.size() - (page * 10), 10)];
        
        for(int i = 0; i < pageLines.length; i++)
        {
            pageLines[i] = lines.get((page * 10) + i);
        }
        
        pagination.put(sender.getName(), new PageData(page, args));
        
        return pageLines;
    }
}
