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
    
    // TODO maybe this or better yet try ranged data values ?
    
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
            CraftingInventory cInv = (CraftingInventory)inv;
            
            ItemStack[] matrix = cInv.getMatrix();
            
        }
        else if(inv instanceof FurnaceInventory)
        {
            
        }
        
//      a.addReason(globalMessage, customMessage, variables)
    }
}