package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<ItemResult> results = new ArrayList<ItemResult>();
    
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
    
    public void setResults(List<ItemResult> results)
    {
        this.results = results;
        
        for(ItemResult r : this.results)
        {
            r.setRecipe(this);
        }
    }
    
    public void setResult(ItemStack result)
    {
        results.clear();
        addResult(result);
    }
    
    public void addResult(ItemStack result)
    {
        if(result instanceof ItemResult)
            results.add(((ItemResult)result).setRecipe(this));
        else
            results.add(new ItemResult(result).setRecipe(this));
    }
    
    public ItemStack getFirstResult()
    {
        return results.get(0);
    }
    
    public ItemStack getDisplayResult(Player player, Location location)
    {
        Arguments a = new Arguments(player, null, location, getRecipeType(), null);
        
        if(!checkFlags(a))
        {
            return Tools.generateItemStackWithMeta(Material.FIRE, 0, 1, Messages.CRAFT_RESULT_FAILED_TITLE.get(), a.reasons());
        }
        
        if(results.size() == 1)
        {
            return getFirstResult();
        }
        
        List<ItemResult> displayResults = new ArrayList<ItemResult>();
        Map<ItemResult, String> unavailableResults = new HashMap<ItemResult, String>();
        int secretNum = 0;
        float secretChance = 0;
        float unavailableChance = 0;
        float failChance = 0;
        
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
                    unavailableResults.put(r, a.reasons().get(0));
                    unavailableChance += r.getChance();
                }
            }
        }
        
        Messages.debug("recipe displays: " + ArrayUtils.toString(displayResults));
        Messages.debug("recipe unavailable: " + ArrayUtils.toString(unavailableResults));
        
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
        
        if(unavailableResults.size() > 0)
        {
            lore.add(Messages.CRAFT_RESULT_UNAVAILABLE.get("{num}", unavailableResults.size() + "", "{chance}", String.format("%3d%%", unavailableChance)));
            
            /*
            lore.add("");
            lore.add(Messages.CRAFT_RESULT_UNAVAILABLE_TITLE.get());
            
            for(Entry<ItemResult, String> entry : unavailableResults.entrySet())
            {
                lore.add(Messages.CRAFT_RESULT_UNAVAILABLE_ITEM.get("{chance}", String.format("%3d%%", entry.getKey().getChance()), "{item}", Tools.printItemStack(entry.getKey()), "{reason}", entry.getValue()));
            }
            */
        }
        
        return Tools.generateItemStackWithMeta(Material.SKULL, 0, 1, Messages.CRAFT_RESULT_RECIEVE_TITLE.get(), lore);
    }
    
    public ItemResult getResult(Player player, Location location)
    {
        List<ItemResult> pickFromResults = new ArrayList<ItemResult>();
        Arguments a = new Arguments(player, null, location, getRecipeType(), null);
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
        
        a = new Arguments(player, null, location, getRecipeType(), result);
        
        if(!checkFlags(a))
        {
            Messages.debug("recipe failed: " + ArrayUtils.toString(a.reasons()));
            
            a.sendReasons(a.player());
            
            return null;
        }
        
        if(result != null && !result.applyFlags(a))
        {
            result = null;
        }
        
        if(result != null)
        {
            a.sendEffects(a.player());
        }
        else
        {
            sendFailed(a);
            
            a.sendReasons(a.player());
        }
        
        return result;
    }
}