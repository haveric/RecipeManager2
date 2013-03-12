package ro.thehunters.digi.recipeManager;

import java.util.List;

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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import ro.thehunters.digi.recipeManager.apievents.RecipeManagerCraftEvent;
import ro.thehunters.digi.recipeManager.apievents.RecipeManagerPrepareCraftEvent;
import ro.thehunters.digi.recipeManager.data.BlockID;
import ro.thehunters.digi.recipeManager.data.FurnaceData;
import ro.thehunters.digi.recipeManager.flags.Args;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;
import ro.thehunters.digi.recipeManager.recipes.WorkbenchRecipe;

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
    
    /*
     *  Workbench craft events
     */
    
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
                return; // player not allowed to craft, stop here
            }
            
            Location location = Workbenches.get(player); // get workbench location or null
            
            if(event.isRepair())
            {
                prepareRepairRecipe(player, inventory, location);
                return; // if it's a repair recipe we don't need to move on
            }
            
            Recipe bukkitRecipe = event.getRecipe();
            
            if(bukkitRecipe == null)
                return; // bukkit recipe is null ! skip it
                
            ItemResult result = (inventory.getResult() == null ? null : new ItemResult(inventory.getResult()));
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
            
            Messages.debug("result = " + result);
            
            if(result != null)
            {
                Args a = Args.create().player(player).location(location).recipe(recipe).inventory(inventory).result(result).build();
                
                if(!recipe.sendPrepare(a))
                {
                    result = null;
                }
                
                // TODO remove
                if(result != null)
                {
                    a.sendEffects(a.player(), Messages.CRAFT_FLAG_PREFIX_RECIPE);
                }
                else
                {
                    a.sendReasons(a.player(), Messages.CRAFT_FLAG_PREFIX_RECIPE);
                }
            }
            
            inventory.setResult(result);
            
            new UpdateInventory(player); // TODO REMOVE
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
            if(RecipeManager.getSettings().SPECIAL_LEATHER_DYE && recipeResult.equals(Vanilla.RECIPE_LEATHERDYE))
            {
                Messages.CRAFT_SPECIAL_LEATHERDYE.print(player);
                inventory.setResult(null);
                return true;
            }
            
            if(RecipeManager.getSettings().SPECIAL_MAP_CLONING && recipeResult.equals(Vanilla.RECIPE_MAPCLONE))
            {
                Messages.CRAFT_SPECIAL_MAP_CLONING.print(player);
                inventory.setResult(null);
                return true;
            }
            
            if(RecipeManager.getSettings().SPECIAL_MAP_EXTENDING && recipeResult.equals(Vanilla.RECIPE_MAPEXTEND))
            {
                Messages.CRAFT_SPECIAL_MAP_EXTENDING.print(player);
                inventory.setResult(null);
                return true;
            }
            
            if(RecipeManager.getSettings().SPECIAL_FIREWORKS && recipeResult.equals(Vanilla.RECIPE_FIREWORKS))
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
                {
                    meta = repaired[0].getItemMeta();
                }
                else if(repaired[1].hasItemMeta())
                {
                    meta = repaired[1].getItemMeta();
                }
                
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
        {
            player.playSound((location == null ? player.getLocation() : location), Sound.ANVIL_USE, 1, 50);
        }
        
        inventory.setResult(result);
    }
    
    private ItemResult prepareCraftResult(Player player, CraftingInventory inventory, WorkbenchRecipe recipe, Location location) throws Exception
    {
        Args a = Args.create().player(player).inventory(inventory).recipe(recipe).location(location).build();
        ItemResult result = recipe.getDisplayResult(a);
        
        if(result != null)
        {
            if(recipe.hasFlag(FlagType.CLONEINGREDIENT))
            {
                // figure out if it was a single-display item to overwrite it
                
//                result = FlagCloneIngredient.getClonedItem(recipe.getFlag(FlagType.CLONEINGREDIENT), inventory);
            }
            else if(recipe.hasFlag(FlagType.GETBOOK))
            {
                
            }
        }
        
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
            ItemResult result = (inventory.getResult() == null ? null : new ItemResult(inventory.getResult()));
            Player player = (event.getView() == null ? null : (Player)event.getView().getPlayer());
            Location location = Workbenches.get(player);
            
            if(result == null)
            {
                event.setCancelled(true);
                
                if(RecipeManager.getSettings().SOUNDS_FAILED_CLICK && player != null)
                {
                    player.playSound(location, Sound.NOTE_BASS, 1, 255);
                }
                
                return;
            }
            
            Messages.debug("current item = " + event.getCurrentItem());
            
            Recipe bukkitRecipe = event.getRecipe();
            ItemStack recipeResult = bukkitRecipe.getResult();
            WorkbenchRecipe recipe = null;
            
            if(bukkitRecipe instanceof ShapedRecipe)
            {
                recipe = RecipeManager.recipes.getCraftRecipe(recipeResult);
            }
            else if(bukkitRecipe instanceof ShapelessRecipe)
            {
                recipe = RecipeManager.recipes.getCombineRecipe(recipeResult);
            }
            else
            {
                Messages.debug("<red>new recipe???!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            
            if(recipe == null)
                return;
            
            Args a = Args.create().player(player).location(location).recipe(recipe).inventory(inventory).build();
            
            result = recipe.getResult(a);
            
            RecipeManagerCraftEvent callEvent = new RecipeManagerCraftEvent(recipe, result, player, event.getCursor(), event.isShiftClick(), event.isRightClick());
            Bukkit.getPluginManager().callEvent(callEvent);
            result = (callEvent.getResult() == null ? null : new ItemResult(callEvent.getResult()));
            
            if(result != null)
            {
                a = Args.create().player(player).inventory(inventory).recipe(recipe).location(location).build();
                
                if(!recipe.sendCrafted(a) || !result.sendCrafted(a))
                {
                    result = null;
                }
                
                if(result != null)
                {
                    a.sendEffects(a.player(), Messages.CRAFT_FLAG_PREFIX_RECIPE);
                }
                else
                {
                    recipe.sendFailed(a);
                    a.sendReasons(a.player(), Messages.CRAFT_FLAG_PREFIX_RECIPE);
                }
            }
            
            inventory.setResult(result);
            
            new UpdateInventory(player); // TODO REMOVE
        }
        catch(Exception e)
        {
            event.getInventory().setResult(null);
            
            CommandSender sender = (event.getView() != null && event.getView().getPlayer() instanceof Player ? (Player)event.getView().getPlayer() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    private class UpdateInventory extends BukkitRunnable
    {
        private final Player player;
        
        public UpdateInventory(Player player)
        {
            this.player = player;
            runTask(RecipeManager.getPlugin());
        }
        
        @Override
        public void run()
        {
            player.updateInventory();
        }
    }
    
    /*
     * Workbenche monitor events
     */
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventInventoryClose(InventoryCloseEvent event)
    {
        if(event.getView().getType() == InventoryType.WORKBENCH)
        {
            Player player = (Player)event.getView().getPlayer();
            Workbenches.remove(player);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventPlayerIntereact(PlayerInteractEvent event)
    {
        switch(event.getAction())
        {
            case RIGHT_CLICK_BLOCK:
            {
                Workbenches.add(event.getPlayer(), event.getClickedBlock().getLocation());
                
                return;
            }
            
            case PHYSICAL:
                return;
                
            default:
            {
                Workbenches.remove(event.getPlayer());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventPlayerTeleport(PlayerTeleportEvent event)
    {
        Workbenches.remove(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPlayerDeath(PlayerDeathEvent event)
    {
        Workbenches.remove(event.getEntity());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPlayerQuit(PlayerQuitEvent event)
    {
        Workbenches.remove(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPlayerKick(PlayerKickEvent event)
    {
        Workbenches.remove(event.getPlayer());
    }
    
    /*
     *  Furnace craft events
     */
    
    @EventHandler
    public void eventInventoryClick(InventoryClickEvent event)
    {
        /*
        try
        {
            Inventory inv = event.getInventory();
            
            if(inv instanceof FurnaceInventory)
            {
                InventoryHolder holder = inv.getHolder();
                
                if(holder != null && holder instanceof Furnace)
                    eventFurnaceClick(event, inv, (Furnace)holder);
                
                return;
            }
        }
        catch(Exception e)
        {
            event.setCancelled(true);
            CommandSender sender = (event.getWhoClicked() instanceof Player ? (Player)event.getWhoClicked() : null);
            Messages.error(sender, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
        */
    }
    
    private void eventFurnaceClick(InventoryClickEvent event, Inventory inv, Furnace furnace) throws Exception
    {
        HumanEntity ent = event.getWhoClicked();
        
        if(ent == null || ent instanceof Player == false)
            return;
        
        Player player = (Player)ent;
        
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        
        boolean shift = event.isShiftClick();
        boolean right = event.isRightClick();
        
        switch(event.getRawSlot())
        {
            case 0: // INGREDIENT slot
            {
                Messages.info(ChatColor.GREEN + "ingredient :: " + cursor.isSimilar(clicked) + " | cursor=" + cursor + " | clicked=" + clicked);
                
                if(!furnaceCheckItems(event, furnace, player, cursor, furnace.getInventory().getFuel()))
                {
                    Messages.debug("CANCELLED!");
                    event.setCancelled(true);
                    event.setResult(Result.DENY);
                    player.updateInventory();
                    return;
                }
                
                return;
            }
            
            case 1: // FUEL slot
            {
                Messages.info(ChatColor.GREEN + "fuel :: " + cursor.isSimilar(clicked) + " | cursor=" + cursor + " | clicked=" + clicked);
                
                if(!furnaceCheckItems(event, furnace, player, furnace.getInventory().getSmelting(), cursor))
                {
                    Messages.debug("CANCELLED!");
                    event.setCancelled(true);
                    event.setResult(Result.DENY);
                    player.updateInventory();
                    return;
                }
                
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
    
    private boolean furnaceCheckItems(InventoryClickEvent event, Furnace furnace, Player player, ItemStack ingredient, ItemStack fuel) throws Exception
    {
        Messages.debug("ingredient=" + ingredient + " | fuel=" + fuel + " | " + event.isLeftClick() + " | " + event.isRightClick() + " | " + event.isShiftClick());
        
        if(!RecipeManager.getPlugin().canCraft(player)) // player not allowed to craft
        {
            event.setCancelled(true);
            event.setResult(Result.DENY);
            return false;
        }
        
        SmeltRecipe smeltRecipe = RecipeManager.recipes.getSmeltRecipe(ingredient);
        
        if(smeltRecipe != null && smeltRecipe.hasFuel())
        {
            Messages.debug("REQ FUEL = " + Tools.printItemStack(smeltRecipe.getFuel()));
            Messages.debug("PLACED FUEL = " + Tools.printItemStack(fuel));
            
            if(fuel == null || fuel.getTypeId() == 0)
                return true;
            
            if(!smeltRecipe.getFuel().isSimilar(fuel))
            {
                return false;
            }
            
            return true;
        }
        
        /*
        FuelRecipe fuelRecpe = RecipeManager.recipes.getFuelRecipe(fuel);
        Location location = furnace.getLocation();
        
        if(smeltRecipe != null)
        {
            Arguments a = new Arguments(player, null, location, RecipeType.SMELT, null);
            
            if(!smeltRecipe.checkFlags(a))
            {
                a.sendReasons(player);
                event.setCancelled(true);
                event.setResult(Result.DENY);
                return false;
            }
        }
        */
        
        return true;
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
        
        if(recipe != null)
        {
            Args a = Args.create().player(player).location(location).recipe(recipe).inventory(furnace.getInventory()).build();
//            Args a = new Args(player, null, location, RecipeType.SMELT, null);
            
            if(!recipe.checkFlags(a))
            {
                a.sendReasons(player, Messages.CRAFT_FLAG_PREFIX_RECIPE);
                event.setCancelled(true);
                event.setResult(Result.DENY);
                return false;
            }
        }
        
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
    public void eventFurnaceBurn(FurnaceBurnEvent event)
    {
        Messages.debug("BURN EVENT");
        
        try
        {
            BlockID id = new BlockID(event.getBlock());
            
            // TODO if reverting to FurnaceWorker storage, check if exists and add furnace!
            
            FurnaceData data = Furnaces.get(id);
            
            final FuelRecipe fuelRecipe = RecipeManager.getRecipes().getFuelRecipe(event.getFuel());
            
            if(fuelRecipe != null)
            {
                // Fuel recipe
                int time = (fuelRecipe.hasFlag(FlagType.REMOVE) ? 0 : fuelRecipe.getBurnTicks());
                
                event.setBurnTime(time);
                event.setBurning(time > 0);
            }
            else
            {
                // Smelting recipe with specific fuel
                
                BlockState state = event.getBlock().getState();
                
                if(state instanceof Furnace == false)
                    return; // highly unlikely but better safe than sorry
                    
                Furnace furnace = (Furnace)state;
                ItemStack ingredient = furnace.getInventory().getSmelting();
                SmeltRecipe smeltRecipe = RecipeManager.getRecipes().getSmeltRecipe(ingredient);
                
                if(smeltRecipe != null)
                {
                    if(!smeltRecipe.hasFuel() || !smeltRecipe.getFuel().isSimilar(event.getFuel()))
                    {
                        event.setCancelled(true);
                    }
                    else
                    {
                        event.setBurning(true);
                        event.setBurnTime((int)Math.ceil(smeltRecipe.getCookTime()) * 20);
                    }
                }
            }
            
            boolean running = !event.isCancelled() && event.isBurning();
            
            data.setBurnTime(running ? event.getBurnTime() : 0);
            
//            Messages.debug("furnace set burn time to = " + data.getBurnTime());
            
            if(running)
            {
                FurnaceWorker.start(); // make sure it's started
                
//              new FurnaceBurnOut(event.getBlock(), event.getFuel(), event.getBurnTime());
            }
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
            
            FurnaceInventory inventory = null;
            
            if(event.getBlock() instanceof Furnace)
                inventory = ((Furnace)event.getBlock()).getInventory();
            
            Args a = Args.create().location(event.getBlock().getLocation()).recipe(recipe).inventory(inventory).result(event.getResult()).build();
            
            recipe.sendCrafted(a);
        }
        catch(Exception e)
        {
            event.setCancelled(true);
            Messages.error(null, e, ChatColor.RED + event.getEventName() + " cancelled due to error:");
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void eventFurnaceTakeResult(FurnaceExtractEvent event)
    {
        if(event.getExpToDrop() == 0)
            return;
        
        BlockState state = event.getBlock().getState();
        
        if(state instanceof Furnace == false)
            return; // highly unlikely but better safe than sorry
            
        Furnace furnace = (Furnace)state;
        ItemStack ingredient = furnace.getInventory().getSmelting();
        SmeltRecipe smeltRecipe = null;
        
        if(ingredient == null || ingredient.getTypeId() == 0)
        {
            ItemStack result = furnace.getInventory().getResult();
            
            if(result == null)
                return;
            
            for(SmeltRecipe r : RecipeManager.getRecipes().indexSmelt.values())
            {
                if(result.isSimilar(r.getResult()))
                {
                    smeltRecipe = r;
                    break;
                }
            }
        }
        else
        {
            smeltRecipe = RecipeManager.getRecipes().getSmeltRecipe(ingredient);
        }
        
        if(smeltRecipe != null)
        {
            event.setExpToDrop(0);
        }
    }
    
    /*
     * Furnace monitor events
     */
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        
        switch(block.getType())
        {
            case BURNING_FURNACE:
            case FURNACE:
            {
                Messages.debug("added furnace at " + BlockID.fromBlock(block).getCoordsString());
                Furnaces.add(BlockID.fromBlock(block));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        
        switch(block.getType())
        {
            case BURNING_FURNACE:
            case FURNACE:
            {
                Messages.debug("removed furnace at " + BlockID.fromBlock(block).getCoordsString());
                Furnaces.remove(BlockID.fromBlock(block));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventWorldLoad(WorldLoadEvent event)
    {
        worldLoad(event.getWorld());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventChunkLoad(ChunkLoadEvent event)
    {
        if(!event.isNewChunk())
            findFurnaces(event.getChunk(), true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventChunkUnload(ChunkUnloadEvent event)
    {
        findFurnaces(event.getChunk(), false);
    }
    
    protected void worldLoad(World world)
    {
        Chunk chunks[] = world.getLoadedChunks();
        
        for(Chunk chunk : chunks)
        {
            findFurnaces(chunk, true);
        }
    }
    
    private void findFurnaces(final Chunk chunk, final boolean add)
    {
        if(chunk == null)
            return;
        
        BlockState[] tileEntities = chunk.getTileEntities();
        
        for(BlockState state : tileEntities)
        {
            if(state != null & state instanceof Furnace)
            {
                if(add)
                {
                    Messages.debug("added furnace at " + new BlockID(state.getLocation()).getCoordsString());
                    Furnaces.add(state.getLocation());
                }
                else
                {
                    Messages.debug("removed furnace at " + new BlockID(state.getLocation()).getCoordsString());
                    Furnaces.remove(state.getLocation());
                }
            }
        }
    }
    
    /*
     * Marked item monitor events
     */
    
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