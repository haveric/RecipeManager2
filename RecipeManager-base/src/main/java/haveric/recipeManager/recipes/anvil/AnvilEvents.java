package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagDisplayName;
import haveric.recipeManager.flag.flags.any.FlagModLevel;
import haveric.recipeManager.flag.flags.any.FlagNeedLevel;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipeEvents;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.anvil.data.Anvil;
import haveric.recipeManager.recipes.anvil.data.Anvils;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnvilEvents extends BaseRecipeEvents {
    public AnvilEvents() { }

    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();

        ItemStack left = inventory.getItem(0);
        ItemStack right = inventory.getItem(1);

        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(left);
        ingredients.add(right);

        BaseRecipe baseRecipe = Recipes.getInstance().getRecipe(RMCRecipeType.ANVIL, ingredients, null);
        if (baseRecipe instanceof BaseAnvilRecipe) {
            BaseAnvilRecipe recipe = (BaseAnvilRecipe) baseRecipe;

            Location location = inventory.getLocation();

            if (location != null) {
                Block block = location.getBlock();
                InventoryView view = event.getView();

                Player player = (Player) view.getPlayer();

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

                event.setResult(result.getItemStack());


                String renameText = inventory.getRenameText();
                int repairCost = recipe.getRepairCost();
                if (recipe.isRenamingAllowed() && renameText != null && !renameText.isEmpty()) {
                    repairCost += 1;
                }

                int finalRepairCost = repairCost;

                updateRepairCost(player, inventory, finalRepairCost);
                Bukkit.getScheduler().runTaskLater(RecipeManager.getPlugin(), () -> updateRepairCost(player, inventory, finalRepairCost), 2);

                Anvils.remove(player);
                Anvils.add(player, recipe, ingredients, result, renameText);
            }
        } else {
            InventoryView view = event.getView();
            Player player = (Player) view.getPlayer();

            Anvils.remove(player);
            String renameText = inventory.getRenameText();
            if (left != null && renameText != null && !renameText.isEmpty()) {
                List<Material> renamingMaterials = RecipeManager.getSettings().getAnvilRenaming();

                if (RecipeManager.getSettings().getSpecialAnvilRenaming()) {
                    if (!renamingMaterials.isEmpty() && !renamingMaterials.contains(left.getType())) {
                        event.setResult(new ItemStack(Material.AIR));
                        player.updateInventory();
                    }
                } else if (renamingMaterials.isEmpty() || renamingMaterials.contains(left.getType())) {
                    event.setResult(new ItemStack(Material.AIR));
                    player.updateInventory();
                }
            }

            if (left != null && right != null) {
                if (left.getType().getMaxDurability() > 0) {
                    if (right.getItemMeta() instanceof EnchantmentStorageMeta) {
                        List<Material> enchantMaterials = RecipeManager.getSettings().getAnvilMaterialEnchant();
                        Map<Enchantment, List<Integer>> enchantEnchantments = RecipeManager.getSettings().getAnvilEnchantments();

                        if (RecipeManager.getSettings().getSpecialAnvilEnchant()) {
                            if (!enchantMaterials.isEmpty() && !enchantMaterials.contains(left.getType())) {
                                event.setResult(new ItemStack(Material.AIR));
                                player.updateInventory();
                            } else if (!enchantEnchantments.isEmpty()) {
                                Map<Enchantment, Integer> bookEnchantments = ((EnchantmentStorageMeta) right.getItemMeta()).getStoredEnchants();

                                boolean enchantAllowed = false;
                                for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                                    List<Integer> levels = enchantEnchantments.get(entry.getKey());

                                    if (levels != null && levels.contains(entry.getValue())) {
                                        enchantAllowed = true;
                                        break;
                                    }
                                }

                                if (!enchantAllowed) {
                                    event.setResult(new ItemStack(Material.AIR));
                                    player.updateInventory();
                                }
                            }
                        } else {
                            if (enchantMaterials.isEmpty() || enchantMaterials.contains(left.getType())) {
                                event.setResult(new ItemStack(Material.AIR));
                                player.updateInventory();
                            } else {
                                boolean enchantNotAllowed = true;

                                if (!enchantEnchantments.isEmpty()) {
                                    Map<Enchantment, Integer> bookEnchantments = ((EnchantmentStorageMeta) right.getItemMeta()).getStoredEnchants();

                                    for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                                        List<Integer> levels = enchantEnchantments.get(entry.getKey());

                                        if (levels != null && levels.contains(entry.getValue())) {
                                            enchantNotAllowed = false;
                                            break;
                                        }
                                    }
                                }

                                if (enchantNotAllowed) {
                                    event.setResult(new ItemStack(Material.AIR));
                                    player.updateInventory();
                                }
                            }
                        }
                    } else if (right.getType().getMaxDurability() > 0) {
                        List<Material> combineMaterials = RecipeManager.getSettings().getAnvilCombineItem();
                        if (RecipeManager.getSettings().getSpecialAnvilCombineItem()) {
                            if (!combineMaterials.isEmpty() && !combineMaterials.contains(left.getType())) {
                                event.setResult(new ItemStack(Material.AIR));
                                player.updateInventory();
                            }
                        } else if (combineMaterials.isEmpty() || combineMaterials.contains(left.getType())) {
                            event.setResult(new ItemStack(Material.AIR));
                            player.updateInventory();
                        }
                    } else {
                        List<Material> repairMaterial = RecipeManager.getSettings().getAnvilRepairMaterial();
                        if (RecipeManager.getSettings().getSpecialAnvilRepairMaterial()) {
                            if (!repairMaterial.isEmpty() && !repairMaterial.contains(right.getType())) {
                                event.setResult(new ItemStack(Material.AIR));
                                player.updateInventory();
                            }
                        } else if (repairMaterial.isEmpty() || repairMaterial.contains(right.getType())) {
                            event.setResult(new ItemStack(Material.AIR));
                            player.updateInventory();
                        }
                    }
                }
            }
        }
    }

    private void updateRepairCost(Player player, AnvilInventory inventory, int repairCost) {
        if (repairCost > 40) {
            inventory.setMaximumRepairCost(repairCost);
        }
        inventory.setRepairCost(repairCost);
        if (player != null) {
            player.updateInventory();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void anvilInventoryClose(InventoryCloseEvent event) {
        HumanEntity ent = event.getPlayer();
        if (ent instanceof Player) {
            Anvils.remove((Player) ent);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerTeleport(PlayerTeleportEvent event) {
        Anvils.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeath(PlayerDeathEvent event) {
        Anvils.remove(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        Anvils.remove(event.getPlayer());
    }

    @EventHandler
    public void anvilDrag(InventoryDragEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();

            if (inv instanceof AnvilInventory) {
                AnvilInventory anvilInventory = (AnvilInventory) inv;
                Location location = inv.getLocation();
                if (location != null) {
                    Player player = (Player) ent;
                    Anvil anvil = Anvils.get(player);
                    if (anvil != null) {
                        // Force refresh by updating the cost
                        int originalRepair = anvilInventory.getRepairCost();
                        if (originalRepair > 0) {
                            updateRepairCost(null, anvilInventory, originalRepair + 1);

                            Bukkit.getScheduler().runTaskLater(RecipeManager.getPlugin(), () -> updateRepairCost(player, anvilInventory, originalRepair), 0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void anvilInventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();
        if (ent instanceof Player) {
            Inventory inv = event.getInventory();
            if (inv instanceof AnvilInventory) {
                AnvilInventory anvilInventory = (AnvilInventory) inv;
                Location location = inv.getLocation();
                if (location != null) {
                    Player player = (Player) ent;
                    Anvil anvil = Anvils.get(player);
                    if (anvil != null) {
                        if (event.getRawSlot() == 2) {
                            if (!RecipeManager.getPlugin().canCraft(player)) {
                                event.setCancelled(true);
                                return;
                            }

                            ClickType clickType = event.getClick();
                            if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.CONTROL_DROP) {
                                event.setCancelled(true);
                                craftFinishAnvil(event, player, anvilInventory, true);
                            } else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT || clickType == ClickType.NUMBER_KEY || clickType == ClickType.DROP) {
                                event.setCancelled(true);
                                craftFinishAnvil(event, player, anvilInventory, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void craftFinishAnvil(InventoryClickEvent event, Player player, AnvilInventory inventory, boolean isShiftClick) {
        InventoryView view = event.getView();
        Location location = inventory.getLocation();

        Anvil anvil = Anvils.get(player);

        int times = 1;
        if (isShiftClick) {
            times = 64;
        }

        // Clone the recipe, so we can add custom flags to it
        AnvilRecipe1_13 recipe = new AnvilRecipe1_13(anvil.getRecipe());
        Args a = Args.create().player(player).inventoryView(view).recipe(recipe).location(location).build();

        String renameText = anvil.getRenameText();
        boolean toRename = recipe.isRenamingAllowed() && renameText != null && !renameText.isEmpty();

        // Convert repair cost into need/mod level flags
        int repairCost = recipe.getRepairCost();
        if (toRename) {
            repairCost += 1;
        }
        if (repairCost != 0 && player.getGameMode() != GameMode.CREATIVE) {
            FlagNeedLevel needLevel = new FlagNeedLevel();
            needLevel.setMinLevel(repairCost);
            needLevel.setMaxLevel(repairCost);
            recipe.addFlag(needLevel);

            FlagModLevel modLevel = new FlagModLevel();
            modLevel.setAmount(-repairCost);
            modLevel.setCraftMessage("false");
            recipe.addFlag(modLevel);
        }

        if (!recipe.checkFlags(a)) {
            SoundNotifier.sendDenySound(player, location);
            event.setCancelled(true);
            return;
        }


        if (toRename) {
            FlagDisplayName displayName = new FlagDisplayName();
            displayName.setResultName(renameText);

            for (ItemResult result : recipe.getResults()) {
                result.addFlag(displayName);
            }
        }

        ItemResult result = anvil.getResult();

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

                ItemStack left = inventory.getItem(0);
                ItemStack right = inventory.getItem(1);

                // Make sure no items have changed or stop crafting
                if (!ToolsItem.isSameItemHash(left, anvil.getIngredientSingleStack(0)) || !ToolsItem.isSameItemHash(right, anvil.getIngredientSingleStack(1))) {
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
                ItemMeta metaOne = result.getItemMeta(); // TODO: Rename metaOne and metaTwo
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
                        } else if (event.isShiftClick() || ToolsItem.merge(event.getCursor(), result.getItemStack()) == null) {
                            noResult = true;
                            // Make sure inventory can fit the results or drop on the ground
                            if (Tools.playerCanAddItem(player, result.getItemStack())) {
                                player.getInventory().addItem(result.getItemStack().clone());
                            } else {
                                player.getWorld().dropItem(player.getLocation(), result.getItemStack().clone());
                            }
                        }
                    }

                    if (!noResult) {
                        ItemStack merged = ToolsItem.merge(event.getCursor(), result.getItemStack());
                        player.setItemOnCursor(merged);
                    }
                }

                recipe.subtractIngredients(inventory, result, false);

                damageAnvil(location, recipe.getAnvilDamageChance());

                // TODO call post-event ?

                firstRun = false;
            }
        }
    }

    private void damageAnvil(Location location, double damageChance) {
        if (location != null) {
            boolean broken = false;
            while (damageChance > 0 && !broken) {
                double random = RecipeManager.random.nextFloat() * 100;
                if (random < damageChance) {
                    Block block = location.getBlock();

                    Material blockType = block.getType();
                    if (blockType == Material.ANVIL) {
                        block.setType(Material.CHIPPED_ANVIL);
                    } else if (blockType == Material.CHIPPED_ANVIL) {
                        block.setType(Material.DAMAGED_ANVIL);
                    } else if (blockType == Material.DAMAGED_ANVIL) {
                        block.setType(Material.AIR);
                        broken = true;
                    }
                }

                damageChance -= 100;
            }
        }
    }

    private void updateAnvilInventory(Player player, AnvilInventory anvilInventory) {
        Anvil anvil = Anvils.get(player);

        boolean leftMatch = ToolsItem.isSameItemHash(anvilInventory.getItem(0), anvil.getIngredientSingleStack(0));

        boolean rightMatch = false;
        if (leftMatch) {
            rightMatch = ToolsItem.isSameItemHash(anvilInventory.getItem(1), anvil.getIngredientSingleStack(1));

            if (rightMatch) {
                // Force a new prepare event by setting an item
                anvilInventory.setItem(0, anvil.getIngredient(0));
            }
        }

        if (!leftMatch || !rightMatch) {
            Anvils.remove(player);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void anvilPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material blockType = event.getClickedBlock().getType();

            if (blockType == Material.ANVIL || blockType == Material.CHIPPED_ANVIL || blockType == Material.DAMAGED_ANVIL) {
                if (!RecipeManager.getPlugin().canCraft(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
