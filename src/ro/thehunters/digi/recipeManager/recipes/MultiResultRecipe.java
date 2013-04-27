package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.flags.ArgBuilder;
import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class MultiResultRecipe extends BaseRecipe
{
    private List<ItemResult> results = new ArrayList<ItemResult>();
    
    protected MultiResultRecipe()
    {
    }
    
    public MultiResultRecipe(BaseRecipe recipe)
    {
        super(recipe);
        
        if(recipe instanceof MultiResultRecipe)
        {
            MultiResultRecipe r = (MultiResultRecipe)recipe;
            
            results = new ArrayList<ItemResult>(r.getResults().size());
            
            for(ItemResult i : r.getResults())
            {
                results.add(i.clone());
            }
        }
    }
    
    public MultiResultRecipe(Flags flags)
    {
        super(flags);
    }
    
    public boolean hasResults()
    {
        return !results.isEmpty();
    }
    
    /**
     * @return results list, never null.
     */
    public List<ItemResult> getResults()
    {
        return results;
    }
    
    /**
     * @param a
     * @return list of cloned results available for specified arguments
     */
    /* TODO remove ?
    public List<ItemResult> getResults(Args a)
    {
        List<ItemResult> list = new ArrayList<ItemResult>(results.size());
        
        for(ItemResult r : results)
        {
            r = r.clone();
            a = ArgBuilder.create(a).result(r).build();
            
            if(r.checkFlags(a))
            {
                list.add(r);
            }
        }
        
        return list;
    }
    */
    
    /**
     * @param results
     *            the results list or null if you want to clear results
     */
    public void setResults(List<ItemResult> results)
    {
        if(results == null)
        {
            this.results.clear();
            return;
        }
        
        this.results = results;
        
        for(ItemResult r : this.results)
        {
            r.setRecipe(this);
        }
    }
    
    /**
     * Removes all other results and add the specified result.
     * 
     * @param result
     */
    public void setResult(ItemStack result)
    {
        results.clear();
        addResult(result);
    }
    
    /**
     * Adds the specified result to the list.
     * 
     * @param result
     *            result item, must not be null.
     */
    public void addResult(ItemStack result)
    {
        Validate.notNull(result, "The 'result' argument must not be null!");
        
        if(result instanceof ItemResult)
        {
            results.add(((ItemResult)result).setRecipe(this));
        }
        else
        {
            results.add(new ItemResult(result).setRecipe(this));
        }
    }
    
    public String getResultsString()
    {
        StringBuilder s = new StringBuilder();
        
        int resultNum = getResults().size();
        
        if(resultNum > 0)
        {
            ItemStack result = getFirstResult();
            
            if(result != null)
            {
                if(result.getAmount() > 1)
                {
                    s.append("x").append(result.getAmount()).append(" ");
                }
                
                s.append(result.getType().toString().toLowerCase());
                
                if(result.getDurability() != 0)
                {
                    s.append(":").append(result.getDurability());
                }
                
                if(resultNum > 1)
                {
                    s.append(" +").append(resultNum - 1).append(" more");
                }
            }
            else
            {
                s.append("nothing");
            }
        }
        else
        {
            s.append("no result");
        }
        
        return s.toString();
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
            {
                return r.getChance();
            }
        }
        
        return 0;
    }
    
    /**
     * @return the first valid result as a clone or null.
     */
    public ItemResult getFirstResult()
    {
        // TODO remove ?
        /*
        for(ItemResult r : results)
        {
            if(r.getTypeId() != 0 && !r.hasFlag(FlagType.SECRET))
            {
                return r;
            }
        }
        
        // if no non-secret result was found, then we must return something...
        */
        for(ItemResult r : results)
        {
            if(r.getTypeId() != 0)
            {
                return r.clone();
            }
        }
        
        return null; // no valid results defined
    }
    
    /**
     * Get a random result from the list.<br>
     * Returns AIR if failure chance occured.
     * 
     * @param a
     *            dynamic arguments, use {@link ArgBuilder#create()} to build arguments for this.
     * @return the result as a clone, never null.
     */
    public ItemResult getResult(Args a)
    {
        a.clear();
        
        List<ItemResult> list = new ArrayList<ItemResult>();
        float maxChance = 0;
        
        for(ItemResult r : results)
        {
            a.clear();
            
            if(r.checkFlags(a))
            {
                list.add(r);
                maxChance += r.getChance();
            }
        }
        
        ItemResult result = null;
        float rand = RecipeManager.random.nextFloat() * maxChance;
        float chance = 0;
        
        for(ItemResult r : list)
        {
            if((chance += r.getChance()) >= rand)
            {
                result = r.clone();
                break;
            }
        }
        
        a.clear();
        a.setResult(result);
        result.sendPrepare(a);
        
        return result;
    }
}
