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
import haveric.recipeManager.tools.*;
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

        int inventorySize = inventory.getSize();
        boolean isNewSmithingTable = inventorySize > 3;

        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < inventory.getSize() - 1; i++) {
            ingredients.add(inventory.getItem(i));
        }

        BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.SMITHING, ingredients, null);
        RMSmithingRecipe recipe = null;
        if (baseRecipe instanceof RMSmithing1_19_4TransformRecipe) {
            recipe = (RMSmithing1_19_4TransformRecipe) baseRecipe;
        } else if (!isNewSmithingTable && baseRecipe instanceof RMSmithingRecipe) {
            recipe = (RMSmithingRecipe) baseRecipe;
        }

        if (recipe != null) {
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

            if (!result.equals(recipeResult) && Version.has1_19_4Support()) {
                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimBolt()) {
                    if (Version.has1_20_5Support() && Vanilla.recipeMatchesTrimKey(recipe, "bolt")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.bolt");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimCoast()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "coast")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.coast");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimDune()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "dune")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.dune");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimEye()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "eye")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.eye");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimFlow()) {
                    if (Version.has1_20_5Support() && Vanilla.recipeMatchesTrimKey(recipe, "flow")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.flow");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimHost()) {
                    if (Version.has1_20Support() && Vanilla.recipeMatchesTrimKey(recipe, "host")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.host");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimRaiser()) {
                    if (Version.has1_20Support() && Vanilla.recipeMatchesTrimKey(recipe, "raiser")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.raiser");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimRib()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "rib")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.rib");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSentry()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "sentry")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.sentry");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimShaper()) {
                    if (Version.has1_20Support() && Vanilla.recipeMatchesTrimKey(recipe, "shaper")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.shaper");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSilence()) {
                    if (Version.has1_20Support() && Vanilla.recipeMatchesTrimKey(recipe, "silence")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.silence");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSnout()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "snout")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.snout");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimSpire()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "spire")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.spire");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimTide()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "tide")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.tide");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimVex()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "vex")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.vex");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimWard()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "ward")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.ward");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimWayfinder()) {
                    if (Version.has1_20Support() && Vanilla.recipeMatchesTrimKey(recipe, "wayfinder")) {
                        Messages.getInstance().sendOnce(player, "craft.special.armortrim.wayfinder");
                        inv.setResult(null);
                        return true;
                    }
                }

                if (!RecipeManager.getSettings().getSpecialSmithingArmorTrimWild()) {
                    if (Vanilla.recipeMatchesTrimKey(recipe, "wild")) {
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

        InventoryView view = event.getView();
        Player player = (Player) view.getPlayer();

        Location location = inventory.getLocation();
        if (location != null) {
            ItemResult result;
            if (inventory.getResult() == null) {
                result = null;
            } else {
                result = new ItemResult(inventory.getResult());
            }

            if (!event.isShiftClick() && result == null) {
                event.setCancelled(true);
                SoundNotifier.sendDenySound(player, location);
                return;
            }

            int inventorySize = inventory.getSize();
            boolean isNewSmithingTable = inventorySize > 3;

            ItemStack originalPrimary = inventory.getItem(0);
            ItemStack originalSecondary = inventory.getItem(1);
            ItemStack originalTertiary = inventory.getItem(2);

            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(originalPrimary);
            ingredients.add(originalSecondary);
            if (isNewSmithingTable) {
                ingredients.add(originalTertiary);
            }

            BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.SMITHING, ingredients, null);
            RMSmithingRecipe recipe = null;
            if (baseRecipe instanceof RMSmithing1_19_4TransformRecipe) {
                recipe = (RMSmithing1_19_4TransformRecipe) baseRecipe;
            } else if (!isNewSmithingTable && baseRecipe instanceof RMSmithingRecipe) {
                recipe = (RMSmithingRecipe) baseRecipe;
            }

            if (recipe != null) {
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

                        if (isNewSmithingTable) {
                            // Make sure no items have changed or stop crafting
                            ItemStack tertiary = inventory.getItem(2);
                            if (!ToolsItem.isSameItemHash(tertiary, originalTertiary)) {
                                break;
                            }
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

                        boolean recipeCraftSuccess = false;
                        boolean resultCraftSuccess = false;
                        if (!skipCraft) {
                            // Reset result's metadata for each craft
                            result.clearMetadata();

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
                    } else if (Supports.experimental1_20() && rawSlot == smithingTableInventory.getSize() - 1) {
                        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.CONTROL_DROP) {
                            craftFinishSmithing(event, player, smithingTableInventory);
                        } else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT || clickType == ClickType.NUMBER_KEY || clickType == ClickType.DROP) {
                            craftFinishSmithing(event, player, smithingTableInventory);
                        }
                    }
                }
            }
        }
    }

    private void craftFinishSmithing(InventoryClickEvent event, Player player, SmithingInventory inventory) {
        InventoryView view = event.getView();
        Location location = inventory.getLocation();

        if (location != null) {
            ItemResult result;
            if (inventory.getResult() == null) {
                result = null;
            } else {
                result = new ItemResult(inventory.getResult());
            }

            if (!event.isShiftClick() && result == null) {
                event.setCancelled(true);
                SoundNotifier.sendDenySound(player, location);
                return;
            }

            ItemStack originalPrimary = inventory.getItem(0);
            ItemStack originalSecondary = inventory.getItem(1);
            ItemStack originalTertiary = inventory.getItem(2);

            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(originalPrimary);
            ingredients.add(originalSecondary);
            ingredients.add(originalTertiary);

            BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.SMITHING, ingredients, null);
            RMSmithingRecipe recipe = null;
            if (baseRecipe instanceof RMSmithing1_19_4TransformRecipe) {
                recipe = (RMSmithing1_19_4TransformRecipe) baseRecipe;
            }

            if (recipe != null) {
                event.setCancelled(true);
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
                        ItemStack tertiary = inventory.getItem(2);

                        // Make sure no items have changed or stop crafting
                        if (!ToolsItem.isSameItemHash(primary, originalPrimary) || !ToolsItem.isSameItemHash(secondary, originalSecondary) || !ToolsItem.isSameItemHash(tertiary, originalTertiary)) {
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

                        boolean recipeCraftSuccess = false;
                        boolean resultCraftSuccess = false;
                        if (!skipCraft) {
                            // Reset result's metadata for each craft
                            result.clearMetadata();

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
}
