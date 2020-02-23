package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import haveric.recipeManager.nms.NMSVersionHandler;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe1_13;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.furnace.RMBlastingRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManager.recipes.furnace.RMSmokingRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.*;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeInfo.RecipeOwner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Control for Bukkit recipes to avoid confusion with RecipeManager's recipes
 */
public class Vanilla {
    protected static Map<BaseRecipe, RMCRecipeInfo> initialRecipes = new HashMap<>();

    /** Leather dyeing's special recipe result, you can use it to identify vanilla recipes. */
    public static final ItemStack RECIPE_LEATHERDYE = new ItemStack(Material.LEATHER_HELMET, 0, (short) 0);

    /**
     * Map cloning's special recipe result, you can use it to identify vanilla recipes.
     */
    public static ItemStack RECIPE_MAPCLONE;

    /**
     * Map extending's special recipe result, you can use it to identify vanilla recipes.
     */
    public static ItemStack RECIPE_MAPEXTEND;
    public static ItemStack RECIPE_MAPEXTEND_1_11;

    /**
     * Fireworks' special recipe result, you can use it to identify vanilla recipes.
     */
    public static ItemStack RECIPE_FIREWORKS;

    /**
     * Item repair special recipe result (SHAPELESS Recipe)
     */
    public static final ItemStack RECIPE_REPAIR = new ItemStack(Material.LEATHER_HELMET, 1, (short) 0);

    /**
     * Banner special recipe result
     */
    protected static ItemStack RECIPE_BANNER = null;

    protected static ItemStack RECIPE_SHIELD_BANNER = null;

    protected static ItemStack RECIPE_TIPPED_ARROW = null;

    /**
     * Book cloning's special recipe
     */
    public static final ItemStack RECIPE_BOOKCLONE = new ItemStack(Material.WRITTEN_BOOK, 0, (short) -1);


    /**
     * Default time a furnace recipe burns for.<br>
     * This is a game constant.
     */
    public static final float FURNACE_RECIPE_TIME = 10f;
    public static final float BLASTING_RECIPE_TIME = 5f;
    public static final float SMOKER_RECIPE_TIME = 5f;
    public static final float CAMPFIRE_RECIPE_TIME = 30f;
    private static final RMCRecipeInfo info = new RMCRecipeInfo(RecipeOwner.MINECRAFT, null); // shared info

    protected static void init() {
        clean();

        initFuels();
        initCompostRecipes();
        initSpecialRecipes();
        initVanillaRecipes();
        indexInitialRecipes();
    }

    protected static void clean() {
        initialRecipes.clear();
    }

    private static void initFuels() {
        // Add vanilla Minecraft fuels just for warning if user adds one that already exists or tries to overwrite a nonexistent one
        addFuelRecipe(Material.COAL, 80);
        addFuelRecipe(Material.STICK, 5);
        addFuelRecipe(Material.ACACIA_STAIRS, 15);
        addFuelRecipe(Material.DARK_OAK_STAIRS, 15);
        addFuelRecipe(Material.BOOKSHELF, 15);
        addFuelRecipe(Material.CHEST, 15);
        addFuelRecipe(Material.TRAPPED_CHEST, 15);
        addFuelRecipe(Material.DAYLIGHT_DETECTOR, 15);
        addFuelRecipe(Material.JUKEBOX, 15);
        addFuelRecipe(Material.NOTE_BLOCK, 15);
        addFuelRecipe(Material.BLAZE_ROD, 120);
        addFuelRecipe(Material.COAL_BLOCK, 800);
        addFuelRecipe(Material.LAVA_BUCKET, 1000);

        addFuelRecipe(Material.SPRUCE_FENCE, 15);
        addFuelRecipe(Material.BIRCH_FENCE, 15);
        addFuelRecipe(Material.JUNGLE_FENCE, 15);
        addFuelRecipe(Material.DARK_OAK_FENCE, 15);
        addFuelRecipe(Material.ACACIA_FENCE, 15);

        addFuelRecipe(Material.SPRUCE_FENCE_GATE, 15);
        addFuelRecipe(Material.BIRCH_FENCE_GATE, 15);
        addFuelRecipe(Material.JUNGLE_FENCE_GATE, 15);
        addFuelRecipe(Material.DARK_OAK_FENCE_GATE, 15);
        addFuelRecipe(Material.ACACIA_FENCE_GATE, 15);

        if (!Version.has1_13BasicSupport()) {
            addFuelRecipe("LOG", 15);
            addFuelRecipe("LOG_2", 15);
            addFuelRecipe("WOOD", 15);
            addFuelRecipe("WOOD_STEP", 7.5f);
            addFuelRecipe("SAPLING", 5);
            addFuelRecipe("WOOD_AXE", 10);
            addFuelRecipe("WOOD_HOE", 10);
            addFuelRecipe("WOOD_PICKAXE", 10);
            addFuelRecipe("WOOD_SPADE", 10);
            addFuelRecipe("WOOD_SWORD", 10);
            addFuelRecipe("WOOD_PLATE", 15);
            addFuelRecipe("FENCE", 15);
            addFuelRecipe("FENCE_GATE", 15);
            addFuelRecipe("WOOD_STAIRS", 15);
            addFuelRecipe("BIRCH_WOOD_STAIRS", 15);
            addFuelRecipe("SPRUCE_WOOD_STAIRS", 15);
            addFuelRecipe("JUNGLE_WOOD_STAIRS", 15);
            addFuelRecipe("TRAP_DOOR", 15);
            addFuelRecipe("WORKBENCH", 15);
            addFuelRecipe("HUGE_MUSHROOM_1", 15);
            addFuelRecipe("HUGE_MUSHROOM_2", 15);
            addFuelRecipe("BANNER", 15);
        }

        if (Version.has1_11Support()) {
            addFuelRecipe(Material.LADDER, 15);
            addFuelRecipe(Material.BOW, 10);
            addFuelRecipe(Material.FISHING_ROD, 15);
            addFuelRecipe(Material.BOWL, 5);

            if (!Version.has1_13BasicSupport()) {
                addFuelRecipe("WOOL", 5);
                addFuelRecipe("CARPET", 3.35f);
                addFuelRecipe("WOOD_BUTTON", 5);
                addFuelRecipe("WOOD_DOOR", 10);
                addFuelRecipe("DARK_OAK_DOOR_ITEM", 10);
                addFuelRecipe("ACACIA_DOOR_ITEM", 10);
                addFuelRecipe("BIRCH_DOOR_ITEM", 10);
                addFuelRecipe("JUNGLE_DOOR_ITEM", 10);
                addFuelRecipe("SPRUCE_DOOR_ITEM", 10);
                addFuelRecipe("BOAT", 20);
                addFuelRecipe("BOAT_ACACIA", 20);
                addFuelRecipe("BOAT_BIRCH", 20);
                addFuelRecipe("BOAT_DARK_OAK", 20);
                addFuelRecipe("BOAT_JUNGLE", 20);
                addFuelRecipe("BOAT_SPRUCE", 20);
            }

            if (!Version.has1_14Support()) {
                addFuelRecipe("SIGN", 10);
            }
        }

        if (Version.has1_13BasicSupport()) {
            // Updated old fuels that got renamed in 1.13
            addFuelRecipe(Material.ACACIA_LOG, 15);
            addFuelRecipe(Material.BIRCH_LOG, 15);
            addFuelRecipe(Material.DARK_OAK_LOG, 15);
            addFuelRecipe(Material.JUNGLE_LOG, 15);
            addFuelRecipe(Material.OAK_LOG, 15);
            addFuelRecipe(Material.SPRUCE_LOG, 15);
            addFuelRecipe(Material.STRIPPED_ACACIA_LOG, 15);
            addFuelRecipe(Material.STRIPPED_BIRCH_LOG, 15);
            addFuelRecipe(Material.STRIPPED_DARK_OAK_LOG, 15);
            addFuelRecipe(Material.STRIPPED_JUNGLE_LOG, 15);
            addFuelRecipe(Material.STRIPPED_OAK_LOG, 15);
            addFuelRecipe(Material.STRIPPED_SPRUCE_LOG, 15);

            addFuelRecipe(Material.ACACIA_WOOD, 15);
            addFuelRecipe(Material.BIRCH_WOOD, 15);
            addFuelRecipe(Material.DARK_OAK_WOOD, 15);
            addFuelRecipe(Material.JUNGLE_WOOD, 15);
            addFuelRecipe(Material.OAK_WOOD, 15);
            addFuelRecipe(Material.SPRUCE_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_ACACIA_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_BIRCH_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_DARK_OAK_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_JUNGLE_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_OAK_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_SPRUCE_WOOD, 15);

            addFuelRecipe(Material.ACACIA_SLAB, 7.5f);
            addFuelRecipe(Material.BIRCH_SLAB, 7.5f);
            addFuelRecipe(Material.DARK_OAK_SLAB, 7.5f);
            addFuelRecipe(Material.JUNGLE_SLAB, 7.5f);
            addFuelRecipe(Material.OAK_SLAB, 7.5f);
            addFuelRecipe(Material.SPRUCE_SLAB, 7.5f);
            //addFuelRecipe(Material.PETRIFIED_OAK_SLAB, 7.5f);  // Maybe?

            addFuelRecipe(Material.ACACIA_SAPLING, 5);
            addFuelRecipe(Material.BIRCH_SAPLING, 5);
            addFuelRecipe(Material.DARK_OAK_SAPLING, 5);
            addFuelRecipe(Material.JUNGLE_SAPLING, 5);
            addFuelRecipe(Material.OAK_SAPLING, 5);
            addFuelRecipe(Material.SPRUCE_SAPLING, 5);

            addFuelRecipe(Material.WOODEN_AXE, 10);
            addFuelRecipe(Material.WOODEN_HOE, 10);
            addFuelRecipe(Material.WOODEN_PICKAXE, 10);
            addFuelRecipe(Material.WOODEN_SHOVEL, 10);
            addFuelRecipe(Material.WOODEN_SWORD, 10);

            addFuelRecipe(Material.ACACIA_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.BIRCH_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.DARK_OAK_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.JUNGLE_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.OAK_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.SPRUCE_PRESSURE_PLATE, 15);

            addFuelRecipe(Material.OAK_FENCE, 15);
            addFuelRecipe(Material.OAK_FENCE_GATE, 15);

            addFuelRecipe(Material.ACACIA_STAIRS, 15);
            addFuelRecipe(Material.BIRCH_STAIRS, 15);
            addFuelRecipe(Material.DARK_OAK_STAIRS, 15);
            addFuelRecipe(Material.JUNGLE_STAIRS, 15);
            addFuelRecipe(Material.OAK_STAIRS, 15);
            addFuelRecipe(Material.SPRUCE_STAIRS, 15);

            addFuelRecipe(Material.ACACIA_TRAPDOOR, 15);
            addFuelRecipe(Material.BIRCH_TRAPDOOR, 15);
            addFuelRecipe(Material.DARK_OAK_TRAPDOOR, 15);
            addFuelRecipe(Material.JUNGLE_TRAPDOOR, 15);
            addFuelRecipe(Material.OAK_TRAPDOOR, 15);
            addFuelRecipe(Material.SPRUCE_TRAPDOOR, 15);

            addFuelRecipe(Material.CRAFTING_TABLE, 15);

            addFuelRecipe(Material.BROWN_MUSHROOM_BLOCK, 15);
            addFuelRecipe(Material.RED_MUSHROOM_BLOCK, 15);

            addFuelRecipe(Material.BLACK_BANNER, 15);
            addFuelRecipe(Material.BLUE_BANNER, 15);
            addFuelRecipe(Material.BROWN_BANNER, 15);
            addFuelRecipe(Material.CYAN_BANNER, 15);
            addFuelRecipe(Material.GRAY_BANNER, 15);
            addFuelRecipe(Material.GREEN_BANNER, 15);
            addFuelRecipe(Material.LIGHT_BLUE_BANNER, 15);
            addFuelRecipe(Material.LIGHT_GRAY_BANNER, 15);
            addFuelRecipe(Material.LIME_BANNER, 15);
            addFuelRecipe(Material.MAGENTA_BANNER, 15);
            addFuelRecipe(Material.ORANGE_BANNER, 15);
            addFuelRecipe(Material.PINK_BANNER, 15);
            addFuelRecipe(Material.PURPLE_BANNER, 15);
            addFuelRecipe(Material.RED_BANNER, 15);
            addFuelRecipe(Material.WHITE_BANNER, 15);
            addFuelRecipe(Material.YELLOW_BANNER, 15);

            addFuelRecipe(Material.BLACK_WOOL, 5);
            addFuelRecipe(Material.BLUE_WOOL, 5);
            addFuelRecipe(Material.BROWN_WOOL, 5);
            addFuelRecipe(Material.CYAN_WOOL, 5);
            addFuelRecipe(Material.GRAY_WOOL, 5);
            addFuelRecipe(Material.GREEN_WOOL, 5);
            addFuelRecipe(Material.LIGHT_BLUE_WOOL, 5);
            addFuelRecipe(Material.LIGHT_GRAY_WOOL, 5);
            addFuelRecipe(Material.LIME_WOOL, 5);
            addFuelRecipe(Material.MAGENTA_WOOL, 5);
            addFuelRecipe(Material.ORANGE_WOOL, 5);
            addFuelRecipe(Material.PINK_WOOL, 5);
            addFuelRecipe(Material.PURPLE_WOOL, 5);
            addFuelRecipe(Material.RED_WOOL, 5);
            addFuelRecipe(Material.WHITE_WOOL, 5);
            addFuelRecipe(Material.YELLOW_WOOL, 5);

            addFuelRecipe(Material.BLACK_CARPET, 3.35f);
            addFuelRecipe(Material.BLUE_CARPET, 3.35f);
            addFuelRecipe(Material.BROWN_CARPET, 3.35f);
            addFuelRecipe(Material.CYAN_CARPET, 3.35f);
            addFuelRecipe(Material.GRAY_CARPET, 3.35f);
            addFuelRecipe(Material.GREEN_CARPET, 3.35f);
            addFuelRecipe(Material.LIGHT_BLUE_CARPET, 3.35f);
            addFuelRecipe(Material.LIGHT_GRAY_CARPET, 3.35f);
            addFuelRecipe(Material.LIME_CARPET, 3.35f);
            addFuelRecipe(Material.MAGENTA_CARPET, 3.35f);
            addFuelRecipe(Material.ORANGE_CARPET, 3.35f);
            addFuelRecipe(Material.PINK_CARPET, 3.35f);
            addFuelRecipe(Material.PURPLE_CARPET, 3.35f);
            addFuelRecipe(Material.RED_CARPET, 3.35f);
            addFuelRecipe(Material.WHITE_CARPET, 3.35f);
            addFuelRecipe(Material.YELLOW_CARPET, 3.35f);

            addFuelRecipe(Material.ACACIA_BUTTON, 5);
            addFuelRecipe(Material.BIRCH_BUTTON, 5);
            addFuelRecipe(Material.DARK_OAK_BUTTON, 5);
            addFuelRecipe(Material.JUNGLE_BUTTON, 5);
            addFuelRecipe(Material.OAK_BUTTON, 5);
            addFuelRecipe(Material.SPRUCE_BUTTON, 5);

            addFuelRecipe(Material.ACACIA_DOOR, 10);
            addFuelRecipe(Material.BIRCH_DOOR, 10);
            addFuelRecipe(Material.DARK_OAK_DOOR, 10);
            addFuelRecipe(Material.JUNGLE_DOOR, 10);
            addFuelRecipe(Material.OAK_DOOR, 10);
            addFuelRecipe(Material.SPRUCE_DOOR, 10);

            if (!Version.has1_15Support()) {
                addFuelRecipe(Material.ACACIA_BOAT, 20);
                addFuelRecipe(Material.BIRCH_BOAT, 20);
                addFuelRecipe(Material.DARK_OAK_BOAT, 20);
                addFuelRecipe(Material.JUNGLE_BOAT, 20);
                addFuelRecipe(Material.OAK_BOAT, 20);
                addFuelRecipe(Material.SPRUCE_BOAT, 20);
            }

            // New fuels in 1.13
            addFuelRecipe(Material.DRIED_KELP_BLOCK, 200);
        }

        if (Version.has1_14Support()) {
            addFuelRecipe(Material.ACACIA_SIGN, 10);
            addFuelRecipe(Material.BIRCH_SIGN, 10);
            addFuelRecipe(Material.JUNGLE_SIGN, 10);
            addFuelRecipe(Material.OAK_SIGN, 10);
            addFuelRecipe(Material.SPRUCE_SIGN, 10);
            addFuelRecipe(Material.DARK_OAK_SIGN, 10);

            // New fuels in 1.14
            if (!Version.has1_15Support()) {
                addFuelRecipe(Material.SCAFFOLDING, 2.5f);
            }
            addFuelRecipe(Material.CARTOGRAPHY_TABLE, 15);
            addFuelRecipe(Material.FLETCHING_TABLE, 15);
            addFuelRecipe(Material.SMITHING_TABLE, 15);
            addFuelRecipe(Material.LECTERN, 15);
            addFuelRecipe(Material.COMPOSTER, 15);
            addFuelRecipe(Material.BARREL, 15);
            addFuelRecipe(Material.BAMBOO, 2.5f);
            addFuelRecipe(Material.DEAD_BUSH, 5);
        }

        if (Version.has1_15Support()) {
            addFuelRecipe(Material.SCAFFOLDING, 20);

            addFuelRecipe(Material.ACACIA_BOAT, 60);
            addFuelRecipe(Material.BIRCH_BOAT, 60);
            addFuelRecipe(Material.DARK_OAK_BOAT, 60);
            addFuelRecipe(Material.JUNGLE_BOAT, 60);
            addFuelRecipe(Material.OAK_BOAT, 60);
            addFuelRecipe(Material.SPRUCE_BOAT, 60);
        }

        // Index fuel recipes
        for (BaseRecipe recipe : initialRecipes.keySet()) {
            if (recipe instanceof FuelRecipe) {
                RecipeManager.getRecipes().indexFuels.put(((FuelRecipe) recipe).getIndexString(), (FuelRecipe) recipe);
            }
        }
    }

    private static void addFuelRecipe(String legacyMaterial, float burnTime) {
        addFuelRecipe(Material.getMaterial(legacyMaterial), burnTime);
    }

    private static void addFuelRecipe(Material material, float burnTime) {
        initialRecipes.put(new FuelRecipe(material, burnTime), info);
    }

    private static void initCompostRecipes() {
        if (Version.has1_14Support()) {
            addCompostRecipe(Material.BEETROOT_SEEDS, 30);
            addCompostRecipe(Material.DRIED_KELP, 30);
            addCompostRecipe(Material.GRASS, 30);
            addCompostRecipe(Material.KELP, 30);
            addCompostRecipe(Material.ACACIA_LEAVES, 30);
            addCompostRecipe(Material.BIRCH_LEAVES, 30);
            addCompostRecipe(Material.DARK_OAK_LEAVES, 30);
            addCompostRecipe(Material.JUNGLE_LEAVES, 30);
            addCompostRecipe(Material.OAK_LEAVES, 30);
            addCompostRecipe(Material.SPRUCE_LEAVES, 30);
            addCompostRecipe(Material.MELON_SEEDS, 30);
            addCompostRecipe(Material.PUMPKIN_SEEDS, 30);
            addCompostRecipe(Material.ACACIA_SAPLING, 30);
            addCompostRecipe(Material.BIRCH_SAPLING, 30);
            addCompostRecipe(Material.DARK_OAK_SAPLING, 30);
            addCompostRecipe(Material.JUNGLE_SAPLING, 30);
            addCompostRecipe(Material.OAK_SAPLING, 30);
            addCompostRecipe(Material.SPRUCE_SAPLING, 30);
            addCompostRecipe(Material.SEAGRASS, 30);
            addCompostRecipe(Material.SWEET_BERRIES, 30);
            addCompostRecipe(Material.WHEAT_SEEDS, 30);

            addCompostRecipe(Material.CACTUS, 50);
            addCompostRecipe(Material.DRIED_KELP_BLOCK, 50);
            addCompostRecipe(Material.MELON_SLICE, 50);
            addCompostRecipe(Material.SUGAR_CANE, 50);
            addCompostRecipe(Material.TALL_GRASS, 50);
            addCompostRecipe(Material.VINE, 50);

            addCompostRecipe(Material.APPLE, 65);
            addCompostRecipe(Material.BEETROOT, 65);
            addCompostRecipe(Material.CARROT, 65);
            addCompostRecipe(Material.COCOA_BEANS, 65);
            addCompostRecipe(Material.FERN, 65);
            addCompostRecipe(Material.LARGE_FERN, 65);
            addCompostRecipe(Material.DANDELION, 65);
            addCompostRecipe(Material.POPPY, 65);
            addCompostRecipe(Material.BLUE_ORCHID, 65);
            addCompostRecipe(Material.ALLIUM, 65);
            addCompostRecipe(Material.AZURE_BLUET, 65);
            addCompostRecipe(Material.RED_TULIP, 65);
            addCompostRecipe(Material.ORANGE_TULIP, 65);
            addCompostRecipe(Material.WHITE_TULIP, 65);
            addCompostRecipe(Material.PINK_TULIP, 65);
            addCompostRecipe(Material.OXEYE_DAISY, 65);
            addCompostRecipe(Material.CORNFLOWER, 65);
            addCompostRecipe(Material.LILY_OF_THE_VALLEY, 65);
            addCompostRecipe(Material.WITHER_ROSE, 65);
            addCompostRecipe(Material.SUNFLOWER, 65);
            addCompostRecipe(Material.LILAC, 65);
            addCompostRecipe(Material.ROSE_BUSH, 65);
            addCompostRecipe(Material.PEONY, 65);
            addCompostRecipe(Material.LILY_PAD, 65);
            addCompostRecipe(Material.MELON, 65);
            addCompostRecipe(Material.BROWN_MUSHROOM, 65);
            addCompostRecipe(Material.RED_MUSHROOM, 65);
            addCompostRecipe(Material.MUSHROOM_STEM, 65);
            addCompostRecipe(Material.POTATO, 65);
            addCompostRecipe(Material.PUMPKIN, 65);
            addCompostRecipe(Material.CARVED_PUMPKIN, 65);
            addCompostRecipe(Material.SEA_PICKLE, 65);
            addCompostRecipe(Material.WHEAT, 65);

            addCompostRecipe(Material.BAKED_POTATO, 85);
            addCompostRecipe(Material.BREAD, 85);
            addCompostRecipe(Material.COOKIE, 85);
            addCompostRecipe(Material.HAY_BLOCK, 85);
            addCompostRecipe(Material.BROWN_MUSHROOM_BLOCK, 85);
            addCompostRecipe(Material.RED_MUSHROOM_BLOCK, 85);

            addCompostRecipe(Material.CAKE, 100);
            addCompostRecipe(Material.PUMPKIN_PIE, 100);
        }
    }

    private static void addCompostRecipe(Material ingredient, double chance) {
        CompostRecipe recipe = new CompostRecipe(ingredient, chance);
        initialRecipes.put(recipe, info);
        RecipeManager.getRecipes().indexCompost.put(recipe.getIndexString().get(0), recipe);
    }

    private static void initSpecialRecipes() {
        if (!Version.has1_13BasicSupport()) {
            RECIPE_MAPCLONE = new ItemStack(Material.getMaterial("EMPTY_MAP"), 0, (short) -1);
            RECIPE_MAPEXTEND = new ItemStack(Material.getMaterial("EMPTY_MAP"), 0, (short) 0);
            RECIPE_MAPEXTEND_1_11 = new ItemStack(Material.getMaterial("EMPTY_MAP"), 1, (short) 0);
            RECIPE_FIREWORKS = new ItemStack(Material.getMaterial("FIREWORK"), 0, (short) 0);
            RECIPE_BANNER = new ItemStack(Material.getMaterial("BANNER"), 0, (short) 0);
        }

        if (Version.has1_9Support()) {
            RECIPE_SHIELD_BANNER = new ItemStack(Material.SHIELD, 0, (short) 0);
        }
        if (Version.has1_11Support()) {
            RECIPE_TIPPED_ARROW = new ItemStack(Material.TIPPED_ARROW, 8, (short) 0);
        }
    }

    private static void initVanillaRecipes() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r == null || (RecipeManager.getRecipes() != null && RecipeManager.getRecipes().isCustomRecipe(r))) {
                    continue;
                }

                BaseRecipe recipe = null;

                if (r instanceof ShapedRecipe) {
                    if (Version.has1_13Support()) {
                        recipe = new CraftRecipe1_13((ShapedRecipe) r);
                    } else {
                        recipe = new CraftRecipe((ShapedRecipe) r);
                    }
                } else if (r instanceof ShapelessRecipe) {
                    if (Version.has1_13Support()) {
                        recipe = new CombineRecipe1_13((ShapelessRecipe) r);
                    } else {
                        recipe = new CombineRecipe((ShapelessRecipe) r);
                    }
                } else if (r instanceof FurnaceRecipe) {
                    if (Version.has1_13Support()) {
                        recipe = new RMFurnaceRecipe1_13((FurnaceRecipe) r);
                    } else {
                        recipe = new RMFurnaceRecipe((FurnaceRecipe) r);
                    }
                } else if (r instanceof BlastingRecipe) {
                    recipe = new RMBlastingRecipe((BlastingRecipe) r);
                } else if (r instanceof SmokingRecipe) {
                    recipe = new RMSmokingRecipe((SmokingRecipe) r);
                } else if (r instanceof CampfireRecipe) {
                    recipe = new RMCampfireRecipe((CampfireRecipe) r);
                } else if (r instanceof StonecuttingRecipe) {
                    recipe = new RMStonecuttingRecipe((StonecuttingRecipe) r);
                }

                if (recipe == null) {
                    continue;
                }

                if (isSpecialRecipe(r)) {
                    recipe.setVanillaSpecialRecipe(true);
                }
                initialRecipes.put(recipe, info);
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }
    }

    private static void indexInitialRecipes() {
        for (Entry<BaseRecipe, RMCRecipeInfo> e : initialRecipes.entrySet()) {
            BaseRecipe recipe = e.getKey();
            RecipeManager.getRecipes().index.put(recipe, e.getValue());
            RecipeManager.getRecipes().indexName.put(recipe.getName(), recipe);
        }
    }

    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeCustomRecipe(BaseRecipe recipe) {
        if (recipe instanceof CraftRecipe1_13) {
            return removeCraftRecipe((CraftRecipe1_13) recipe);
        }

        if (recipe instanceof CraftRecipe) {
            return removeCraftLegacyRecipe((CraftRecipe) recipe);
        }

        if (recipe instanceof CombineRecipe) {
            return removeCombineRecipe((CombineRecipe) recipe);
        }

        if (recipe instanceof RMFurnaceRecipe1_13) {
            return removeSmeltRecipe((RMFurnaceRecipe1_13) recipe);
        }

        if (recipe instanceof RMBlastingRecipe) {
            return removeBlastingRecipe((RMBlastingRecipe) recipe);
        }

        if (recipe instanceof RMSmokingRecipe) {
            return removeSmokingRecipe((RMSmokingRecipe) recipe);
        }

        if (recipe instanceof RMFurnaceRecipe) {
            return removeSmeltLegacyRecipe((RMFurnaceRecipe) recipe);
        }

        if (recipe instanceof RMCampfireRecipe) {
            return removeCampfireRecipe((RMCampfireRecipe) recipe);
        }

        if (recipe instanceof RMStonecuttingRecipe) {
            return removeStonecuttingRecipe((RMStonecuttingRecipe) recipe);
        }

        return null;
    }

    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeCraftLegacyRecipe(CraftRecipe recipe) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        ShapedRecipe sr;
        Recipe r;

        String[] sh;

        ItemStack[] matrix = recipe.getIngredients();
        RMBukkitTools.trimItemMatrix(matrix);
        ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
        int height = recipe.getHeight();
        int width = recipe.getWidth();

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe) {
                    sr = (ShapedRecipe) r;
                    sh = sr.getShape();

                    if (sh.length == height && sh[0].length() == width && NMSVersionHandler.getToolsRecipe().matchesShapedLegacy(sr, matrix, matrixMirror, width, height)) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return sr;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeCraftRecipe(CraftRecipe1_13 recipe) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        ShapedRecipe sr;
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe) {
                    sr = (ShapedRecipe) r;

                    if (NMSVersionHandler.getToolsRecipe().matchesShaped(sr, recipe.getChoiceShape(), recipe.getIngredientsChoiceMap())) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return sr;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeCombineRecipe(CombineRecipe recipe) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        if (Version.has1_13Support()) {
            List<List<Material>> ingredientChoices = recipe.getIngredientChoiceList();

            while (iterator.hasNext()) {
                try {
                    r = iterator.next();

                    if (r instanceof ShapelessRecipe) {
                        if (NMSVersionHandler.getToolsRecipe().matchesShapeless(r, ingredientChoices)) {
                            iterator.remove();

                            baseRecipeIterator.finish();

                            return r;
                        }
                    }
                } catch (NullPointerException e) {
                    // Catch any invalid Bukkit recipes
                }
            }
        } else {
            List<ItemStack> items = recipe.getIngredients();

            while (iterator.hasNext()) {
                try {
                    r = iterator.next();

                    if (r instanceof ShapelessRecipe) {
                        if (NMSVersionHandler.getToolsRecipe().matchesShapelessLegacy(r, items)) {
                            iterator.remove();

                            baseRecipeIterator.finish();

                            return r;
                        }
                    }
                } catch (NullPointerException e) {
                    // Catch any invalid Bukkit recipes
                }
            }
        }

        return null;
    }

    /**
     * Removes a RecipeManager smelt recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    private static Recipe removeSmeltLegacyRecipe(RMFurnaceRecipe recipe) {
        return removeFurnaceRecipe(recipe.getIngredient());
    }

    /**
     * Removes a RecipeManager smelt recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    private static Recipe removeSmeltRecipe(RMFurnaceRecipe1_13 recipe) {
        return removeFurnaceRecipe(new ItemStack(recipe.getIngredientChoice().get(0)));
    }

    private static Recipe removeFurnaceRecipe(ItemStack ingredient) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = NMSVersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof FurnaceRecipe) {
                    if (toolsRecipe.matchesFurnace(r, ingredient)) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    private static Recipe removeBlastingRecipe(RMBlastingRecipe recipe) {
        if (Version.has1_13Support()) {
            return removeBlastingRecipe(new ItemStack(recipe.getIngredientChoice().get(0)));
        } else {
            return removeBlastingRecipe(recipe.getIngredient());
        }
    }

    private static Recipe removeBlastingRecipe(ItemStack ingredient) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = NMSVersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof BlastingRecipe) {
                    if (toolsRecipe.matchesBlasting(r, ingredient)) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    private static Recipe removeSmokingRecipe(RMSmokingRecipe recipe) {
        if (Version.has1_13Support()) {
            return removeSmokingRecipe(new ItemStack(recipe.getIngredientChoice().get(0)));
        } else {
            return removeSmokingRecipe(recipe.getIngredient());
        }
    }

    private static Recipe removeSmokingRecipe(ItemStack ingredient) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = NMSVersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof SmokingRecipe) {
                    if (toolsRecipe.matchesSmoking(r, ingredient)) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    private static Recipe removeCampfireRecipe(RMCampfireRecipe recipe) {
        return removeCampfireRecipe(new ItemStack(recipe.getIngredientChoice().get(0)));
    }

    private static Recipe removeCampfireRecipe(ItemStack ingredient) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = NMSVersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof CampfireRecipe) {
                    if (toolsRecipe.matchesCampfire(r, ingredient)) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    private static Recipe removeStonecuttingRecipe(RMStonecuttingRecipe recipe) {
        return removeStonecuttingRecipe(new ItemStack(recipe.getIngredientChoice().get(0)), recipe.getResult());
    }

    private static Recipe removeStonecuttingRecipe(ItemStack ingredient, ItemStack result) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = NMSVersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof StonecuttingRecipe) {
                    if (toolsRecipe.matchesStonecutting(r, ingredient, result)) {
                        iterator.remove();

                        baseRecipeIterator.finish();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    // 1.12 replacement support
    
    /**
     * Replaces a recipe with a RecipeManager recipe. V1_12 support only!
     *
     * @param recipe
     *            RecipeManager recipe
     * @return replaced recipe or null if not found.
     */
    public static Recipe replaceCustomRecipe(BaseRecipe recipe) {
        if (!Version.has1_12Support()) return null;

        if (recipe instanceof CraftRecipe1_13) {
            return replaceCraftRecipeV1_13((CraftRecipe1_13) recipe);
        }
        if (recipe instanceof CraftRecipe) {
            return replaceCraftRecipeV1_12((CraftRecipe) recipe);
        }

        if (recipe instanceof CombineRecipe) {
            return replaceCombineRecipeV1_12((CombineRecipe) recipe);
        }

        return null;

    }

    /**
     * V1_13 or newer only.
     * Replaces a RecipeManager recipe on the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return replaced recipe or null if not found
     */
    public static Recipe replaceCraftRecipeV1_13(CraftRecipe1_13 recipe) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        ShapedRecipe sr;
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe) {
                    sr = (ShapedRecipe) r;

                    if (NMSVersionHandler.getToolsRecipe().matchesShaped(sr, recipe.getChoiceShape(), recipe.getIngredientsChoiceMap())) {
                        ItemStack overrideItem = Tools.createItemRecipeId(recipe.getFirstResult(), recipe.getIndex());
                        baseRecipeIterator.replace(recipe, overrideItem);
                        baseRecipeIterator.finish();
                        return sr;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    /**
     * V1_12 only.
     * Replaces a RecipeManager recipe on the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return replaced recipe or null if not found
     */
    public static Recipe replaceCraftRecipeV1_12(CraftRecipe recipe) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        ShapedRecipe sr;
        Recipe r;

        String[] sh;

        ItemStack[] matrix = recipe.getIngredients();
        RMBukkitTools.trimItemMatrix(matrix);
        ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
        int height = recipe.getHeight();
        int width = recipe.getWidth();

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe) {
                    sr = (ShapedRecipe) r;
                    sh = sr.getShape();

                    if (sh.length == height && sh[0].length() == width && NMSVersionHandler.getToolsRecipe().matchesShapedLegacy(sr, matrix, matrixMirror, width, height)) {
                        ItemStack overrideItem = Tools.createItemRecipeId(recipe.getFirstResult(), recipe.getIndex());
                        baseRecipeIterator.replace(recipe, overrideItem);
                        baseRecipeIterator.finish();
                        return sr;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        return null;
    }

    /**
     * V1_12 or newer supported only.
     * Replaces a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return replaced recipe or null if not found
     */
    public static Recipe replaceCombineRecipeV1_12(CombineRecipe recipe) {
        BaseRecipeIterator baseRecipeIterator = NMSVersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = baseRecipeIterator.getIterator();
        Recipe r;

        if (Version.has1_13Support()) {
            List<List<Material>> choiceList = recipe.getIngredientChoiceList();

            while (iterator.hasNext()) {
                try {
                    r = iterator.next();

                    if (r instanceof ShapelessRecipe) {
                        if (NMSVersionHandler.getToolsRecipe().matchesShapeless(r, choiceList)) {
                            ItemStack overrideItem = Tools.createItemRecipeId(recipe.getFirstResult(), recipe.getIndex());
                            baseRecipeIterator.replace(recipe, overrideItem);
                            baseRecipeIterator.finish();
                            return r;
                        }
                    }
                } catch (NullPointerException e) {
                    // Catch any invalid Bukkit recipes
                }
            }
        } else {
            List<ItemStack> items = recipe.getIngredients();

            while (iterator.hasNext()) {
                try {
                    r = iterator.next();

                    if (r instanceof ShapelessRecipe) {
                        if (NMSVersionHandler.getToolsRecipe().matchesShapelessLegacy(r, items)) {
                            ItemStack overrideItem = Tools.createItemRecipeId(recipe.getFirstResult(), recipe.getIndex());
                            baseRecipeIterator.replace(recipe, overrideItem);
                            baseRecipeIterator.finish();
                            return r;
                        }
                    }
                } catch (NullPointerException e) {
                    // Catch any invalid Bukkit recipes
                }
            }
        }

        return null;
    }

    // 1.12 replacement support end
    
    /**
     * Remove all RecipeManager recipes from the server.
     */
    public static void removeCustomRecipes() {
        if (RecipeManager.getRecipes() == null) {
            return;
        }

        List<Recipe> originalRecipes = new ArrayList<>();

        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe recipe;

        // In 1.12, we don't use the special iterator here, because "remove" is actually suppress.
        //  Since we really want to remove, we instead catalogue the non-custom, in case
        //  other plugins have added recipes. Then, when done, we clear all, and readd just those
        //  discovered recipes, in the order we found them, to limit disruption.
        while (iterator.hasNext()) {
            try {
                recipe = iterator.next();

                if (recipe != null) {
                    if (RecipeManager.getRecipes().isCustomRecipe(recipe)) {
                        if (!Version.has1_12Support()) {
                            iterator.remove();
                        }
                    } else if (Version.has1_12Support()) { // TODO: Ideally check key, if minecraft: domain, ignore.
                        originalRecipes.add(recipe);
                    }
                }
            } catch (NullPointerException | NoSuchElementException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        if (Version.has1_12Support()) {
            Bukkit.resetRecipes();
            Vanilla.init();

            for (Recipe newRecipe : originalRecipes) {
                try {
                    if (!Vanilla.isSpecialRecipe(newRecipe)) {
                        Bukkit.addRecipe(newRecipe);
                    }
                } catch (Exception e) { /* for v1.12, we'll reload to preserve ordering, then blindly try to reapply recipes. */ }
            }
        }
    }

    /**
     * Remove all recipes from the server except special ones
     */
    public static void removeAllButSpecialRecipes() {
        Iterator<Recipe> iterator = NMSVersionHandler.getRecipeIterator().getIterator();
        Recipe recipe;

        while (iterator.hasNext()) {
            try {
                recipe = iterator.next();

                if (recipe != null) {
                    if (isSpecialRecipe(recipe)) {
                        continue;
                    }

                    iterator.remove();
                }
            } catch (NullPointerException | NoSuchElementException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        // In 1.12, special iterator will automatically clean up when it runs out of recipes to remove.
    }

    /**
     * Adds all recipes that already existed when the plugin was enabled.
     */
    public static void restoreInitialRecipes() {
        // TODO: In 1.12, this will fail.
        for (Entry<BaseRecipe, RMCRecipeInfo> entry : initialRecipes.entrySet()) {
            // TODO maybe check if recipe is already in server?
            Bukkit.addRecipe(entry.getKey().getBukkitRecipe(true));
        }
    }

    /**
     * Adds all recipes except special that already existed when the plugin was enabled.
     */
    public static void restoreAllButSpecialRecipes() {
        // TODO: In 1.12, this will fail.
        for (Entry<BaseRecipe, RMCRecipeInfo> entry : initialRecipes.entrySet()) {
            BaseRecipe recipe = entry.getKey();

            if (recipe instanceof FuelRecipe) {
                RecipeManager.getRecipes().indexFuels.put(((FuelRecipe) recipe).getIndexString(), (FuelRecipe) recipe);
            } else {
                if (recipe.isVanillaSpecialRecipe()) {
                    continue;
                }
                Recipe bukkitRecipe = recipe.getBukkitRecipe(true);

                if (bukkitRecipe != null) {
                    if (isSpecialRecipe(bukkitRecipe)) {
                        continue;
                    }

                    // TODO maybe check if recipe is already in server ?
                    Bukkit.addRecipe(bukkitRecipe);
                }
            }
        }
    }

    /**
     * @return a copy of the initial recipes map.
     */
    public static Map<BaseRecipe, RMCRecipeInfo> getInitialRecipes() {
        return ImmutableMap.copyOf(initialRecipes);
    }

    public static boolean isSpecialRecipe(Recipe recipe) {
        boolean isSpecial = false;

        if (Version.has1_12Support()) {
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shaped = (ShapedRecipe) recipe;

                NamespacedKey key = shaped.getKey();
                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "mapextending": // 1.12 only
                        case "map_extending": // 1.13+

                        case "tippedarrow": // 1.12 only
                        case "tipped_arrow": // 1.13+
                            isSpecial = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

                NamespacedKey key = shapeless.getKey();
                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "armordye": // 1.12 only
                        case "armor_dye": // 1.13+

                        case "bannerduplicate": // 1.12 only
                        case "banner_duplicate": // 1.13+

                        case "bookcloning": // 1.12 only
                        case "book_cloning": // 1.13+

                        case "firework_star": // 1.13+

                        case "firework_star_fade": // 1.13+

                        case "fireworks": // 1.12 only
                        case "firework_rocket": // 1.13+

                        case "mapcloning": // 1.12 only
                        case "map_cloning": // 1.13+

                        case "repairitem": // 1.12 only
                        case "repair_item": // 1.13+

                        case "shulkerboxcoloring": // 1.12 only
                        case "shulker_box_coloring": // 1.13+

                        case "shielddecoration": // 1.12 only
                        case "shield_decoration": // 1.13+

                        case "banneraddpattern": // 1.12 only
                        case "banner_add_pattern": // 1.13 only

                        case "suspicious_stew": // 1.14
                            isSpecial = true;
                            break;

                        default:
                            break;
                    }
                }
            }
        } else if (recipe != null) {
            ItemStack result = recipe.getResult();

            if (result.equals(RECIPE_LEATHERDYE) || result.equals(RECIPE_FIREWORKS) || result.equals(RECIPE_MAPCLONE) || result.equals(RECIPE_MAPEXTEND) || result.equals(RECIPE_MAPEXTEND_1_11) || result.equals(RECIPE_BOOKCLONE)) {
                isSpecial = true;
            }

            if (result.equals(RECIPE_BANNER)) {
                isSpecial = true;
            }

            if (result.equals(RECIPE_SHIELD_BANNER)) {
                isSpecial = true;
            }

            if (recipe instanceof ShapelessRecipe && result.equals(RECIPE_REPAIR)) {
                isSpecial = true;
            }

            if (result.equals(RECIPE_TIPPED_ARROW)) {
                isSpecial = true;
            }

            if (result.getType().equals(Material.AIR)) {
                isSpecial = true;
            }
        }

        return isSpecial;
    }

    public static boolean recipeMatchesArmorDye(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "armordye": // 1.12 only
                        case "armor_dye": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (recipe.getResult().getType() == Material.AIR && result.getType() == Material.LEATHER_HELMET) {
                matches = true;
            } else if (recipe.getResult().equals(RECIPE_LEATHERDYE)) {
                matches = true;
            }
        }

        return matches;
    }

    public static boolean recipeMatchesMapCloning(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "mapcloning": // 1.12 only
                        case "map_cloning": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (Version.has1_11Support()) {
                if (recipe.getResult().getType() == Material.AIR && result.getType().equals(Material.MAP) && result.getAmount() > 1) {
                    matches = true;
                }
            } else if (recipe.getResult().equals(Vanilla.RECIPE_MAPCLONE)) {
                matches = true;
            }
        }

        return matches;
    }

    public static boolean recipeMatchesMapExtending(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shaped = (ShapedRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shaped.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "mapextending": // 1.12 only
                        case "map_extending": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (Version.has1_11Support()) {
                if (recipe.getResult().equals(Vanilla.RECIPE_MAPEXTEND_1_11)) {
                    matches = true;
                }
            } else if (recipe.getResult().equals(Vanilla.RECIPE_MAPEXTEND)) {
                matches = true;
            }
        }

        return matches;
    }

    public static boolean recipeMatchesFireworkRocket(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "fireworks": // 1.12 only
                        case "firework_rocket": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (Version.has1_11Support()) {
                if (recipe.getResult().getType() == Material.AIR && result.getType() == Material.getMaterial("FIREWORK")) {
                    matches = true;
                }
            } else if (recipe.getResult().equals(Vanilla.RECIPE_FIREWORKS) && result.getType() != Material.getMaterial("FIREWORK_CHARGE")) {
                matches = true;
            }
        }

        return matches;
    }

    public static boolean recipeMatchesFireworkStar(Recipe recipe, ItemStack result, ItemStack[] matrix) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_13BasicSupport()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    if (key.getKey().equals("firework_star")) {
                        matches = true;
                    }
                }
            } else {
                if (result.getType() == Material.getMaterial("FIREWORK_CHARGE")) {
                    if (recipe.getResult().getType() == Material.AIR) {
                        matches = true;
                    }

                    if (recipe.getResult().equals(Vanilla.RECIPE_FIREWORKS)) {
                        matches = true;
                    }
                }

                if (matches) {
                    // Unmatch on firework star fade recipe
                    boolean hasFireworkStar = false;
                    for (ItemStack item : matrix) {
                        if (item != null && item.getType() == Material.getMaterial("FIREWORK_CHARGE")) {
                            hasFireworkStar = true;
                            break;
                        }
                    }
                    if (hasFireworkStar) {
                        matches = false;
                    }
                }
            }
        }

        return matches;
    }

    public static boolean recipeMatchesFireworkStarFade(Recipe recipe, ItemStack result, ItemStack[] matrix) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_13BasicSupport()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    if ("firework_star_fade".equals(key.getKey())) { // 1.13+
                        matches = true;
                    }
                }
            } else {
                if (result.getType() == Material.getMaterial("FIREWORK_CHARGE")) {
                    if (recipe.getResult().getType() == Material.AIR) {
                        matches = true;
                    }

                    if (recipe.getResult().equals(Vanilla.RECIPE_FIREWORKS)) {
                        matches = true;
                    }
                }

                if (matches) {
                    // Unmatch on firework star recipe
                    boolean hasFireworkStar = false;
                    for (ItemStack item : matrix) {
                        if (item != null && item.getType() == Material.getMaterial("FIREWORK_CHARGE")) {
                            hasFireworkStar = true;
                            break;
                        }
                    }
                    if (!hasFireworkStar) {
                        matches = false;
                    }
                }
            }
        }

        return matches;
    }

    public static boolean recipeMatchesBookCloning(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "bookcloning": // 1.12 only
                        case "book_cloning": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (Version.has1_11Support()) {
                if (result.getType().equals(Material.WRITTEN_BOOK)) {
                    matches = true;
                }
            } else if (recipe.getResult().equals(Vanilla.RECIPE_BOOKCLONE)) {
                matches = true;
            // 1.10
            } else if (recipe.getResult().getType() == Material.WRITTEN_BOOK && recipe.getResult().getAmount() == 0) {
                matches = true;
            }
        }

        return matches;
    }

    // Replaced by loom recipes in 1.14
    public static boolean recipeMatchesBannerAddPattern(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "banneraddpattern": // 1.12 only
                        case "banner_add_pattern": // 1.13 only
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (recipe.getResult().getType() == Material.AIR && result.getType() == Material.getMaterial("BANNER") || recipe.getResult().equals(Vanilla.RECIPE_BANNER)) {
                List<ItemStack> ingredients = shapeless.getIngredientList();
                if (ingredients.size() == 1 && ingredients.get(0).getType() == Material.getMaterial("BANNER")) {
                    matches = true;
                }
            }
        }

        return matches;
    }

    public static boolean recipeMatchesBannerDuplicate(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "bannerduplicate": // 1.12 only
                        case "banner_duplicate": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if ((recipe.getResult().getType() == Material.AIR && result.getType() == Material.getMaterial("BANNER")) || recipe.getResult().equals(Vanilla.RECIPE_BANNER)) {
                List<ItemStack> ingredients = shapeless.getIngredientList();

                if (ingredients.size() == 1) {
                    if (Version.has1_11Support() && ingredients.get(0).getType() == Material.AIR) {
                        matches = true;
                    // 1.10
                    } else if (ingredients.get(0).getType() == Material.getMaterial("INK_SACK")) {
                        matches = true;
                    }
                }
            }
        }

        return matches;
    }

    // 1.9+
    public static boolean recipeMatchesShieldDecoration(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "shielddecoration": // 1.12 only
                        case "shield_decoration": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (Version.has1_11Support()) {
                if (result.getType().equals(Material.SHIELD)) {
                    matches = true;
                }
            } else if (Version.has1_9Support() && recipe.getResult().equals(Vanilla.RECIPE_SHIELD_BANNER)) {
                matches = true;
            }
        }

        return matches;
    }

    // 1.9+
    public static boolean recipeMatchesTippedArrow(Recipe recipe) {
        boolean matches = false;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shaped = (ShapedRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shaped.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "tippedarrow": // 1.12 only
                        case "tipped_arrow": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (recipe.getResult().equals(Vanilla.RECIPE_TIPPED_ARROW)) {
                matches = true;
            // 1.10
            } else if (Version.has1_9Support() && recipe.getResult().getType() == Material.TIPPED_ARROW && recipe.getResult().getAmount() == 8) {
                matches = true;
            }
        }

        return matches;
    }

    // 1.11+
    public static boolean recipeMatchesShulkerDye(Recipe recipe, ItemStack result) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_12Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    switch (key.getKey()) {
                        case "shulkerboxcoloring": // 1.12 only
                        case "shulker_box_coloring": // 1.13+
                            matches = true;
                            break;

                        default:
                            break;
                    }
                }
            } else if (Version.has1_11Support() && ToolsItem.isShulkerBox(result.getType())) {
                matches = true;
            }
        }

        return matches;
    }

    // 1.14+
    public static boolean recipeMatchesSuspiciousStew(Recipe recipe) {
        boolean matches = false;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

            if (Version.has1_14Support()) {
                NamespacedKey key = shapeless.getKey();

                if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                    if (key.getKey().equals("suspicious_stew")) {
                        matches = true;
                    }
                }
            }
        }

        return matches;
    }
}
