package haveric.recipeManager;

import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CombineRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.FuelRecipe;
import haveric.recipeManager.recipes.RecipeInfo;
import haveric.recipeManager.recipes.RecipeInfo.RecipeOwner;
import haveric.recipeManager.recipes.SmeltRecipe;
import haveric.recipeManager.tools.Tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.google.common.collect.ImmutableMap;

/**
 * Control for bukkit recipes to avoid confusion with RecipeManager's recipes
 */
public class Vanilla {
    protected static Map<BaseRecipe, RecipeInfo> initialRecipes = new HashMap<BaseRecipe, RecipeInfo>();

    /**
     * Leather dyeing's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_LEATHERDYE = new ItemStack(Material.LEATHER_HELMET, 0, (short) 0);

    /**
     * Map cloning's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_MAPCLONE = new ItemStack(Material.EMPTY_MAP, 0, (short) -1);

    /**
     * Map extending's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_MAPEXTEND = new ItemStack(Material.EMPTY_MAP, 0, (short) 0);

    /**
     * Fireworks' special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_FIREWORKS = new ItemStack(Material.FIREWORK, 0, (short) 0);

    /**
     * Item repair special recipe result (SHAPELESS Recipe)
     */
    public static final ItemStack RECIPE_REPAIR = new ItemStack(Material.LEATHER_HELMET, 1, (short) 0);

    /**
     * Banner special recipe result
     */
    public static final ItemStack RECIPE_BANNER = new ItemStack(Material.BANNER, 0, (short) 0);

    /**
     * Book cloning's special recipe
     */
    public static final ItemStack RECIPE_BOOKCLONE = new ItemStack(Material.WRITTEN_BOOK, 0, (short) -1);


    /**
     * Default time a furnace recipe burns for.<br>
     * This is a game constant.
     */
    public static final float FURNACE_RECIPE_TIME = 10f;

    /**
     * The data value wildcard for recipe ingredients.<br>
     * If an ingredient has this data value its data value will be ignored.
     */
    public static final short DATA_WILDCARD = Short.MAX_VALUE;

    protected static void init() {
        clean();

        RecipeInfo info = new RecipeInfo(RecipeOwner.MINECRAFT, null); // shared info

        // Add vanilla Minecraft fuels just for warning if user adds one that already exists or tries to overwrite an nonexistent one
        initialRecipes.put(new FuelRecipe(Material.COAL, 80), info);
        initialRecipes.put(new FuelRecipe(Material.LOG, 15), info);
        initialRecipes.put(new FuelRecipe(Material.LOG_2, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_STEP, 7.5f), info);
        initialRecipes.put(new FuelRecipe(Material.SAPLING, 5), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_AXE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_HOE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_PICKAXE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_SPADE, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_SWORD, 10), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_PLATE, 15), info);
        initialRecipes.put(new FuelRecipe(Material.STICK, 5), info);
        initialRecipes.put(new FuelRecipe(Material.FENCE, 15), info);
        initialRecipes.put(new FuelRecipe(Material.FENCE_GATE, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.ACACIA_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.BIRCH_WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.DARK_OAK_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.SPRUCE_WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.JUNGLE_WOOD_STAIRS, 15), info);
        initialRecipes.put(new FuelRecipe(Material.TRAP_DOOR, 15), info);
        initialRecipes.put(new FuelRecipe(Material.WORKBENCH, 15), info);
        initialRecipes.put(new FuelRecipe(Material.BOOKSHELF, 15), info);
        initialRecipes.put(new FuelRecipe(Material.CHEST, 15), info);
        initialRecipes.put(new FuelRecipe(Material.TRAPPED_CHEST, 15), info);
        initialRecipes.put(new FuelRecipe(Material.DAYLIGHT_DETECTOR, 15), info);
        initialRecipes.put(new FuelRecipe(Material.JUKEBOX, 15), info);
        initialRecipes.put(new FuelRecipe(Material.NOTE_BLOCK, 15), info);
        initialRecipes.put(new FuelRecipe(Material.HUGE_MUSHROOM_1, 15), info);
        initialRecipes.put(new FuelRecipe(Material.HUGE_MUSHROOM_2, 15), info);
        initialRecipes.put(new FuelRecipe(Material.BLAZE_ROD, 120), info);
        initialRecipes.put(new FuelRecipe(Material.COAL_BLOCK, 800), info);
        initialRecipes.put(new FuelRecipe(Material.LAVA_BUCKET, 1000), info);

        try {
            initialRecipes.put(new FuelRecipe(Material.BANNER, 15), info);

            initialRecipes.put(new FuelRecipe(Material.SPRUCE_FENCE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.BIRCH_FENCE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.JUNGLE_FENCE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.DARK_OAK_FENCE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.ACACIA_FENCE, 15), info);

            initialRecipes.put(new FuelRecipe(Material.SPRUCE_FENCE_GATE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.BIRCH_FENCE_GATE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.JUNGLE_FENCE_GATE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.DARK_OAK_FENCE_GATE, 15), info);
            initialRecipes.put(new FuelRecipe(Material.ACACIA_FENCE_GATE, 15), info);
        } catch (NoSuchFieldError e) {
            // Does not support 1.8 items
        }

        // Index fuel recipes
        for (BaseRecipe recipe : initialRecipes.keySet()) {
            if (recipe instanceof FuelRecipe) {
                RecipeManager.getRecipes().indexFuels.put(((FuelRecipe) recipe).getIndexString(), (FuelRecipe) recipe);
            }
        }

        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe r;

        while (iterator.hasNext()) {
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
                recipe = new SmeltRecipe((FurnaceRecipe) r);
            }

            if (recipe == null) {
                continue;
            }

            initialRecipes.put(recipe, info);
        }

        for (Entry<BaseRecipe, RecipeInfo> e : initialRecipes.entrySet()) {
            BaseRecipe recipe = e.getKey();
            RecipeManager.getRecipes().index.put(recipe, e.getValue());
            RecipeManager.getRecipes().indexName.put(recipe.getName(), recipe);
        }
    }

    protected static void clean() {
        initialRecipes.clear();
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

        if (recipe instanceof SmeltRecipe) {
            return removeSmeltRecipe((SmeltRecipe) recipe);
        }

        return null;
    }

    /**
     * Removes a Bukkit recipe from the <b>server</b> <b>Note: This method converts the Bukkit recipe to RecipeManager recipe. If you have the BaseRecipe object you should use
     * {@link #removeCustomRecipe(BaseRecipe)}</b>
     *
     * @param recipe
     *            Bukkit recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeBukkitRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            return removeShapedRecipe((ShapedRecipe) recipe);
        }

        if (recipe instanceof ShapelessRecipe) {
            return removeShapelessRecipe((ShapelessRecipe) recipe);
        }

        if (recipe instanceof FurnaceRecipe) {
            return removeFurnaceRecipe((FurnaceRecipe) recipe);
        }

        return null;
    }

    /**
     * Removes a Bukkit recipe from the <b>server</b><br>
     * <b>Note: This method converts the Bukkit recipe to RecipeManager recipe. If you have the CraftRecipe object you should use {@link #removeCraftRecipe(CraftRecipe)}</b>
     *
     * @param recipe
     *            Bukkit recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeShapedRecipe(ShapedRecipe recipe) {
        return removeCraftRecipe(new CraftRecipe(recipe));
    }

    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeCraftRecipe(CraftRecipe recipe) {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        ShapedRecipe sr;
        Recipe r;
        String[] sh;

        ItemStack[] matrix = recipe.getIngredients();
        Tools.trimItemMatrix(matrix);
        ItemStack[] matrixMirror = Tools.mirrorItemMatrix(matrix);
        int height = recipe.getHeight();
        int width = recipe.getWidth();

        while (iterator.hasNext()) {
            r = iterator.next();

            if (r instanceof ShapedRecipe) {
                sr = (ShapedRecipe) r;
                sh = sr.getShape();

                if (sh.length == height && sh[0].length() == width && Tools.compareShapedRecipeToMatrix(sr, matrix, matrixMirror)) {
                    iterator.remove();
                    return sr;
                }
            }
        }

        return null;
    }

    /**
     * Removes a Bukkit recipe from the <b>server</b><br>
     * <b>Note: This method converts the Bukkit recipe to RecipeManager recipe. If you have the CombineRecipe object you should use {@link #removeCombineRecipe(CombineRecipe)}</b>
     *
     * @param recipe
     *            Bukkit recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeShapelessRecipe(ShapelessRecipe recipe) {
        return removeCombineRecipe(new CombineRecipe(recipe));
    }

    /**
     * Removes a RecipeManager recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeCombineRecipe(CombineRecipe recipe) {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        ShapelessRecipe sr;
        Recipe r;

        List<ItemStack> items = recipe.getIngredients();

        while (iterator.hasNext()) {
            r = iterator.next();

            if (r instanceof ShapelessRecipe) {
                sr = (ShapelessRecipe) r;

                if (Tools.compareIngredientList(items, sr.getIngredientList())) {
                    iterator.remove();
                    return sr;
                }
            }
        }

        return null;
    }

    /**
     * Removes a Bukkit furnace recipe from the <b>server</b><br>
     * Unlike {@link #removeShapedRecipe(ShapedRecipe)} and {@link #removeShapelessRecipe(ShapelessRecipe)} this method does not convert recipes since it only needs the ingredient.
     *
     * @param recipe
     *            Bukkit recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeFurnaceRecipe(FurnaceRecipe recipe) {
        return removeFurnaceRecipe(recipe.getInput());
    }

    /**
     * Removes a RecipeManager smelt recipe from the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return removed recipe or null if not found
     */
    public static Recipe removeSmeltRecipe(SmeltRecipe recipe) {
        return removeFurnaceRecipe(recipe.getIngredient());
    }

    private static Recipe removeFurnaceRecipe(ItemStack ingredient) {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        FurnaceRecipe fr;
        Recipe r;

        while (iterator.hasNext()) {
            r = iterator.next();

            if (r instanceof FurnaceRecipe) {
                fr = (FurnaceRecipe) r;

                if (ingredient.getType() == fr.getInput().getType()) {
                    iterator.remove();
                    return fr;
                }
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

        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe recipe;

        while (iterator.hasNext()) {
            recipe = iterator.next();

            if (recipe != null && RecipeManager.getRecipes().isCustomRecipe(recipe)) {
                iterator.remove();
            }
        }
    }

    /**
     * Remove all recipes from the server except special ones
     */
    public static void removeAllButSpecialRecipes() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe recipe;

        while (iterator.hasNext()) {
            recipe = iterator.next();

            if (recipe != null) {
                ItemStack result = recipe.getResult();

                if (result.equals(RECIPE_LEATHERDYE) || result.equals(RECIPE_FIREWORKS) || result.equals(RECIPE_MAPCLONE) || result.equals(RECIPE_MAPEXTEND) || result.equals(RECIPE_BANNER) || result.equals(RECIPE_BOOKCLONE)) {
                    continue;
                }

                if (recipe instanceof ShapelessRecipe && result.equals(RECIPE_REPAIR)) {
                    continue;
                }

                iterator.remove();
            }
        }
    }

    /**
     * Adds all recipes that already existed when the plugin was enabled.
     */
    public static void restoreInitialRecipes() {
        for (Entry<BaseRecipe, RecipeInfo> entry : initialRecipes.entrySet()) {
            // TODO maybe check if recipe is already in server ?
            Bukkit.addRecipe(entry.getKey().getBukkitRecipe());
        }
    }

    /**
     * Adds all recipes except special that already existed when the plugin was enabled.
     */
    public static void restoreAllButSpecialRecipes() {
        for (Entry<BaseRecipe, RecipeInfo> entry : initialRecipes.entrySet()) {
            BaseRecipe recipe = entry.getKey();
            Recipe bukkitRecipe = recipe.getBukkitRecipe();

            if (bukkitRecipe != null) {
                ItemStack result = bukkitRecipe.getResult();

                if (result.equals(RECIPE_LEATHERDYE) || result.equals(RECIPE_FIREWORKS) || result.equals(RECIPE_MAPCLONE) || result.equals(RECIPE_MAPEXTEND) || result.equals(RECIPE_BANNER) || result.equals(RECIPE_BOOKCLONE)) {
                    continue;
                }

                if (bukkitRecipe instanceof ShapelessRecipe && result.equals(RECIPE_REPAIR)) {
                    continue;
                }
                // TODO maybe check if recipe is already in server ?
                Bukkit.addRecipe(bukkitRecipe);
            }
        }
    }

    /**
     * @return a copy of the initial recipes map.
     */
    public static Map<BaseRecipe, RecipeInfo> getInitialRecipes() {
        return ImmutableMap.copyOf(initialRecipes);
    }
}
