package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeInfo.RecipeOwner;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.cooking.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMBlastingRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMSmokingRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.smithing.RMSmithing1_19_4TransformRecipe;
import haveric.recipeManager.recipes.smithing.RMSmithingRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.*;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
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

    /**
     * Default time a furnace recipe burns for.<br>
     * This is a game constant.
     */
    public static final float FURNACE_RECIPE_TIME = 10f;
    public static final float BLASTING_RECIPE_TIME = 5f;
    public static final float SMOKER_RECIPE_TIME = 5f;
    public static final float CAMPFIRE_RECIPE_TIME = 30f;
    public static final int BREWING_RECIPE_DEFAULT_TICKS = 400;

    private static final RMCRecipeInfo info = new RMCRecipeInfo(RecipeOwner.MINECRAFT, null); // shared info

    protected static void init() {
        clean();

        initFuels();
        initCompostRecipes();
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

        addFuelRecipe(Material.LADDER, 15);
        addFuelRecipe(Material.BOW, 10);
        addFuelRecipe(Material.FISHING_ROD, 15);
        addFuelRecipe(Material.BOWL, 5);

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

        addFuelRecipe(Material.ACACIA_PLANKS, 15);
        addFuelRecipe(Material.BIRCH_PLANKS, 15);
        addFuelRecipe(Material.DARK_OAK_PLANKS, 15);
        addFuelRecipe(Material.JUNGLE_PLANKS, 15);
        addFuelRecipe(Material.OAK_PLANKS, 15);
        addFuelRecipe(Material.SPRUCE_PLANKS, 15);

        // New fuels in 1.13
        addFuelRecipe(Material.DRIED_KELP_BLOCK, 200);

        addFuelRecipe(Material.ACACIA_SIGN, 10);
        addFuelRecipe(Material.BIRCH_SIGN, 10);
        addFuelRecipe(Material.JUNGLE_SIGN, 10);
        addFuelRecipe(Material.OAK_SIGN, 10);
        addFuelRecipe(Material.SPRUCE_SIGN, 10);
        addFuelRecipe(Material.DARK_OAK_SIGN, 10);

        // New fuels in 1.14
        addFuelRecipe(Material.CARTOGRAPHY_TABLE, 15);
        addFuelRecipe(Material.FLETCHING_TABLE, 15);
        addFuelRecipe(Material.SMITHING_TABLE, 15);
        addFuelRecipe(Material.LECTERN, 15);
        addFuelRecipe(Material.COMPOSTER, 15);
        addFuelRecipe(Material.BARREL, 15);
        addFuelRecipe(Material.BAMBOO, 2.5f);
        addFuelRecipe(Material.DEAD_BUSH, 5);

        if (!Version.has1_19Support()) {
            addFuelRecipe(Material.SCAFFOLDING, 20);
        }
        addFuelRecipe(Material.ACACIA_BOAT, 60);
        addFuelRecipe(Material.BIRCH_BOAT, 60);
        addFuelRecipe(Material.DARK_OAK_BOAT, 60);
        addFuelRecipe(Material.JUNGLE_BOAT, 60);
        addFuelRecipe(Material.OAK_BOAT, 60);
        addFuelRecipe(Material.SPRUCE_BOAT, 60);

        // 1.17
        addFuelRecipe(Material.AZALEA, 5);
        addFuelRecipe(Material.FLOWERING_AZALEA, 5);

        if (Version.has1_19Support()) {
            addFuelRecipe(Material.MANGROVE_ROOTS, 15);
            addFuelRecipe(Material.SCAFFOLDING, 2.5f);

            addFuelRecipe(Material.MANGROVE_BOAT, 60);
            addFuelRecipe(Material.MANGROVE_BUTTON, 5);
            addFuelRecipe(Material.MANGROVE_DOOR, 10);
            addFuelRecipe(Material.MANGROVE_FENCE, 15);
            addFuelRecipe(Material.MANGROVE_FENCE_GATE, 15);
            addFuelRecipe(Material.MANGROVE_LOG, 15);
            addFuelRecipe(Material.MANGROVE_PLANKS, 15);
            addFuelRecipe(Material.MANGROVE_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.MANGROVE_SIGN, 10);
            addFuelRecipe(Material.MANGROVE_SLAB, 7.5f);
            addFuelRecipe(Material.MANGROVE_STAIRS, 15);
            addFuelRecipe(Material.MANGROVE_TRAPDOOR, 15);
            addFuelRecipe(Material.MANGROVE_WOOD, 15);

            addFuelRecipe(Material.MANGROVE_PROPAGULE, 5); // sapling

            addFuelRecipe(Material.STRIPPED_MANGROVE_LOG, 15);
            addFuelRecipe(Material.STRIPPED_MANGROVE_WOOD, 15);

            addFuelRecipe(Material.ACACIA_CHEST_BOAT, 60);
            addFuelRecipe(Material.BIRCH_CHEST_BOAT, 60);
            addFuelRecipe(Material.DARK_OAK_CHEST_BOAT, 60);
            addFuelRecipe(Material.JUNGLE_CHEST_BOAT, 60);
            addFuelRecipe(Material.MANGROVE_CHEST_BOAT, 60);
            addFuelRecipe(Material.OAK_CHEST_BOAT, 60);
            addFuelRecipe(Material.SPRUCE_CHEST_BOAT, 60);
        }

        if (Version.has1_19_3Support()) {
            addFuelRecipe(Material.CHISELED_BOOKSHELF, 15);

            addFuelRecipe(Material.ACACIA_HANGING_SIGN, 40);
            addFuelRecipe(Material.BAMBOO_HANGING_SIGN, 40);
            addFuelRecipe(Material.BIRCH_HANGING_SIGN, 40);
            addFuelRecipe(Material.DARK_OAK_HANGING_SIGN, 40);
            addFuelRecipe(Material.JUNGLE_HANGING_SIGN, 40);
            addFuelRecipe(Material.MANGROVE_HANGING_SIGN, 40);
            addFuelRecipe(Material.OAK_HANGING_SIGN, 40);
            addFuelRecipe(Material.SPRUCE_HANGING_SIGN, 40);

            addFuelRecipe(Material.BAMBOO_BLOCK, 15);
            addFuelRecipe(Material.BAMBOO_BUTTON, 5);
            addFuelRecipe(Material.BAMBOO_CHEST_RAFT, 60);
            addFuelRecipe(Material.BAMBOO_DOOR, 10);
            addFuelRecipe(Material.BAMBOO_FENCE, 15);
            addFuelRecipe(Material.BAMBOO_FENCE_GATE, 15);
            addFuelRecipe(Material.BAMBOO_MOSAIC, 15);
            addFuelRecipe(Material.BAMBOO_MOSAIC_SLAB, 15);
            addFuelRecipe(Material.BAMBOO_MOSAIC_STAIRS, 15);
            addFuelRecipe(Material.BAMBOO_PLANKS, 15);
            addFuelRecipe(Material.BAMBOO_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.BAMBOO_RAFT, 60);
            addFuelRecipe(Material.BAMBOO_SIGN, 10);
            addFuelRecipe(Material.BAMBOO_SLAB, 15);
            addFuelRecipe(Material.BAMBOO_STAIRS, 15);
            addFuelRecipe(Material.BAMBOO_TRAPDOOR, 15);
            addFuelRecipe(Material.STRIPPED_BAMBOO_BLOCK, 15);
        }

        if (Version.has1_19_4Support()) {
            addFuelRecipe(Material.CHERRY_BOAT, 60);
            addFuelRecipe(Material.CHERRY_BUTTON, 5);
            addFuelRecipe(Material.CHERRY_DOOR, 10);
            addFuelRecipe(Material.CHERRY_FENCE, 15);
            addFuelRecipe(Material.CHERRY_FENCE_GATE, 15);
            addFuelRecipe(Material.CHERRY_HANGING_SIGN, 40);
            addFuelRecipe(Material.CHERRY_LOG, 15);
            addFuelRecipe(Material.CHERRY_PLANKS, 15);
            addFuelRecipe(Material.CHERRY_PRESSURE_PLATE, 15);
            addFuelRecipe(Material.CHERRY_SAPLING, 5);
            addFuelRecipe(Material.CHERRY_SIGN, 10);
            addFuelRecipe(Material.CHERRY_SLAB, 15);
            addFuelRecipe(Material.CHERRY_STAIRS, 15);
            addFuelRecipe(Material.CHERRY_TRAPDOOR, 15);
            addFuelRecipe(Material.CHERRY_WOOD, 15);
            addFuelRecipe(Material.STRIPPED_CHERRY_LOG, 15);
            addFuelRecipe(Material.STRIPPED_CHERRY_WOOD, 15);
        }

        // Index fuel recipes
        for (BaseRecipe recipe : initialRecipes.keySet()) {
            if (recipe instanceof FuelRecipe) {
                RecipeManager.getRecipes().addRecipeToQuickfindIndex(RMCRecipeType.FUEL.getDirective(), recipe);
            }
        }
    }

    private static void addFuelRecipe(Material material, float burnTime) {
        initialRecipes.put(new FuelRecipe(material, burnTime), info);
    }

    private static void initCompostRecipes() {
        addCompostRecipe(Material.BEETROOT_SEEDS, 30);
        addCompostRecipe(Material.DRIED_KELP, 30);

        if (Supports.shortGrassMaterial()) {
            addCompostRecipe(Material.SHORT_GRASS, 30);
        } else {
            addCompostRecipe(Material.getMaterial("GRASS"), 30);
        }
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

        addCompostRecipe(Material.WEEPING_VINES, 50);
        addCompostRecipe(Material.TWISTING_VINES, 50);
        addCompostRecipe(Material.NETHER_SPROUTS, 50);

        addCompostRecipe(Material.CRIMSON_ROOTS, 65);
        addCompostRecipe(Material.WARPED_ROOTS, 65);

        addCompostRecipe(Material.CRIMSON_FUNGUS, 65);
        addCompostRecipe(Material.WARPED_FUNGUS, 65);
        addCompostRecipe(Material.NETHER_WART, 65);
        addCompostRecipe(Material.SHROOMLIGHT, 65);

        addCompostRecipe(Material.NETHER_WART_BLOCK, 85);
        addCompostRecipe(Material.WARPED_WART_BLOCK, 85);

        // 1.17
        addCompostRecipe(Material.AZALEA_LEAVES, 30);
        addCompostRecipe(Material.GLOW_BERRIES, 30);
        addCompostRecipe(Material.HANGING_ROOTS, 30);
        addCompostRecipe(Material.MOSS_CARPET, 30);
        addCompostRecipe(Material.SMALL_DRIPLEAF, 30);

        addCompostRecipe(Material.FLOWERING_AZALEA_LEAVES , 50);
        addCompostRecipe(Material.GLOW_LICHEN, 50);

        addCompostRecipe(Material.AZALEA, 65);
        addCompostRecipe(Material.BIG_DRIPLEAF, 65);
        addCompostRecipe(Material.MOSS_BLOCK, 65);
        addCompostRecipe(Material.SPORE_BLOSSOM, 65);

        addCompostRecipe(Material.FLOWERING_AZALEA, 85);

        if (Version.has1_19Support()) {
            addCompostRecipe(Material.MANGROVE_LEAVES, 30);
            addCompostRecipe(Material.MANGROVE_PROPAGULE, 30);
            addCompostRecipe(Material.MANGROVE_ROOTS, 30);
        }

        if (Version.has1_19_4Support()) {
            addCompostRecipe(Material.TORCHFLOWER, 85);
            addCompostRecipe(Material.TORCHFLOWER_SEEDS, 30);
            addCompostRecipe(Material.PINK_PETALS, 30);

            addCompostRecipe(Material.CHERRY_LEAVES, 30);
            addCompostRecipe(Material.CHERRY_SAPLING, 30);
        }

        if (Version.has1_20Support()) {
            addCompostRecipe(Material.PITCHER_PLANT, 85);
            addCompostRecipe(Material.PITCHER_POD, 30);
        }
    }

    private static void addCompostRecipe(Material ingredient, double chance) {
        CompostRecipe recipe = new CompostRecipe(ingredient, chance);
        initialRecipes.put(recipe, info);
        RecipeManager.getRecipes().addRecipeToQuickfindIndex(RMCRecipeType.COMPOST.getDirective(), recipe);
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
                    recipe = new CraftRecipe((ShapedRecipe) r);
                } else if (r instanceof ShapelessRecipe) {
                    recipe = new CombineRecipe((ShapelessRecipe) r);
                } else if (r instanceof FurnaceRecipe) {
                    recipe = new RMFurnaceRecipe((FurnaceRecipe) r);
                } else if (r instanceof BlastingRecipe) {
                    recipe = new RMBlastingRecipe((BlastingRecipe) r);
                } else if (r instanceof SmokingRecipe) {
                    recipe = new RMSmokingRecipe((SmokingRecipe) r);
                } else if (r instanceof CampfireRecipe) {
                    recipe = new RMCampfireRecipe((CampfireRecipe) r);
                } else if (r instanceof StonecuttingRecipe) {
                    recipe = new RMStonecuttingRecipe((StonecuttingRecipe) r);
                } else if (Supports.experimental1_20() && r instanceof SmithingTransformRecipe) {
                    recipe = new RMSmithing1_19_4TransformRecipe((SmithingTransformRecipe) r);
                } else if (r instanceof SmithingRecipe) {
                    recipe = new RMSmithingRecipe((SmithingRecipe) r);
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
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
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
        if (recipe instanceof CraftRecipe) {
            return removeCraftRecipe((CraftRecipe) recipe);
        }

        if (recipe instanceof CombineRecipe) {
            return removeCombineRecipe((CombineRecipe) recipe);
        }

        if (recipe instanceof RMFurnaceRecipe) {
            return removeSmeltRecipe((RMFurnaceRecipe) recipe);
        }

        if (recipe instanceof RMBlastingRecipe) {
            return removeBlastingRecipe((RMBlastingRecipe) recipe);
        }

        if (recipe instanceof RMSmokingRecipe) {
            return removeSmokingRecipe((RMSmokingRecipe) recipe);
        }

        if (recipe instanceof RMCampfireRecipe) {
            return removeCampfireRecipe((RMCampfireRecipe) recipe);
        }

        if (recipe instanceof RMStonecuttingRecipe) {
            return removeStonecuttingRecipe((RMStonecuttingRecipe) recipe);
        }

        if (recipe instanceof RMSmithing1_19_4TransformRecipe) {
            return removeSmithing1_19_4TransformRecipe((RMSmithing1_19_4TransformRecipe) recipe);
        }

        if (recipe instanceof RMSmithingRecipe) {
            return removeSmithingRecipe((RMSmithingRecipe) recipe);
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
    private static Recipe removeSmeltRecipe(RMFurnaceRecipe recipe) {
        RecipeChoice choice = recipe.getIngredientChoice();
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return removeFurnaceRecipe(new ItemStack(materialChoice.getChoices().get(0)));
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return removeFurnaceRecipe(exactChoice.getChoices().get(0));
        } else {
            return null;
        }
    }

    private static Recipe removeFurnaceRecipe(ItemStack ingredient) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof FurnaceRecipe) {
                    if (toolsRecipe.matchesFurnace(r, ingredient)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    private static Recipe removeBlastingRecipe(RMBlastingRecipe recipe) {
        RecipeChoice choice = recipe.getIngredientChoice();
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return removeBlastingRecipe(new ItemStack(materialChoice.getChoices().get(0)));
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return removeBlastingRecipe(exactChoice.getChoices().get(0));
        } else {
            return null;
        }
    }

    private static Recipe removeBlastingRecipe(ItemStack ingredient) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof BlastingRecipe) {
                    if (toolsRecipe.matchesBlasting(r, ingredient)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    private static Recipe removeSmokingRecipe(RMSmokingRecipe recipe) {
        RecipeChoice choice = recipe.getIngredientChoice();
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return removeSmokingRecipe(new ItemStack(materialChoice.getChoices().get(0)));
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return removeSmokingRecipe(exactChoice.getChoices().get(0));
        } else {
            return null;
        }
    }

    private static Recipe removeSmokingRecipe(ItemStack ingredient) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof SmokingRecipe) {
                    if (toolsRecipe.matchesSmoking(r, ingredient)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    private static Recipe removeCampfireRecipe(RMCampfireRecipe recipe) {
        RecipeChoice choice = recipe.getIngredientChoice();
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return removeCampfireRecipe(new ItemStack(materialChoice.getChoices().get(0)));
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return removeCampfireRecipe(exactChoice.getChoices().get(0));
        } else {
            return null;
        }
    }

    private static Recipe removeCampfireRecipe(ItemStack ingredient) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof CampfireRecipe) {
                    if (toolsRecipe.matchesCampfire(r, ingredient)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    private static Recipe removeStonecuttingRecipe(RMStonecuttingRecipe recipe) {
        RecipeChoice choice = recipe.getIngredientChoice();
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return removeStonecuttingRecipe(new ItemStack(materialChoice.getChoices().get(0)), recipe.getResult());
        } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return removeStonecuttingRecipe(exactChoice.getChoices().get(0), recipe.getResult());
        } else {
            return null;
        }
    }

    private static Recipe removeStonecuttingRecipe(ItemStack ingredient, ItemStack result) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof StonecuttingRecipe) {
                    if (toolsRecipe.matchesStonecutting(r, ingredient, result)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    private static Recipe removeSmithing1_19_4TransformRecipe(RMSmithing1_19_4TransformRecipe recipe) {
        ItemStack templateIngredient;
        ItemStack baseIngredient;
        ItemStack addIngredient;

        RecipeChoice templateChoice = recipe.getTemplateIngredient();
        if (templateChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            templateIngredient = new ItemStack(materialChoice.getChoices().get(0));
        } else if (templateChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            templateIngredient = exactChoice.getChoices().get(0);
        } else {
            return null;
        }

        RecipeChoice baseChoice = recipe.getPrimaryIngredient();
        if (baseChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            baseIngredient = new ItemStack(materialChoice.getChoices().get(0));
        } else if (baseChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            baseIngredient = exactChoice.getChoices().get(0);
        } else {
            return null;
        }

        RecipeChoice addChoice = recipe.getSecondaryIngredient();
        if (addChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            addIngredient = new ItemStack(materialChoice.getChoices().get(0));
        } else if (addChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            addIngredient = exactChoice.getChoices().get(0);
        } else {
            return null;
        }

        return removeSmithingTransformRecipe(templateIngredient, baseIngredient, addIngredient);
    }

    private static Recipe removeSmithingTransformRecipe(ItemStack templateIngredient, ItemStack baseIngredient, ItemStack addIngredient) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof SmithingTransformRecipe) {
                    if (toolsRecipe.matchesSmithingTransform(r, templateIngredient, baseIngredient, addIngredient)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    private static Recipe removeSmithingRecipe(RMSmithingRecipe recipe) {
        RecipeChoice baseChoice = recipe.getPrimaryIngredient();
        ItemStack baseIngredient;
        ItemStack addIngredient;

        if (baseChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            baseIngredient = new ItemStack(materialChoice.getChoices().get(0));
        } else if (baseChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            baseIngredient = exactChoice.getChoices().get(0);
        } else {
            return null;
        }

        RecipeChoice addChoice = recipe.getSecondaryIngredient();
        if (addChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            addIngredient = new ItemStack(materialChoice.getChoices().get(0));
        } else if (addChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            addIngredient = exactChoice.getChoices().get(0);
        } else {
            return null;
        }

        return removeSmithingRecipe(baseIngredient, addIngredient);
    }

    private static Recipe removeSmithingRecipe(ItemStack baseIngredient, ItemStack addIngredient) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        BaseToolsRecipe toolsRecipe = VersionHandler.getToolsRecipe();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof SmithingRecipe) {
                    if (toolsRecipe.matchesSmithing(r, baseIngredient, addIngredient)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    /**
     * Removes a RecipeManager craft recipe on the <b>server</b>
     *
     * @param recipe RecipeManager recipe
     *
     * @return replaced recipe or null if not found
     */
    public static Recipe removeCraftRecipe(CraftRecipe recipe) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe sr) {
                    if (VersionHandler.getToolsRecipe().matchesShaped(sr, recipe.getChoicePattern(), recipe.getIngredientsChoiceMap())) {
                        iterator.remove();

                        return sr;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }

    /**
     * Removes a RecipeManager combine recipe from the <b>server</b>
     *
     * @param recipe RecipeManager recipe
     *
     * @return replaced recipe or null if not found
     */
    public static Recipe removeCombineRecipe(CombineRecipe recipe) {
        RecipeIterator recipeIterator = VersionHandler.getRecipeIterator();
        Iterator<Recipe> iterator = recipeIterator.getIterator();
        Recipe r;

        List<RecipeChoice> ingredientChoices = recipe.getIngredientChoiceList();
        List<List<Material>> ingredientChoiceList = new ArrayList<>();

        for (RecipeChoice choice : ingredientChoices) {
            if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                ingredientChoiceList.add(materialChoice.getChoices());
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) ingredientChoices;
                List<ItemStack> items = exactChoice.getChoices();
                List<Material> materials = new ArrayList<>();
                for (ItemStack item : items) {
                    materials.add(item.getType());
                }

                ingredientChoiceList.add(materials);
            }
        }

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapelessRecipe) {
                    if (VersionHandler.getToolsRecipe().matchesShapeless(r, ingredientChoiceList)) {
                        iterator.remove();

                        return r;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
            } catch (NoSuchElementException e) {
                // Vanilla datapack is disabled
            }
        }

        return null;
    }
    
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

        while (iterator.hasNext()) {
            try {
                recipe = iterator.next();

                if (recipe != null) {
                    if (!RecipeManager.getRecipes().isCustomRecipe(recipe)) { // TODO: Ideally check key, if minecraft: domain, ignore.
                        originalRecipes.add(recipe);
                    }
                }
            } catch (NullPointerException | NoSuchElementException e) {
                // Catch any invalid Bukkit recipes
            }
        }

        Bukkit.resetRecipes();
        Vanilla.init();

        for (Recipe newRecipe : originalRecipes) {
            try {
                if (!Vanilla.isSpecialRecipe(newRecipe)) {
                    Bukkit.addRecipe(newRecipe);
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * Remove all recipes from the server except special ones
     */
    public static void removeAllButSpecialRecipes() {
        Iterator<Recipe> iterator = VersionHandler.getRecipeIterator().getIterator();
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
    }

    /**
     * @return a copy of the initial recipes map.
     */
    public static Map<BaseRecipe, RMCRecipeInfo> getInitialRecipes() {
        return ImmutableMap.copyOf(initialRecipes);
    }

    public static boolean isSpecialRecipe(Recipe recipe) {
        boolean isSpecial = false;

        if (recipe instanceof Keyed keyedRecipe) {
            NamespacedKey key = keyedRecipe.getKey();

            if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
                switch (key.getKey()) {
                    case "map_extending": // 1.13+
                    case "tipped_arrow": // 1.13+
                    case "armor_dye": // 1.13+
                    case "banner_duplicate": // 1.13+
                    case "book_cloning": // 1.13+
                    case "firework_star": // 1.13+
                    case "firework_star_fade": // 1.13+
                    case "firework_rocket": // 1.13+
                    case "map_cloning": // 1.13+
                    case "repair_item": // 1.13+
                    case "shulker_box_coloring": // 1.13+
                    case "shield_decoration": // 1.13+
                    case "banner_add_pattern": // 1.13 only

                    case "suspicious_stew": // 1.14

                    // 1.19.4 (1.20 experimental)
                    case "decorated_pot":
                    case "coast_armor_trim_smithing_template_smithing_trim":
                    case "dune_armor_trim_smithing_template_smithing_trim":
                    case "eye_armor_trim_smithing_template_smithing_trim":
                    case "rib_armor_trim_smithing_template_smithing_trim":
                    case "sentry_armor_trim_smithing_template_smithing_trim":
                    case "snout_armor_trim_smithing_template_smithing_trim":
                    case "spire_armor_trim_smithing_template_smithing_trim":
                    case "tide_armor_trim_smithing_template_smithing_trim":
                    case "vex_armor_trim_smithing_template_smithing_trim":
                    case "ward_armor_trim_smithing_template_smithing_trim":
                    case "wild_armor_trim_smithing_template_smithing_trim":

                    // 1.20
                    case "host_armor_trim_smithing_template_smithing_trim":
                    case "raiser_armor_trim_smithing_template_smithing_trim":
                    case "shaper_armor_trim_smithing_template_smithing_trim":
                    case "silence_armor_trim_smithing_template_smithing_trim":
                    case "wayfinder_armor_trim_smithing_template_smithing_trim":

                    // 1.20.5
                    case "bolt_armor_trim_smithing_template_smithing_trim":
                    case "flow_armor_trim_smithing_template_smithing_trim":
                        isSpecial = true;
                        break;

                    default:
                        break;
                }
            }
        }

        return isSpecial;
    }

    public static boolean recipeMatchesTrimKey(Recipe recipe, String trim) {
        return recipeMatchesKey(recipe, trim + "_armor_trim_smithing_template_smithing_trim");
    }

    public static boolean recipeMatchesKey(Recipe recipe, String keyToMatch) {
        Keyed keyedRecipe = (Keyed) recipe;
        NamespacedKey key = keyedRecipe.getKey();

        if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
            return key.getKey().equals(keyToMatch);
        }

        return false;
    }
}
