package haveric.recipeManager;

import haveric.recipeManager.api.events.RecipeManagerCraftEvent;
import haveric.recipeManager.api.events.RecipeManagerPrepareCraftEvent;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.WorkbenchRecipe;
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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * RecipeManager handled events
 */
public class Events implements Listener {
    public Events() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
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

            if (prepareSpecialRecipe(player, inv, result, bukkitRecipe)) {
                return; // stop here if it's a special recipe
            }

            WorkbenchRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe);

            if (recipe == null) {
                return; // not a custom recipe or recipe not found, no need to move on
            }

            Args a = Args.create().player(player).inventoryView(view).location(location).recipe(recipe).build();

            result = recipe.getDisplayResult(a); // get the result from recipe

            // Call the RecipeManagerPrepareCraftEvent
            RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(recipe, result, player, location);
            PluginManager pm = Bukkit.getPluginManager();
            if (pm != null) { // Null check used for Tests to skip event calling
                Bukkit.getPluginManager().callEvent(callEvent);

                if (callEvent.getResult() == null) {
                    result = null;
                } else {
                    result = new ItemResult(callEvent.getResult());
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
            }

            inv.setResult(result);
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

    private boolean prepareSpecialRecipe(Player player, CraftingInventory inv, ItemStack result, Recipe recipe) {
        ItemStack recipeResult = recipe.getResult();

        if (!result.equals(recipeResult)) { // result was processed by the game and it doesn't match the original recipe
            if (!Settings.getInstance().getSpecialLeatherDye()) {
                boolean leatherDye = false;

                if (Version.has1_11Support()) {
                    if (result.hasItemMeta() && result.getItemMeta() instanceof LeatherArmorMeta) {
                        leatherDye = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_LEATHERDYE)) {
                    leatherDye = true;
                }

                if (leatherDye) {
                    Messages.getInstance().sendOnce(player, "craft.special.leatherdye");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialMapCloning()) {
                boolean mapClone = false;

                if (Version.has1_11Support()) {
                    if (result.getType().equals(Material.MAP) && result.getAmount() > 1) {
                        mapClone = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_MAPCLONE)) {
                    mapClone = true;
                }

                if (mapClone) {
                    Messages.getInstance().sendOnce(player, "craft.special.map.cloning");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialMapExtending()) {
                boolean mapExtend = false;

                if (Version.has1_11Support()) {
                    if (recipeResult.equals(Vanilla.RECIPE_MAPEXTEND_1_11)) {
                        mapExtend = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_MAPEXTEND)) {
                    mapExtend = true;
                }

                if (mapExtend) {
                    Messages.getInstance().sendOnce(player, "craft.special.map.extending");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialFireworks()) {
                boolean fireworks = false;


                if (Version.has1_11Support()) {
                    Material fireworkRocketMaterial;
                    if (Version.has1_13Support()) {
                        fireworkRocketMaterial = Material.FIREWORK_ROCKET;
                    } else {
                        fireworkRocketMaterial = Material.getMaterial("FIREWORK");
                    }
                    if (result.getType() == fireworkRocketMaterial) {
                        fireworks = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_FIREWORKS)) {
                    fireworks = true;
                }

                if (fireworks) {
                    Messages.getInstance().sendOnce(player, "craft.special.fireworks");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialFireworkStar()) {
                if (Version.has1_11Support()) {
                    Material fireworkStarMaterial;
                    if (Version.has1_13Support()) {
                        fireworkStarMaterial = Material.FIREWORK_STAR;
                    } else {
                        fireworkStarMaterial = Material.getMaterial("FIREWORK_CHARGE");
                    }
                    if (result.getType() == fireworkStarMaterial) {
                        Messages.getInstance().sendOnce(player, "craft.special.fireworkstar");
                        inv.setResult(null);
                        return true;
                    }
                }
            }

            if (!Settings.getInstance().getSpecialBookCloning()) {
                boolean bookClone = false;

                if (Version.has1_11Support()) {
                    if (result.getType().equals(Material.WRITTEN_BOOK)) {
                        bookClone = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_BOOKCLONE)) {
                    bookClone = true;
                }

                if (bookClone) {
                    Messages.getInstance().sendOnce(player, "craft.special.book.cloning");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialBanner()) {
                boolean isBanner = false;

                if (Version.has1_13Support()) {
                    // TODO: Does 1.13 support special recipes?
                } else if (Version.has1_11Support()) {
                    if (result.getType().equals(Material.getMaterial("BANNER"))) {
                        isBanner = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_BANNER)) {
                    isBanner = true;
                }

                if (isBanner) {
                    Messages.getInstance().sendOnce(player, "craft.special.banner");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialShieldBanner()) {
                boolean isShieldBanner = false;

                if (Version.has1_11Support()) {
                    if (result.getType().equals(Material.SHIELD)) {
                        isShieldBanner = true;
                    }
                } else if (recipeResult.equals(Vanilla.RECIPE_SHIELD_BANNER)) {
                    isShieldBanner = true;
                }

                if (isShieldBanner) {
                    Messages.getInstance().sendOnce(player, "craft.special.shieldbanner");
                    inv.setResult(null);
                    return true;
                }
            }

            if (!Settings.getInstance().getSpecialTippedArrows() && recipeResult.equals(Vanilla.RECIPE_TIPPED_ARROW)) {
                Messages.getInstance().sendOnce(player, "craft.special.tippedarrows");
                inv.setResult(null);
                return true;
            }

            if (!Settings.getInstance().getSpecialShulkerDye()) {
                if (Version.has1_11Support() && ToolsItem.isShulkerBox(result.getType())) {
                    Messages.getInstance().sendOnce(player, "craft.special.shulkerdye");
                    inv.setResult(null);
                    return true;
                }
            }
        }

        return false;
    }

    private void prepareRepairRecipe(Player player, CraftingInventory inv, Location location) throws Throwable {
        if (!Settings.getInstance().getSpecialRepair()) {
            SoundNotifier.sendDenySound(player, location);
            Messages.getInstance().sendOnce(player, "craft.repair.disabled");
            inv.setResult(null);
            return;
        }

        ItemStack result = inv.getRecipe().getResult();

        if (Settings.getInstance().getSpecialRepairMetadata()) {
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
                    result.setItemMeta(meta);
                }
            }
        }

        RecipeManagerPrepareCraftEvent callEvent = new RecipeManagerPrepareCraftEvent(null, result, player, location);
        Bukkit.getPluginManager().callEvent(callEvent);

        result = callEvent.getResult();

        if (result != null) {
            SoundNotifier.sendRepairSound(player, location);
        }

        inv.setResult(result);
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
            WorkbenchRecipe recipe = RecipeManager.getRecipes().getWorkbenchRecipe(bukkitRecipe);
            if (recipe == null) {
                return;
            }

            Args a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).build();

            if (!recipe.checkFlags(a)) {
                SoundNotifier.sendDenySound(player, location);
                event.setCancelled(true);
                return;
            }

            result = Recipes.recipeGetResult(a, recipe); // gets the same stored result if event was previously cancelled
            if (result != null) {
                result.clearMetadata(); // Reset result's metadata to remove prepare's effects
            }

            int mouseButton;
            if (event.isRightClick()) {
                mouseButton = 1;
            } else {
                mouseButton = 0;
            }
            // Call the PRE event TODO upgrade to MouseButton when PR is pulled
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

            a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).result(result).build();

            int times = craftResult(event, inv, player, recipe, result, a); // craft the result
            if (result != null) {
                a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).result(result).build();

                if (times > 0) {
                    Recipes.recipeResetResult(a.playerUUID());
                }

                ItemStack[] originalMatrix = inv.getMatrix().clone();
                boolean firstRun = true;
                while (--times >= 0) {
                    // Make sure no items have changed or stop crafting
                    if (isDifferentMatrix(originalMatrix, inv.getMatrix())) {
                        //MessageSender.getInstance().info("Stop Crafting - Different matrix");
                        break;
                    }

                    // Make sure all flag conditions are still valid or stop crafting
                    if (!recipe.checkFlags(a) || !result.checkFlags(a)) {
                        //MessageSender.getInstance().info("Stop Crafting - Flags no longer match");
                        break;
                    }

                    // Reset result's metadata for each craft
                    result.clearMetadata();

                    a.setFirstRun(firstRun);
                    a.clear();

                    boolean recipeCraftSuccess = recipe.sendCrafted(a);
                    if (recipeCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                    }

                    a.clear();

                    boolean resultPrepareSuccess = result.sendPrepare(a);
                    if (resultPrepareSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(result)));
                    }

                    a.clear();

                    boolean resultCraftSuccess = result.sendCrafted(a);
                    if (resultCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(result)));
                    }

                    boolean doneCrafting = false;

                    boolean subtract = false;
                    boolean onlyExtra = true;
                    if (recipeCraftSuccess && resultPrepareSuccess && resultCraftSuccess) {
                        if (recipe.isMultiResult()) {
                            subtract = true;
                            onlyExtra = false;
                        }

                        if (recipe.hasFlag(FlagType.INGREDIENT_CONDITION) || result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                            subtract = true;
                        }

                        if (result.hasFlag(FlagType.NO_RESULT)) {
                            event.setCurrentItem(new ItemStack(Material.AIR));
                            event.setCursor(new ItemStack(Material.AIR));
                            subtract = true;
                            onlyExtra = false;
                        }

                        if (event.isShiftClick()) {
                            subtract = true;
                            onlyExtra = false;
                            event.setCancelled(true);

                            if (!result.hasFlag(FlagType.NO_RESULT)) {
                                // Make sure inventory can fit the results or stop crafting
                                if (Tools.playerCanAddItem(player, result)) {
                                    player.getInventory().addItem(result.clone());
                                } else {
                                    //MessageSender.getInstance().info("Stop Crafting - Full Inventory");
                                    doneCrafting = true;
                                }
                            }
                        }
                    }

                    if (subtract) {
                        recipe.subtractIngredients(inv, result, onlyExtra);
                    }

                    if (doneCrafting) {
                        break;
                    }

                    // TODO call post-event ?
                    // Bukkit.getPluginManager().callEvent(new RecipeManagerCraftEventPost(recipe, result, player, event.getCursor(), event.isShiftClick(), event.isRightClick() ? 1 : 0));

                    firstRun = false;
                }
            }

            inv.setResult(result);

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

    private boolean isDifferentMatrix(ItemStack[] original, ItemStack[] current) {
        boolean different = false;

        if (original.length == current.length) {
            for (int i = 0; i < original.length; i++) {
                ItemStack originalStack = original[i];
                ItemStack currentStack = current[i];

                if (originalStack != null) {
                    if (currentStack == null && originalStack.getType() == Material.AIR) {
                        // Null == AIR
                    } else if (currentStack == null || currentStack.getType() != originalStack.getType()) {
                        different = true;
                        break;
                    }
                }
            }
        }

        return different;
    }

    private int craftResult(CraftItemEvent event, CraftingInventory inv, Player player, WorkbenchRecipe recipe, ItemResult result, Args a) throws Throwable {
        if (recipe.isMultiResult()) {
            // more special treatment needed for multi-result ones...

            event.setCancelled(true); // need to cancel this from the start.

            // check if result is air / recipe failed
            if (result == null || result.getType() == Material.AIR) {
                Messages.getInstance().sendOnce(player, "craft.recipe.multi.failed");
                SoundNotifier.sendFailSound(player, a.location());
            } else {
                if (event.isShiftClick()) {
                    Messages.getInstance().sendOnce(player, "craft.recipe.multi.noshiftclick");

                    return 0;
                } else {
                    ItemStack cursor = event.getCursor();

                    if (!recipe.hasFlag(FlagType.INDIVIDUAL_RESULTS) && cursor != null && cursor.getType() != Material.AIR) {
                        Messages.getInstance().sendOnce(player, "craft.recipe.multi.chance.cursorhasitem");
                        return 0;
                    }

                    ItemStack merged = ToolsItem.merge(cursor, result);

                    if (merged == null) {
                        Messages.getInstance().sendOnce(player, "craft.recipe.multi.cursorfull");
                        return 0;
                    }

                    event.setCursor(merged);

                }
            }
        } else {
            if (result == null || result.getType() == Material.AIR) {
                event.setCurrentItem(null);
                return 0;
            }

            if (event.isShiftClick()) {
                int craftAmount = recipe.getCraftableTimes(inv, result); // Calculate how many times the recipe can be crafted
                ItemStack item = result.clone();
                item.setAmount(result.getAmount() * craftAmount);

                int space = Tools.playerFreeSpaceForItem(player, item);
                int crafted = Math.min((int) Math.ceil(space / (double) result.getAmount()), craftAmount);

                if (crafted > 0) {
                    event.setCurrentItem(result);
                    return crafted;
                }

                return 0;
            }

            ItemStack cursor = event.getCursor();
            ItemStack merged = ToolsItem.merge(cursor, result);

            if (merged == null) {
                return 0;
            }

            event.setCurrentItem(result);
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

                Material craftingTableMaterial;
                Material enchantingTableMaterial;
                if (Version.has1_13Support()) {
                    craftingTableMaterial = Material.CRAFTING_TABLE;
                    enchantingTableMaterial = Material.ENCHANTING_TABLE;
                } else {
                    craftingTableMaterial = Material.getMaterial("WORKBENCH");
                    enchantingTableMaterial = Material.getMaterial("ENCHANTMENT_TABLE");
                }

                Material blockType = block.getType();

                if (blockType == Material.ANVIL || blockType == craftingTableMaterial || blockType == enchantingTableMaterial) {
                    if (!RecipeManager.getPlugin().canCraft(event.getPlayer())) {
                        event.setCancelled(true);
                        return;
                    }

                    if (blockType == craftingTableMaterial) {
                        Workbenches.add(event.getPlayer(), event.getClickedBlock().getLocation());
                    }
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
        String name = player.getName();

        Players.remove(player);
        Workbenches.remove(player);
        Recipes.recipeResetResult(player.getUniqueId());
        Messages.getInstance().clearPlayer(name);
    }

    /*
     * Marked item monitor events
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (Settings.getInstance().getUpdateBooks()) {
            RecipeBooks.getInstance().updateBook(player, item);
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
                    MessageSender.getInstance().send(player, "[RecipeManager] New version: <green>" + latestVersion + "<reset>! You're using <yellow>" + currentVersion + "<reset>, grab it at: <light_purple>" + Updater.getLatestLink());
                } else if (compare == 2) {
                    MessageSender.getInstance().send(player, "[RecipeManager] New alpha/beta version: <green>" + latestVersion + " " + Updater.getLatestBetaStatus() + "<reset>! You're using <yellow>" + currentVersion + "<reset>, grab it at: <light_purple>" + Updater.getLatestLink());
                } else if (compare == 3) {
                    MessageSender.getInstance().send(player, "[RecipeManager] BukkitDev has a different alpha/beta version: <green>" + latestVersion + " " + Updater.getLatestBetaStatus() + "<reset>! You're using <yellow>" + currentVersion + " " + Updater.getCurrentBetaStatus() + "<reset>, grab it at: <light_purple>" + Updater.getLatestLink());
                }
            }
        }
    }
}
