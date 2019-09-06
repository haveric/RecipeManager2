package haveric.recipeManager.recipes.compost;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.SoundNotifier;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.compost.data.ComposterData;
import haveric.recipeManager.recipes.compost.data.Composters;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompostEvents implements Listener {
    public CompostEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getCompostEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getCompostEvents(), RecipeManager.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void placeCompost(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block != null && block.getType() == Material.COMPOSTER) {
                BlockData blockData = block.getBlockData();
                Levelled levelled = (Levelled) blockData;
                int curLevel = levelled.getLevel();
                int maxLevel = levelled.getMaximumLevel();

                ComposterData data = Composters.get(block.getLocation());
                if (data.getPlayerUUID() == null) {
                    data.setPlayerUUID(event.getPlayer().getUniqueId());
                }

                if (curLevel < maxLevel - 1) { // Add to compost pile
                    ItemStack item = event.getItem();
                    if (item != null) {
                        ItemStack clone = item.clone();
                        clone.setAmount(1);
                        addCompost(event, data, block, clone, event.getPlayer());
                    }
                } else if (curLevel == maxLevel) { // Give Item
                    CompostRecipe recipe = data.getRecipe();

                    if (recipe != null) {
                        event.setCancelled(true);

                        ItemResult result = takeCompostResult(data, recipe, block);
                        if (result != null) {
                            dropCompostResult(block, result);
                        }
                    }
                }
            }
        }
    }

    private void addToCompost(ComposterData composterData, Block block, double levels, boolean spawnParticle) {
        composterData.addToLevel(levels);

        BlockData blockData = block.getBlockData();
        Levelled levelled = (Levelled) blockData;
        int curLevel = levelled.getLevel();
        int toSet = (int) Math.floor(composterData.getLevel());
        int newLevel = Math.min(toSet, levelled.getMaximumLevel() - 1);

        if (newLevel != curLevel) {
            levelled.setLevel(newLevel);
            block.setBlockData(levelled);
        }

        if (spawnParticle) {
            World world = block.getWorld();
            Location blockLocation = block.getLocation();
            Location particleLocation = new Location(world, blockLocation.getX() + .5, blockLocation.getY() + newLevel / 8.0, blockLocation.getZ() + .5);
            world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, 10, .2, .2, .2);
        }
    }

    private void dropCompostResult(Block block, ItemStack toDrop) {
        World world = block.getWorld();
        double d0 = (double)(RecipeManager.random.nextFloat() * 0.7F) + 0.15000000596046448D;
        double d1 = (double)(RecipeManager.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
        double d2 = (double)(RecipeManager.random.nextFloat() * 0.7F) + 0.15000000596046448D;
        Item droppedItem = world.dropItem(new Location(world, block.getX() + d0, block.getY() + d1, block.getZ() + d2), toDrop.clone());
        droppedItem.setPickupDelay(10); // Default pickup delay

        world.playSound(block.getLocation(), Sound.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private void addCompost(Cancellable event, ComposterData data, Block block, ItemStack item, Player player) {
        UUID playerUUID = data.getPlayerUUID();
        CompostRecipe removed = Recipes.getInstance().getRemovedCompostRecipe(item);
        if (removed != null) {
            event.setCancelled(true);
        }

        CompostRecipe recipe = Recipes.getInstance().getCompostRecipe(item);
        if (recipe != null) {
            // No overridden compost recipes, so let's ignore vanilla recipes
            if (recipe.getInfo().getOwner() == RMCRecipeInfo.RecipeOwner.MINECRAFT && !Recipes.hasAnyOverridenCompostRecipe) {
                return;
            }

            boolean spawnParticles = false;

            if (event instanceof PlayerInteractEvent) {
                event.setCancelled(true);
                spawnParticles = true;
            } else if (event instanceof InventoryMoveItemEvent) {
                ((InventoryMoveItemEvent) event).setItem(new ItemStack(Material.AIR));
            }

            Args a = Args.create().player(playerUUID).recipe(recipe).location(block.getLocation()).extra(item).build();
            if (!recipe.checkFlags(a)) {
                if (player != null) {
                    SoundNotifier.sendDenySound(player, block.getLocation());
                }
                event.setCancelled(true);
                return;
            }

            BlockData blockData = block.getBlockData();
            Levelled levelled = (Levelled) blockData;
            int curLevel = levelled.getLevel();

            // Start fill
            if (curLevel == 0) {
                boolean anyResultPassed = false;
                List<ItemResult> itemResults = recipe.getResults();
                for (ItemResult r : itemResults) {
                    r = r.clone();
                    a.clearReasons();
                    a.setResult(r);

                    if (r.checkFlags(a)) {
                        if (r.getType() != Material.AIR) {
                            anyResultPassed = true;
                            break;
                        }
                    }
                }

                if (anyResultPassed) {
                    if (player != null) {
                        decrementHolding(player);
                    }

                    double chance = recipe.getLevelSuccessChance();
                    if (chance >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance) {
                        addToCompost(data, block, recipe.getLevels(), spawnParticles);
                        data.addIngredient(item);
                    }
                } else {
                    if (player != null) {
                        SoundNotifier.sendDenySound(player, block.getLocation());
                    }
                    event.setCancelled(true);
                }
            } else { // Normal fill
                CompostRecipe dataRecipe = data.getRecipe();

                boolean sameRecipe = false;
                boolean sameResult = false;
                boolean matchesVanillaResult = false;
                if (dataRecipe == null) {
                    if (!recipe.isMultiResult()) {
                        ItemResult recipeResult = recipe.getFirstResult();

                        if (recipeResult.getHashCode() == CompostRecipe.VANILLA_ITEM_RESULT.getHashCode()) {
                            matchesVanillaResult = true;
                        }
                    }

                    if (!matchesVanillaResult) {
                        if (player != null) {
                            player.sendMessage("Composter does not have a custom recipe set.");
                        }
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    sameRecipe = recipe.getIndex() == dataRecipe.getIndex();
                    if (!sameRecipe) {
                        if (!recipe.isMultiResult() && !dataRecipe.isMultiResult()) {
                            ItemResult recipeResult = recipe.getFirstResult();
                            ItemResult dataRecipeResult = dataRecipe.getFirstResult();

                            if (recipeResult.getHashCode() == dataRecipeResult.getHashCode()) {
                                sameResult = true;
                            }
                        }
                    }
                }

                if (sameRecipe || sameResult || matchesVanillaResult) {
                    List<ItemStack> dataIngredients = data.getIngredients();
                    List<ItemStack> ingredientsWithNew = new ArrayList<>(dataIngredients);
                    ingredientsWithNew.add(item);
                    a.setExtra(ingredientsWithNew);

                    boolean anyResultPassed = false;
                    List<ItemResult> potentialResults = recipe.getResults();
                    if (recipe.isMultiResult()) {
                        if (recipe.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
                            for (ItemResult r : potentialResults) {
                                a.clear();

                                if (r.checkFlags(a)) {
                                    anyResultPassed = true;
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
                                    anyResultPassed = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        ItemResult result = potentialResults.get(0).clone();

                        if (result.checkFlags(a)) {
                            anyResultPassed = true;
                        }
                    }

                    if (anyResultPassed) {
                        if (player != null) {
                            decrementHolding(player);
                        }

                        double chance = recipe.getLevelSuccessChance();
                        if (chance >= 100 || (RecipeManager.random.nextFloat() * 100) <= chance) {
                            addToCompost(data, block, recipe.getLevels(), spawnParticles);
                            data.addIngredient(item);
                        }
                    } else {
                        if (player != null) {
                            player.sendMessage("Ingredient does not match the same conditions as the composter is filled with.");
                        }
                    }
                } else {
                    if (player != null) {
                        player.sendMessage("This composter requires different ingredients.");
                        StringBuilder toString = new StringBuilder();
                        boolean first = true;
                        for (ItemStack ingredient : data.getIngredients()) {
                            if (first) {
                                first = false;
                            } else {
                                toString.append(",");
                            }

                            toString.append(ingredient.getType().name()).append(" x ").append(ingredient.getAmount());
                            if (ingredient.hasItemMeta()) {
                                toString.append(":").append(ingredient.getItemMeta());
                            }
                        }
                        player.sendMessage("Composter contains: " + toString.toString());
                    }
                }
            }
        }
    }

    private void decrementHolding(Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack holding = player.getInventory().getItemInMainHand();
            holding.setAmount(holding.getAmount() - 1);
        }
    }

    private ItemResult takeCompostResult(ComposterData data, CompostRecipe recipe, Block block) {
        ItemResult returnResult = null;
        List<ItemStack> dataIngredients = data.getIngredients();
        Args a = Args.create().player(data.getPlayerUUID()).recipe(recipe).location(block.getLocation()).extra(dataIngredients).build();

        boolean skipCraft = false;
        ItemResult result = null;
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
        }

        a.setResult(result);

        boolean recipeCraftSuccess = false;
        boolean resultCraftSuccess = false;
        if (!skipCraft) {
            a.setFirstRun(true); // TODO: Remove and create onCraftComplete
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
                }
            }

            if (!noResult) {
                returnResult = result;
                data.clearIngredients();
            }

            BlockData blockData = block.getBlockData();
            Levelled levelled = (Levelled) blockData;

            // Reset to empty
            levelled.setLevel(0);
            block.setBlockData(levelled);
        }

        return returnResult;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void inventoryMove(InventoryMoveItemEvent event) {
        Inventory sourceInventory = event.getSource();
        Inventory destinationInventory = event.getDestination();
        InventoryHolder sourceHolder = sourceInventory.getHolder();
        InventoryHolder destHolder = destinationInventory.getHolder();

        if (destHolder instanceof BlockInventoryHolder) {
            BlockInventoryHolder blockHolder = (BlockInventoryHolder) destHolder;
            Block block = blockHolder.getBlock();

            if (block.getType() == Material.COMPOSTER) {
                ComposterData data = Composters.get(block.getLocation());

                addCompost(event, data, block, event.getItem(), null);
            }
        }

        if (sourceHolder instanceof BlockInventoryHolder) {
            BlockInventoryHolder blockHolder = (BlockInventoryHolder) sourceHolder;
            Block block = blockHolder.getBlock();

            if (block.getType() == Material.COMPOSTER) {
                ComposterData data = Composters.get(block.getLocation());
                CompostRecipe recipe = data.getRecipe();

                if (recipe != null) {
                    ItemResult result = takeCompostResult(data, recipe, block);
                    if (result != null) {
                        event.setItem(result);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void composterPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.COMPOSTER) {
            Composters.add(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void composterBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.COMPOSTER) {
            Composters.remove(block.getLocation());
        }
    }
}
