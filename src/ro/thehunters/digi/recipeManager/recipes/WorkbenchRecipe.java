package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.flags.Arguments;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class WorkbenchRecipe extends BaseRecipe
{
    private List<ItemResult> results;
    
    protected WorkbenchRecipe()
    {
    }
    
    public WorkbenchRecipe(BaseRecipe recipe)
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
    
    public ItemStack getFirstResult()
    {
        return results.get(0);
    }
    
    public ItemStack getDisplayResult(Player player, String playerName, Location location)
    {
        playerName = (player == null ? playerName : player.getName());
        Arguments a = new Arguments(player, playerName, location, getRecipeType(), null);
        
        if(!checkFlags(a))
        {
            return Tools.generateItemStackWithMeta(Material.FIRE, 0, 1, Messages.CRAFT_RESULT_FAILED_TITLE.get(), a.reasons());
        }
        
        if(results.size() == 1)
        {
            return getFirstResult();
        }
        
        List<ItemResult> displayResults = new ArrayList<ItemResult>();
        Map<ItemResult, String> unallowedResults = new HashMap<ItemResult, String>();
        ItemResult result = null;
        int secretChance = 0;
        int secretNum = 0;
        int unallowedChance = 0;
        int failChance = 0;
        
        for(ItemResult r : results)
        {
            if(r.getTypeId() == 0)
            {
                failChance = r.getChance();
            }
            else if(r.hasFlag(FlagType.SECRET))
            {
                secretChance += r.getChance();
                secretNum++;
            }
            else
            {
                a.clearReasons();
                
                if(r.checkFlags(a))
                {
                    displayResults.add(r);
                }
                else
                {
                    unallowedResults.put(r, a.reasons().get(0));
                    unallowedChance += r.getChance();
                }
            }
        }
        
        Messages.debug("recipe displays: " + ArrayUtils.toString(displayResults));
        Messages.debug("recipe unallowed: " + ArrayUtils.toString(unallowedResults));
        
        List<String> lore = new ArrayList<String>();
        
        for(ItemResult r : displayResults)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_ITEM.get("{chance}", String.format("%3d%%", r.getChance()), "{item}", Tools.printItemStack(r)));
        }
        
        if(failChance > 0)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_NOTHING.get("{chance}", String.format("%3d%%", failChance)));
        }
        
        if(secretNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_SECRETS.get("{chance}", String.format("%3d%%", secretChance), "{num}", "" + secretNum));
        }
        
        if(unallowedResults.size() > 0)
        {
            if(hasFlag(FlagType.HIDERESULTS))
            {
                lore.add(Messages.CRAFT_RESULT_UNALLOWED_HIDDEN.get("{chance}", String.format("%3d%%", unallowedChance)));
            }
            else
            {
                lore.add("");
                lore.add(Messages.CRAFT_RESULT_UNALLOWED_TITLE.get());
                
                for(Entry<ItemResult, String> entry : unallowedResults.entrySet())
                {
                    lore.add(Messages.CRAFT_RESULT_UNALLOWED_ITEM.get("{chance}", String.format("%3d%%", entry.getKey().getChance()), "{item}", Tools.printItemStack(entry.getKey()), "{reason}", entry.getValue()));
                }
            }
        }
        
        return Tools.generateItemStackWithMeta(Material.PORTAL, 0, 1, Messages.CRAFT_RESULT_RECIEVE_TITLE.get(), lore);
    }
    
    public ItemResult getResult(Player player, String playerName, Location location)
    {
        List<ItemResult> pickFromResults = new ArrayList<ItemResult>();
        playerName = (player == null ? playerName : player.getName());
        Arguments a = new Arguments(player, playerName, location, getRecipeType(), null);
        int maxChance = 0;
        
        for(ItemResult r : results)
        {
            a.clearReasons();
            
            if(r.checkFlags(a))
            {
                pickFromResults.add(r);
                maxChance += r.getChance();
            }
        }
        
        ItemResult result = null;
        int rand = RecipeManager.random.nextInt(maxChance);
        int chance = 0;
        
        for(ItemResult r : results)
        {
            if((chance += r.getChance()) > rand)
            {
                result = r;
                break;
            }
        }
        
        a = new Arguments(player, playerName, location, getRecipeType(), result);
        
        if(!checkFlags(a))
        {
            Messages.debug("recipe failed: " + ArrayUtils.toString(a.reasons()));
            
            a.sendReasons(a.getPlayer());
            
            return null;
        }
        
        if(result != null && !result.applyFlags(a))
        {
            result = null;
        }
        
        if(result != null)
        {
            a.sendEffects(a.getPlayer());
        }
        else
        {
            sendFailed(a);
            
            a.sendReasons(a.getPlayer());
        }
        
        return result;
    }
}