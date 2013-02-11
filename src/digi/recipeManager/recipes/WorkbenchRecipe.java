package digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.RecipeManager;
import digi.recipeManager.recipes.flags.Flags;

public class WorkbenchRecipe extends RmRecipe
{
    private List<ItemResult> results;
    
    protected WorkbenchRecipe()
    {
    }
    
    public WorkbenchRecipe(RmRecipe recipe)
    {
        super(recipe);
    }
    
    public WorkbenchRecipe(Flags flags)
    {
        super(flags);
    }
    
    public boolean hasResults()
    {
        return results != null && !results.isEmpty();
    }
    
    public List<ItemResult> getResults()
    {
        return results;
    }
    
    public void setResult(ItemStack result)
    {
        results = new ArrayList<ItemResult>();
        results.add(new ItemResult(result, 100));
    }
    
    public void setResults(List<ItemResult> results)
    {
        this.results = results;
    }
    
    public ItemStack getCraftResult(Player player)
    {
        if(results.size() == 1)
            return getFirstResult();
        
        int maxChance = 0;
        
        for(ItemResult item : results)
        {
            if(item.canCraftResult(player))
                maxChance += item.getChance();
        }
        
        int rand = RecipeManager.rand.nextInt(maxChance);
        int chance = 0;
        ItemStack result = null;
        
        for(ItemResult item : results)
        {
            if((chance += item.getChance()) > rand)
            {
                result = item;
                break;
            }
        }
        
        return result;
    }
    
    public ItemStack getFirstResult()
    {
        return results.get(0);
    }
    
    public ItemStack getResult(Player player, Location location, boolean display)
    {
        /*
        if(results.size() == 1)
            return getFirstResult();
        
        ItemStack item = new ItemStack(Material.PORTAL, 0);
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<String>();
        int secretChance = 0;
        
        for(ItemResult result : results)
        {
            if(result.canSeeResult(player))
                secretChance += result.getChance();
            else
                lore.add(result.print());
        }
        
        if(!lore.isEmpty())
        {
            lore.add(String.format("%s %3d%% %sSecret item(s)...", ChatColor.DARK_RED, secretChance, ChatColor.RED));
            meta.setDisplayName(ChatColor.GOLD + "You will get a random item:");
            meta.setLore(lore);
        }
        else
            meta.setDisplayName(ChatColor.GOLD + "You will get an unknown item!");
        
        item.setItemMeta(meta);
        
        return item;
        */
        
        // TODO
        
        return getFirstResult();
    }
}