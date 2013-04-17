package ro.thehunters.digi.recipeManager.recipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.flags.ArgBuilder;
import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.FlagDebug;
import ro.thehunters.digi.recipeManager.flags.FlagIngredientCondition;
import ro.thehunters.digi.recipeManager.flags.FlagIngredientCondition.Conditions;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class WorkbenchRecipe extends MultiResultRecipe
{
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
            /*
            List<String> lore = new ArrayList<String>();
            
            for(String r : a.reasons())
            {
                lore.add(Messages.CRAFT_RESULT_DENIED_REASON.get("{reason}", r));
            }
            
            return Tools.createItemStackWithMeta(Material.FIRE, 0, 0, Messages.CRAFT_RESULT_DENIED_TITLE.get(), lore);
            */
            
            FlagDebug.grabReasons(a.playerName(), this, a.reasons());
            
            a.sendReasons(a.player(), Messages.FLAG_PREFIX_RECIPE.get());
            
            return Tools.createItemStackWithMeta(Material.FIRE, 0, 0, Messages.CRAFT_RESULT_DENIED_TITLE.get(), Messages.CRAFT_RESULT_DENIED_INFO.get());
        }
        
        /*
        if(!isMultiResult())
        {
            ItemResult result = getFirstResult();
            
            if(result == null)
            {
                return null;
            }
            
            if(!result.hasFlag(FlagType.SECRET))
            {
                if(result.checkFlags(a))
                {
                    result.sendPrepare(a);
                    
                    a.sendEffects(a.player(), Messages.FLAG_PREFIX_RESULT.get("{item}", Tools.printItem(result)));
                    
                    return result;
                }
                else
                {
                    a.sendReasons(a.player(), Messages.FLAG_PREFIX_RESULT.get("{item}", Tools.printItem(result)));
                }
                
                return null;
            }
        }
        */
        
        List<ItemResult> displayResults = new ArrayList<ItemResult>();
        float failChance = 0;
        int secretNum = 0;
        float secretChance = 0;
        int unavailableNum = 0;
        float unavailableChance = 0;
        
        for(ItemResult r : getResults())
        {
            r = r.clone();
            
            if(r.getTypeId() == 0)
            {
                failChance = r.getChance();
            }
            else
            {
                a.clearReasons();
                
                if(r.checkFlags(a))
                {
                    a = ArgBuilder.create().inventory(a.inventory()).location(a.location()).player(a.player()).player(a.playerName()).recipe(a.recipe()).recipe(a.recipeType()).result(r).build();
                    
                    // TODO come up with an idea to fix @forchance/@forpermission and/or result flags!!!!!
                    // TODO clone results
                    
                    r.sendPrepare(a);
                    
                    if(r.hasFlag(FlagType.SECRET))
                    {
                        secretNum++;
                        secretChance += r.getChance();
                    }
                    else
                    {
                        displayResults.add(r);
                        
                        a.sendEffects(a.player(), Messages.FLAG_PREFIX_RESULT.get("{item}", Tools.printItem(r)));
                    }
                }
                else
                {
                    unavailableNum++;
                    unavailableChance += r.getChance();
                    
                    if(!r.hasFlag(FlagType.SECRET))
                    {
                        FlagDebug.grabReasons(a.playerName(), r, a.reasons());
                        
                        a.sendReasons(a.player(), Messages.FLAG_PREFIX_RESULT.get("{item}", Tools.printItem(r)));
                    }
                }
            }
        }
        
//        Messages.debug("recipe displays: " + ArrayUtils.toString(displayResults));
//        Messages.debug("recipe unavailable: " + ArrayUtils.toString(unavailableResults));
        
        if(displayResults.size() == 1 && secretNum == 0 && unavailableNum == 0 && failChance == 0)
        {
            return displayResults.get(0);
        }
        
        List<String> lore = new ArrayList<String>();
        String title = null;
        boolean recieve = (secretNum + displayResults.size()) > 0;
        
        if(recieve)
        {
            title = Messages.CRAFT_RESULT_RECIEVE_TITLE_RANDOM.get();
        }
        else
        {
            title = Messages.CRAFT_RESULT_NORECIEVE_TITLE.get();
            lore.add(Messages.CRAFT_RESULT_DENIED_INFO.get());
        }
        
        for(ItemResult r : displayResults)
        {
            lore.add(Messages.CRAFT_RESULT_LIST_ITEM.get("{chance}", formatChance(r.getChance()), "{item}", Tools.printItem(r), "{clone}", (r.hasFlag(FlagType.CLONEINGREDIENT) ? Messages.FLAG_CLONE_RESULTDISPLAY.get() : "")));
        }
        
        if(failChance > 0)
        {
            lore.add(Messages.CRAFT_RESULT_LIST_FAILURE.get("{chance}", formatChance(failChance)));
        }
        
        if(secretNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_LIST_SECRETS.get("{chance}", formatChance(secretChance), "{num}", String.valueOf(secretNum)));
        }
        
        if(unavailableNum > 0)
        {
            lore.add(Messages.CRAFT_RESULT_LIST_UNAVAILABLE.get("{chance}", formatChance(unavailableChance), "{num}", String.valueOf(unavailableNum)));
        }
        
        return Tools.createItemStackWithMeta(recieve ? Material.CHEST : Material.FIRE, 0, 0, title, lore);
    }
    
    private String formatChance(float chance)
    {
        return chance == 100 ? "100%" : String.format((Math.round(chance) == chance ? "%4.0f%%" : "%4.1f%%"), chance);
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
                    Conditions cond = flag.getIngredientConditions(item);
                    
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
