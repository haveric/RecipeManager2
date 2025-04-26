package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.BaseRecipeEvents;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.brew.data.BrewingStandData;
import haveric.recipeManager.recipes.brew.data.BrewingStands;
import haveric.recipeManager.tools.InventoryCompatibilityUtil;
import haveric.recipeManager.tools.ToolsInventory;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BrewEvents extends BaseRecipeEvents {
    public BrewEvents() { }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void brewingStandPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BREWING_STAND) {
            BrewingStands.add(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void brewingStandBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BREWING_STAND) {
            BrewingStands.remove(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void brewingStandPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.BREWING_STAND) {
                if (!RecipeManager.getPlugin().canCraft(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void brewingStandDrag(InventoryDragEvent event) {
        HumanEntity entity = event.getWhoClicked();

        if (entity instanceof Player player) {
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();

            if (inv instanceof BrewerInventory brewerInventory && holder instanceof BrewingStand) {
                Inventory bottomInventory = InventoryCompatibilityUtil.getBottomInventory(event);

                Set<Integer> rawSlots = event.getRawSlots();
                ItemStack cursor = event.getOldCursor();
                Material cursorMaterial = cursor.getType();
                for (int i = 0; i <= 3; i++) {
                    if (rawSlots.contains(i)) {
                        boolean needsCustomStacking;
                        if (i == 3) {
                            needsCustomStacking = BrewInventoryUtil.isIngredient(cursorMaterial);
                        } else {
                            needsCustomStacking = BrewInventoryUtil.isPotionOrResult(cursorMaterial);
                        }

                        if (needsCustomStacking) {
                            event.setCancelled(true);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    ToolsInventory.simulateDrag(player, inv, bottomInventory, event.getNewItems(), event.getCursor());
                                }
                            }.runTaskLater(RecipeManager.getPlugin(), 0);

                            BrewingStandData data = BrewingStands.get(((BrewingStand) holder).getLocation());
                            data.setFuelerUUID(entity.getUniqueId());
                            prepareCustomBrewEventLater(brewerInventory);

                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void brewingStandInventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();

        if (ent instanceof Player player) {
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();

            if (inv instanceof BrewerInventory brewerInventory && holder instanceof BrewingStand) {
                int rawSlot = event.getRawSlot();
                if (rawSlot >= 0) {
                    ClickType clickType = event.getClick();
                    Material material = null;
                    if (rawSlot < brewerInventory.getSize()) {
                        BrewingStandData data = BrewingStands.get(((BrewingStand) holder).getLocation());
                        data.setFuelerUUID(ent.getUniqueId());

                        boolean needsCustomStacking;
                        int stackSize = 1;
                        if (rawSlot == 3) {
                            stackSize = 64;
                        }

                        if (clickType == ClickType.NUMBER_KEY) {
                            Inventory bottomInventory = InventoryCompatibilityUtil.getBottomInventory(event);
                            int hotbarButton = event.getHotbarButton();
                            ItemStack item = bottomInventory.getItem(hotbarButton);
                            if (item != null) {
                                material = item.getType();
                            }

                            if (rawSlot == 3) {
                                needsCustomStacking = BrewInventoryUtil.isIngredient(material);
                            } else {
                                needsCustomStacking = BrewInventoryUtil.isPotionOrResult(material);
                            }

                            if (needsCustomStacking) {
                                event.setCancelled(true);
                                ToolsInventory.simulateHotbarSwap(brewerInventory, rawSlot, bottomInventory, hotbarButton, stackSize);
                            }
                        } else if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT && clickType != ClickType.DOUBLE_CLICK) {
                            ItemStack item = event.getCursor();
                            if (item != null) {
                                material = item.getType();
                            }

                            if (rawSlot == 3) {
                                needsCustomStacking = BrewInventoryUtil.isIngredient(material);
                            } else {
                                needsCustomStacking = BrewInventoryUtil.isPotionOrResult(material);
                            }

                            if (needsCustomStacking) {
                                event.setCancelled(true);
                                ToolsInventory.simulateDefaultClick(player, brewerInventory, rawSlot, clickType, stackSize);
                            }
                        }
                    } else {
                        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                            BrewingStandData data = BrewingStands.get(((BrewingStand) holder).getLocation());
                            data.setFuelerUUID(ent.getUniqueId());

                            ItemStack currentItem = event.getCurrentItem();
                            if (currentItem != null) {
                                material = currentItem.getType();

                                Inventory bottomInventory = InventoryCompatibilityUtil.getBottomInventory(event);
                                if (BrewInventoryUtil.isIngredient(material)) {
                                    event.setCancelled(true);
                                    ToolsInventory.simulateShiftClick(bottomInventory,brewerInventory, event.getSlot(), 64, 3);
                                } else if (BrewInventoryUtil.isPotionOrResult(material)) {
                                    event.setCancelled(true);
                                    ToolsInventory.simulateShiftClick(bottomInventory, brewerInventory, event.getSlot(), 1, 0, 2);
                                }
                            }
                        }
                    }

                    prepareCustomBrewEventLater(brewerInventory);
                }
            }
        }
    }

    @EventHandler
    public void brewingStandFuelEvent(BrewingStandFuelEvent event) {
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (state instanceof BrewingStand brewingStand) {
            prepareCustomBrewEventLater(brewingStand.getInventory());
        }
    }

    @EventHandler
    public void hopperMoveEvent(InventoryMoveItemEvent event) {
        if (!BrewInventoryUtil.hasCustomItems()) {
            return;
        }

        Inventory destInventory = event.getDestination();
        if (destInventory.getType() == InventoryType.BREWING) {
            event.setCancelled(true);
            BrewerInventory brewInventory = (BrewerInventory) destInventory;
            Inventory sourceInventory = event.getSource();

            Location sourceLocation = sourceInventory.getLocation();
            Location destLocation = brewInventory.getLocation();
            if (sourceLocation != null && destLocation != null) {
                ItemStack item = event.getItem().clone();
                Material material = item.getType();
                if (sourceLocation.getBlockY() > destLocation.getBlockY()) { // Fill ingredient
                    if (BrewInventoryUtil.isIngredient(material)) {
                        event.setCancelled(true);

                        ItemStack ingredientSlot = brewInventory.getItem(3);
                        if (ingredientSlot == null || ingredientSlot.getType() == Material.AIR) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < sourceInventory.getSize(); i++) {
                                        ItemStack sourceItem = sourceInventory.getItem(i);

                                        if (sourceItem != null && sourceItem.getType() != Material.AIR && ToolsItem.isSameItemHash(item, sourceItem)) {
                                            ItemStack originalItem = sourceItem.clone();
                                            originalItem.setAmount(sourceItem.getAmount() - 1);
                                            sourceInventory.setItem(i, originalItem);

                                            ItemStack newItem = sourceItem.clone();
                                            newItem.setAmount(1);
                                            brewInventory.setItem(3, newItem);
                                            prepareCustomBrewEventLater(brewInventory);
                                            break;
                                        }
                                    }
                                }
                            }.runTaskLater(RecipeManager.getPlugin(), 0);
                        } else if (ingredientSlot.getAmount() < ingredientSlot.getType().getMaxStackSize() && ToolsItem.isSameItemHash(item, ingredientSlot)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < sourceInventory.getSize(); i++) {
                                        ItemStack sourceItem = sourceInventory.getItem(i);
                                        if (sourceItem != null && sourceItem.getType() != Material.AIR && ToolsItem.isSameItemHash(item, sourceItem)) {
                                            ItemStack originalItem = sourceItem.clone();
                                            originalItem.setAmount(sourceItem.getAmount() - 1);
                                            sourceInventory.setItem(i, originalItem);

                                            ItemStack newItem = sourceItem.clone();
                                            newItem.setAmount(ingredientSlot.getAmount() + 1);
                                            brewInventory.setItem(3, newItem);
                                            prepareCustomBrewEventLater(brewInventory);
                                            break;
                                        }
                                    }
                                }
                            }.runTaskLater(RecipeManager.getPlugin(), 0);
                        }
                    }
                } else { // Fill potion / fuel
                    if (BrewInventoryUtil.isPotionOrResult(material)) {
                        event.setCancelled(true);

                        for (int j = 0; j <= 2; j++) {
                            ItemStack potionSlot = brewInventory.getItem(j);
                            if (potionSlot == null || potionSlot.getType() == Material.AIR) {
                                int currentSlot = j;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < sourceInventory.getSize(); i++) {
                                            ItemStack sourceItem = sourceInventory.getItem(i);

                                            if (sourceItem != null && sourceItem.getType() != Material.AIR && ToolsItem.isSameItemHash(item, sourceItem)) {
                                                ItemStack originalItem = sourceItem.clone();
                                                originalItem.setAmount(sourceItem.getAmount() - 1);
                                                sourceInventory.setItem(i, originalItem);

                                                ItemStack newItem = sourceItem.clone();
                                                newItem.setAmount(1);
                                                brewInventory.setItem(currentSlot, newItem);
                                                prepareCustomBrewEventLater(brewInventory);
                                                break;
                                            }
                                        }
                                    }
                                }.runTaskLater(RecipeManager.getPlugin(), 0);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void prepareCustomBrewEventLater(BrewerInventory inventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                prepareCustomBrewEvent(inventory);
            }
        }.runTaskLater(RecipeManager.getPlugin(), 0);
    }

    private void prepareCustomBrewEvent(BrewerInventory inventory) {
        ItemStack ingredient = inventory.getIngredient();

        boolean startCustomBrewing = false;
        if (ingredient != null) {
            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(ingredient);

            for (int i = 0; i <= 2; i++) {
                ItemStack potion = inventory.getItem(i);
                if (potion != null) {
                    ingredients.add(potion);
                }
            }

            BaseRecipe baseRecipe = RecipeManager.getRecipes().getRecipe(RMCRecipeType.BREW, ingredients);
            if (baseRecipe instanceof BrewRecipe) {
                BrewingStand holder = inventory.getHolder();
                if (holder != null) {
                    BrewRecipe recipe = (BrewRecipe) baseRecipe;
                    Location location = holder.getLocation();
                    BrewingStandData data = BrewingStands.get(location);

                    Args a = Args.create().inventory(inventory).location(location).player(data.getFuelerUUID()).recipe(recipe).build();
                    List<ItemResult> results = recipe.getResults();

                    if (!results.isEmpty()) {
                        ItemResult result = results.get(0);
                        if (result != null) {
                            if (recipe.checkFlags(a) && result.checkFlags(a)) {
                                startCustomBrewing = true;
                                startCustomBrewing(inventory, recipe.getBrewingTimeInTicks());
                            }
                        }
                    }
                }
            }
        }

        if (!startCustomBrewing) {
            cancelCustomBrewing(inventory);
        }
    }

    private void startCustomBrewing(BrewerInventory inventory, int recipeBrewingTicks) {
        BrewingStand holder = inventory.getHolder();
        if (holder != null) {
            BrewingStandData data = BrewingStands.get(holder.getLocation());

            int fuelLevel = holder.getFuelLevel();
            if (!data.isBrewing() && fuelLevel > 0) {
                int startBrewingTime = Vanilla.BREWING_RECIPE_DEFAULT_TICKS; // 400
                float speed = (float) startBrewingTime / (float) recipeBrewingTicks; // 400 / 50 = 8
                int totalBrewingTime = Math.max(recipeBrewingTicks - 1, 0);

                holder.setFuelLevel(fuelLevel - 1);
                holder.setBrewingTime(startBrewingTime);
                holder.update();
                data.setCurrentBrewTime(startBrewingTime);

                BukkitTask updateTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        updateBrewingProgress(data, inventory, speed);
                    }
                }.runTaskTimer(RecipeManager.getPlugin(), 0, 1);
                data.setUpdateTask(updateTask);

                BukkitTask finishBrewingTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        updateTask.cancel();

                        finishBrewing(data, inventory);
                    }
                }.runTaskLater(RecipeManager.getPlugin(), totalBrewingTime);
                data.setFinishBrewingTask(finishBrewingTask);
            }
        }
    }

    private void cancelCustomBrewing(BrewerInventory inventory) {
        BrewingStand holder = inventory.getHolder();
        if (holder != null) {
            BrewingStandData data = BrewingStands.get(holder.getLocation());
            if (data.isBrewing()) {
                holder.setBrewingTime(Vanilla.BREWING_RECIPE_DEFAULT_TICKS);
                holder.update();
            }
            data.cancelBrewing();
        }
    }

    private void completeCustomBrewing(BrewerInventory inventory) {
        BrewingStand holder = inventory.getHolder();
        if (holder != null) {
            BrewingStandData data = BrewingStands.get(holder.getLocation());
            data.completeBrewing();
        }
    }

    private void updateBrewingProgress(BrewingStandData data, BrewerInventory inventory, float timeProgress) {
        BrewingStand holder = inventory.getHolder();
        if (holder != null) {
            float newProgress = (data.getCurrentBrewTime() - timeProgress);
            data.setCurrentBrewTime(newProgress);

            holder.setBrewingTime((int) newProgress);
            holder.update();
        }
    }

    private void finishBrewing(BrewingStandData data, BrewerInventory inventory) {
        BrewingStand holder = inventory.getHolder();
        if (holder != null) {
            Location location = holder.getLocation();

            boolean anyCrafted = false;
            boolean hasKeepItem = false;
            List<BrewRecipe> recipesWithCondition = new ArrayList<>();
            Map<BrewRecipe, List<Integer>> recipeSlots = new HashMap<>();
            for (int i = 0; i <= 2; i++) {
                List<ItemStack> ingredients = new ArrayList<>();
                ItemStack ingredient = inventory.getIngredient();

                if (ingredient != null) {
                    ingredients.add(ingredient);

                    ItemStack potion = inventory.getItem(i);
                    if (potion != null) {
                        ingredients.add(potion);
                        BaseRecipe baseRecipe = RecipeManager.getRecipes().getRecipe(RMCRecipeType.BREW, ingredients);
                        if (baseRecipe instanceof BrewRecipe recipe) {
                            if (!recipeSlots.containsKey(recipe)) {
                                recipeSlots.put(recipe, new ArrayList<>());
                            }
                            List<Integer> recipeSlot = recipeSlots.get(recipe);
                            recipeSlot.add(i);
                        }
                    }
                }
            }

            for (Map.Entry<BrewRecipe, List<Integer>> entry : recipeSlots.entrySet()) {
                BrewRecipe recipe = entry.getKey();
                Args a = Args.create().inventory(inventory).location(location).player(data.getFuelerUUID()).recipe(recipe).build();
                a.setFirstRun(true);

                List<ItemResult> results = recipe.getResults();
                if (!results.isEmpty()) {
                    ItemResult result = results.get(0);

                    if (result != null) {
                        result = result.clone();

                        a.setResult(result);
                        boolean recipeCheckFlags = recipe.checkFlags(a);
                        boolean resultCheckFlags = result.checkFlags(a);

                        if (recipeCheckFlags && resultCheckFlags) {
                            boolean recipeCraft = recipe.sendCrafted(a);
                            boolean resultCraft = result.sendCrafted(a);

                            if (recipeCraft && resultCraft) {
                                ItemStack bukkitResult = result.getItemStack();
                                if (a.hasExtra()) {
                                    @SuppressWarnings("unchecked")
                                    List<Boolean> potionBools = (List<Boolean>) a.extra();

                                    for (int i : entry.getValue()) {
                                        if (potionBools.size() == 1 || potionBools.get(i)) {
                                            if (!recipesWithCondition.contains(recipe)) {
                                                recipesWithCondition.add(recipe);
                                            }

                                            inventory.setItem(i, bukkitResult.clone());
                                            anyCrafted = true;
                                        }

                                        if (potionBools.size() == 1 || potionBools.size() > 3) {
                                            hasKeepItem = true;
                                        }
                                    }
                                } else {
                                    for (int i : entry.getValue()) {
                                        inventory.setItem(i, bukkitResult.clone());
                                        anyCrafted = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (anyCrafted) {
                int amountToSubtract = 1;
                for (BrewRecipe brewRecipe : recipesWithCondition) {
                    List<ItemResult> results = brewRecipe.getResults();
                    if (!results.isEmpty()) {
                        ItemResult result = results.get(0);
                        amountToSubtract = Math.max(amountToSubtract, brewRecipe.subtractIngredientCondition(inventory, result));
                    }
                }

                if (hasKeepItem) {
                    amountToSubtract -= 1;
                }

                ItemStack originalIngredient = inventory.getItem(3);
                originalIngredient.setAmount(originalIngredient.getAmount() - amountToSubtract);

                inventory.setItem(3, originalIngredient);

                completeCustomBrewing(inventory);
                prepareCustomBrewEventLater(inventory);
            }
        }
    }


    @EventHandler
    public void brewEvent(BrewEvent event) {
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (state instanceof BrewingStand brewingStand) {
            BrewingStandData data = BrewingStands.get(brewingStand.getLocation());
            if (data.isBrewing()) {
                event.setCancelled(true);
            }
        }
    }
}
