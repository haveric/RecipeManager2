package digi.recipeManager;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.data.*;
import digi.recipeManager.recipes.SmeltRecipe;

public class FurnaceWorker implements Runnable
{
    private float ticks;
    
    protected FurnaceWorker(int ticks)
    {
        this.ticks = (float)(10.0 / ticks);
    }
    
    @Override
    public void run()
    {
        Iterator<Entry<BlockID, MutableFloat>> iterator = RecipeManager.events.furnaceSmelting.entrySet().iterator();
        Entry<BlockID, MutableFloat> entry;
        Furnace furnace;
        FurnaceInventory inventory;
        ItemStack smelt;
        ItemStack result;
        ItemStack recipeResult;
        SmeltRecipe recipe;
        float time;
        
        while(iterator.hasNext())
        {
            entry = iterator.next();
            furnace = convertBlockIdToFurnace(entry.getKey()); // convert location string to Furnace block object
            
            if(furnace == null) // the burning furnace no longer exists by whatever reason
            {
                iterator.remove();
                continue;
            }
            
            inventory = furnace.getInventory();
            smelt = inventory.getSmelting();
            
            if(smelt == null || smelt.getType() == Material.AIR) // if there's nothing to smelt, skip furnace
            {
                entry.getValue().value = 0;
                continue;
            }
            
            recipe = RecipeManager.getRecipes().getSmeltRecipe(smelt);
            
            if(recipe == null || recipe.getMinTime() <= -1.0) // No custom recipe for item or it has default time
            {
                entry.getValue().value = 0;
                continue;
            }
            
            recipeResult = recipe.getResult();
            result = inventory.getResult();
            
            // If we have a result and it's not the same as what we're making or it's at max stack size then skip furnace
            if(result != null && (!recipeResult.isSimilar(result) || result.getAmount() >= result.getType().getMaxStackSize()))
            {
                entry.getValue().value = 0;
                continue;
            }
            
            if(recipe.getMinTime() <= 0.0) // instant smelting
            {
                furnace.setCookTime((short)200);
            }
            else
            {
                time = entry.getValue().value;
                
                if(time >= 200 || furnace.getCookTime() == 0 || furnace.getCookTime() >= 200)
                {
                    entry.getValue().value = 0;
                }
                else
                {
                    time = time + (ticks / recipe.getCookTime());
                    furnace.setCookTime((short)Math.min(Math.max(Math.round(time), 1), 199));
                    entry.getValue().value = time;
                }
            }
        }
        
        iterator = null;
    }
    
    private Furnace convertBlockIdToFurnace(BlockID blockID)
    {
        Block block = blockID.toBlock();
        
        if(block == null || block.getType() != Material.BURNING_FURNACE) // not a running furnace
            return null;
        
        BlockState blockState = block.getState();
        
        if(!(blockState instanceof Furnace)) // not really a furnace
            return null;
        
        Furnace furnace = (Furnace)blockState;
        
        if(furnace.getBurnTime() <= 0) // furnace is not really running
            return null;
        
        return furnace;
    }
}
