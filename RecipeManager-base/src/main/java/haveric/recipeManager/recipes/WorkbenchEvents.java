package haveric.recipeManager.recipes;

import haveric.recipeManager.*;
import haveric.recipeManager.api.events.RecipeManagerCraftEvent;
import haveric.recipeManager.api.events.RecipeManagerPrepareCraftEvent;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchEvents extends BaseRecipeEvents {
    public WorkbenchEvents() { }

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

            InventoryView view = event.getView();
            Player player = (Player) view.getPlayer();

            if (RecipeManager.getPlugin() != null) { // Needed for tests
                if (!RecipeManager.getPlugin().canCraft(player)) {
                    inv.setResult(null);
                    return; // player not allowed to craft, stop here
                }
            }

            Location location;
            // get workbench location or null
            if (inv.getSize() > 9) {
                location = Workbenches.get(player);
            } else {
                location = null;
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

            if (prepareSpecialRecipe(player, inv, result.getItemStack(), bukkitRecipe, location)) {
                return; // stop here if it's a special recipe
            }

            PreparableResultRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe, inv.getContents());
            if (recipe == null) {
                return; // not a custom recipe or recipe not found, no need to move on
            }

            Args a = Args.create().player(player).inventoryView(view).location(location).recipe(recipe).build();

            result = recipe.getDisplayResult(a);

            // Call the RecipeManagerPrepareCraftEvent
            RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(recipe, result, player, location);
            PluginManager pm = Bukkit.getPluginManager();
            if (pm != null) { // Null check used for Tests to skip event calling
                Bukkit.getPluginManager().callEvent(callEvent);

                if (callEvent.getResult() == null) {
                    result = null;
                } else {
                    result = new ItemResult(callEvent.getResult(), false);
                }
            }

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

            inv.setResult(result.getItemStack());
        } catch (Throwable e) {
            if (event.getInventory() != null) {
                event.getInventory().setResult(null);
            }

            CommandSender sender;
            if (event.getView() != null && event.getView().getPlayer() instanceof Player) {
                sender = event.getView().getPlayer();
            } else {
                sender = null;
            }

            MessageSender.getInstance().error(sender, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private boolean prepareSpecialRecipe(Player player, CraftingInventory inv, ItemStack result, Recipe recipe, Location location) {
        ItemStack recipeResult = recipe.getResult();

        if (!result.equals(recipeResult)) { // result was processed by the game, and it doesn't match the original recipe
            if (Vanilla.recipeMatchesRepair(recipe)) {
                if (prepareRepairRecipe(player, inv, location)) {
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialLeatherDye()) {
                if (Vanilla.recipeMatchesArmorDye(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.leatherdye");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialMapCloning()) {
                if (Vanilla.recipeMatchesMapCloning(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.map.cloning");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialMapExtending()) {
                if (Vanilla.recipeMatchesMapExtending(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.map.extending");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialFireworks()) {
                if (Vanilla.recipeMatchesFireworkRocket(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.fireworks");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialFireworkStar()) {
                if (Vanilla.recipeMatchesFireworkStar(recipe, result, inv.getMatrix())) {
                    Messages.getInstance().sendOnce(player, "craft.special.fireworkstar");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialFireworkStarFade()) {
                if (Vanilla.recipeMatchesFireworkStarFade(recipe, result, inv.getMatrix())) {
                    Messages.getInstance().sendOnce(player, "craft.special.fireworkstarfade");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialBookCloning()) {
                if (Vanilla.recipeMatchesBookCloning(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.book.cloning");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialBanner()) {
                if (Vanilla.recipeMatchesBannerAddPattern(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.banner");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialBannerDuplicate()) {
                if (Vanilla.recipeMatchesBannerDuplicate(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.bannerduplicate");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialShieldBanner()) {
                if (Vanilla.recipeMatchesShieldDecoration(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.shieldbanner");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialTippedArrows()) {
                if (Vanilla.recipeMatchesTippedArrow(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.tippedarrows");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialShulkerDye()) {
                if (Vanilla.recipeMatchesShulkerDye(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.shulkerdye");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialSuspiciousStew()) {
                if (Vanilla.recipeMatchesSuspiciousStew(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.suspiciousstew");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!RecipeManager.getSettings().getSpecialDecoratedPot()) {
                if (Vanilla.recipeMatchesDecoratedPot(recipe)) {
                    Messages.getInstance().sendOnce(player, "craft.special.decoratedpot");
                    inv.setResult(null);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean prepareRepairRecipe(Player player, CraftingInventory inv, Location location) {
        if (!RecipeManager.getSettings().getSpecialRepair()) {
            SoundNotifier.sendDenySound(player, location);
            Messages.getInstance().sendOnce(player, "craft.repair.disabled");
            inv.setResult(null);
            return true;
        }

        ItemStack result;
        // 1.15 and 1.16 do not handle repair recipes well, so use the inventory instead of the recipe
        if (!Version.has1_17Support() && Version.has1_15Support()) {
            result = inv.getResult();
        } else {
            result = inv.getRecipe().getResult();
        }

        if (RecipeManager.getSettings().getSpecialRepairMetadata()) {
            ItemStack[] matrix = inv.getMatrix();
            ItemStack[] repaired = new ItemStack[2];

            int repairIndex = 0;

            for (ItemStack item : matrix) {
                if (item != null && item.getType() != Material.AIR) {
                    repaired[repairIndex] = item;

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
                    short actualDurability = result.getDurability();
                    result.setItemMeta(meta);
                    result.setDurability(actualDurability);
                }
            }
        }

        RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(null, new ItemResult(result), player, location);
        Bukkit.getPluginManager().callEvent(callEvent);

        result = callEvent.getResult().getItemStack();

        if (result != null && result.getType() != Material.AIR) {
            SoundNotifier.sendRepairSound(player, location);
            inv.setResult(result);
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void craftFinish(CraftItemEvent event) {
        try {
            final CraftingInventory inv = event.getInventory();

            ItemResult result;
            if (inv.getResult() == null) {
                result = null;
            } else {
                result = new ItemResult(inv.getResult());
            }

            InventoryView view = event.getView();
            final Player player = (Player) view.getPlayer();

            Location location = Workbenches.get(player);
            if (!event.isShiftClick() && result == null) {
                event.setCancelled(true);
                SoundNotifier.sendDenySound(player, location);
                return;
            }

            Recipe bukkitRecipe = event.getRecipe();
            PreparableResultRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe, inv.getContents());
            if (recipe == null) {
                return;
            }

            Args a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).build();

            if (!recipe.checkFlags(a)) {
                SoundNotifier.sendDenySound(player, location);
                event.setCancelled(true);
                return;
            }

            if (result != null) {
                result.clearMetadata(); // Reset result's metadata to remove prepare's effects
            }

            int mouseButton;
            if (event.isRightClick()) {
                mouseButton = 1;
            } else {
                mouseButton = 0;
            }
            // Call the PRE event
            RecipeManagerCraftEvent callEvent = new RecipeManagerCraftEvent(recipe, result, player, event.getCursor(), event.isShiftClick(), mouseButton);

            PluginManager pm = Bukkit.getPluginManager();
            if (pm != null) { // Null check used for Tests to skip event calling
                pm.callEvent(callEvent);
            }

            if (callEvent.isCancelled()) { // if event was cancelled by some other plugin then cancel this event
                event.setCancelled(true);
                return;
            }

            result = callEvent.getResult(); // get the result from the event if it was changed

            int times = craftResult(event, inv, result); // craft the result
            if (result != null) {
                a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).result(result).build();

                ItemStack[] originalMatrix = inv.getMatrix().clone();
                boolean firstRun = true;
                while (--times >= 0) {
                    // Make sure no items have changed or stop crafting
                    if (Tools.isDifferentMatrix(originalMatrix, inv.getMatrix())) {
                        //MessageSender.getInstance().info("Stop Crafting - Different matrix");
                        break;
                    }

                    // Make sure all flag conditions are still valid or stop crafting
                    if (!recipe.checkFlags(a)) {
                        //MessageSender.getInstance().info("Stop Crafting - Flags no longer match");
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

                        if (!hasMatch || result.isAir()) {
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

                    boolean subtract = false;
                    boolean onlyExtra = true;
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

                            if (recipe.hasFlag(FlagType.INGREDIENT_CONDITION) || result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                                subtract = true;
                            }

                            if (!noResult) {
                                if (result.hasFlag(FlagType.NO_RESULT)) {
                                    noResult = true;
                                } else if (event.isShiftClick() || ToolsItem.merge(event.getCursor(), result.getItemStack()) == null) {
                                    noResult = true;
                                    // Make sure inventory can fit the results or drop on the ground
                                    if (Tools.playerCanAddItem(player, result.getItemStack())) {
                                        player.getInventory().addItem(result.getItemStack().clone());
                                    } else {
                                        player.getWorld().dropItem(player.getLocation(), result.getItemStack().clone());
                                    }

                                    for (ItemStack item : originalMatrix) {
                                        if (item != null) {
                                            Material itemType = item.getType();

                                            if (itemType != Material.AIR) {
                                                Material returnedMaterial;
                                                if (Version.has1_15Support()) {
                                                    try {
                                                        returnedMaterial = itemType.getCraftingRemainingItem();
                                                    } catch (NoSuchMethodError e) {
                                                        returnedMaterial = ToolsItem.getCraftingRemainingItem(itemType);
                                                    }
                                                } else {
                                                    returnedMaterial = ToolsItem.getCraftingRemainingItem(itemType);
                                                }

                                                if (returnedMaterial != null) {
                                                    ItemStack returnedItem = new ItemStack(returnedMaterial);

                                                    if (Tools.playerCanAddItem(player, returnedItem)) {
                                                        player.getInventory().addItem(returnedItem);
                                                    } else {
                                                        player.getWorld().dropItem(player.getLocation(), returnedItem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (noResult) {
                            if (!cancelCraft) {
                                subtract = true;
                                onlyExtra = false;
                            }
                            event.setCancelled(true);
                        } else {
                            event.setCurrentItem(result.getItemStack());
                        }
                    }

                    if (subtract) {
                        recipe.subtractIngredients(inv, result, onlyExtra);
                    }

                    // TODO call post-event ?
                    // Bukkit.getPluginManager().callEvent(new RecipeManagerCraftEventPost(recipe, result, player, event.getCursor(), event.isShiftClick(), event.isRightClick() ? 1 : 0));

                    firstRun = false;
                }
            }

            if (pm != null) { // Null check used for Tests to skip event calling
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(new PrepareItemCraftEvent(inv, player.getOpenInventory(), false));
                    }
                }.runTaskLater(RecipeManager.getPlugin(), 0);

                new UpdateInventory(player, 2); // update inventory 2 ticks later
            }
        } catch (Throwable e) {
            event.setCancelled(true);
            CommandSender sender;
            if (event.getView() != null && event.getView().getPlayer() instanceof Player) {
                sender = event.getView().getPlayer();
            } else {
                sender = null;
            }

            MessageSender.getInstance().error(sender, e, event.getEventName() + " cancelled due to error:");
        }
    }

    private int craftResult(CraftItemEvent event, CraftingInventory inv, ItemResult result) {
        if (result == null || result.isAir()) {
            event.setCurrentItem(null);
            return 0;
        }

        if (event.isShiftClick()) {
            return inv.getMaxStackSize();
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
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Block block = event.getClickedBlock();

                Material craftingTableMaterial;
                if (Version.has1_13Support()) {
                    craftingTableMaterial = Material.CRAFTING_TABLE;
                } else {
                    craftingTableMaterial = Material.getMaterial("WORKBENCH");
                }

                if (block.getType() == craftingTableMaterial) {
                    if (!RecipeManager.getPlugin().canCraft(event.getPlayer())) {
                        event.setCancelled(true);
                        return;
                    }

                    Workbenches.add(event.getPlayer(), event.getClickedBlock().getLocation());
                }

                break;

            case PHYSICAL:
                break;

            default:
                Workbenches.remove(event.getPlayer());
                break;
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
        String name = player.getName();

        Players.remove(player);
        Workbenches.remove(player);
        Messages.getInstance().clearPlayer(name);
    }
}
