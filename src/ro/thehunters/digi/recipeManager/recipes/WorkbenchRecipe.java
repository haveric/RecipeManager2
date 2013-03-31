package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.FlagIngredientCondition;
import ro.thehunters.digi.recipeManager.flags.FlagIngredientCondition.Conditions;
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
            if(r.getTypeId() != 0 && !r.hasFlag(FlagType.SECRET))
            {
                return r;
            }
        }
        
        // if no non-secret result was found, then we must return something...
        for(ItemResult r : results)
        {
            if(r.getTypeId() != 0)
            {
                return r;
            }
        }
        
        return null; // no valid results defined
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
            
            return Tools.createItemStackWithMeta(Material.FIRE, 0, 0, Messages.CRAFT_RESULT_DENIED_TITLE.get(), lore);
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
        
        for(ItemResult r : displayResults)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_ITEM.get("{chance}", formatChance(r.getChance()), "{item}", Tools.printItem(r), "{clone}", (r.hasFlag(FlagType.CLONEINGREDIENT) ? Messages.FLAG_CLONE_RESULTDISPLAY.get() : "")));
        }
        
        if(failChance > 0)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_NOTHING.get("{chance}", formatChance(failChance)));
        }
        
        if(secretNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_RECIEVE_SECRETS.get("{chance}", formatChance(secretChance), "{num}", String.valueOf(secretNum)));
        }
        
        if(unavailableNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_UNAVAILABLE.get("{chance}", formatChance(unavailableChance), "{num}", String.valueOf(unavailableNum)));
        }
        
        return Tools.createItemStackWithMeta(Material.PORTAL, 3, 0, Messages.CRAFT_RESULT_RECIEVE_TITLE.get(), lore);
    }
    
    private String formatChance(float chance)
    {
        return chance == 100 ? "100%" : String.format((Math.round(chance) == chance ? "%4.0f%%" : "%4.1f%%"), chance);
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
    
    public int getCraftableTimes(CraftingInventory inv)
    {
        int craftAmount = inv.getMaxStackSize();
        
        for(ItemStack i : inv.getMatrix())
        {
            if(i != null && i.getTypeId() != 0)
            {
                craftAmount = Math.min(i.getAmount(), craftAmount);
            }
        }
        
        return craftAmount;
    }
    
    public void subtractIngredients(CraftingInventory inv, boolean onlyExtra)
    {
        FlagIngredientCondition flag = (hasFlag(FlagType.INGREDIENTCONDITION) ? getFlag(FlagIngredientCondition.class) : null);
        
        for(int i = 1; i < 10; i++)
        {
            ItemStack item = inv.getItem(i);
            
            if(item != null)
            {
                int amt = item.getAmount();
                int newAmt = amt;
                
                if(flag != null)
                {
                    Conditions cond = flag.getConditions(item);
                    
                    if(cond != null && cond.getAmount() > 0)
                    {
                        Messages.debug("flag removed amount " + cond.getAmount() + " from " + Tools.printItem(item));
                        
                        newAmt -= cond.getAmount();
                    }
                }
                
                if(!onlyExtra)
                {
                    newAmt -= 1;
                    
                    Messages.debug("extra removed amount 1 from " + Tools.printItem(item));
                }
                
                if(amt != newAmt)
                {
                    if(newAmt > 0)
                    {
                        item.setAmount(newAmt);
                    }
                    else
                    {
                        inv.clear(i);
                    }
                }
            }
        }
    }
}