package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
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
        Validate.notNull(result);
        
        if(result instanceof ItemResult)
            results.add(((ItemResult)result).setRecipe(this));
        else
            results.add(new ItemResult(result).setRecipe(this));
    }
    
    /**
     * @return true if recipe has more than 1 result or has failure chance (2 results, one being air), otherwise false.
     */
    public boolean isMultiResult()
    {
        return results.size() > 1;
    }
    
    /**
     * @return failure chance or 0 if it can not fail.
     */
    public float getFailChance()
    {
        for(ItemResult r : results)
        {
            if(r.getTypeId() == 0)
                return r.getChance();
        }
        
        return 0;
    }
    
    /**
     * @return the first valid result or null.
     */
    public ItemResult getFirstResult()
    {
        for(ItemResult r : results)
        {
            if(r.getTypeId() != 0)
                return r;
        }
        
        return null;
    }
    
    /**
     * Generate a display result for showing off all results (if available).
     * 
     * @param a
     * @return the result if it's only one or a special multi-result information item
     */
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
        
        if(!isMultiResult())
        {
            ItemResult result = getFirstResult();
            
            if(result == null)
                return null;
            
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
        
        return Tools.generateItemStackWithMeta(Material.PORTAL, 3, 0, Messages.CRAFT_RESULT_RECIEVE_TITLE.get(), lore);
    }
    
    public ItemResult getResult(Args a)
    {
        a.clear();
        
        List<ItemResult> pickResults = new ArrayList<ItemResult>();
        int maxChance = 0;
        
        for(ItemResult r : results)
        {
            a.clear();
            
            if(r.checkFlags(a))
            {
                pickResults.add(r);
                maxChance += r.getChance();
            }
        }
        
        int rand = RecipeManager.random.nextInt(maxChance);
        int chance = 0;
        ItemResult result = null;
        
        for(ItemResult r : pickResults)
        {
            if((chance += r.getChance()) > rand)
            {
                result = r;
                break;
            }
        }
        
        a.clear();
        return result;
    }
}