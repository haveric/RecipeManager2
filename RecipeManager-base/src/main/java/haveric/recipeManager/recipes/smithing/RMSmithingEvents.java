package haveric.recipeManager.recipes.smithing;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipeEvents;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsInventory;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RMSmithingEvents extends BaseRecipeEvents {
    public RMSmithingEvents() { }

    @EventHandler(priority = EventPriority.LOW)
    public void smithingPrepare(PrepareSmithingEvent event) {
        InventoryView view = event.getView();
        Player player = (Player) view.getPlayer();

        SmithingInventory inventory = event.getInventory();

        ItemResult result;
        if (inventory.getResult() == null) {
            result = null;
        } else {
            result = new ItemResult(inventory.getResult());
        }

        if (prepareSpecialSmithingRecipe(player, inventory, result)) {
            return; // stop here if it's a special smithing recipe
        }

        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < inventory.getSize() - 1; i++) {
            ingredients.add(inventory.getItem(i));
        }

        BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.SMITHING, ingredients, null);
        if (baseRecipe instanceof RMSmithingRecipe) {
            RMSmithingRecipe recipe = (RMSmithingRecipe) baseRecipe;

            Location location = inventory.getLocation();
            if (location != null) {
                Args a = Args.create().player(player).inventoryView(view).location(location).recipe(recipe).build();
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

                event.setResult(result);
            }
        }
    }

    public boolean prepareSpecialSmithingRecipe(Player player, SmithingInventory inv, ItemStack result) {
        if (result == null) {
            return false;
        }

        Recipe recipe = inv.getRecipe();
        if (recipe != null) {
            ItemStack recipeResult = recipe.getResult();

            if (!result.equals(recipeResult)) {
                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimCoast()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimCoast(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.coast");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimDune()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimDune(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.dune");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimEye()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimEye(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.eye");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimRib()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimRib(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.rib");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSentry()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimSentry(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.sentry");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSnout()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimSnout(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.snout");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSpire()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimSpire(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.spire");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimTide()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimTide(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.tide");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimVex()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimVex(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.vex");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimWard()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimWard(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.ward");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimWild()) {
                    if (Vanilla.recipeMatchesSmithingArmorTrimWild(recipe)) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.wild");
                        inv.setResult(null);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @EventHandler
    public void smithingCraft(SmithItemEvent event) {
        SmithingInventory inventory = event.getInventory();

        ItemResult result;
        if (inventory.getResult() == null) {
            result = null;
        } else {
            result = new ItemResult(inventory.getResult());
        }

        InventoryView view = event.getView();
        Player player = (Player) view.getPlayer();

        Location location = inventory.getLocation();
        if (location != null) {
            if (!event.isShiftClick() && result == null) {
                event.setCancelled(true);
                SoundNotifier.sendDenySound(player, location);
                return;
            }

            ItemStack originalPrimary = inventory.getItem(0);
            ItemStack originalSecondary = inventory.getItem(1);

            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(originalPrimary);
            ingredients.add(originalSecondary);

            BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.SMITHING, ingredients, null);
            if (baseRecipe instanceof RMSmithingRecipe) {
                RMSmithingRecipe recipe = (RMSmithingRecipe) baseRecipe;

                Args a = Args.create().player(player).inventoryView(view).location(location).recipe(recipe).build();

                if (!recipe.checkFlags(a)) {
                    SoundNotifier.sendDenySound(player, location);
                    event.setCancelled(true);
                    return;
                }

                if (result != null) {
                    result.clearMetadata(); // Reset result's metadata to remove prepare's effects
                }

                int times = 1;
                if (event.isShiftClick()) {
                    times = 64;
                }

                if (result != null) {
                    a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).result(result).build();

                    boolean firstRun = true;
                    for (int i = 0; i < times; i++) {
                        // Make sure block is still valid
                        Material blockType = location.getBlock().getType();
                        if (!recipe.isValidBlockMaterial(blockType)) {
                            break;
                        }
                        ItemStack primary = inventory.getItem(0);
                        ItemStack secondary = inventory.getItem(1);

                        // Make sure no items have changed or stop crafting
                        if (!ToolsItem.isSameItemHash(primary, originalPrimary) || !ToolsItem.isSameItemHash(secondary, originalSecondary)) {
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
                        if (Version.has1_13BasicSupport()) {
                            ItemMeta meta = result.getItemMeta();
                            if (meta instanceof Damageable) {
                                originalDamage = ((Damageable) meta).getDamage();
                            }
                        } else {
                            originalDamage = result.getDurability();
                        }

                        boolean recipeCraftSuccess = false;
                        boolean resultCraftSuccess = false;
                        if (!skipCraft) {
                            // Reset result's metadata for each craft
                            result.clearMetadata();

                            // We're handling durability on the result line outside of flags, so it needs to be reset after clearing the metadata
                            if (originalDamage != -1) {
                                if (Version.has1_13BasicSupport()) {
                                    ItemMeta meta = result.getItemMeta();

                                    if (meta instanceof Damageable) {
                                        ((Damageable) meta).setDamage(originalDamage);
                                        result.setItemMeta(meta);
                                    }
                                } else {
                                    result.setDurability((short) originalDamage);
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
                                SoundNotifier.sendDenySound(player, location);
                                recipe.sendFailed(a);
                                noResult = true;
                            } else {
                                if (recipe.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
                                    float chance = result.getChance();
                                    float rand = RecipeManager.random.nextFloat() * 100;

                                    if (chance >= 0 && chance < rand) {
                                        noResult = true;
                                    }
                                }

                                if (!noResult) {
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
                            }

                            if (noResult) {
                                event.setCancelled(true);
                            } else {
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
    }

    @EventHandler
    public void smithingTableInventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();
            if (inv instanceof SmithingInventory) {
                SmithingInventory smithingTableInventory = (SmithingInventory) inv;
                Location location = inv.getLocation();

                if (location != null) {
                    Player player = (Player) ent;

                    ClickType clickType = event.getClick();
                    int rawSlot = event.getRawSlot();
                    if (rawSlot >= 0 && rawSlot < smithingTableInventory.getSize() - 1) {
                        if (clickType == ClickType.NUMBER_KEY) {
                            event.setCancelled(true);
                            ToolsInventory.simulateHotbarSwap(smithingTableInventory, rawSlot, event.getView().getBottomInventory(), event.getHotbarButton());
                        } else if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT && clickType != ClickType.DOUBLE_CLICK) {
                            event.setCancelled(true);
                            ToolsInventory.simulateDefaultClick(player, smithingTableInventory, rawSlot, clickType);
                        }
                    }
                }
            }
        }
    }
}
