package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;
import haveric.recipeManager.UpdateInventory;
import haveric.recipeManager.api.events.RecipeManagerFuelBurnEndEvent;
import haveric.recipeManager.api.events.RecipeManagerFuelBurnRandomEvent;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flaggable;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.furnace.data.FurnaceData;
import haveric.recipeManager.recipes.furnace.data.Furnaces;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

public class RMBaseFurnaceEvents implements Listener {
    public RMBaseFurnaceEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getRMFurnaceEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getRMFurnaceEvents(), RecipeManager.getPlugin());
    }

    private boolean isRecipeSameAsResult(Args a) {
        boolean isSame = false;
        ItemStack smelted = a.inventory().getItem(2);

        if (smelted != null && smelted.getType() != Material.AIR) {
            ItemResult result = a.result();
            if (result != null) {
                isSame = ToolsItem.isSameItem(smelted, result, true);
            }
        } else {
            isSame = true;
        }

        return isSame;
    }

    /*
     * Furnace monitor events
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void furnacePlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        Location location = block.getLocation();

        if (type == Material.FURNACE || (!Version.has1_13Support() && type == Material.getMaterial("BURNING_FURNACE"))) {
            Furnaces.add(location);
        } else if (Version.has1_14Support() && (type == Material.BLAST_FURNACE || type == Material.SMOKER)) {
            Furnaces.add(location);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void furnaceBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        Location location = block.getLocation();

        if (type == Material.FURNACE || (!Version.has1_13Support() && type == Material.getMaterial("BURNING_FURNACE"))) {
            Furnaces.remove(location);
        } else if (Version.has1_14Support() && (type == Material.BLAST_FURNACE || type == Material.SMOKER)) {
            Furnaces.remove(location);
        }
    }

    private RMBaseFurnaceRecipe getSpecificFurnaceRecipe(Furnace furnace, ItemStack item) {
        RMBaseFurnaceRecipe recipe;

        if (Version.has1_14Support() && furnace instanceof BlastFurnace) {
            recipe = RecipeManager.getRecipes().getRMBlastingRecipe(item);
        } else if (Version.has1_14Support() && furnace instanceof Smoker) {
            recipe = RecipeManager.getRecipes().getRMSmokingRecipe(item);
        } else {
            recipe = RecipeManager.getRecipes().getSmeltRecipe(item);
        }

        return recipe;
    }

    /*
     * Furnace craft events
     */
    @EventHandler
    public void inventoryDrag(InventoryDragEvent event) {
        Inventory inv = event.getInventory();

        if (inv instanceof FurnaceInventory) {
            HumanEntity entity = event.getWhoClicked();

            if (entity instanceof Player) {
                if (event.getRawSlots().contains(0)) {
                    FurnaceInventory inventory = (FurnaceInventory) inv;

                    Furnace furnace = inventory.getHolder();
                    ItemStack slot = inventory.getItem(0);

                    if (slot == null || slot.getType() == Material.AIR) {
                        ItemStack cursor = event.getOldCursor();

                        RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, cursor);

                        if (recipe != null) {
                            if (recipe.hasFlag(FlagType.REMOVE)) {
                                event.setCancelled(true);
                            }

                            FurnaceData data = Furnaces.get(furnace.getLocation());
                            ItemStack fuel = data.getFuel();

                            if (fuel == null) {
                                fuel = inventory.getFuel();
                            }

                            ItemStack recipeFuel = recipe.getFuel();

                            if (recipeFuel != null && !ToolsItem.isSameItem(recipeFuel, fuel, true)) {
                                event.setCancelled(true);
                            } else {
                                Args a = Args.create().player(data.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventoryView(event.getView()).extra(inventory.getSmelting()).build();
                                ItemResult result = recipe.getResult(a);

                                if (furnaceHandleFlaggable(recipe, a, false, true) && (result == null || furnaceHandleFlaggable(result, a, false, true)) && isRecipeSameAsResult(a)) {
                                    if (Version.has1_14Support()) {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) recipe.getCookTicks());
                                    } else {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) (200 - recipe.getCookTicks()));
                                    }
                                } else {
                                    // TODO: Handle preventing the furnace with a different result in a different way
                                    if (!Version.has1_14Support()) {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();

        if (ent instanceof Player) {
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();

            if (inv instanceof FurnaceInventory && holder instanceof Furnace) {
                furnaceClick(event, (Furnace) holder, (Player) ent);
            }
        }
    }

    private void furnaceClick(InventoryClickEvent event, Furnace furnace, Player player) {
        FurnaceData data = Furnaces.get(furnace.getLocation());
        if (data.getFuelerUUID() == null) {
            data.setFuelerUUID(player.getUniqueId());
        }

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
                            RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, hotbarItem);

                            if (recipe != null) {
                                if (recipe.hasFlag(FlagType.REMOVE)) {
                                    event.setCancelled(true);
                                }

                                data.setFuelerUUID(player.getUniqueId());

                                Args a = Args.create().player(data.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventoryView(event.getView()).extra(inventory.getSmelting()).build();

                                if (furnaceHandleFlaggable(recipe, a, false, true) && isRecipeSameAsResult(a)) {
                                    if (Version.has1_14Support()) {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) recipe.getCookTicks());
                                    } else {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) (200 - recipe.getCookTicks()));
                                    }
                                } else {
                                    // TODO: Handle preventing the furnace with a different result in a different way
                                    if (!Version.has1_14Support()) {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) 0);
                                    }
                                }
                            }
                        }
                    }
                } else if (cursor != null && cursor.getType() != Material.AIR) {
                    if (clicked == null || clicked.getType() == Material.AIR || !ToolsItem.isSameItem(cursor, clicked, true)) {
                        RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, cursor);

                        if (recipe != null) {
                            if (recipe.hasFlag(FlagType.REMOVE)) {
                                event.setCancelled(true);
                            }

                            data.setFuelerUUID(player.getUniqueId());

                            ItemStack fuel = data.getFuel();

                            if (fuel == null) {
                                fuel = inventory.getFuel();
                            }

                            ItemStack recipeFuel = recipe.getFuel();

                            if (recipeFuel != null && !ToolsItem.isSameItem(recipeFuel, fuel, true)) {
                                event.setCancelled(true);
                            } else {
                                Args a = Args.create().player(data.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventoryView(event.getView()).extra(inventory.getSmelting()).build();
                                ItemResult result = recipe.getResult(a);


                                if (furnaceHandleFlaggable(recipe, a, false, true) && (result == null || furnaceHandleFlaggable(result, a, false, true)) && isRecipeSameAsResult(a)) {
                                    if (Version.has1_14Support()) {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) recipe.getCookTicks());
                                    } else {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) (200 - recipe.getCookTicks()));
                                    }
                                } else {
                                    // TODO: Handle preventing the furnace with a different result in a different way
                                    if (!Version.has1_14Support()) {
                                        ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) 0);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 1: // FUEL slot
                ItemStack fuel = ToolsItem.nullIfAir(cursor);

                if (fuel != null) {
                    data.setFuelerUUID(player.getUniqueId());
                }

                if (event.getClick() == ClickType.NUMBER_KEY) {
                    int hotbarButton = event.getHotbarButton();
                    ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);

                    FuelRecipe fuelRecipe = Recipes.getInstance().getFuelRecipe(hotbarItem);

                    if (fuelRecipe != null && !fuelRecipe.getInfo().getOwner().equals(RMCRecipeInfo.RecipeOwner.MINECRAFT)) {
                        if (hotbarItem != null && hotbarItem.getType() != Material.AIR) {
                            if (clicked == null || clicked.getType() == Material.AIR) {
                                event.setCurrentItem(hotbarItem.clone());
                                ToolsItem.replaceItem(player.getInventory(), hotbarButton, new ItemStack(Material.AIR));
                                event.setResult(Event.Result.DENY);
                            }
                        }
                    }
                } else if (event.isLeftClick()) {
                    FuelRecipe fuelRecipe = Recipes.getInstance().getFuelRecipe(cursor);

                    if (fuelRecipe != null && !fuelRecipe.getInfo().getOwner().equals(RMCRecipeInfo.RecipeOwner.MINECRAFT)) {
                        if (cursor != null && cursor.getType() != Material.AIR) {
                            if (clicked == null || clicked.getType() == Material.AIR) {
                                event.setCurrentItem(cursor.clone());
                                event.setCursor(new ItemStack(Material.AIR));
                                event.setResult(Event.Result.DENY);
                            } else {
                                if (ToolsItem.isSameItem(cursor, clicked, false)) {
                                    int clickedAmount = clicked.getAmount();
                                    int cursorAmount = cursor.getAmount();

                                    int total = clickedAmount + cursorAmount;
                                    int maxStack = clicked.getType().getMaxStackSize();
                                    if (total <= maxStack) {
                                        ItemStack combinedClone = clicked.clone();
                                        combinedClone.setAmount(total);
                                        event.setCurrentItem(combinedClone);
                                        event.setCursor(new ItemStack(Material.AIR));
                                        event.setResult(Event.Result.DENY);
                                    } else {
                                        int left = total - maxStack;

                                        ItemStack maxClone = clicked.clone();
                                        maxClone.setAmount(maxStack);
                                        event.setCurrentItem(maxClone);

                                        if (left > 0) {
                                            ItemStack leftClone = clicked.clone();
                                            leftClone.setAmount(left);
                                            event.setCursor(leftClone);
                                        }
                                        event.setResult(Event.Result.DENY);
                                    }
                                } else {
                                    ItemStack clickedClone = clicked.clone();
                                    ItemStack cursorClone = cursor.clone();
                                    event.setCurrentItem(cursorClone);
                                    event.setCursor(clickedClone);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        }
                    }
                } else if (event.isRightClick()) {
                    FuelRecipe fuelRecipe = Recipes.getInstance().getFuelRecipe(cursor);

                    if (fuelRecipe != null && !fuelRecipe.getInfo().getOwner().equals(RMCRecipeInfo.RecipeOwner.MINECRAFT)) {
                        if (cursor != null && cursor.getType() != Material.AIR) {
                            if (clicked == null || clicked.getType() == Material.AIR) {
                                int cursorAmount = cursor.getAmount();
                                ItemStack cursorClone = cursor.clone();
                                cursorClone.setAmount(cursorAmount - 1);

                                ItemStack singleClone = cursor.clone();
                                singleClone.setAmount(1);
                                event.setCurrentItem(singleClone);
                                event.setCursor(cursorClone);
                                event.setResult(Event.Result.DENY);
                            } else {
                                if (ToolsItem.isSameItem(cursor, clicked, false)) {
                                    int clickedAmount = clicked.getAmount();
                                    int cursorAmount = cursor.getAmount();

                                    int maxStack = clicked.getType().getMaxStackSize();

                                    if (clickedAmount + 1 < maxStack) {
                                        ItemStack clickedClone = clicked.clone();
                                        clickedClone.setAmount(clickedAmount + 1);
                                        event.setCurrentItem(clickedClone);

                                        ItemStack cursorClone = cursor.clone();
                                        cursorClone.setAmount(cursorAmount - 1);
                                        event.setCursor(cursorClone);
                                        event.setResult(Event.Result.DENY);
                                    }
                                } else {
                                    ItemStack clickedClone = clicked.clone();
                                    ItemStack cursorClone = cursor.clone();
                                    event.setCurrentItem(cursorClone);
                                    event.setCursor(clickedClone);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        }
                    }
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

                if (Settings.getInstance().getFurnaceShiftClick() == 'f' || event.isRightClick()) {
                    if (RecipeManager.getRecipes().getFuelRecipe(clicked) != null) {
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
                    if (targetSlot == 1) {
                        inventory.setItem(targetSlot, clicked); // send the item to the slot
                        event.setCurrentItem(null); // clear the clicked slot

                        event.setCancelled(true); // cancel only if we're going to mess with the items
                        new UpdateInventory(player, 0); // update inventory to see the changes client-side
                    } else {
                        RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, clicked);

                        if (recipe != null) {
                            data.setFuelerUUID(player.getUniqueId());

                            boolean same = false;

                            if (Version.has1_13Support()) {
                                for (Material material : recipe.getIngredientChoice()) {
                                    if (clicked.getType() == material) {
                                        same = true;
                                        break;
                                    }
                                }
                            } else {
                                same = ToolsItem.isSameItem(clicked, recipe.getIngredient(), true);
                            }

                            if (same) {
                                data = Furnaces.get(furnace.getLocation());
                                fuel = data.getFuel();

                                if (fuel == null) {
                                    fuel = inventory.getFuel();
                                }

                                ItemStack recipeFuel = recipe.getFuel();

                                if (recipeFuel != null && !ToolsItem.isSameItem(recipeFuel, fuel, true)) {
                                    event.setCancelled(true);
                                } else {
                                    Args a = Args.create().player(data.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventoryView(event.getView()).extra(inventory.getSmelting()).build();
                                    ItemResult result = recipe.getResult(a);

                                    if (furnaceHandleFlaggable(recipe, a, false, true) && (result == null || furnaceHandleFlaggable(result, a, false, true)) && isRecipeSameAsResult(a)) {
                                        inventory.setItem(targetSlot, clicked); // send the item to the slot
                                        event.setCurrentItem(null); // clear the clicked slot

                                        if (Version.has1_14Support()) {
                                            ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) recipe.getCookTicks());
                                        } else {
                                            ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) (200 - recipe.getCookTicks()));
                                        }
                                    } else {
                                        // TODO: Handle preventing the furnace with a different result in a different way
                                        if (!Version.has1_14Support()) {
                                            ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) 0);
                                        }
                                    }
                                }
                            } else {
                                event.setCancelled(true);
                            }
                            event.setCancelled(true); // cancel only if we're going to mess with the items
                            new UpdateInventory(player, 0); // update inventory to see the changes client-side
                        }
                    }
                } else {
                    // Otherwise the targeted slot contains some item, need to identify if we can stack over it

                    int maxStack = Math.min(inventory.getMaxStackSize(), item.getType().getMaxStackSize()); // see how much we can place on that slot
                    int itemAmount = item.getAmount(); // get how many items there are in the stack

                    if (similarItems && itemAmount < maxStack) { // if item has room for more and they're similar
                        event.setCancelled(true); // cancel only if we're going to mess with the items
                        data.setFuelerUUID(player.getUniqueId());

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

    private boolean furnaceHandleFlaggable(Flaggable flaggable, Args a, boolean craft, boolean sendReasons) {
        if (flaggable == null) {
            return false;
        }

        String msg = Messages.getInstance().parse("flag.prefix.furnace", "{location}", Tools.printLocation(a.location()));

        a.clear();

        if (flaggable.checkFlags(a)) {
            a.sendEffects(a.player(), msg);
        } else {
            if (sendReasons) {
                a.sendReasons(a.player(), msg);
            }
            return false;
        }

        if (craft) {
            a.clear();

            if (flaggable.sendCrafted(a)) {
                a.sendEffects(a.player(), msg);
            } else {
                if (sendReasons) {
                    a.sendReasons(a.player(), msg);
                }
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

        final Furnace furnace = (Furnace) event.getBlock().getState();

        FurnaceInventory inventory = furnace.getInventory();

        Location furnaceLocation = furnace.getLocation();
        final FurnaceData data = Furnaces.get(furnaceLocation);

        ItemStack fuel = event.getFuel();

        final FuelRecipe fuelRecipe = RecipeManager.getRecipes().getFuelRecipe(fuel);

        if (fuelRecipe != null) {
            if (fuelRecipe.hasFlag(FlagType.REMOVE)) {
                event.setCancelled(true);
            }

            Args a = Args.create().player(data.getFuelerUUID()).location(furnaceLocation).recipe(fuelRecipe).inventory(inventory).extra(inventory.getSmelting()).build();

            if (!furnaceHandleFlaggable(fuelRecipe, a, true, false)) {
                event.setCancelled(true);
            }

            burnTime = (short) fuelRecipe.getBurnTicks();
        }

        data.setFuel(fuel);

        ItemStack ingredient = inventory.getSmelting();
        RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, ingredient);

        if (recipe != null) {
            if (recipe.hasFlag(FlagType.REMOVE)) {
                event.setCancelled(true);
            }

            ItemStack recipeFuel = recipe.getFuel();

            if (recipeFuel != null && !ToolsItem.isSameItem(recipeFuel, fuel, true)) {
                event.setCancelled(true);
            }

            Args a = Args.create().player(data.getFuelerUUID()).location(furnaceLocation).recipe(recipe).inventory(inventory).extra(inventory.getSmelting()).build();
            ItemResult result = recipe.getResult(a);

            boolean recipeFlaggable = furnaceHandleFlaggable(recipe, a, false, false);
            boolean resultFlaggable = false;
            if (result != null) {
                resultFlaggable = furnaceHandleFlaggable(result, a, false, false);
            }

            if (!isRecipeSameAsResult(a) || !recipeFlaggable || (result != null && !resultFlaggable)) {
                event.setCancelled(true);
            }

            if (Version.has1_14Support()) {
                cookTime = (short) recipe.getCookTicks();
            } else {
                cookTime = (short) (200 - recipe.getCookTicks());
            }
        }

        if (fuelRecipe != null) {
            event.setBurnTime(burnTime);

            long randTime = (long) Math.floor(Math.random() * burnTime);
            Bukkit.getScheduler().runTaskLater(RecipeManager.getPlugin(), new Runnable() {
                public void run() {
                    Bukkit.getPluginManager().callEvent(new RecipeManagerFuelBurnRandomEvent(fuelRecipe, furnace, data.getFuelerUUID()));
                }
            }, randTime);

            Bukkit.getScheduler().runTaskLater(RecipeManager.getPlugin(), new Runnable() {
                public void run() {
                    Bukkit.getPluginManager().callEvent(new RecipeManagerFuelBurnEndEvent(fuelRecipe, furnace, data.getFuelerUUID()));
                }
            }, burnTime);
        }

        if (Version.has1_13Support()) {
            if (recipe != null && furnace.getCookTime() == 0 && furnace.getBurnTime() == 0) {
                runFurnaceUpdateLater(furnace.getBlock(), cookTime);
            }
        } else { // Old method of setting cook time forcefully
            boolean isBurning = furnace.getType() == Material.getMaterial("BURNING_FURNACE");

            if (recipe != null && !isBurning) {
                runFurnaceUpdateLater(furnace.getBlock(), cookTime);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void randomBurn(RecipeManagerFuelBurnRandomEvent event) {
        FuelRecipe recipe = event.getRecipe();
        Furnace furnace = event.getFurnace();
        FurnaceInventory inventory = furnace.getInventory();
        Args a = Args.create().player(event.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).inventory(inventory).extra(inventory.getSmelting()).build();

        a.clear();

        String msg = Messages.getInstance().parse("flag.prefix.furnace", "{location}", Tools.printLocation(a.location()));

        if (recipe.sendFuelRandom(a)) {
            a.sendEffects(a.player(), msg);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void afterBurn(RecipeManagerFuelBurnEndEvent event) {
        FuelRecipe recipe = event.getRecipe();
        Furnace furnace = event.getFurnace();
        FurnaceInventory inventory = furnace.getInventory();
        Location furnaceLocation = furnace.getLocation();
        FurnaceData data = Furnaces.get(furnaceLocation);
        data.setFuel(null);

        Args a = Args.create().player(event.getFuelerUUID()).location(furnaceLocation).recipe(recipe).inventory(inventory).extra(inventory.getSmelting()).build();

        a.clear();

        String msg = Messages.getInstance().parse("flag.prefix.furnace", "{location}", Tools.printLocation(a.location()));

        if (recipe.sendFuelEnd(a)) {
            a.sendEffects(a.player(), msg);
        }
    }

    private void runFurnaceUpdateLater(Block block, short cookTime) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Furnace furnace = (Furnace) block.getState();

                if (Version.has1_14Support()) {
                    furnace.setCookTimeTotal(cookTime);
                } else {
                    furnace.setCookTime(cookTime);
                }
                furnace.update();
            }
        }.runTaskLater(RecipeManager.getPlugin(), 0);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void furnaceSmelt(FurnaceSmeltEvent event) {
        Block block = event.getBlock();
        Furnace furnace = (Furnace) block.getState();
        FurnaceInventory inventory = furnace.getInventory();

        short cookTime = 0;
        ItemStack ingredient = inventory.getSmelting();
        RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, ingredient);

        if (recipe != null) {
            if (recipe.hasFlag(FlagType.REMOVE)) {
                event.setCancelled(true);
            }

            FurnaceData data = Furnaces.get(furnace.getLocation());

            Args a = Args.create().player(data.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).inventory(inventory).extra(inventory.getSmelting()).build();

            ItemResult result = recipe.getResult(a);

            event.setResult(event.getResult());

            boolean recipeFlaggable = furnaceHandleFlaggable(recipe, a, true, true);
            boolean resultFlaggable = false;
            if (result != null) {
                resultFlaggable = furnaceHandleFlaggable(result, a, true, true);
            }

            if (!isRecipeSameAsResult(a) || !recipeFlaggable || (result != null && !resultFlaggable)) {
                event.setResult(new ItemStack(Material.AIR));
            } else {
                if (a.result() == null || a.result().getType() == Material.AIR || result.hasFlag(FlagType.NO_RESULT)) {
                    event.setResult(new ItemStack(Material.AIR));
                } else {
                    event.setResult(result.toItemStack());

                    if (recipe.hasFlag(FlagType.INGREDIENT_CONDITION) || result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                        recipe.subtractIngredient(inventory, result, true);
                    }
                }
            }

            if (Version.has1_14Support()) {
                cookTime = (short) recipe.getCookTicks();
            } else {
                cookTime = (short) (200 - recipe.getCookTicks());
            }
        }

        if (recipe != null) {
            ItemStack recipeFuel = recipe.getFuel();

            // TODO: Handle Furnace disabling recipe in 1.14
            if (!Version.has1_14Support()) {
                if (recipeFuel != null && !ToolsItem.isSameItem(recipeFuel, inventory.getFuel(), true)) {
                    cookTime = 0;
                }
            }

            runFurnaceUpdateLater(block, cookTime);
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


            RMBaseFurnaceRecipe recipe = furnaceResultRecipe((Furnace) state);

            if (recipe != null) {
                event.setExpToDrop(0);
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private RMBaseFurnaceRecipe furnaceResultRecipe(Furnace furnace) {
        ItemStack ingredient = ToolsItem.nullIfAir(furnace.getInventory().getSmelting());
        RMBaseFurnaceRecipe smeltRecipe = null;
        ItemStack result = furnace.getInventory().getResult();


        if (ingredient == null) {
            // Guess recipe by result - inaccurate

            if (result == null) {
                return null;
            }

            if (Version.has1_14Support() && furnace instanceof BlastFurnace) {
                for (RMBlastingRecipe r : RecipeManager.getRecipes().indexBlasting.values()) {
                    if (result.isSimilar(r.getResult())) {
                        smeltRecipe = r;
                        break;
                    }
                }
            } else if (Version.has1_14Support() && furnace instanceof Smoker) {
                for (RMSmokingRecipe r : RecipeManager.getRecipes().indexSmoking.values()) {
                    if (result.isSimilar(r.getResult())) {
                        smeltRecipe = r;
                        break;
                    }
                }
            } else {
                if (Version.has1_13Support()) {
                    for (RMFurnaceRecipe1_13 r : RecipeManager.getRecipes().indexSmelt.values()) {
                        if (result.isSimilar(r.getResult())) {
                            smeltRecipe = r;
                            break;
                        }
                    }
                } else {
                    for (RMFurnaceRecipe r : RecipeManager.getRecipes().indexSmeltLegacy.values()) {
                        if (result.isSimilar(r.getResult())) {
                            smeltRecipe = r;
                            break;
                        }
                    }
                }
            }
        } else {
            smeltRecipe = getSpecificFurnaceRecipe(furnace, ingredient);
        }

        return smeltRecipe;
    }

    @EventHandler
    public void inventoryMove(InventoryMoveItemEvent event) {
        Inventory dest = event.getDestination();

        if (dest instanceof FurnaceInventory) {
            FurnaceInventory furnaceInventory = (FurnaceInventory) dest;

            ItemStack smeltingItem = furnaceInventory.getSmelting();

            if (smeltingItem == null || smeltingItem.getType() == Material.AIR) {
                ItemStack movedItem = event.getItem();
                Furnace furnace = furnaceInventory.getHolder();

                RMBaseFurnaceRecipe recipe = getSpecificFurnaceRecipe(furnace, movedItem);

                if (recipe != null) {
                    if (recipe.hasFlag(FlagType.REMOVE)) {
                        event.setCancelled(true);
                    }

                    FurnaceData data = Furnaces.get(furnace.getLocation());

                    Args a = Args.create().player(data.getFuelerUUID()).location(furnace.getLocation()).recipe(recipe).result(recipe.getResult()).inventory(furnaceInventory).extra(furnaceInventory.getSmelting()).build();

                    if (furnaceHandleFlaggable(recipe, a, false, true) && isRecipeSameAsResult(a)) {
                        if (Version.has1_14Support()) {
                            ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) recipe.getCookTicks());
                        } else {
                            ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) (200 - recipe.getCookTicks()));
                        }
                    } else {
                        // TODO: Handle preventing the furnace with a different result in a different way
                        if (!Version.has1_14Support()) {
                            ToolsItem.updateFurnaceCookTimeDelayed(furnace, (short) 0);
                        }
                    }
                }
            }
        }
    }
}
