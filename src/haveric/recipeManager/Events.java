package haveric.recipeManager;

import haveric.recipeManager.api.events.RecipeManagerCraftEvent;
import haveric.recipeManager.api.events.RecipeManagerPrepareCraftEvent;
import haveric.recipeManager.data.BlockID;
import haveric.recipeManager.data.FurnaceData;
import haveric.recipeManager.flags.Args;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flaggable;
import haveric.recipeManager.recipes.FuelRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SmeltRecipe;
import haveric.recipeManager.recipes.WorkbenchRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dispenser;


/**
 * RecipeManager handled events
 */
public class Events implements Listener {
    protected Events() {
        // events are registered in the reload() method

        for (World world : Bukkit.getWorlds()) {
            worldLoad(world);
        }
    }

    protected void clean() {
        HandlerList.unregisterAll(this);
    }

    protected static void reload() {
        HandlerList.unregisterAll(RecipeManager.getEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getEvents(), RecipeManager.getPlugin());
    }

    /*
     * Workbench craft events
     */

    @EventHandler(priority = EventPriority.LOW)
    public void prepareCraft(PrepareItemCraftEvent event) {
        try {
            CraftingInventory inv = event.getInventory();

            if (inv.getResult() == null) {
                return; // event was cancelled by some other plugin
            }

            Player player;
            if (event.getView() == null) {
                player = null;
            } else {
                player = (Player) event.getView().getPlayer();
            }

            if (!RecipeManager.getPlugin().canCraft(player)) {
                inv.setResult(null);
                return; // player not allowed to craft, stop here
            }

            Location location;
            // get workbench location or null
            if (inv.getSize() > 9) {
                location = Workbenches.get(player);
            } else {
                location = null;
            }

            if (event.isRepair()) {
                prepareRepairRecipe(player, inv, location);
                return; // if it's a repair recipe we don't need to move on
            }

            Recipe bukkitRecipe = event.getRecipe();

            if (bukkitRecipe == null) {
                return; // Bukkit recipe is null ! skip it
            }

            ItemResult result;
            if (inv.getResult() == null) {
                result = null;
            } else {
                result = new ItemResult(inv.getResult());
            }

            ItemStack recipeResult = bukkitRecipe.getResult();

            if (prepareSpecialRecipe(player, inv, result, recipeResult)) {
                return; // stop here if it's a special recipe
            }

            WorkbenchRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe);

            if (recipe == null) {
                return; // not a custom recipe or recipe not found, no need to move on
            }

            Args a = Args.create().player(player).inventory(inv).location(location).recipe(recipe).build();

            result = recipe.getDisplayResult(a); // get the result from recipe

            // Call the RecipeManagerPrepareCraftEvent
            RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(recipe, result, player, location);
            Bukkit.getPluginManager().callEvent(callEvent);

            if (callEvent.getResult() == null) {
                result = null;
            } else {
                result = new ItemResult(callEvent.getResult());
            }

            if (result != null) {
                a.setResult(result);

                if (recipe.sendPrepare(a)) {
                    a.sendEffects(a.player(), Messages.FLAG_PREFIX_RECIPE.get());
                } else {
                    a.sendReasons(a.player(), Messages.FLAG_PREFIX_RECIPE.get());
                    result = null;
                }
            }

            inv.setResult(result);
        } catch (Throwable e) {
            if (event.getInventory() != null) {
                event.getInventory().setResult(null);
            }

            CommandSender sender;
            if (event.getView() != null && event.getView().getPlayer() instanceof Player) {
                sender = (Player) event.getView().getPlayer();
            } else {
                sender = null;
            }

            Messages.error(sender, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private boolean prepareSpecialRecipe(Player player, CraftingInventory inv, ItemStack result, ItemStack recipeResult) {
        if (!result.equals(recipeResult)) { // result was processed by the game and it doesn't match the original recipe
            if (!Settings.getInstance().getSpecialLeatherDye() && recipeResult.equals(Vanilla.RECIPE_LEATHERDYE)) {
                Messages.CRAFT_SPECIAL_LEATHERDYE.printOnce(player);
                inv.setResult(null);
                return true;
            }

            if (!Settings.getInstance().getSpecialMapCloning() && recipeResult.equals(Vanilla.RECIPE_MAPCLONE)) {
                Messages.CRAFT_SPECIAL_MAP_CLONING.printOnce(player);
                inv.setResult(null);
                return true;
            }

            if (!Settings.getInstance().getSpecialMapExtending() && recipeResult.equals(Vanilla.RECIPE_MAPEXTEND)) {
                Messages.CRAFT_SPECIAL_MAP_EXTENDING.printOnce(player);
                inv.setResult(null);
                return true;
            }

            if (!Settings.getInstance().getSpecialFireworks() && recipeResult.equals(Vanilla.RECIPE_FIREWORKS)) {
                Messages.CRAFT_SPECIAL_FIREWORKS.printOnce(player);
                inv.setResult(null);
                return true;
            }
        }

        return false;
    }

    private void prepareRepairRecipe(Player player, CraftingInventory inv, Location location) throws Throwable {
        if (!Settings.getInstance().getSpecialRepair()) {
            Messages.sendDenySound(player, location);
            Messages.CRAFT_REPAIR_DISABLED.printOnce(player);
            inv.setResult(null);
            return;
        }

        ItemStack result = inv.getRecipe().getResult();

        if (Settings.getInstance().getSpecialRepairMetadata()) {
            ItemStack[] matrix = inv.getMatrix();
            ItemStack[] repaired = new ItemStack[2];
            int[] repair = new int[2];
            int repairIndex = 0;

            int matrixLength = matrix.length;
            for (int i = 0; i < matrixLength; i++) {
                if (matrix[i] != null && matrix[i].getType() != Material.AIR) {
                    repair[repairIndex] = i;
                    repaired[repairIndex] = matrix[i];

                    if (++repairIndex > 1) {
                        break;
                    }
                }
            }

            if (repaired[0] != null && repaired[1] != null) {
                ItemMeta meta = null;

                if (repaired[0].hasItemMeta()) {
                    meta = repaired[0].getItemMeta();
                } else if (repaired[1].hasItemMeta()) {
                    meta = repaired[1].getItemMeta();
                }

                if (meta != null) {
                    result = inv.getResult();
                    result.setItemMeta(meta);
                }
            }
        }

        RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(null, result, player, location);
        Bukkit.getPluginManager().callEvent(callEvent);

        result = callEvent.getResult();

        if (result != null) {
            Messages.sendRepairSound(player, location);
        }

        inv.setResult(result);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void craftFinish(CraftItemEvent event) {
        try {
            CraftingInventory inv = event.getInventory();
            ItemResult result;
            if (inv.getResult() == null) {
                result = null;
            } else {
                result = new ItemResult(inv.getResult());
            }

            final Player player;
            if (event.getView() == null) {
                player = null;
            } else {
                player = (Player) event.getView().getPlayer();
            }

            Location location = Workbenches.get(player);

            if (result == null) {
                event.setCancelled(true);
                Messages.sendDenySound(player, location);
                return;
            }

            Recipe bukkitRecipe = event.getRecipe();
            WorkbenchRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe);

            if (recipe == null) {
                return;
            }

            Args a = Args.create().player(player).inventory(inv).recipe(recipe).location(location).build();

            if (!recipe.checkFlags(a)) {
                Messages.sendDenySound(player, location);
                event.setCancelled(true);
                return;
            }

            result = Recipes.recipeGetResult(a, recipe); // gets the same stored result if event was previously cancelled

            int mouseButton;
            if (event.isRightClick()) {
                mouseButton = 1;
            } else {
                mouseButton = 0;
            }
            // Call the PRE event TODO upgrade to MouseButton when PR is pulled
            RecipeManagerCraftEvent callEvent = new RecipeManagerCraftEvent(recipe, result, player, event.getCursor(), event.isShiftClick(), mouseButton);
            Bukkit.getPluginManager().callEvent(callEvent);

            if (callEvent.isCancelled()) { // if event was cancelled by some other plugin then cancel this event
                event.setCancelled(true);
                return;
            }

            result = callEvent.getResult(); // get the result from the event if it was changed

            a = Args.create().player(player).inventory(inv).recipe(recipe).location(location).result(result).build();

            int times = craftResult(event, inv, player, recipe, result, a); // craft the result

            if (result != null) {
                a = Args.create().player(player).inventory(inv).recipe(recipe).location(location).result(result).build();

                if (times > 0) {
                    Recipes.recipeResetResult(a.playerName());
                }

                while (--times >= 0) {
                    a.clear();

                    if (recipe.sendCrafted(a)) {
                        a.sendEffects(a.player(), Messages.FLAG_PREFIX_RECIPE.get());
                    }

                    a.clear();

                    if (result.sendPrepare(a)) {
                        a.sendEffects(a.player(), Messages.FLAG_PREFIX_RESULT.get("{item}", ToolsItem.print(result)));
                    }

                    a.clear();

                    if (result.sendCrafted(a)) {
                        a.sendEffects(a.player(), Messages.FLAG_PREFIX_RESULT.get("{item}", ToolsItem.print(result)));
                    }

                    // TODO call post-event ?
                    // Bukkit.getPluginManager().callEvent(new RecipeManagerCraftEventPost(recipe, result, player, event.getCursor(), event.isShiftClick(), event.isRightClick() ? 1 : 0));
                }
            }

            new UpdateInventory(player, 2); // update inventory 2 ticks later
        } catch (Throwable e) {
            event.setCancelled(true);
            CommandSender sender;
            if (event.getView() != null && event.getView().getPlayer() instanceof Player) {
                sender = (Player) event.getView().getPlayer();
            } else {
                sender = null;
            }

            Messages.error(sender, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private int craftResult(CraftItemEvent event, CraftingInventory inv, Player player, WorkbenchRecipe recipe, ItemResult result, Args a) throws Throwable {
        if (!recipe.isMultiResult()) {
            if (result == null || result.getType() == Material.AIR) {
                event.setCurrentItem(null);
                return 0;
            }

            if (event.isShiftClick()) {
                if (recipe.hasNoShiftBit()) {
                    Messages.CRAFT_RECIPE_FLAG_NOSHIFTCLICK.printOnce(player);

                    event.setCancelled(true); // cancel regardless just to be safe

                    if (Tools.playerCanAddItem(player, result)) {
                        player.getInventory().addItem(result);
                        recipe.subtractIngredients(inv, result, false); // subtract from ingredients manually

                        return 1;
                    }

                    return 0;
                }

                int craftAmount = recipe.getCraftableTimes(inv); // Calculate how many times the recipe can be crafted

                ItemStack item = result.clone();
                item.setAmount(result.getAmount() * craftAmount);

                int space = Tools.playerFreeSpaceForItem(player, item);
                int crafted = Math.min((int) Math.ceil(Float.valueOf(space) / result.getAmount()), craftAmount);

                if (crafted > 0) {
                    event.setCurrentItem(result);
                    return crafted;
                }

                return 0;
            }

            ItemStack cursor = event.getCursor();
            ItemStack merged = ToolsItem.merge(cursor, result);

            if (merged != null) {
                event.setCurrentItem(result);
            } else {
                return 0;
            }

            if (recipe.hasFlag(FlagType.INGREDIENTCONDITION)) {
                recipe.subtractIngredients(inv, result, true);
            }
        } else {
            // more special treatment needed for multi-result ones...

            event.setCancelled(true); // need to cancel this from the start.

            // check if result is air / recipe failed
            if (result == null || result.getType() == Material.AIR) {
                Messages.CRAFT_RECIPE_MULTI_FAILED.printOnce(player);
                Messages.sendFailSound(player, a.location());
            } else {
                if (event.isShiftClick()) {
                    if (!recipe.hasNoShiftBit()) {
                        Messages.CRAFT_RECIPE_FLAG_NOSHIFTCLICK.printOnce(player);
                        event.setCancelled(true);
                        return 0;
                    }

                    Messages.CRAFT_RECIPE_MULTI_NOSHIFTCLICK.printOnce(player);

                    if (Tools.playerCanAddItem(player, result)) {
                        player.getInventory().addItem(result);
                    } else {
                        return 0;
                    }
                } else {
                    ItemStack cursor = event.getCursor();
                    ItemStack merged = ToolsItem.merge(cursor, result);

                    if (merged != null) {
                        event.setCursor(merged);
                    } else {
                        Messages.CRAFT_RECIPE_MULTI_CURSORFULL.printOnce(player);
                        return 0;
                    }
                }
            }

            recipe.subtractIngredients(inv, result, false); // subtract from ingredients manually

            // update displayed result
            // TODO need accurate reading if there is a recipe!
            /*
             * if(inv.getResult() != null && inv.getResult().getType() != Material.AIR) { event.setCurrentItem(recipe.getDisplayResult(a)); } else { event.setCurrentItem(null); }
             */
        }

        return 1;
    }

    /*
     * Workbench monitor events
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void inventoryClose(InventoryCloseEvent event) {
        HumanEntity human = event.getPlayer();

        if (event.getView().getType() == InventoryType.WORKBENCH) {
            Workbenches.remove(human);
        }

        if (Settings.getInstance().getFixModResults()) {
            for (ItemStack item : human.getInventory().getContents()) {
                itemProcess(item);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Block block = event.getClickedBlock();

                switch (block.getType()) {
                    case WORKBENCH:
                    case FURNACE:
                    case BURNING_FURNACE:
                    case BREWING_STAND:
                    case ENCHANTMENT_TABLE:
                    case ANVIL:
                        if (!RecipeManager.getPlugin().canCraft(event.getPlayer())) {
                            event.setCancelled(true);
                            return;
                        }

                        if (block.getType() == Material.WORKBENCH) {
                            Workbenches.add(event.getPlayer(), event.getClickedBlock().getLocation());
                        }

                        break;
                    default:
                        break;
                }

                break;


            case PHYSICAL:
                break;

            default:
                Workbenches.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerTeleport(PlayerTeleportEvent event) {
        Workbenches.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeath(PlayerDeathEvent event) {
        Workbenches.remove(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Players.remove(player);
        Workbenches.remove(player);
        Recipes.recipeResetResult(player.getName());
        Messages.clearPlayer(player.getName());
    }

    /*
     * Furnace craft events
     */

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        try {
            Inventory inv = event.getInventory();

            if (inv instanceof FurnaceInventory) {
                InventoryHolder holder = inv.getHolder();

                if (holder != null && holder instanceof Furnace) {
                    HumanEntity ent = event.getWhoClicked();

                    if (ent == null || !(ent instanceof Player)) {
                        return;
                    }

                    furnaceClick(event, (Furnace) holder, (Player) ent);
                }

                return;
            }
        } catch (Throwable e) {
            event.setCancelled(true);
            CommandSender sender;
            if (event.getWhoClicked() instanceof Player) {
                sender = (Player) event.getWhoClicked();
            } else {
                sender = null;
            }

            Messages.error(sender, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private void furnaceClick(InventoryClickEvent event, Furnace furnace, Player player) throws Throwable {
        if (!RecipeManager.getPlugin().canCraft(player)) {
            event.setCancelled(true);
            return;
        }

        if (event.getRawSlot() == -1) {
            return;
        }

        FurnaceInventory inventory = furnace.getInventory();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getRawSlot();

        switch (slot) {
            case 0: // INGREDIENT slot
                if (event.getClick() == ClickType.NUMBER_KEY) {
                    if (clicked == null || clicked.getType() == Material.AIR) {
                        int hotbarButton = event.getHotbarButton();
                        ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);

                        if (hotbarItem != null && hotbarItem.getType() != Material.AIR) {
                            SmeltRecipe recipe = RecipeManager.getRecipes().getSmeltRecipe(hotbarItem);

                            if (recipe != null) {
                                if (recipe.hasFlag(FlagType.REMOVE)) {
                                    event.setCancelled(true);
                                }

                                FurnaceData data = Furnaces.get(furnace.getLocation());

                                Args a = Args.create().player(data.getFueler()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventory(inventory).extra(inventory.getSmelting()).build();

                                if (furnaceHandleFlaggable(recipe, a, true)) {
                                    furnace.setCookTime((short) (200 - recipe.getCookTicks()));
                                } else {
                                    furnace.setCookTime((short) 0);
                                }
                            }
                        }
                    }

                } else if (cursor != null && cursor.getType() != Material.AIR) {
                    if (clicked == null || clicked.getType() == Material.AIR || !ToolsItem.isSameItem(cursor, clicked, true)) {
                        SmeltRecipe recipe = RecipeManager.getRecipes().getSmeltRecipe(cursor);

                        if (recipe != null) {
                            if (recipe.hasFlag(FlagType.REMOVE)) {
                                event.setCancelled(true);
                            }

                            FurnaceData data = Furnaces.get(furnace.getLocation());

                            Args a = Args.create().player(data.getFueler()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventory(inventory).extra(inventory.getSmelting()).build();

                            if (furnaceHandleFlaggable(recipe, a, true)) {
                                furnace.setCookTime((short) (200 - recipe.getCookTicks()));
                            } else {
                                furnace.setCookTime((short) 0);
                            }
                        }
                    }
                }
                break;
            case 1: // FUEL slot
                // TODO middle click detection required
                if (event.isShiftClick() /* || event.isMiddleClick() */) {
                    cursor = null; // if you're shift+clicking or using middle click on the slot then you're not placing anything
                }

                if (!furnaceModifySlot(furnace, inventory, player, slot, cursor)) {
                    event.setCancelled(true);
                    new UpdateInventory(player, 0);
                    return;
                }

                break;

            case 2: // RESULT slot
                break;

            default: // player inventory - Shift+Click handling in player inventory while having furnace UI opened
                if (slot == -999 || !event.isShiftClick() || clicked == null || clicked.getType() == Material.AIR) {
                    break; // abort if clicked outside of inventory OR not shift+click OR clicked on empty slot
                }

                // Get the target slot for the shift+click
                // First checks if the setting is for normal shift+click mode
                // Then checks if the clicked item is a fuel recipe and sends it to fuel slot if so, otherwise to ingredient slot
                // If it's left/right click mode then see if it's right click and send to fuel slot otherwise to ingredient slot
                int targetSlot = 0;
                if (Settings.getInstance().getFurnaceShiftClick() == 'f') {
                    if (RecipeManager.getRecipes().getFuelRecipe(clicked) != null || event.isRightClick()) {
                        targetSlot = 1;
                    }
                }

                ItemStack item = inventory.getItem(targetSlot); // Get the item at the target slot
                boolean similarItems = clicked.isSimilar(item); // Check if the clicked item is similar to the item at the targeted slot

                // Check if it's normal shift+click mode setting and if targeted slot is the fuel slot and there is an item there but it's not similar to our clicked item
                if (Settings.getInstance().getFurnaceShiftClick() == 'f' && targetSlot == 1 && item != null && !similarItems) {
                    targetSlot = 0; // change the target slot to ingredient slot
                    item = inventory.getItem(targetSlot); // get the item at the new set slot
                    similarItems = clicked.isSimilar(item); // update similarity check
                }

                if (item == null || item.getType() == Material.AIR) { // If targeted item slot is empty
                    // Check if item is allowed to be placed on that slot

                    if (furnaceModifySlot(furnace, inventory, player, targetSlot, clicked)) {
                        inventory.setItem(targetSlot, clicked); // send the item to the slot
                        event.setCurrentItem(null); // clear the clicked slot
                        event.setCancelled(true); // cancel only if we're going to mess with the items
                        new UpdateInventory(player, 0); // update inventory to see the changes client-side
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    // Otherwise the targeted slot contains some item, need to identify if we can stack over it

                    int maxStack = Math.max(inventory.getMaxStackSize(), item.getType().getMaxStackSize()); // see how much we can place on that slot
                    int itemAmount = item.getAmount(); // get how many items there are in the stack

                    if (similarItems && itemAmount < maxStack) { // if item has room for more and they're similar
                        event.setCancelled(true); // cancel only if we're going to mess with the items

                        int amount = itemAmount + clicked.getAmount(); // add the stacks together
                        int diff = amount - maxStack; // check to see if there are any leftovers

                        item.setAmount(Math.min(amount, maxStack)); // set the amount of targeted slot to the added amount OR max stack if it's exceeded

                        if (diff > 0) {
                            clicked.setAmount(diff); // reduce stack amount from clicked stack if there are leftovers
                        } else {
                            event.setCurrentItem(null); // entirely remove the clicked stack if there are no leftovers
                        }

                        new UpdateInventory(player, 0); // update inventory to see the changes client-side
                    }
                }
        }
    }

    private boolean furnaceModifySlot(Furnace furnace, FurnaceInventory inv, Player player, int slot, ItemStack item) throws Throwable {
        // Furnace is burning
        if (furnace.getBurnTime() > 0) {
            ItemStack itemToTest;
            if (slot == 0) {
                itemToTest = item;
            } else {
                itemToTest = inv.getSmelting();
            }
            ItemStack i = ToolsItem.nullIfAir(itemToTest);
            ItemStack f = ToolsItem.nullIfAir(inv.getFuel());

            SmeltRecipe sr = RecipeManager.getRecipes().getSmeltRecipe(i);

            if (sr == null && f != null) {
                sr = RecipeManager.getRecipes().getSmeltRecipeWithFuel(f);
            }

            if (sr != null && sr.hasFuel()) {
                // recipe is smelt+fuel

                ItemStack fuelToTest;
                if (slot == 0) {
                    fuelToTest = inv.getSmelting();
                } else {
                    fuelToTest = f;
                }
                if (item != null && item.isSimilar(fuelToTest)) {
                    // recipe is smelt+fuel but added similar items!
                } else {
                    // recipe is a smelt+fuel recipe, removing active burn time
                    furnace.setBurnTime((short) 0);
                }
            }
        }

        ItemStack ingredientToTest;
        if (slot == 0) {
            ingredientToTest = item;
        } else {
            ingredientToTest = inv.getSmelting();
        }
        ItemStack ingredient = ToolsItem.nullIfAir(ingredientToTest);

        ItemStack testFuel;
        if (slot == 1) {
            testFuel = item;
        } else {
            testFuel = inv.getFuel();
        }
        ItemStack fuel = ToolsItem.nullIfAir(testFuel);

        FurnaceData data = Furnaces.get(furnace.getLocation());

        if (slot == 0) {
            data.setFrozen(false);

            if (ingredient != null) {
                data.setSmelter(player);
                data.setSmelting(ingredient);
            }
        }

        if (slot == 1 && fuel != null) {
            data.setFueler(player);
            data.setFuel(fuel);
        }

        SmeltRecipe smeltRecipe = RecipeManager.getRecipes().getSmeltRecipe(ingredient);
        Location location = furnace.getLocation();

        if (smeltRecipe == null && fuel != null) {
            smeltRecipe = RecipeManager.getRecipes().getSmeltRecipeWithFuel(fuel);
        }

        if (smeltRecipe != null) {
            if (smeltRecipe.hasFuel() && fuel != null && ingredient != null) {
                if (!ToolsItem.isSimilarDataWildcard(smeltRecipe.getIngredient(), ingredient)) {
                    Messages.SMELT_FUEL_NEEDINGREDIENT.print(player, null, "{ingredient}", ToolsItem.print(smeltRecipe.getIngredient()), "{fuel}", ToolsItem.print(smeltRecipe.getFuel()));
                    return false;
                }

                if (!ToolsItem.isSimilarDataWildcard(smeltRecipe.getFuel(), fuel)) {
                    Messages.SMELT_FUEL_NEEDFUEL.print(player, null, "{ingredient}", ToolsItem.print(smeltRecipe.getIngredient()), "{fuel}", ToolsItem.print(smeltRecipe.getFuel()));
                    return false;
                }
            }

            if (slot == 0) {
                Args a = Args.create().player(player).location(location).inventory(inv).recipe(smeltRecipe).extra(ingredient).build();

                if (smeltRecipe.checkFlags(a)) {
                    a.sendEffects(player, Messages.FLAG_PREFIX_RECIPE.get());
                    a.clear();

                    if (smeltRecipe.sendPrepare(a)) {
                        a.sendEffects(player, Messages.FLAG_PREFIX_RECIPE.get());
                        return true;
                    }

                    a.sendReasons(player, Messages.FLAG_PREFIX_RECIPE.get());
                    return false;
                }

                a.sendReasons(player, Messages.FLAG_PREFIX_RECIPE.get());
                return false;
            }
        }

        FuelRecipe fuelRecipe = RecipeManager.getRecipes().getFuelRecipe(fuel);

        if (fuelRecipe != null) {
            if (slot == 1) {
                Args a = Args.create().player(player).location(location).inventory(inv).recipe(smeltRecipe).extra(fuel).build();

                if (fuelRecipe.checkFlags(a)) {
                    a.sendEffects(player, Messages.FLAG_PREFIX_RECIPE.get());
                    a.clear();

                    if (fuelRecipe.sendPrepare(a)) {
                        a.sendEffects(player, Messages.FLAG_PREFIX_RECIPE.get());
                        return true;
                    }

                    a.sendReasons(player, Messages.FLAG_PREFIX_RECIPE.get());
                    return false;
                }

                a.sendReasons(player, Messages.FLAG_PREFIX_RECIPE.get());
                return false;
            }
        }

        return true;
    }

    private boolean furnaceHandleFlaggable(Flaggable flaggable, Args a, boolean craft) {
        if (flaggable == null) {
            return false;
        }

        ItemStack smelted = a.inventory().getItem(2);

        if (smelted != null && smelted.getType() != Material.AIR) {
            ItemResult result = a.result();
            if (result != null) {
                boolean isSame = ToolsItem.isSameItem(smelted, result, true);

                if (!isSame) {
                    return false;
                }
            }
        }


        String msg = Messages.FLAG_PREFIX_FURNACE.get("{location}", Tools.printLocation(a.location()));

        a.clear();

        if (flaggable.checkFlags(a)) {
            a.sendEffects(a.player(), msg);
        } else {
            a.sendReasons(a.player(), msg);
            return false;
        }

        a.clear();

        if (flaggable.sendPrepare(a)) {
            a.sendEffects(a.player(), msg);
        } else {
            a.sendReasons(a.player(), msg);
            return false;
        }

        if (craft) {
            a.clear();

            if (flaggable.sendCrafted(a)) {
                a.sendEffects(a.player(), msg);
            } else {
                a.sendReasons(a.player(), msg);
                return false;
            }
        }

        a.clear();

        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void furnaceBurn(FurnaceBurnEvent event) {
        short burnTime = 0;
        short cookTime = 0;
        Furnace furnace = (Furnace) event.getBlock().getState();
        FurnaceInventory inventory = furnace.getInventory();
        FurnaceData data = Furnaces.get(furnace.getLocation());

        FuelRecipe fuelRecipe = RecipeManager.getRecipes().getFuelRecipe(event.getFuel());

        if (fuelRecipe != null) {
            if (fuelRecipe.hasFlag(FlagType.REMOVE)) {
                event.setCancelled(true);
            }

            Args a = Args.create().player(data.getFueler()).location(furnace.getLocation()).recipe(fuelRecipe).inventory(inventory).extra(inventory.getSmelting()).build();

            if (!furnaceHandleFlaggable(fuelRecipe, a, true)) {
                event.setCancelled(true);
            }

            burnTime = (short) fuelRecipe.getBurnTicks();
        }

        ItemStack ingredient = inventory.getSmelting();
        SmeltRecipe recipe = RecipeManager.getRecipes().getSmeltRecipe(ingredient);

        if (recipe != null) {
            if (recipe.hasFlag(FlagType.REMOVE)) {
                event.setCancelled(true);
            }

            Args a = Args.create().player(data.getFueler()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventory(inventory).extra(inventory.getSmelting()).build();

            if (!furnaceHandleFlaggable(recipe, a, true)) {
                event.setCancelled(true);
            }

            cookTime = (short) (200 - recipe.getCookTicks());
        }

        if (fuelRecipe != null) {
            event.setBurnTime(burnTime);
        }

        if (recipe != null) {
            furnace.setCookTime(cookTime);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void furnaceSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        FurnaceInventory inventory = furnace.getInventory();

        short cookTime = 0;
        ItemStack ingredient = inventory.getSmelting();
        SmeltRecipe recipe = RecipeManager.getRecipes().getSmeltRecipe(ingredient);

        if (recipe != null) {
            if (recipe.hasFlag(FlagType.REMOVE)) {
                event.setCancelled(true);
            }

            FurnaceData data = Furnaces.get(furnace.getLocation());

            Args a = Args.create().player(data.getFueler()).location(furnace.getLocation()).recipe(recipe).inventory(inventory).extra(inventory.getSmelting()).build();

            ItemResult result = recipe.getResult(a);

            event.setResult(event.getResult());

            if (!furnaceHandleFlaggable(recipe, a, true) || (result != null && !furnaceHandleFlaggable(result, a, true))) {
                // Frozen?
                /*
                if (a.hasPlayer()) {
                    Messages.SMELT_FROZEN.print(a.player(), null, "{location}", Tools.printLocation(a.location()));
                }

                data.setFrozen(true);
                event.setCancelled(true);
                */
            } else {
                if (a.result() == null || a.result().getType() == Material.AIR) {
                    recipe.subtractIngredient(inventory, false);
                    event.setCancelled(true);
                }
            }

            cookTime = (short) (200 - recipe.getCookTicks());
        }

        if (recipe != null) {
            furnace.setCookTime(cookTime);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void furnaceTakeResult(FurnaceExtractEvent event) {
        try {
            if (event.getExpToDrop() == 0) {
                return;
            }

            BlockState state = event.getBlock().getState();

            if (!(state instanceof Furnace)) {
                return; // highly unlikely but better safe than sorry
            }

            SmeltRecipe recipe = furnaceResultRecipe((Furnace) state);

            if (recipe != null) {
                event.setExpToDrop(0);
            }
        } catch (Throwable e) {
            Messages.error(null, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private SmeltRecipe furnaceResultRecipe(Furnace furnace) {
        ItemStack ingredient = ToolsItem.nullIfAir(furnace.getInventory().getSmelting());
        SmeltRecipe smeltRecipe = null;
        ItemStack result = furnace.getInventory().getResult();

        if (ingredient == null) {
            // Guess recipe by result - inaccurate

            if (result == null) {
                return null;
            }

            for (SmeltRecipe r : RecipeManager.getRecipes().indexSmelt.values()) {
                if (result.isSimilar(r.getResult())) {
                    smeltRecipe = r;
                    break;
                }
            }
        } else {
            smeltRecipe = RecipeManager.getRecipes().getSmeltRecipe(ingredient);
        }

        return smeltRecipe;
    }

    // TODO find a way to detect if event actually moved an item !
    @EventHandler
    public void inventoryItemMove(InventoryMoveItemEvent event) {
        try {
            if (event.getDestination() instanceof FurnaceInventory) {
                int slot = hopperFurnaceSlot(event.getSource(), false);

                if (slot < 0) {
                    return;
                }

                FurnaceInventory inv = (FurnaceInventory) event.getDestination();
                Furnace furnace = inv.getHolder();

                // TODO get player that placed the initial item in the hopper ?
                if (!furnaceModifySlot(furnace, inv, null, slot, event.getItem())) {
                    event.setCancelled(true);
                }
            }
            /*
             * else if(event.getSource() instanceof FurnaceInventory) { SlotType slot = hopperFurnaceSlot(event.getDestination(), true);
             *
             * if(slot == null) { return; }
             *
             * Messages.debug("RESULT TAKEN FROM FURNACE: " + event.getItem()); }
             */
        } catch (Throwable e) {
            event.setCancelled(true);
            Messages.error(null, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private int hopperFurnaceSlot(Inventory inventory, boolean take) {
        if (inventory != null) {
            InventoryHolder hopperHolder = inventory.getHolder();

            if (hopperHolder instanceof Hopper) {
                if (take) {
                    return 2; // RESULT
                }

                Hopper hopper = ((Hopper) hopperHolder);
                Dispenser dir = new Dispenser(0, hopper.getRawData());

                switch (dir.getFacing()) {
                    case NORTH:
                    case SOUTH:
                    case EAST:
                    case WEST:
                        return 1; // FUEL

                    case DOWN:
                        return 0; // CRAFTING
                    default:
                        break;
                }
            }
        }

        return -1;
    }

    /*
     * Furnace monitor events
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event) {
        placeOrBreakFurnace(event.getBlock(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        placeOrBreakFurnace(event.getBlock(), false);
    }

    private void placeOrBreakFurnace(Block block, boolean place) {
        switch (block.getType()) {
            case BURNING_FURNACE:
            case FURNACE:
                if (place) {
                    Furnaces.add(block.getLocation());
                } else {
                    Furnaces.remove(block.getLocation());
                }
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void worldLoad(WorldLoadEvent event) {
        worldLoad(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void chunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) {
            findFurnaces(event.getChunk(), true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void chunkUnload(ChunkUnloadEvent event) {
        findFurnaces(event.getChunk(), false);
    }

    protected void worldLoad(World world) {
        Chunk[] chunks = world.getLoadedChunks();

        for (Chunk chunk : chunks) {
            findFurnaces(chunk, true);
        }
    }

    private void findFurnaces(final Chunk chunk, final boolean add) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }

        BlockState[] tileEntities;

        // Workaround for CB issues with block states.
        try {
            tileEntities = chunk.getTileEntities();
        // Loading Error for chunk at chunk.getX(), chunk.getZ(). Attempting workaround...
        } catch (Throwable e) {
            List<BlockState> list = new ArrayList<BlockState>(32);
            int maxY = chunk.getWorld().getMaxHeight();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < maxY; y++) {
                        Block block = chunk.getBlock(x, y, z);

                        switch (block.getType()) {
                            case FURNACE:
                            case BURNING_FURNACE:
                                list.add(block.getState());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            tileEntities = list.toArray(new BlockState[list.size()]);
        }

        Set<BlockID> added;
        if (add) {
            added = new HashSet<BlockID>(tileEntities.length);
        } else {
            added = null;
        }

        for (BlockState state : tileEntities) {
            if (state instanceof Furnace) {
                BlockID id = BlockID.fromLocation(state.getLocation());

                if (add) {
                    Furnaces.set(id, (Furnace) state);
                    added.add(id);
                } else {
                    Furnaces.remove(id);
                }
            }
        }

        if (add) {
            Furnaces.cleanChunk(chunk, added);
        }
    }

    /*
     * Marked item monitor events
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (Settings.getInstance().getUpdateBooks()) {
            RecipeManager.getRecipeBooks().updateBook(player, item);
        }

        if (Settings.getInstance().getFixModResults()) {
            itemProcess(item);
        }
    }

    private void itemProcess(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        List<String> lore = meta.getLore();

        if (lore == null || lore.isEmpty()) {
            return;
        }

        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);

            if (s != null && s.startsWith(Recipes.RECIPE_ID_STRING)) {
                lore.remove(i);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    /*
     * Update check notifier
     */

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Players.addJoined(player);

        if (Settings.getInstance().getUpdateCheckEnabled() && player.hasPermission("recipemanager.command.rmupdate")) {
            String latestVersion = Updater.getLatestVersion();
            String currentVersion = Updater.getCurrentVersion();

            if (latestVersion != null) {
                int compare = Updater.compareVersions();

                if (compare == -1) {
                    Messages.send(player, "[RecipeManager] New version: <green>" + latestVersion + "<reset> ! You're using <yellow>" + currentVersion + "<reset>, grab it at: <light_purple>" + Updater.getLatestLink());
                } else if (compare == 2) {
                    Messages.send(player, "[RecipeManager] New alpha/beta version: <green>" + latestVersion + " " + Updater.getLatestBetaStatus() + "<reset> ! You're using <yellow>" + currentVersion + "<reset>, grab it at: <light_purple>" + Updater.getLatestLink());
                } else if (compare == 3) {
                    Messages.send(player, "[RecipeManager] BukkitDev has a different alpha/beta version: <green>" + latestVersion + " " + Updater.getLatestBetaStatus() + "<reset> ! You're using <yellow>" + currentVersion + " " + Updater.getCurrentBetaStatus() + "<reset>, grab it at: <light_purple>" + Updater.getLatestLink());
                }
            }
        }
    }
}
