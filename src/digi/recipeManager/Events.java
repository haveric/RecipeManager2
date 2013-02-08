package digi.recipeManager;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import digi.recipeManager.data.*;

/**
 * RecipeManager handled events
 */
public class Events implements Listener
{
    public Events()
    {
        Bukkit.getPluginManager().registerEvents(this, RecipeManager.getPlugin());
        
        for(World world : Bukkit.getWorlds())
        {
            worldLoad(world);
        }
    }
    
    @EventHandler
    public void eventPrepareCraft(PrepareItemCraftEvent event)
    {
        if(event.isRepair() && RecipeManager.getSettings().SPECIAL_REPAIR)
        {
            event.getInventory().setResult(null);
            return;
        }
        
        CraftingInventory inventory = event.getInventory();
        Recipe bukkitRecipe = event.getRecipe();
        
        Player player = (Player)event.getView().getPlayer();
        ItemStack result = inventory.getResult();
        ItemStack recipeResult = bukkitRecipe.getResult();
        
        if(!result.equals(recipeResult)) // result was processed by the game and it doesn't match the original recipe
        {
            if(recipeResult.equals(BukkitRecipes.RECIPE_LEATHERDYE) && RecipeManager.getSettings().SPECIAL_LEATHER_DYE)
            {
                Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                inventory.setResult(null);
                return;
            }
            
            if(recipeResult.equals(BukkitRecipes.RECIPE_MAPCLONE) && RecipeManager.getSettings().SPECIAL_MAP_CLONING)
            {
                Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                inventory.setResult(null);
                return;
            }
            
            if(recipeResult.equals(BukkitRecipes.RECIPE_MAPEXTEND) && RecipeManager.getSettings().SPECIAL_MAP_EXTENDING)
            {
                Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                inventory.setResult(null);
                return;
            }
            
            if(recipeResult.equals(BukkitRecipes.RECIPE_FIREWORKS) && RecipeManager.getSettings().SPECIAL_FIREWORKS)
            {
                Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                inventory.setResult(null);
                return;
            }
        }
        
        if(bukkitRecipe instanceof ShapedRecipe)
        {
            CraftRecipe recipe = RecipeManager.recipes.getCraftRecipe(bukkitRecipe.getResult());
            
            if(recipe != null && recipe.isUsableBy(player))
            {
                player.sendMessage("[debug] Custom craft recipe!");
                prepareCraftingRecipe(player, inventory, recipe);
            }
        }
        else if(bukkitRecipe instanceof ShapelessRecipe)
        {
            CombineRecipe recipe = RecipeManager.recipes.getCombineRecipe(bukkitRecipe.getResult());
            
            if(recipe != null && recipe.isUsableBy(player))
            {
                player.sendMessage("[debug] Custom combine recipe!");
                prepareCraftingRecipe(player, inventory, recipe);
            }
        }
        else
        {
            System.out.print("[DEBUG] new recipe???!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
    
    private void prepareCraftingRecipe(Player player, CraftingInventory inventory, MultiResultRecipe recipe)
    {
        inventory.setResult(recipe.getDisplayResult(player));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventFurnaceBurn(FurnaceBurnEvent event)
    {
        BlockID blockID = new BlockID(event.getBlock());
        
        if(furnaceSmelting != null && !furnaceSmelting.containsKey(blockID))
            furnaceSmelting.put(blockID, new MutableFloat());
        
        FuelRecipe recipe = RecipeManager.getRecipes().getFuelRecipe(event.getFuel());
        
        if(recipe != null)
        {
            Flags flags = recipe.getFlags();
            
            event.setBurnTime(flags.isRemove() ? 0 : recipe.getBurnTicks());
            event.setBurning(flags.isRemove() ? false : true);
        }
    }
    
    @EventHandler
    public void eventFurnaceExtract(FurnaceExtractEvent event)
    {
    }
    
    @EventHandler
    public void eventFurnaceSmelt(FurnaceSmeltEvent event)
    {
        
    }
    
    // Monitor furnace ... stuff TODO
    
    // TODO Testing...
    protected HashMap<BlockID, MutableFloat> furnaceSmelting = new HashMap<BlockID, MutableFloat>();
//    private int                              furnaceTaskId   = 0;
    
//    private Set<BlockID>                     furnaceNotified = new HashSet<BlockID>();
    private Set<BlockID>                     furnaceStop     = new HashSet<BlockID>();
    
    private Map<BlockID, BlockFurnaceData>   furnaceData     = new HashMap<BlockID, BlockFurnaceData>();
//    private Map<BlockID, int[]>              workbench       = null;
    protected boolean                        workbenchEvents = false;
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        
        switch(block.getType())
        {
            case BURNING_FURNACE:
            case FURNACE:
            {
                BlockID blockID = new BlockID(block);
                
                furnaceStop.remove(blockID);
                furnaceData.remove(blockID);
                
                if(furnaceSmelting != null)
                    furnaceSmelting.remove(blockID);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventWorldLoad(WorldLoadEvent event)
    {
        if(furnaceSmelting == null)
            return;
        
        worldLoad(event.getWorld());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventChunkLoad(ChunkLoadEvent event)
    {
        if(furnaceSmelting == null)
            return;
        
        if(!event.isNewChunk())
            furnaceChunk(event.getChunk(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventChunkUnload(ChunkUnloadEvent event)
    {
        if(furnaceSmelting == null)
            return;
        
        furnaceChunk(event.getChunk(), false);
    }
    
    protected void worldLoad(World world)
    {
        Chunk chunks[] = world.getLoadedChunks();
        
        for(Chunk chunk : chunks)
        {
            furnaceChunk(chunk, true);
        }
    }
    
    private void furnaceChunk(Chunk chunk, boolean add)
    {
        if(chunk == null)
            return;
        
        BlockState[] tileEntities = chunk.getTileEntities();
        
        for(BlockState blockState : tileEntities)
        {
            if(blockState != null & blockState instanceof Furnace)
            {
                if(add)
                    furnaceSmelting.put(new BlockID(blockState.getLocation()), new MutableFloat());
                else
                    furnaceSmelting.remove(new BlockID(blockState.getLocation()));
            }
        }
    }
    
    // Remove marked items
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void inventoryClose(InventoryCloseEvent event)
    {
        for(ItemStack item : event.getPlayer().getInventory().getContents())
        {
            itemProcess(item);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerItemHeld(PlayerItemHeldEvent event)
    {
        itemProcess(event.getPlayer().getInventory().getItem(event.getNewSlot()));
    }
    
    private void itemProcess(ItemStack item)
    {
        if(item == null || !item.hasItemMeta())
            return;
        
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        
        if(lore == null || lore.isEmpty())
            return;
        
        int index = lore.size() - 1;
        
        if(lore.get(index).startsWith(Recipes.RECIPE_ID_STRING))
            lore.remove(index);
    }
}