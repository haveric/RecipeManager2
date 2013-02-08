package digi.recipeManager.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import digi.recipeManager.RecipeManager;

public class MultiResultRecipe extends RmRecipe
{
    private List<ItemResult> results;
    
    protected MultiResultRecipe()
    {
    }
    
    public MultiResultRecipe(RmRecipe recipe)
    {
        super(recipe);
    }
    
    public MultiResultRecipe(Flags flags)
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
    
    public ItemStack getDisplayResult(Player player)
    {
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
    }
}