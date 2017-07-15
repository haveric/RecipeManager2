package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import haveric.recipeManager.recipes.*;
import haveric.recipeManager.tools.RecipeIteratorV1_12;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeV1_12;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeOwner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

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
    public static final ItemStack RECIPE_MAPCLONE = new ItemStack(Material.EMPTY_MAP, 0, (short) -1);

    /**
     * Map extending's special recipe result, you can use it to identify vanilla recipes.
     */
    public static final ItemStack RECIPE_MAPEXTEND = new ItemStack(Material.EMPTY_MAP, 0, (short) 0);
    public static final ItemStack RECIPE_MAPEXTEND_1_11 = new ItemStack(Material.EMPTY_MAP, 1, (short) 0);

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
    protected static ItemStack RECIPE_BANNER = null;

    protected static ItemStack RECIPE_SHIELD_BANNER = null;

    protected static ItemStack RECIPE_TIPPED_ARROW = null;
    protected static ItemStack RECIPE_TIPPED_ARROW2 = null;

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

        RMCRecipeInfo info = new RMCRecipeInfo(RecipeOwner.MINECRAFT, null); // shared info

        // Add vanilla Minecraft fuels just for warning if user adds one that already exists or tries to overwrite a nonexistent one
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

        if (Version.has1_8Support()) {
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

            RECIPE_BANNER = new ItemStack(Material.BANNER, 0, (short) 0);
        }

        if (Version.has1_9Support()) {
            RECIPE_SHIELD_BANNER = new ItemStack(Material.SHIELD, 0, (short) 0);
        }

        if (Version.has1_11Support()) {
            initialRecipes.put(new FuelRecipe(Material.WOOL, 5), info);
            initialRecipes.put(new FuelRecipe(Material.CARPET, 3.35f), info);
            initialRecipes.put(new FuelRecipe(Material.LADDER, 15), info);
            initialRecipes.put(new FuelRecipe(Material.WOOD_BUTTON, 5), info);
            initialRecipes.put(new FuelRecipe(Material.BOW, 10), info);
            initialRecipes.put(new FuelRecipe(Material.FISHING_ROD, 15), info);
            initialRecipes.put(new FuelRecipe(Material.SIGN, 10), info);
            initialRecipes.put(new FuelRecipe(Material.BOWL, 5), info);
            initialRecipes.put(new FuelRecipe(Material.WOOD_DOOR, 10), info); // was WOODEN_DOOR, which is block type, not item type...
            initialRecipes.put(new FuelRecipe(Material.DARK_OAK_DOOR_ITEM, 10), info);
            initialRecipes.put(new FuelRecipe(Material.ACACIA_DOOR_ITEM, 10), info);
            initialRecipes.put(new FuelRecipe(Material.BIRCH_DOOR_ITEM, 10), info);
            initialRecipes.put(new FuelRecipe(Material.JUNGLE_DOOR_ITEM, 10), info);
            initialRecipes.put(new FuelRecipe(Material.SPRUCE_DOOR_ITEM, 10), info);
            initialRecipes.put(new FuelRecipe(Material.BOAT, 20), info);
            initialRecipes.put(new FuelRecipe(Material.BOAT_ACACIA, 20), info);
            initialRecipes.put(new FuelRecipe(Material.BOAT_BIRCH, 20), info);
            initialRecipes.put(new FuelRecipe(Material.BOAT_DARK_OAK, 20), info);
            initialRecipes.put(new FuelRecipe(Material.BOAT_JUNGLE, 20), info);
            initialRecipes.put(new FuelRecipe(Material.BOAT_SPRUCE, 20), info);

            RECIPE_TIPPED_ARROW = new ItemStack(Material.TIPPED_ARROW, 8, (short) 0);
        }

        if (Version.has1_12Support()) {
            RECIPE_TIPPED_ARROW2 = new ItemStack(Material.TIPPED_ARROW, 8, (short) 0);
            PotionMeta meta = (PotionMeta) RECIPE_TIPPED_ARROW2.getItemMeta();
            PotionData potionData = new PotionData(PotionType.WATER);
            meta.setBasePotionData(potionData);
            RECIPE_TIPPED_ARROW2.setItemMeta(meta);
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
                    recipe = new SmeltRecipe((FurnaceRecipe) r);
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

        for (Entry<BaseRecipe, RMCRecipeInfo> e : initialRecipes.entrySet()) {
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

        // In 1.12, we cannot remove the normal way. So, we have a special NMS iterator that will handle the details.
        if (Version.has1_12Support()) {
            iterator = new RecipeIteratorV1_12(iterator);
        }

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe) {
                    sr = (ShapedRecipe) r;
                    sh = sr.getShape();

                    if (sh.length == height && sh[0].length() == width &&
                            (Version.has1_12Support() ? ToolsRecipeV1_12.matches(recipe, r) : 
                            Tools.compareShapedRecipeToMatrix(sr, matrix, matrixMirror))) {
                        iterator.remove();
                        if (Version.has1_12Support()) {
                            ( (RecipeIteratorV1_12) iterator).finish();
                        }
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

        // In 1.12, we cannot remove the normal way. So, we have a special NMS iterator that will handle the details.
        if (Version.has1_12Support()) {
            iterator = new RecipeIteratorV1_12(iterator);
        }

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapelessRecipe) {
                    sr = (ShapelessRecipe) r;

                    if (Version.has1_12Support() ? ToolsRecipeV1_12.matches(recipe, r) :
                            Tools.compareIngredientList(items, sr.getIngredientList())) {
                        iterator.remove();
                        if (Version.has1_12Support()) {
                            ( (RecipeIteratorV1_12) iterator).finish();
                        }
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

        // In 1.12, we cannot remove the normal way. So, we have a special NMS iterator that will handle the details.
        if (Version.has1_12Support()) {
            iterator = new RecipeIteratorV1_12(iterator);
        }

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof FurnaceRecipe) {
                    fr = (FurnaceRecipe) r;

                    if (ingredient.getType() == fr.getInput().getType()) { // this works fine in 1.12
                        iterator.remove();
                        if (Version.has1_12Support()) {
                            ( (RecipeIteratorV1_12) iterator).finish();
                        }
                        return fr;
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

        if (recipe instanceof CraftRecipe) {
            return replaceCraftRecipe((CraftRecipe) recipe);
        }

        if (recipe instanceof CombineRecipe) {
            return replaceCombineRecipe((CombineRecipe) recipe);
        }

        return null;

    }

    /**
     * V1_12 or newer supported only.
     * Replaces a RecipeManager recipe on the <b>server</b>
     *
     * @param recipe
     *            RecipeManager recipe
     * @return replaced recipe or null if not found
     */
    public static Recipe replaceCraftRecipe(CraftRecipe recipe) {
        RecipeIteratorV1_12 iterator = new RecipeIteratorV1_12(Bukkit.recipeIterator());
        ShapedRecipe sr;
        Recipe r;
        String[] sh;

        int height = recipe.getHeight();
        int width = recipe.getWidth();

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapedRecipe) {
                    sr = (ShapedRecipe) r;
                    sh = sr.getShape();

                    if (sh.length == height && sh[0].length() == width && ToolsRecipeV1_12.matches(recipe, r)) {
                        iterator.replace(recipe);
                        iterator.finish();
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
    public static Recipe replaceCombineRecipe(CombineRecipe recipe) {
        RecipeIteratorV1_12 iterator = new RecipeIteratorV1_12(Bukkit.recipeIterator());
        ShapelessRecipe sr;
        Recipe r;

        while (iterator.hasNext()) {
            try {
                r = iterator.next();

                if (r instanceof ShapelessRecipe) {
                    sr = (ShapelessRecipe) r;

                    if (ToolsRecipeV1_12.matches(recipe, r)) {
                        iterator.replace(recipe);
                        iterator.finish();
                        return sr;
                    }
                }
            } catch (NullPointerException e) {
                // Catch any invalid Bukkit recipes
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
            } catch (NullPointerException e) {
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
        List<Recipe> specialRecipes = new ArrayList<>();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Recipe recipe;

        // In 1.12, we cannot remove the normal way. So, we have a special NMS iterator that will handle the details.
        if (Version.has1_12Support()) {
            iterator = new RecipeIteratorV1_12(iterator);
        }

        while (iterator.hasNext()) {
            try {
                recipe = iterator.next();

                if (recipe != null) {
                    if (isSpecialRecipe(recipe)) {
                        continue;
                    }

                    iterator.remove();
                }
            } catch (NullPointerException e) {
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

        if (recipe != null) {
            ItemStack result = recipe.getResult();

            if (result.equals(RECIPE_LEATHERDYE) || result.equals(RECIPE_FIREWORKS) || result.equals(RECIPE_MAPCLONE) || result.equals(RECIPE_MAPEXTEND) || result.equals(RECIPE_MAPEXTEND_1_11) || result.equals(RECIPE_BOOKCLONE)) {
                isSpecial = true;
            }

            if (RECIPE_BANNER != null && result.equals(RECIPE_BANNER)) {
                isSpecial = true;
            }

            if (RECIPE_SHIELD_BANNER != null && result.equals(RECIPE_SHIELD_BANNER)) {
                isSpecial = true;
            }

            if (recipe instanceof ShapelessRecipe && result.equals(RECIPE_REPAIR)) {
                isSpecial = true;
            }

            if ((RECIPE_TIPPED_ARROW != null && result.equals(RECIPE_TIPPED_ARROW)) || (RECIPE_TIPPED_ARROW2 != null && result.equals(RECIPE_TIPPED_ARROW2))) {
                isSpecial = true;
            }

            if (result.getType().equals(Material.AIR)) {
                isSpecial = true;
            }
        }

        return isSpecial;
    }
}
