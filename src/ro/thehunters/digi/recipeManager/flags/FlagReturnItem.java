package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Vanilla;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

public class FlagReturnItem extends Flag
{
    private Map<String, ItemStack> returnItems = new HashMap<String, ItemStack>();
    
    public FlagReturnItem()
    {
        type = FlagType.RETURNITEM;
    }
    
    public FlagReturnItem(FlagReturnItem flag)
    {
        this();
        
        // TODO clone
    }
    
    @Override
    public FlagReturnItem clone()
    {
        return new FlagReturnItem(this);
    }
    
    @Override
    protected boolean onValidate()
    {
        BaseRecipe recipe = getRecipe();
        
        if(recipe instanceof WorkbenchRecipe == false)
        {
            
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split(">", 2);
        
        if(split.length != 2)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " does not have 2 items separated by > character.");
        }
        
        ItemStack key = Tools.convertStringToItemStack(split[0], Vanilla.DATA_WILDCARD, true, false, false);
        ItemStack replace = Tools.convertStringToItemStack(split[1], Vanilla.DATA_WILDCARD, true, false, false);
        
        if(key == null || replace == null)
        {
            return false;
        }
        
        String string = key.getTypeId() + (key.getDurability() == Vanilla.DATA_WILDCARD ? "" : ":" + key.getDurability());
        
        returnItems.put(string, replace);
        
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(!a.hasInventory() || a.inventory() instanceof CraftingInventory == false)
        {
            return false;
        }
        
        CraftingInventory inv = (CraftingInventory)a.inventory();
        
        for(ItemStack i : inv.getMatrix())
        {
            if(i == null || i.getTypeId() == 0)
            {
                continue;
            }
            
            ItemStack replace = returnItems.get(i.getTypeId() + ":" + i.getDurability());
            
            if(replace == null)
            {
                replace = returnItems.get(i.getTypeId());
            }
            
            if(replace != null)
            {
                
            }
        }
        
        return true;
    }
}