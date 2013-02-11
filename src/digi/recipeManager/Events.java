package digi.recipeManager;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import digi.recipeManager.api.RecipeManagerPrepareCraftEvent;
import digi.recipeManager.data.*;
import digi.recipeManager.recipes.*;
import digi.recipeManager.recipes.flags.Flags;

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
        try
        {
            Player player = (event.getView() == null ? null : (Player)event.getView().getPlayer());
            CraftingInventory inventory = event.getInventory();
            
            if(!RecipeManager.getPlugin().canCraft(player))
            {
                inventory.setResult(null);
                return;
            }
            
            Location location = getWorkbenchLocation(player);
            
            if(event.isRepair())
            {
                prepareRepair(player, inventory, location);
                return;
            }
            
            Recipe bukkitRecipe = event.getRecipe();
            
            if(bukkitRecipe == null)
                return;
            
            ItemStack result = inventory.getResult();
            ItemStack recipeResult = bukkitRecipe.getResult();
            
            if(!result.equals(recipeResult)) // result was processed by the game and it doesn't match the original recipe
            {
                if(RecipeManager.getSettings().SPECIAL_LEATHER_DYE && recipeResult.equals(BukkitRecipes.RECIPE_LEATHERDYE))
                {
                    Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                    inventory.setResult(null);
                    return;
                }
                
                if(RecipeManager.getSettings().SPECIAL_MAP_CLONING && recipeResult.equals(BukkitRecipes.RECIPE_MAPCLONE))
                {
                    Messages.CRAFT_SPECIAL_MAP_CLONING.print(player);
                    inventory.setResult(null);
                    return;
                }
                
                if(RecipeManager.getSettings().SPECIAL_MAP_EXTENDING && recipeResult.equals(BukkitRecipes.RECIPE_MAPEXTEND))
                {
                    Messages.CRAFT_SPECIAL_MAP_EXTENDING.print(player);
                    inventory.setResult(null);
                    return;
                }
                
                if(RecipeManager.getSettings().SPECIAL_FIREWORKS && recipeResult.equals(BukkitRecipes.RECIPE_FIREWORKS))
                {
                    Messages.CRAFT_SPECIAL_FIREWORKS.print(player);
                    inventory.setResult(null);
                    return;
                }
                
                System.out.print("[debug] Results don't match, special recipe ? " + recipeResult + " vs " + result);
            }
            
            RecipeManagerPrepareCraftEvent callEvent = null;
            WorkbenchRecipe recipe = null;
            
            if(bukkitRecipe instanceof ShapedRecipe)
                recipe = RecipeManager.recipes.getCraftRecipe(recipeResult);
            
            else if(bukkitRecipe instanceof ShapelessRecipe)
                recipe = RecipeManager.recipes.getCombineRecipe(recipeResult);
            
            else
                System.out.print("[debug] new recipe???!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            
            System.out.print("[debug] recipe = " + recipe + " | result = " + recipeResult);
            
            if(recipe == null)
                return;
            
            result = prepareCraftResult(player, inventory, recipe, location);
            callEvent = new RecipeManagerPrepareCraftEvent(recipe, result, player, location);
            
            Bukkit.getPluginManager().callEvent(callEvent);
            result = (callEvent.getResult() == null ? null : new ItemResult(callEvent.getResult()));
            
            /*
            if(result != null && !result.getFlags().check(player, location, true, true))
                result = null;
            */
            
            inventory.setResult(result == null ? null : result);
        }
        catch(Exception e)
        {
            event.getInventory().setResult(null);
            
            CommandSender sender = (event.getView() != null && event.getView().getPlayer() instanceof Player ? (Player)event.getView().getPlayer() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    // TODO add interact event
    
    private Location getWorkbenchLocation(Player player) throws Exception
    {
        if(!workbenchEvents || player == null)
            return null;
        
        int[] vec = workbench.get(player.getName());
        
        return (vec == null ? null : new Location(player.getWorld(), vec[0], vec[1], vec[2]));
    }
    
    private void prepareRepair(Player player, CraftingInventory inventory, Location location) throws Exception
    {
        if(!RecipeManager.settings.SPECIAL_REPAIR)
        {
            inventory.setResult(null);
            Messages.CRAFT_NOREPAIR.print(player);
            return;
        }
        
        ItemStack result = inventory.getRecipe().getResult();
        
        if(RecipeManager.settings.SPECIAL_REPAIR_ENCHANTED)
        {
            ItemStack[] matrix = inventory.getMatrix();
            ItemStack[] repaired = new ItemStack[2];
            int repair[] = new int[2];
            int repairIndex = 0;
            
            for(int i = 1; i < matrix.length; i++)
            {
                if(matrix[i] != null && matrix[i].getTypeId() != 0)
                {
                    repair[repairIndex] = i;
                    repaired[repairIndex] = matrix[i];
                    
                    if(++repairIndex > 1)
                        break;
                }
            }
            
            if(repaired[0] == null || repaired[1] == null)
                return;
            
            Map<Enchantment, Integer> enchantments = repaired[0].getEnchantments();
            
            if(enchantments.size() == 0)
                enchantments = repaired[1].getEnchantments();
            
            if(enchantments.size() > 0)
                inventory.getResult().addUnsafeEnchantments(enchantments);
            
            result = inventory.getResult();
        }
        
        RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(null, result, player, location);
        Bukkit.getPluginManager().callEvent(callEvent);
        
        inventory.setResult(callEvent.getResult());
    }
    
    private ItemStack prepareCraftResult(Player player, CraftingInventory inventory, WorkbenchRecipe recipe, Location location) throws Exception
    {
        /*
        if(!recipe.getFlags().check(player, location, true, true)) // !recipe.isUsableBy(player, true) || !recipe.isUsableBlocks(player, location, true, true) || !recipe.isUsableHeight(player, location, true, true))
            return null;
        */
        
        // TODO getbook and clone flags!
        /*
        Book getBook = recipe.getFlags().getGetBook();
        
        if(getBook != null)
        {
            ItemStack bookItem = RecipeManager.recipes.books.get(getBook);
            
            if(bookItem != null)
            {
                ItemStack result = bookItem.clone();
                result.setAmount(1);
                
                return new ItemResult(result);
            }
        }
        else
        {
            ItemStack copy = recipe.getFlags().getCopy();
            
            if(copy != null)
            {
                for(ItemStack item : inventory.getContents())
                {
                    if(ItemUtil.compare(item, copy))
                    {
                        ItemStack result = item.clone();
                        result.setAmount(1);
                        
                        return new ItemResult(result);
                    }
                }
            }
        }
        */
        
        return recipe.getResult(player, location, true);
    }
    
    @EventHandler
    public void eventCraft(CraftItemEvent event)
    {
        try
        {
            System.out.print("CraftItemEvent :: " + event.getCurrentItem());
            
            Player player = (event.getView() == null ? null : (Player)event.getView().getPlayer());
            Recipe bukkitRecipe = event.getRecipe();
            ItemStack recipeResult = bukkitRecipe.getResult();
            WorkbenchRecipe recipe = null;
            
            if(bukkitRecipe instanceof ShapedRecipe)
                recipe = RecipeManager.recipes.getCraftRecipe(recipeResult);
            
            else if(bukkitRecipe instanceof ShapelessRecipe)
                recipe = RecipeManager.recipes.getCombineRecipe(recipeResult);
            
            else
                System.out.print("[debug] new recipe???!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            
            if(recipe == null)
                return;
            
            if(recipe.getFlags().getLaunchFirework() != null)
            {
                // TODO this was just a test - use proper checks and stuff
                Firework ent = (Firework)player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                
                ent.setFireworkMeta(recipe.getFlags().getLaunchFirework());
            }
            
//            ItemResult result = craftResult(player, inventory, recipe, location);
//            callEvent = new RecipeManagerPrepareCraftEvent(recipe, result, player, location);
            
//            Bukkit.getPluginManager().callEvent(callEvent);
//            result = (callEvent.getResult() == null ? null : new ItemResult(callEvent.getResult()));
            
            /*
            if(result != null && !result.getFlags().check(player, location, true, true))
                result = null;
            */
            
//            inventory.setResult(result == null ? null : result);
        }
        catch(Exception e)
        {
            event.getInventory().setResult(null);
            
            CommandSender sender = (event.getView() != null && event.getView().getPlayer() instanceof Player ? (Player)event.getView().getPlayer() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventFurnaceBurn(FurnaceBurnEvent event)
    {
        try
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
        catch(Exception e)
        {
            event.setCancelled(true);
            Messages.error(null, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    @EventHandler
    public void eventFurnaceSmelt(FurnaceSmeltEvent event)
    {
        try
        {
            SmeltRecipe recipe = RecipeManager.getRecipes().getSmeltRecipe(event.getSource());
            
            if(recipe == null)
                return;
            
        }
        catch(Exception e)
        {
            event.setCancelled(true);
            Messages.error(null, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    // Monitor furnace ... stuff TODO
    
    // TODO Testing...
    protected HashMap<BlockID, MutableFloat> furnaceSmelting = new HashMap<BlockID, MutableFloat>();
//    private int                              furnaceTaskId   = 0;
    
//    private Set<BlockID>                     furnaceNotified = new HashSet<BlockID>();
    private Set<BlockID>                     furnaceStop     = new HashSet<BlockID>();
    
    private Map<BlockID, BlockFurnaceData>   furnaceData     = new HashMap<BlockID, BlockFurnaceData>();
    private Map<BlockID, int[]>              workbench       = null;
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
        /* CraftBukkit way
        if(chunk == null || RecipeManager.recipes.furnaceSmelting == null)
            return;
        
        net.minecraft.server.v1_4_R1.Chunk mcChunk = ((CraftChunk)chunk).getHandle();
        TileEntity tile;
        
        for(Object obj : mcChunk.tileEntities.values())
        {
            if(obj != null && obj instanceof TileEntity)
            {
                tile = (TileEntity)obj;
                
                if(tile instanceof TileEntityFurnace)
                {
                    if(add)
                        RecipeManager.recipes.furnaceSmelting.put(Recipes.locationToString(new Location(chunk.getWorld(), tile.x, tile.y, tile.z)), new MutableDouble());
                    else
                        RecipeManager.recipes.furnaceSmelting.remove(Recipes.locationToString(new Location(chunk.getWorld(), tile.x, tile.y, tile.z)));
                }
            }
        }
        */
        
        // TODO check if this has been fixed (no idea how though)
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