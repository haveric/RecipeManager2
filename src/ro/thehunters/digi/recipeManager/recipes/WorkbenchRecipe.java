package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.flags.Args;
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
    
    public ItemResult getFirstResult()
    {
        for(ItemResult r : results)
        {
            if(r != null)
                return r;
        }
        
        return null;
    }
    
    public ItemResult getDisplayResult(Args a)
    {
        a.clear();
        
        if(!checkFlags(a))
        {
            List<String> lore = new ArrayList<String>();
            
            for(String r : a.reasons())
            {
                lore.add(Messages.CRAFT_RESULT_DENIED_REASON.get("{reason}", r));
            }
            
            return Tools.generateItemStackWithMeta(Material.FIRE, 0, 0, Messages.CRAFT_RESULT_DENIED_TITLE.get(), lore);
        }
        
        if(results.size() == 1)
        {
            ItemResult result = getFirstResult();
            
            if(result.checkFlags(a))
            {
                result.sendPrepare(a);
                return result;
            }
            
            return null;
        }
        
        List<ItemResult> displayResults = new ArrayList<ItemResult>();
        float failChance = 0;
        int secretNum = 0;
        float secretChance = 0;
        int unavailableNum = 0;
        float unavailableChance = 0;
        
        for(ItemResult r : results)
        {
            if(r.getTypeId() == 0)
            {
                failChance = r.getChance();
            }
            else if(r.hasFlag(FlagType.SECRET))
            {
                secretNum++;
                secretChance += r.getChance();
            }
            else
            {
                a.clearReasons();
                
                if(r.checkFlags(a))
                {
                    r.sendPrepare(a);
                    displayResults.add(r);
                }
                else
                {
                    unavailableNum++;
                    unavailableChance += r.getChance();
                }
            }
        }
        
//        Messages.debug("recipe displays: " + ArrayUtils.toString(displayResults));
//        Messages.debug("recipe unavailable: " + ArrayUtils.toString(unavailableResults));
        
        List<String> lore = new ArrayList<String>();
        String FORMAT_CHANCE = "%2.0f%%";
        
        for(ItemResult r : displayResults)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_ITEM.get("{chance}", String.format(FORMAT_CHANCE, r.getChance()), "{item}", Tools.printItemStack(r)));
        }
        
        if(failChance > 0)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_NOTHING.get("{chance}", String.format(FORMAT_CHANCE, failChance)));
        }
        
        if(secretNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_SECRETS.get("{chance}", String.format(FORMAT_CHANCE, secretChance), "{num}", String.valueOf(secretNum)));
        }
        
        if(unavailableNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_UNAVAILABLE.get("{chance}", String.format(FORMAT_CHANCE, unavailableChance), "{num}", String.valueOf(unavailableNum)));
        }
        
        return Tools.generateItemStackWithMeta(Material.SKULL, 3, 0, Messages.CRAFT_RESULT_RECIEVE_TITLE.get(), lore);
    }
    
    public ItemResult getResult(Args a)
    {
        a.clear();
        
        List<ItemResult> pickFromResults = new ArrayList<ItemResult>();
        int maxChance = 0;
        
        for(ItemResult r : results)
        {
            a.clear();
            
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
        
        a.clear();
        
        if(!checkFlags(a))
        {
            Messages.debug("recipe failed: " + ArrayUtils.toString(a.reasons()));
            
            a.sendReasons(a.player(), Messages.CRAFT_FLAG_PREFIX_RESULT);
            
            return null;
        }
        
        return result;
    }
}