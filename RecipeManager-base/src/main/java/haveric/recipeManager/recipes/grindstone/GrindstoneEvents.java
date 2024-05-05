package haveric.recipeManager.recipes.grindstone;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.UpdateInventory;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.data.BaseRecipeData;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipeEvents;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.grindstone.data.Grindstones;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsInventory;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

public class GrindstoneEvents extends BaseRecipeEvents {
    public GrindstoneEvents() { }

    @EventHandler(priority= EventPriority.MONITOR)
    public void grindstoneInventoryClose(InventoryCloseEvent event) {
        HumanEntity ent = event.getPlayer();
        if (ent instanceof Player) {
            Grindstones.remove((Player) ent);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerTeleport(PlayerTeleportEvent event) {
        Grindstones.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeath(PlayerDeathEvent event) {
        Grindstones.remove(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        Grindstones.remove(event.getPlayer());
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

                        BaseRecipeData grindstone = Grindstones.get(player);
                        if (grindstone != null) {
                            if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.CONTROL_DROP) {
                                event.setCancelled(true);
                                craftFinishGrindstone(event, player, grindstoneInventory, true);
                                prepareGrindstoneLater(grindstoneInventory, player, event.getView());
                                new UpdateInventory(player, 2);
                            } else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT || clickType == ClickType.NUMBER_KEY || clickType == ClickType.DROP) {
                                event.setCancelled(true);
                                craftFinishGrindstone(event, player, grindstoneInventory, false);
                                prepareGrindstoneLater(grindstoneInventory, player, event.getView());
                                new UpdateInventory(player, 2);
                            }
                        }
                    } else if (rawSlot == 0 || rawSlot == 1) {
                        if (clickType == ClickType.NUMBER_KEY) {
                            event.setCancelled(true);
                            ToolsInventory.simulateHotbarSwap(grindstoneInventory, rawSlot, event.getView().getBottomInventory(), event.getHotbarButton());
                        } else if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT && clickType != ClickType.DOUBLE_CLICK) {
                            event.setCancelled(true);
                            ToolsInventory.simulateDefaultClick(player, grindstoneInventory, rawSlot, clickType);
                        }

                        prepareGrindstoneLater(grindstoneInventory, player, event.getView());
                    } else if (rawSlot > 2) {
                        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                            ItemStack currentItem = event.getCurrentItem();

                            if (currentItem != null) {
                                if (currentItem.getItemMeta() instanceof EnchantmentStorageMeta || currentItem.getType().getMaxDurability() > 0) {
                                    prepareGrindstoneLater(grindstoneInventory, player, event.getView());
                                }
                            }
                        }
                    }
                }
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

        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(top);
        ingredients.add(bottom);

        BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.GRINDSTONE, ingredients, null);
        if (baseRecipe instanceof GrindstoneRecipe) {
            GrindstoneRecipe recipe = (GrindstoneRecipe) baseRecipe;

            Location location = inventory.getLocation();

            if (location != null) {
                Block block = location.getBlock();

                Args a = Args.create().player(player).inventoryView(view).location(block.getLocation()).recipe(recipe).build();
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

                Grindstones.remove(player);
                Grindstones.add(player, recipe, ingredients, result);
            }
        } else {
            if (top != null && bottom != null) {
                if (top.getType() == bottom.getType() && top.getType().getMaxDurability() > 0) {
                    List<Material> combineMaterials = RecipeManager.getSettings().getGrindstoneCombineItem();
                    if (RecipeManager.getSettings().getSpecialGrindstoneCombineItem()) {
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
                        Map<Enchantment, List<Integer>> disallowEnchantments = RecipeManager.getSettings().getGrindstoneBookEnchantments();

                        if (RecipeManager.getSettings().getSpecialGrindstoneDisenchantBook()) {
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
                        List<Material> disenchantMaterials = RecipeManager.getSettings().getGrindstoneItemMaterials();
                        Map<Enchantment, List<Integer>> disallowEnchantments = RecipeManager.getSettings().getGrindstoneItemEnchantments();

                        if (RecipeManager.getSettings().getSpecialGrindstoneDisenchantItem()) {
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
        }
    }

    private void craftFinishGrindstone(InventoryClickEvent event, Player player, GrindstoneInventory inventory, boolean isShiftClick) {
        InventoryView view = event.getView();
        Location location = inventory.getLocation();

        BaseRecipeData grindstone = Grindstones.get(player);

        int times = 1;
        if (isShiftClick) {
            times = 64;
        }
        // Clone the recipe, so we can add custom flags to it
        GrindstoneRecipe recipe = new GrindstoneRecipe(grindstone.getRecipe());
        Args a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).build();

        if (!recipe.checkFlags(a)) {
            SoundNotifier.sendDenySound(player, location);
            event.setCancelled(true);
            return;
        }

        ItemResult result = grindstone.getResult();

        if (result != null) {
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
                if (!ToolsItem.isSameItemHash(top, grindstone.getIngredientSingleStack(0)) || !ToolsItem.isSameItemHash(bottom, grindstone.getIngredientSingleStack(1))) {
                    break;
                }

                // Make sure all flag conditions are still valid or stop crafting
                if (!recipe.checkFlags(a)) {
                    break;
                }

                boolean skipCraft = false;
                boolean cancelCraft = false;
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
                            } else {
                                cancelCraft = true;
                                break;
                            }
                        }

                        if (!cancelCraft) {
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
                    }

                    if (!hasMatch || result.getType() == Material.AIR) {
                        skipCraft = true;
                    }
                } else {
                    result = potentialResults.get(0).clone();

                    if (!result.checkFlags(a)) {
                        SoundNotifier.sendDenySound(player, location);
                        event.setCancelled(true);
                        break;
                    }
                }
                a.setResult(result);

                int originalDamage = -1;
                ItemMeta metaOne = result.getItemMeta(); // TODO: Change metaOne and metaTwo
                if (metaOne instanceof Damageable) {
                    originalDamage = ((Damageable) metaOne).getDamage();
                }

                boolean recipeCraftSuccess = false;
                boolean resultCraftSuccess = false;
                if (!skipCraft) {
                    // Reset result's metadata for each craft
                    result.clearMetadata();

                    // We're handling durability on the result line outside of flags, so it needs to be reset after clearing the metadata
                    if (originalDamage != -1) {
                        ItemMeta metaTwo = result.getItemMeta();

                        if (metaTwo instanceof Damageable) {
                            ((Damageable) metaTwo).setDamage(originalDamage);
                            result.setItemMeta(metaTwo);
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
