package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import ro.thehunters.digi.recipeManager.api.RecipeManagerCraftEvent;
import ro.thehunters.digi.recipeManager.api.RecipeManagerPrepareCraftEvent;
import ro.thehunters.digi.recipeManager.data.BlockFurnaceData;
import ro.thehunters.digi.recipeManager.data.BlockID;
import ro.thehunters.digi.recipeManager.data.MutableFloat;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;
import ro.thehunters.digi.recipeManager.recipes.flags.RecipeFlags;

/**
 * RecipeManager handled events
 */
public class Events implements Listener
{
    @Override
    protected void finalize() throws Throwable // TODO REMOVE
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + getClass().getName() + " :: finalize()");
        
        super.finalize();
    }
    
    protected Events()
    {
        // evets are registered in the reload() method
        
        for(World world : Bukkit.getWorlds())
        {
            worldLoad(world);
        }
    }
    
    protected static void reload(CommandSender sender)
    {
        HandlerList.unregisterAll(RecipeManager.events);
        Bukkit.getPluginManager().registerEvents(RecipeManager.events, RecipeManager.getPlugin());
    }
    
    @EventHandler
    public void eventPlayerIntereact(PlayerInteractEvent event)
    {
        switch(event.getAction())
        {
            case RIGHT_CLICK_BLOCK:
            {
                Location l = event.getClickedBlock().getLocation();
                
                playerWorkbench.put(event.getPlayer().getName(), new int[] { l.getBlockX(), l.getBlockY(), l.getBlockZ() });
                
                return;
            }
            
            case PHYSICAL:
                return;
                
            default:
            {
                playerWorkbench.remove(event.getPlayer().getName());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventInventoryClose(InventoryCloseEvent event)
    {
        if(event.getView().getType() == InventoryType.WORKBENCH)
        {
            Player player = (Player)event.getView().getPlayer();
            playerWorkbench.remove(player.getName());
        }
    }
    
    // TODO maybe tackle anvil stuff ?
    /*
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventInventoryClick(final InventoryClickEvent event)
    {
        if(event.getView().getType() != InventoryType.ANVIL)
            return;
        
        final Player player = (Player)event.getView().getPlayer();
        
        if(event.getSlotType() == SlotType.CRAFTING)
        {
            event.setCancelled(true);
            player.updateInventory();
        }
        
        Bukkit.getScheduler().runTask(RecipeManager.getPlugin(), new Runnable()
        {
            public void run()
            {
                eventInventoryClickPost(event);
            }
        });
    }
    
    private void eventInventoryClickPost(InventoryClickEvent event)
    {
        ItemStack[] items = event.getInventory().getContents();
        Player player = (Player)event.getView().getPlayer();
        
        player.sendMessage("Result = " + items[0] + " | " + items[1] + " | " + event.getSlotType());
        player.updateInventory();
    }
    */
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void eventInventoryClick(InventoryClickEvent event)
    {
        try
        {
            Inventory inv = event.getInventory();
            
            if(inv == null)
                return;
            
            if(inv instanceof FurnaceInventory) // furnace inventory
            {
                InventoryHolder holder = inv.getHolder();
                
                if(holder == null || !(holder instanceof Furnace)) // no furnace block for inventory, we don't care then
                    return;
                
                furnaceClickEvent(event, inv, (Furnace)holder);
            }
        }
        catch(Exception e)
        {
            event.setCancelled(true);
            CommandSender sender = (event.getWhoClicked() instanceof Player ? (Player)event.getWhoClicked() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    private void furnaceClickEvent(InventoryClickEvent event, Inventory inv, Furnace furnace) throws Exception
    {
        HumanEntity human = event.getWhoClicked();
        
        if(human == null || human instanceof Player == false)
            return;
        
        Player player = (Player)human;
        
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        
        boolean shift = event.isShiftClick();
        boolean right = event.isRightClick();
        
        switch(event.getRawSlot())
        {
            case 0: // INGREDIENT slot
            {
                Messages.info(ChatColor.GREEN + "ingredient :: " + cursor.isSimilar(clicked) + " | cursor=" + cursor + " | clicked=" + clicked);
                
                return;
            }
            
            case 1: // FUEL slot
            {
                Messages.info(ChatColor.GREEN + "fuel :: " + cursor.isSimilar(clicked) + " | cursor=" + cursor + " | clicked=" + clicked);
                
                return;
            }
            
            case 2: // RESULT slot
            {
                Messages.info(ChatColor.GREEN + "result :: " + cursor.isSimilar(clicked) + " | cursor=" + cursor + " | clicked=" + clicked);
                
                return;
            }
            
            default: // player inventory - Shift+Click handling in player inventory while having furnace UI opened
            {
                if(!event.isShiftClick() || clicked == null || clicked.getTypeId() == 0) // not shiftclick or clicked empty slot
                    return;
                
                Messages.info(ChatColor.GREEN + "shift+inv :: " + cursor.isSimilar(clicked) + " | cursor=" + cursor + " | clicked=" + clicked);
                
                int targetSlot = ((RecipeManager.getSettings().FURNACE_SHIFT_CLICK == 'f' ? RecipeManager.recipes.getFuelRecipe(clicked) != null : event.isRightClick()) ? 1 : 0);
                ItemStack item = inv.getItem(targetSlot);
                boolean similarItems = clicked.isSimilar(item);
                
                if(RecipeManager.getSettings().FURNACE_SHIFT_CLICK == 'f' && targetSlot == 1 && item != null && !similarItems)
                {
                    targetSlot = 0;
                    item = inv.getItem(targetSlot);
                    similarItems = clicked.isSimilar(item);
                }
                
                if(item == null || item.getTypeId() == 0) // nothing in slot, place entire clicked stack
                {
                    if(targetSlot == 1 ? furnaceClickFuel(event, furnace, player, clicked) : furnaceClickIngredient(event, furnace, player, clicked))
                    {
                        inv.setItem(targetSlot, clicked);
                        event.setCurrentItem(null);
                        event.setCancelled(true);
                    }
                }
                else
                {
                    int maxStack = item.getType().getMaxStackSize();
                    int itemAmount = item.getAmount();
                    
                    if(similarItems && itemAmount < maxStack) // ingredient has room for more in the stack and it's similar!
                    {
                        Messages.info(ChatColor.GREEN + "shift+click similar :: " + event.getCurrentItem() + " | " + itemAmount + " < " + maxStack);
                        
                        int amount = itemAmount + clicked.getAmount();
                        int diff = amount - maxStack;
                        
                        item.setAmount(Math.min(amount, maxStack));
                        
                        if(diff > 0)
                            clicked.setAmount(diff);
                        else
                            event.setCurrentItem(null);
                        
                        event.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
        
        /*
        Player player = (Player)event.getWhoClicked();
        
        Messages.info(ChatColor.GREEN + "furnaceClickEvent :: cursor=" + event.getCursor() + " | clicked=" + event.getCurrentItem());
        
        switch(event.getRawSlot())
        {
            case 0: // INGREDIENT slot
            {
                furnaceClickIngredient(event, furnace, player, event.getCursor());
                return;
            }
            
            case 1: // FUEL slot
            {
                furnaceClickFuel(event, furnace, player, event.getCursor());
                return;
            }
            
            case 2:
                return; // Result slot
                
            default: // player inventory - Shift+Click handling in player inventory while having furnace UI opened
            {
                if(!event.isShiftClick())
                    return;
                
                ItemStack clicked = event.getCurrentItem();
                
                if(clicked == null || clicked.getTypeId() == 0) // clicked empty slot
                    return;
                
                int slot = ((RecipeManager.getSettings().FURNACE_SHIFT_CLICK == 'f' ? RecipeManager.recipes.getFuelRecipe(clicked) != null : event.isRightClick()) ? 1 : 0);
                ItemStack item = inv.getItem(slot);
                boolean itemsAlike = (item != null && item.getTypeId() == clicked.getTypeId() && item.getDurability() == clicked.getDurability());
                
                if(RecipeManager.getSettings().FURNACE_SHIFT_CLICK == 'f' && slot == 1 && item != null && !itemsAlike)
                {
                    slot = 0;
                    item = inv.getItem(slot);
                    itemsAlike = (item != null && item.getTypeId() == clicked.getTypeId() && item.getDurability() == clicked.getDurability());
                }
                
                if(item == null || item.getTypeId() == 0) // nothing in slot, place entire clicked stack
                {
                    if(slot == 1 ? furnaceClickFuel(event, furnace, player, clicked) : furnaceClickIngredient(event, furnace, player, clicked))
                    {
                        inv.setItem(slot, clicked);
                        event.setCurrentItem(null);
                        event.setCancelled(true);
                    }
                }
                else
                {
                    int itemStack = item.getType().getMaxStackSize();
                    int itemAmount = item.getAmount();
                    
                    if(itemsAlike && itemAmount < itemStack) // ingredient has room for more in the stack and it's the same type and data
                    {
                        int amount = itemAmount + clicked.getAmount();
                        int diff = amount - itemStack;
                        
                        item.setAmount(Math.min(amount, itemStack));
                        
                        if(diff > 0)
                            clicked.setAmount(diff);
                        else
                            event.setCurrentItem(null);
                        
                        event.setCancelled(true);
                    }
                }
            }
        }
        */
    }
    
    private boolean furnaceClickIngredient(InventoryClickEvent event, Furnace furnace, Player player, ItemStack placed) throws Exception
    {
        if(!RecipeManager.getPlugin().canCraft(player)) // player not allowed to craft
        {
            event.setCancelled(true);
            event.setResult(Result.DENY);
            return false;
        }
        
        SmeltRecipe recipe = RecipeManager.recipes.getSmeltRecipe(placed);
        Location location = furnace.getLocation();
        
        Messages.info(ChatColor.GREEN + "placed ingredient = " + placed + " | clicked=" + event.getCurrentItem());
        
        /*
        ItemStack clicked = event.getCurrentItem();
        
        if(clicked != null && clicked.getTypeId() > 0)
        {
            ItemMeta meta = clicked.getItemMeta();
            
            if(meta != null)
            {
                List<String> lore = meta.getLore();
                
                if(lore != null)
                {
                    lore.clear(); // TODO remove specific line!
                    meta.setLore(lore);
                    clicked.setItemMeta(meta);
                }
            }
        }
        
        if(cursor != null)
        {
            ItemMeta meta = cursor.getItemMeta();
            
            if(meta != null)
            {
                List<String> lore = meta.getLore();
                
                if(lore == null)
                    lore = new ArrayList<String>();
                
                lore.add(Recipes.FURNACE_OWNER_STRING + player.getName());
                
                meta.setLore(lore);
                cursor.setItemMeta(meta);
            }
        }
        
        /*
        if(recipe != null && (!recipe.isUsableBy(player, true) || !recipe.isUsableProximity(player, location, true) || !recipe.isUsableBlocks(player, location, false, true) || !recipe.isUsableHeight(player, location, false, true)))
        {
            event.setCancelled(true);
            event.setResult(Result.DENY);
            return false;
        }
        
        furnaceNotified.remove(player.getName());
        furnaceStop.remove(Recipes.locationToString(furnace.getLocation()));
        
        if(recipe != null)
        {
            if(recipe.getFlags().getProximity() != null)
            {
                if(recipe.getFlags().getProximity().getValue() > 0)
                {
                    Messages.CRAFT_WARNDISTANCE.print(player, recipe.getFlags().getProximity().getSuccessMessage(), new String[][] { { "{distance}", "" + recipe.getFlags().getProximity().getValue() } });
                }
                else
                    Messages.CRAFT_WARNONLINE.print(player, recipe.getFlags().getProximity().getSuccessMessage());
            }
            
            RecipeManager.recipes.getFurnaceData(furnace.getLocation(), true).setSmelter(player.getName()).setSmeltItem(recipe.getIngredient());
        }
        */
        
        return true; // custom recipe or not, no reason to restrict
    }
    
    private boolean furnaceClickFuel(InventoryClickEvent event, Furnace furnace, Player player, ItemStack placed) throws Exception
    {
        if(!RecipeManager.getPlugin().canCraft(player)) // player not allowed to craft
        {
            event.setCancelled(true);
            event.setResult(Result.DENY);
            return false;
        }
        
        FuelRecipe recipe = RecipeManager.recipes.getFuelRecipe(placed);
        Location location = furnace.getLocation();
        
        Messages.info(ChatColor.GREEN + "placed fuel = " + placed + " | clicked=" + event.getCurrentItem());
        
        /*
        if(recipe != null && (!recipe.isUsableBy(player, true) || !recipe.isUsableProximity(player, location, true) || !recipe.isUsableBlocks(player, location, false, true) || !recipe.isUsableHeight(player, location, false, true)))
        {
            event.setCancelled(true);
            event.setResult(Result.DENY);
            return false;
        }
        
        furnaceNotified.remove(player.getName());
        furnaceStop.remove(Recipes.locationToString(furnace.getLocation()));
        
        if(recipe != null)
        {
            if(recipe.getFlags().getProximity() != null)
            {
                if(recipe.getFlags().getProximity().getValue() > 0)
                {
                    Messages.CRAFT_WARNDISTANCE.print(player, recipe.getFlags().getProximity().getSuccessMessage(), new String[][] { { "{distance}", "" + recipe.getFlags().getProximity().getValue() } });
                }
                else
                    Messages.CRAFT_WARNONLINE.print(player, recipe.getFlags().getProximity().getSuccessMessage());
            }
            
            RecipeManager.recipes.getFurnaceData(furnace.getLocation(), true).setFueler(player.getName()).setFuelItem(recipe.getFuel());
        }
        */
        
        return true; // custom recipe or not, no reason to restrict
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void eventPrepareCraft(PrepareItemCraftEvent event)
    {
        try
        {
            Player player = (event.getView() == null ? null : (Player)event.getView().getPlayer());
            CraftingInventory inventory = event.getInventory();
            
            if(!RecipeManager.getPlugin().canCraft(player))
            {
                inventory.setResult(null);
                return;  // player not allowed to craft, stop here
            }
            
            Location location = getPlayerWorkbench(player); // get workbench location or null
            
            if(event.isRepair())
            {
                prepareRepairRecipe(player, inventory, location);
                return; // if it's a repair recipe we don't need to move on
            }
            
            Recipe bukkitRecipe = event.getRecipe();
            
            if(bukkitRecipe == null)
                return; // bukkit recipe is null ! skip it
                
            ItemStack result = inventory.getResult();
            ItemStack recipeResult = bukkitRecipe.getResult();
            
            if(prepareSpecialRecipe(player, inventory, result, recipeResult))
                return; // stop here if it's a special recipe
                
            WorkbenchRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe);
            
            if(recipe == null)
                return; // not a custom recipe or recipe not found, no need to move on
                
            result = prepareCraftResult(player, inventory, recipe, location); // get the result from recipe
            
            // Call the RecipeManagerPrepareCraftEvent
            RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(recipe, result, player, location);
            Bukkit.getPluginManager().callEvent(callEvent);
            
            result = (callEvent.getResult() == null ? null : new ItemResult(callEvent.getResult()));
            
            inventory.setResult(result == null ? null : result);
        }
        catch(Exception e)
        {
            event.getInventory().setResult(null);
            
            CommandSender sender = (event.getView() != null && event.getView().getPlayer() instanceof Player ? (Player)event.getView().getPlayer() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    private boolean prepareSpecialRecipe(Player player, CraftingInventory inventory, ItemStack result, ItemStack recipeResult)
    {
        if(!result.equals(recipeResult)) // result was processed by the game and it doesn't match the original recipe
        {
            if(RecipeManager.getSettings().SPECIAL_LEATHER_DYE && recipeResult.equals(BukkitRecipes.RECIPE_LEATHERDYE))
            {
                Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                inventory.setResult(null);
                return true;
            }
            
            if(RecipeManager.getSettings().SPECIAL_MAP_CLONING && recipeResult.equals(BukkitRecipes.RECIPE_MAPCLONE))
            {
                Messages.CRAFT_SPECIAL_MAP_CLONING.print(player);
                inventory.setResult(null);
                return true;
            }
            
            if(RecipeManager.getSettings().SPECIAL_MAP_EXTENDING && recipeResult.equals(BukkitRecipes.RECIPE_MAPEXTEND))
            {
                Messages.CRAFT_SPECIAL_MAP_EXTENDING.print(player);
                inventory.setResult(null);
                return true;
            }
            
            if(RecipeManager.getSettings().SPECIAL_FIREWORKS && recipeResult.equals(BukkitRecipes.RECIPE_FIREWORKS))
            {
                Messages.CRAFT_SPECIAL_FIREWORKS.print(player);
                inventory.setResult(null);
                return true;
            }
            
            System.out.print("[debug] Results don't match, special recipe ? " + recipeResult + " vs " + result);
        }
        
        return false;
    }
    
    private void prepareRepairRecipe(Player player, CraftingInventory inventory, Location location) throws Exception
    {
        if(!RecipeManager.getSettings().SPECIAL_REPAIR)
        {
            if(player != null)
                player.playSound((location == null ? player.getLocation() : location), Sound.NOTE_BASS, 1, 255);
            
            inventory.setResult(Tools.generateItemStackWithMeta(Material.TRIPWIRE, 0, 0, Messages.CRAFT_REPAIR_DISABLED.get()));
            return;
        }
        
        ItemStack result = inventory.getRecipe().getResult();
        
        if(RecipeManager.getSettings().SPECIAL_REPAIR_METADATA)
        {
            ItemStack[] matrix = inventory.getMatrix();
            ItemStack[] repaired = new ItemStack[2];
            int repair[] = new int[2];
            int repairIndex = 0;
            
            for(int i = 0; i < matrix.length; i++)
            {
                if(matrix[i] != null && matrix[i].getTypeId() != 0)
                {
                    repair[repairIndex] = i;
                    repaired[repairIndex] = matrix[i];
                    
                    if(++repairIndex > 1)
                        break;
                }
            }
            
            if(repaired[0] != null && repaired[1] != null)
            {
                ItemMeta meta = null;
                
                if(repaired[0].hasItemMeta())
                    meta = repaired[0].getItemMeta();
                
                else if(repaired[1].hasItemMeta())
                    meta = repaired[1].getItemMeta();
                
                if(meta != null)
                {
                    result = inventory.getResult();
                    result.setItemMeta(meta);
                }
            }
        }
        
        RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(null, result, player, location);
        Bukkit.getPluginManager().callEvent(callEvent);
        
        result = callEvent.getResult();
        
        if(RecipeManager.getSettings().SOUNDS_REPAIR && result != null && player != null)
            player.playSound((location == null ? player.getLocation() : location), Sound.ANVIL_USE, 1, 50);
        
        inventory.setResult(result);
    }
    
    private ItemStack prepareCraftResult(Player player, CraftingInventory inventory, WorkbenchRecipe recipe, Location location) throws Exception
    {
        ItemStack result = recipe.getResult(player, (player == null ? null : player.getName()), location, true);
        
        /*
        if(result != null)
        {
            int[] vec = (workbenchEvents ? workbench.get(player.getName()) : null);
            
            if(vec == null || !recipe.isUsableBlocks(player, player.getWorld(), vec[0], vec[1], vec[2], true, true) || !recipe.isUsableHeight(player, player.getWorld(), vec[1], true, true))
            {
                ItemStack item = null;
                
                Book getBook = recipe.getFlags().getGetBook();
                
                if(getBook != null)
                {
                    ItemStack bookItem = RecipeManager.recipes.books.get(getBook);
                    
                    if(bookItem != null)
                    {
                        item = bookItem.clone();
                        item.setAmount(1);
                    }
                }
                else
                {
                    ItemStack copy = recipe.getFlags().getClone();
                    
                    if(copy != null)
                    {
                        for(ItemStack i : inventory.getContents())
                        {
                            if(copy.equals(i))
                            {
                                item = i.clone();
                                item.setAmount(1);
                                break;
                            }
                        }
                    }
                }
                
                if(item != null)
                    result = item;
            }
        }
        */
        
        return result;
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void eventCraftFinish(CraftItemEvent event)
    {
        try
        {
            CraftingInventory inventory = event.getInventory();
            ItemStack result = inventory.getResult();
            Player player = (event.getView() == null ? null : (Player)event.getView().getPlayer());
            Location location = getPlayerWorkbench(player);
            
            if(result == null || result.getAmount() <= 0)
            {
                event.setCancelled(true);
                
                if(RecipeManager.getSettings().SOUNDS_FAILED_CLICK && player != null)
                    player.playSound(location, Sound.NOTE_BASS, 1, 255);
                
                return;
            }
            
            System.out.print("CraftItemEvent :: " + event.getCurrentItem());
            
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
            
            result = recipe.getResult(player, (player == null ? null : player.getName()), location, false);
            RecipeManagerCraftEvent callEvent = new RecipeManagerCraftEvent(recipe, result, player, event.getCursor(), event.isShiftClick(), event.isRightClick());
            
            Bukkit.getPluginManager().callEvent(callEvent);
            result = (callEvent.getResult() == null ? null : new ItemResult(callEvent.getResult()));
            
            inventory.setResult(result == null ? null : result);
        }
        catch(Exception e)
        {
            event.getInventory().setResult(null);
            
            CommandSender sender = (event.getView() != null && event.getView().getPlayer() instanceof Player ? (Player)event.getView().getPlayer() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void eventFurnaceBurn(FurnaceBurnEvent event)
    {
        try
        {
            BlockID blockID = new BlockID(event.getBlock());
            
            if(furnaceSmelting != null && !furnaceSmelting.containsKey(blockID))
                furnaceSmelting.put(blockID, new MutableFloat());
            
            FuelRecipe recipe = RecipeManager.getRecipes().getFuelRecipe(event.getFuel());
            
            if(recipe == null)
                return;
            
            RecipeFlags flags = recipe.getFlags();
            
            event.setBurnTime(flags.isRemove() ? 0 : recipe.getBurnTicks());
            event.setBurning(flags.isRemove() ? false : true);
        }
        catch(Exception e)
        {
            event.setCancelled(true);
            Messages.error(null, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
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
    
    private Location getPlayerWorkbench(Player player) throws Exception
    {
        if(player == null)
            return null;
        
        int[] vec = playerWorkbench.get(player.getName());
        Location playerLoc = player.getLocation();
        
        if(vec == null)
            return playerLoc;
        
        Location loc = new Location(player.getWorld(), vec[0], vec[1], vec[2]);
        
        // TODO check if it's accurate
        return (loc.distanceSquared(playerLoc) > 4 * 4 ? playerLoc : loc);
    }
    
    // Monitor furnace ... stuff TODO
    
    // TODO Testing...
    protected HashMap<BlockID, MutableFloat> furnaceSmelting = new HashMap<BlockID, MutableFloat>();
    
    private Set<BlockID>                     furnaceNotified = new HashSet<BlockID>();
    private Set<BlockID>                     furnaceStop     = new HashSet<BlockID>();
    
    // TODO replace this with Placer: <name> in item lore !
    private Map<BlockID, BlockFurnaceData>   furnaceData     = new HashMap<BlockID, BlockFurnaceData>();
    
    private Map<String, int[]>               playerWorkbench = new HashMap<String, int[]>();
    
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
    // TODO disable/enable switch ?
    
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
        String line = lore.get(index);
        
        if(line != null && line.startsWith(Recipes.RECIPE_ID_STRING))
        {
            lore.remove(index);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }
}