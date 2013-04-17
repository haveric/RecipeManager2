package ro.thehunters.digi.recipeManager;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import ro.thehunters.digi.recipeManager.data.BlockID;
import ro.thehunters.digi.recipeManager.data.FurnaceData;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

class FurnaceWorker implements Runnable
{
    private final float tickRate;
    private static BukkitTask task;
    
    protected static void init()
    {
    }
    
    private FurnaceWorker()
    {
        stop();
        
        int ticks = RecipeManager.getSettings().FURNACE_TICKS;
        tickRate = 10 * ticks;
        task = Bukkit.getScheduler().runTaskTimer(RecipeManager.getPlugin(), this, 0, ticks);
    }
    
    protected static void start()
    {
        if(!isRunning())
        {
            new FurnaceWorker();
        }
    }
    
    protected static void restart()
    {
        stop();
        new FurnaceWorker();
    }
    
    protected static boolean isRunning()
    {
        return task != null;
    }
    
    protected static void stop()
    {
        if(isRunning())
        {
            task.cancel();
            task = null;
        }
    }
    
    protected static void clean()
    {
        stop();
    }
    
    @Override
    public void run()
    {
        Iterator<Entry<BlockID, FurnaceData>> iterator = Furnaces.getFurnaces().entrySet().iterator();
        
        while(iterator.hasNext())
        {
            Entry<BlockID, FurnaceData> entry = iterator.next();
            FurnaceData data = entry.getValue();
            
            if(!data.isBurning())
            {
                continue;
            }
            
            Furnace furnace = convertBlockIdToFurnace(entry.getKey()); // convert blockID to Furnace block object
            
            if(furnace == null) // the burning furnace no longer exists for whatever reason
            {
                iterator.remove();
                continue;
            }
            
            FurnaceInventory inventory = furnace.getInventory();
            ItemStack smelt = inventory.getSmelting();
            
            if(smelt == null || smelt.getType() == Material.AIR) // if there's nothing to smelt, skip furnace
            {
                data.setCookProgress(0);
                continue;
            }
            
            SmeltRecipe recipe = RecipeManager.getRecipes().getSmeltRecipe(smelt);
            
            if(recipe == null || !recipe.hasCustomTime()) // No custom recipe for item or it has default time
            {
                data.setCookProgress(0);
                continue;
            }
            
            if(recipe.isMultiResult())
            {
                // TODO ...
            }
            else
            {
                ItemResult recipeResult = recipe.getFirstResult();
                ItemStack result = inventory.getResult();
                
                // If we have a result and it's not the same as what we're making or it's at max stack size then skip furnace
                if(result != null && (!recipeResult.isSimilar(result) || result.getAmount() >= result.getType().getMaxStackSize()))
                {
                    data.setCookProgress(0);
                    continue;
                }
            }
            
            if(recipe.getMinTime() <= 0.0) // instant smelting
            {
                furnace.setCookTime((short)200);
            }
            else
            {
                float progress = data.getCookProgress();
                
                if(progress >= 199 || (progress == 0 && furnace.getCookTime() == 0) || furnace.getCookTime() >= 199)
                {
                    data.setCookProgress(0);
                }
                else
                {
                    if(data.getCookTime() == null)
                    {
                        data.setCookTime(recipe.getCookTime());
                    }
                    
                    progress += (tickRate / data.getCookTime());
                    
                    data.setCookProgress(progress);
                    
                    furnace.setCookTime(data.getCookProgressForFurnace());
                    
                    if(recipe.hasFuel())
                    {
                        furnace.setBurnTime(Short.MAX_VALUE);
                    }
                }
            }
        }
    }
    
    private Furnace convertBlockIdToFurnace(BlockID blockID)
    {
        Block block = blockID.toBlock();
        
        if(block == null || block.getType() != Material.BURNING_FURNACE)
        {
            return null; // not a running furnace block type
        }
        
        BlockState blockState = block.getState();
        
        if(blockState instanceof Furnace == false)
        {
            return null; // not really a furnace
        }
        
        Furnace furnace = (Furnace)blockState;
        
        if(furnace.getBurnTime() <= 0)
        {
            return null; // furnace is not running
        }
        
        return furnace;
    }
}
