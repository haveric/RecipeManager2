package haveric.recipeManager.recipes.cartography;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;
import haveric.recipeManager.UpdateInventory;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cartography.data.CartographyTable;
import haveric.recipeManager.recipes.cartography.data.CartographyTables;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CartographyEvents implements Listener {
    public CartographyEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getCartographyEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getCartographyEvents(), RecipeManager.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void cartographyInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block != null && block.getType() == Material.CARTOGRAPHY_TABLE) {
                Player player = event.getPlayer();
                CartographyTables.remove(player);
                CartographyTables.add(player, null, null, null, block.getLocation());
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void cartographyInventoryClose(InventoryCloseEvent event) {
        HumanEntity ent = event.getPlayer();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();

            if (inv instanceof CartographyInventory) {
                Location location = inv.getLocation();

                if (location != null) {
                    CartographyTables.remove((Player) ent);
                }
            }
        }
    }

    @EventHandler
    public void cartographyDrag(InventoryDragEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();

            if (inv instanceof CartographyInventory) {
                CartographyInventory cartographyInventory = (CartographyInventory) inv;
                Location location = inv.getLocation();
                if (location != null) {
                    Player player = (Player) ent;

                    prepareCartographyLater(cartographyInventory, player, event.getView());
                }
            }
        }
    }

    @EventHandler
    public void cartographyInventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();
            if (inv instanceof CartographyInventory) {
                Player player = (Player) ent;

                CartographyTable cartographyTable = CartographyTables.get(player);
                Location location = cartographyTable.getLocation();
                if (location != null) {
                    CartographyInventory cartographyInventory = (CartographyInventory) inv;

                    ClickType clickType = event.getClick();
                    int rawSlot = event.getRawSlot();
                    if (rawSlot == 2) {
                        if (!RecipeManager.getPlugin().canCraft(player)) {
                            event.setCancelled(true);
                            return;
                        }

                        if (cartographyTable.getRecipe() != null) {
                            if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                                craftFinishCartography(event, player, cartographyInventory, true);
                                prepareCartographyLater(cartographyInventory, player, event.getView());
                                new UpdateInventory(player, 2);
                            } else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
                                craftFinishCartography(event, player, cartographyInventory, false);
                                prepareCartographyLater(cartographyInventory, player, event.getView());
                                new UpdateInventory(player, 2);
                            }
                        }
                    } else if (rawSlot == 0 || rawSlot == 1) {
                        if (clickType == ClickType.NUMBER_KEY) {
                            event.setCancelled(true);
                            simulateHotbarSwap(cartographyInventory, rawSlot, event.getView().getBottomInventory(), event.getHotbarButton());
                        } else if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT && clickType != ClickType.DOUBLE_CLICK) {
                            event.setCancelled(true);
                            simulateDefaultClick(player, cartographyInventory, rawSlot, clickType);
                        }

                        prepareCartographyLater(cartographyInventory, player, event.getView());
                    } else if (rawSlot > 2) {
                        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                            ItemStack currentItem = event.getCurrentItem();

                            if (currentItem != null) {
                                Material clickedType = currentItem.getType();

                                switch (clickedType) {
                                    case FILLED_MAP:
                                    case MAP:
                                    case PAPER:
                                    case GLASS_PANE:
                                        prepareCartographyLater(cartographyInventory, player, event.getView());
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void simulateHotbarSwap(Inventory inventoryOne, int slotOne, Inventory inventoryTwo, int slotTwo) {
        ItemStack itemOne = inventoryOne.getItem(slotOne);
        ItemStack itemTwo = inventoryTwo.getItem(slotTwo);

        boolean itemOneIsAir = itemOne == null || itemOne.getType() == Material.AIR;
        boolean itemTwoIsAir = itemTwo == null || itemTwo.getType() == Material.AIR;

        if (itemOneIsAir && !itemTwoIsAir) {
            ItemStack newItemOne = itemTwo.clone();

            inventoryOne.setItem(slotOne, newItemOne);
            inventoryTwo.setItem(slotTwo, null);
        } else if (!itemOneIsAir && itemTwoIsAir) {
            ItemStack newItemTwo = itemOne.clone();

            inventoryOne.setItem(slotOne, null);
            inventoryTwo.setItem(slotTwo, newItemTwo);
        } else if (!itemOneIsAir) {
            ItemStack newItemOne = itemTwo.clone();
            ItemStack newItemTwo = itemOne.clone();

            inventoryOne.setItem(slotOne, newItemOne);
            inventoryTwo.setItem(slotTwo, newItemTwo);
        }

    }

    /**
     * Due to some inventories limiting allowed items, we need to simulate all inventory behavior manually and cannot rely on the InventoryAction
     * @param player
     * @param inventory
     * @param slot
     */
    private void simulateDefaultClick(Player player, Inventory inventory, int slot, ClickType clickType) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack clicked = inventory.getItem(slot);

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        boolean clickedIsAir = clicked == null || clicked.getType() == Material.AIR;

        boolean itemsAreSame = ToolsItem.isSameItemHash(cursor, clicked);

        if ((clickType == ClickType.LEFT || clickType == ClickType.RIGHT) && !cursorIsAir && !clickedIsAir && !itemsAreSame) {
            // SWAP_WITH_CURSOR
            ItemStack newClicked = cursor.clone();
            ItemStack newCursor = clicked.clone();

            inventory.setItem(slot, newClicked);
            player.setItemOnCursor(newCursor);
        } else if (clickType == ClickType.LEFT) {
            if (clickedIsAir && !cursorIsAir) {
                // PLACE_ALL
                inventory.setItem(slot, cursor.clone());
                player.setItemOnCursor(null);
            } else if (!clickedIsAir && cursorIsAir) {
                // PICKUP_ALL
                player.setItemOnCursor(clicked.clone());
                inventory.setItem(slot, null);
            } else if (!cursorIsAir && itemsAreSame) {
                int clickedAmount = clicked.getAmount();
                int cursorAmount = cursor.getAmount();

                int stackSize = cursor.getType().getMaxStackSize();

                if (clickedAmount < stackSize) {
                    int combinedAmount = clickedAmount + cursorAmount;

                    if (combinedAmount <= stackSize) {
                        // PLACE_ALL
                        ItemStack newClicked = clicked.clone();
                        newClicked.setAmount(combinedAmount);
                        inventory.setItem(slot, newClicked);
                        player.setItemOnCursor(null);
                    } else {
                        // PLACE_SOME
                        int remaining = combinedAmount - stackSize;
                        ItemStack newClicked = clicked.clone();
                        newClicked.setAmount(stackSize);
                        inventory.setItem(slot, newClicked);

                        ItemStack newCursor = cursor.clone();
                        newCursor.setAmount(remaining);
                        player.setItemOnCursor(newCursor);
                    }
                }
            }
        } else if (clickType == ClickType.RIGHT) {
            if (cursorIsAir && !clickedIsAir) {
                // PICKUP_HALF
                ItemStack newClicked = clicked.clone();
                ItemStack newCursor = clicked.clone();

                int clickedAmount = clicked.getAmount();
                int newCursorAmount = (int) Math.ceil(clickedAmount / 2.0);
                int newClickedAmount = clickedAmount - newCursorAmount;

                newClicked.setAmount(newClickedAmount);
                newCursor.setAmount(newCursorAmount);

                inventory.setItem(slot, newClicked);
                player.setItemOnCursor(newCursor);
            } else if (!cursorIsAir && (clickedIsAir || itemsAreSame)) {
                // PLACE_ONE
                ItemStack newCursor = cursor.clone();
                ItemStack newClicked = cursor.clone();

                int cursorAmount = cursor.getAmount();

                int clickedAmount = 0;
                if (!clickedIsAir) {
                    clickedAmount = clicked.getAmount();
                }

                int newCursorAmount = cursorAmount - 1;
                int newClickedAmount = clickedAmount + 1;

                newClicked.setAmount(newClickedAmount);
                newCursor.setAmount(newCursorAmount);

                inventory.setItem(slot, newClicked);
                player.setItemOnCursor(newCursor);
            }
        }
    }

    private void prepareCartographyLater(CartographyInventory inventory, Player player, InventoryView view) {
        new BukkitRunnable() {
            @Override
            public void run() {
                prepareCartography(inventory, player, view);
            }
        }.runTaskLater(RecipeManager.getPlugin(), 0);
    }

    private void prepareCartography(CartographyInventory inventory, Player player, InventoryView view) {
        ItemStack top = inventory.getItem(0);
        ItemStack bottom = inventory.getItem(1);

        CartographyRecipe recipe = Recipes.getInstance().getCartographyRecipe(top, bottom);
        if (recipe == null) {
            if (top != null && bottom != null) {
                if (top.getType() == Material.FILLED_MAP) {
                    Material bottomType = bottom.getType();
                    if (bottomType == Material.PAPER) {
                        if (!Settings.getInstance().getSpecialCartographyExtend()) {
                            inventory.setItem(2, null);
                            player.updateInventory();
                        }
                    } else if (bottomType == Material.MAP) {
                        if (!Settings.getInstance().getSpecialCartographyClone()) {
                            inventory.setItem(2, null);
                            player.updateInventory();
                        }
                    } else if (bottomType == Material.GLASS_PANE) {
                        if (!Settings.getInstance().getSpecialCartographyLock()) {
                            inventory.setItem(2, null);
                            player.updateInventory();
                        }
                    }
                }
            }
        } else {
            CartographyTable cartographyTable = CartographyTables.get(player);
            Location location = cartographyTable.getLocation();

            if (location != null) {
                Args a = Args.create().player(player).inventoryView(view).location(location).recipe(recipe).build();
                ItemResult result = recipe.getDisplayResult(a);
                if (result != null) {
                    a.setResult(result);

                    if (recipe.sendPrepare(a)) {
                        a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                    } else {
                        a.sendReasons(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                        result = null;
                    }

                    if (result != null) {
                        if (result.sendPrepare(a)) {
                            a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                        } else {
                            a.sendReasons(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                            result = null;
                        }
                    }
                }

                inventory.setItem(2, result);
                player.updateInventory();

                CartographyTables.remove(player);
                List<ItemStack> ingredients = new ArrayList<>();
                ingredients.add(top);
                ingredients.add(bottom);
                CartographyTables.add(player, recipe, ingredients, result, location);
            }
        }
    }

    private void craftFinishCartography(InventoryClickEvent event, Player player, CartographyInventory inventory, boolean isShiftClick) {
        InventoryView view = event.getView();
        CartographyTable cartographyTable = CartographyTables.get(player);
        Location location = cartographyTable.getLocation();

        int times = 1;
        if (isShiftClick) {
            times = 64;
        }

        // Clone the recipe so we can add custom flags to it
        CartographyRecipe recipe = new CartographyRecipe(cartographyTable.getRecipe());
        Args a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).build();

        if (!recipe.checkFlags(a)) {
            SoundNotifier.sendDenySound(player, location);
            return;
        }

        ItemResult result = cartographyTable.getResult();

        // We're handling durability on the result line outside of flags, so the original damage should be saved here
        int originalDamage = -1;
        if (result != null) {
            ItemMeta meta = result.getItemMeta();
            if (meta instanceof Damageable) {
                originalDamage = ((Damageable) meta).getDamage();
            }

            result.clearMetadata(); // Reset result's metadata to remove prepare's effects
        }

        if (result != null) {
            a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).result(result).build();

            boolean firstRun = true;
            for (int i = 0; i < times; i++) {
                // Make sure block is still valid
                if (location != null) {
                    Material blockType = location.getBlock().getType();
                    if (!recipe.isValidBlockMaterial(blockType)) {
                        break;
                    }
                }
                ItemStack top = inventory.getItem(0);
                ItemStack bottom = inventory.getItem(1);

                // Make sure no items have changed or stop crafting
                if (!ToolsItem.isSameItemHash(top, cartographyTable.getIngredientSingleStack(0)) || !ToolsItem.isSameItemHash(bottom, cartographyTable.getIngredientSingleStack(1))) {
                    break;
                }

                // Make sure all flag conditions are still valid or stop crafting
                if (!recipe.checkFlags(a)) {
                    break;
                }

                boolean skipCraft = false;
                List<ItemResult> potentialResults = recipe.getResults();
                if (recipe.isMultiResult()) {
                    boolean hasMatch = false;
                    if (recipe.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
                        for (ItemResult r : potentialResults) {
                            a.clear();

                            if (r.checkFlags(a)) {
                                result = r.clone();
                                hasMatch = true;
                                break;
                            }
                        }
                    } else {
                        float maxChance = 0;

                        List<ItemResult> matchingResults = new ArrayList<>();
                        for (ItemResult r : potentialResults) {
                            a.clear();

                            if (r.checkFlags(a)) {
                                matchingResults.add(r);
                                maxChance += r.getChance();
                            }
                        }

                        float rand = RecipeManager.random.nextFloat() * maxChance;
                        float chance = 0;

                        for (ItemResult r : matchingResults) {
                            chance += r.getChance();

                            if (chance >= rand) {
                                hasMatch = true;
                                result = r.clone();
                                break;
                            }
                        }
                    }

                    if (!hasMatch || result.getType() == Material.AIR) {
                        skipCraft = true;
                    }
                } else {
                    result = potentialResults.get(0).clone();

                    if (!result.checkFlags(a)) {
                        break;
                    }
                }
                a.setResult(result);

                boolean recipeCraftSuccess = false;
                boolean resultCraftSuccess = false;
                if (!skipCraft) {
                    // Reset result's metadata for each craft
                    result.clearMetadata();

                    // We're handling durability on the result line outside of flags, so it needs to be reset after clearing the metadata
                    if (originalDamage != -1) {
                        ItemMeta meta = result.getItemMeta();
                        if (meta instanceof Damageable) {
                            ((Damageable) meta).setDamage(originalDamage);
                            result.setItemMeta(meta);
                        }
                    }

                    a.setFirstRun(firstRun); // TODO: Remove and create onCraftComplete
                    a.clear();

                    recipeCraftSuccess = recipe.sendCrafted(a);
                    if (recipeCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                    }

                    a.clear();

                    resultCraftSuccess = result.sendCrafted(a);
                    if (resultCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(result)));
                    }
                }

                if ((recipeCraftSuccess && resultCraftSuccess) || skipCraft) {
                    boolean noResult = false;

                    if (skipCraft) {
                        noResult = true;
                    } else {
                        if (recipe.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
                            float chance = result.getChance();
                            float rand = RecipeManager.random.nextFloat() * 100;

                            if (chance >= 0 && chance < rand) {
                                noResult = true;
                            }
                        }

                        if (result.hasFlag(FlagType.NO_RESULT)) {
                            noResult = true;
                        } else if (event.isShiftClick() || ToolsItem.merge(event.getCursor(), result) == null) {
                            noResult = true;
                            // Make sure inventory can fit the results or drop on the ground
                            if (Tools.playerCanAddItem(player, result)) {
                                player.getInventory().addItem(result.clone());
                            } else {
                                player.getWorld().dropItem(player.getLocation(), result.clone());
                            }
                        }
                    }

                    if (!noResult) {
                        ItemStack merged = ToolsItem.merge(event.getCursor(), result);
                        player.setItemOnCursor(merged);
                    }
                }

                recipe.subtractIngredients(inventory, result, false);

                // TODO call post-event ?

                firstRun = false;
            }
        }
    }
}
