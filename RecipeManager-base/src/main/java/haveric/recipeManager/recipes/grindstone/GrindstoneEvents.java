package haveric.recipeManager.recipes.grindstone;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.grindstone.data.Grindstone;
import haveric.recipeManager.recipes.grindstone.data.Grindstones;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrindstoneEvents implements Listener {
    public GrindstoneEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getGrindstoneEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getGrindstoneEvents(), RecipeManager.getPlugin());
    }

    @EventHandler
    public void grindstoneDrag(InventoryDragEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();

            if (inv instanceof GrindstoneInventory) {
                GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inv;
                Location location = inv.getLocation();
                if (location != null) {
                    Player player = (Player) ent;

                    prepareGrindstoneLater(grindstoneInventory, player, event.getView());
                }
            }
        }
    }

    @EventHandler
    public void grindstoneInventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();
            if (inv instanceof GrindstoneInventory) {
                GrindstoneInventory grindstoneInventory = (GrindstoneInventory) inv;
                Location location = inv.getLocation();
                if (location != null) {
                    Player player = (Player) ent;

                    ClickType clickType = event.getClick();
                    int rawSlot = event.getRawSlot();
                    if (rawSlot == 2) {
                        if (!RecipeManager.getPlugin().canCraft(player)) {
                            event.setCancelled(true);
                            return;
                        }

                        Grindstone grindstone = Grindstones.get(player);
                        if (grindstone != null) {
                            if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                                craftFinishGrindstone(event, player, grindstoneInventory, true);
                            } else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
                                craftFinishGrindstone(event, player, grindstoneInventory, false);
                            }
                        }
                    } else if (rawSlot == 0 || rawSlot == 1) {
                        if (clickType == ClickType.NUMBER_KEY) {
                            event.setCancelled(true);
                            simulateHotbarSwap(grindstoneInventory, rawSlot, event.getView().getBottomInventory(), event.getHotbarButton());
                        } else if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT && clickType != ClickType.DOUBLE_CLICK) {
                            event.setCancelled(true);
                            simulateDefaultClick(player, grindstoneInventory, rawSlot, clickType);
                        }

                        prepareGrindstoneLater(grindstoneInventory, player, event.getView());
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

    private void prepareGrindstoneLater(GrindstoneInventory inventory, Player player, InventoryView view) {
        new BukkitRunnable() {
            @Override
            public void run() {
                prepareGrindstone(inventory, player, view);
            }
        }.runTaskLater(RecipeManager.getPlugin(), 0);
    }

    private void prepareGrindstone(GrindstoneInventory inventory, Player player, InventoryView view) {
        ItemStack top = inventory.getItem(0);
        ItemStack bottom = inventory.getItem(1);

        GrindstoneRecipe recipe = Recipes.getInstance().getGrindstoneRecipe(top, bottom);
        if (recipe == null) {
            if (top != null && bottom != null) {
                if (top.getType() == bottom.getType() && top.getType().getMaxDurability() > 0) {
                    List<Material> combineMaterials = Settings.getInstance().getGrindstoneCombineItem();
                    if (Settings.getInstance().getSpecialGrindstoneCombineItem()) {
                        if (!combineMaterials.isEmpty() && !combineMaterials.contains(top.getType())) {
                            inventory.setItem(2, null);
                            player.updateInventory();
                        }
                    } else if (combineMaterials.isEmpty() || combineMaterials.contains(top.getType())) {
                        inventory.setItem(2, null);
                        player.updateInventory();
                    }
                }
            } else {
                ItemStack toCheck = null;
                if (top != null) {
                    toCheck = top;
                } else if (bottom != null) {
                    toCheck = bottom;
                }

                if (toCheck != null) {
                    if (toCheck.getItemMeta() instanceof EnchantmentStorageMeta) {
                        Map<Enchantment, List<Integer>> disallowEnchantments = Settings.getInstance().getGrindstoneBookEnchantments();

                        if (Settings.getInstance().getSpecialGrindstoneDisenchantBook()) {
                            if (!disallowEnchantments.isEmpty()) {
                                Map<Enchantment, Integer> bookEnchantments = ((EnchantmentStorageMeta) toCheck.getItemMeta()).getStoredEnchants();

                                boolean enchantAllowed = false;
                                for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                                    List<Integer> levels = disallowEnchantments.get(entry.getKey());

                                    if (levels != null && levels.contains(entry.getValue())) {
                                        enchantAllowed = true;
                                        break;
                                    }
                                }

                                if (!enchantAllowed) {
                                    inventory.setItem(2, null);
                                    player.updateInventory();
                                }
                            }
                        } else {
                            boolean enchantNotAllowed = true;

                            if (!disallowEnchantments.isEmpty()) {
                                Map<Enchantment, Integer> bookEnchantments = ((EnchantmentStorageMeta) toCheck.getItemMeta()).getStoredEnchants();

                                for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                                    List<Integer> levels = disallowEnchantments.get(entry.getKey());

                                    if (levels != null && levels.contains(entry.getValue())) {
                                        enchantNotAllowed = false;
                                        break;
                                    }
                                }
                            }

                            if (enchantNotAllowed) {
                                inventory.setItem(2, null);
                                player.updateInventory();
                            }
                        }
                    } else if (toCheck.getType().getMaxDurability() > 0) {
                        List<Material> disenchantMaterials = Settings.getInstance().getGrindstoneItemMaterials();
                        Map<Enchantment, List<Integer>> disallowEnchantments = Settings.getInstance().getGrindstoneItemEnchantments();

                        if (Settings.getInstance().getSpecialGrindstoneDisenchantItem()) {
                            if (!disenchantMaterials.isEmpty() && !disenchantMaterials.contains(toCheck.getType())) {
                                inventory.setItem(2, null);
                                player.updateInventory();
                            } else if (!disallowEnchantments.isEmpty()) {
                                Map<Enchantment, Integer> itemEnchantments = toCheck.getEnchantments();

                                boolean enchantAllowed = false;
                                for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
                                    List<Integer> levels = disallowEnchantments.get(entry.getKey());

                                    if (levels != null && levels.contains(entry.getValue())) {
                                        enchantAllowed = true;
                                        break;
                                    }
                                }

                                if (!enchantAllowed) {
                                    inventory.setItem(2, null);
                                    player.updateInventory();
                                }
                            }
                        } else {
                            if (disenchantMaterials.isEmpty() || disenchantMaterials.contains(toCheck.getType())) {
                                inventory.setItem(2, null);
                                player.updateInventory();
                            } else {
                                boolean enchantNotAllowed = true;

                                if (!disallowEnchantments.isEmpty()) {
                                    Map<Enchantment, Integer> itemEnchantments = toCheck.getEnchantments();

                                    for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
                                        List<Integer> levels = disallowEnchantments.get(entry.getKey());

                                        if (levels != null && levels.contains(entry.getValue())) {
                                            enchantNotAllowed = false;
                                            break;
                                        }
                                    }
                                }

                                if (enchantNotAllowed) {
                                    inventory.setItem(2, null);
                                    player.updateInventory();
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Location location = inventory.getLocation();

            if (location != null) {
                Block block = location.getBlock();
                ItemResult result;

                boolean sameTop = false;
                boolean sameBottom = false;
                Grindstone grindstone = Grindstones.get(player);
                if (grindstone != null) {
                    ItemStack lastTop = grindstone.getTopIngredient();
                    sameTop = top == null && lastTop == null;
                    if (!sameTop && top != null && lastTop != null) {
                        sameTop = top.hashCode() == lastTop.hashCode();
                    }

                    if (sameTop) {
                        ItemStack lastBottom = grindstone.getBottomIngredient();
                        sameBottom = bottom == null && lastBottom == null;
                        if (!sameBottom && bottom != null && lastBottom != null) {
                            sameBottom = bottom.hashCode() == lastBottom.hashCode();
                        }
                    }
                }

                if (sameTop && sameBottom) {
                    result = grindstone.getResult();
                } else {
                    Args a = Args.create().player(player).inventoryView(view).location(block.getLocation()).recipe(recipe).build();
                    result = recipe.getDisplayResult(a);
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
                }

                inventory.setItem(2, result);
                player.updateInventory();

                Grindstones.remove(player);
                Grindstones.add(player, recipe, top, bottom, result);
            }
        }
    }

    private void craftFinishGrindstone(InventoryClickEvent event, Player player, GrindstoneInventory inventory, boolean isShiftClick) {
        InventoryView view = event.getView();
        Location location = inventory.getLocation();

        Grindstone grindstone = Grindstones.get(player);

        int times = 1;
        if (isShiftClick) {
            times = 64;
        }

        // Clone the recipe so we can add custom flags to it
        GrindstoneRecipe recipe = new GrindstoneRecipe(grindstone.getRecipe());
        Args a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).build();

        if (!recipe.checkFlags(a)) {
            SoundNotifier.sendDenySound(player, location);
            return;
        }

        ItemResult result = grindstone.getResult();

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
                // Make sure block is still a grindstone
                if (location != null) {
                    Material blockType = location.getBlock().getType();
                    if (blockType != Material.GRINDSTONE) {
                        break;
                    }
                }
                ItemStack top = inventory.getItem(0);
                ItemStack bottom = inventory.getItem(1);

                // Make sure no items have changed or stop crafting
                if (!ToolsItem.isSameItemHash(top, grindstone.getTopIngredient()) || !ToolsItem.isSameItemHash(bottom, grindstone.getBottomIngredient())) {
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
