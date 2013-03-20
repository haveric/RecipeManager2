package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FlagIngredientCondition extends Flag
{
    public FlagIngredientCondition()
    {
        type = FlagType.INGREDIENTCONDITION;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        Inventory inv = a.inventory();
        
        if(inv instanceof CraftingInventory)
        {
            CraftingInventory craftInv = (CraftingInventory)inv;
            
            for(ItemStack i : craftInv)
            {
                
            }
        }
        else if(inv instanceof FurnaceInventory)
        {
            
        }
        
//      a.addReason(globalMessage, customMessage, variables) // < TODO
    }
}