package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeInfo.RecipeOwner;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.*;
import haveric.recipeManager.recipes.combine.BaseCombineRecipe;
import haveric.recipeManager.recipes.craft.BaseCraftRecipe;
import haveric.recipeManager.tools.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * RecipeManager's recipe storage
 */
public class Recipes {
    // constants
    public static final String RECIPE_NAMESPACE_STRING = "recipemanager";
    public static final String RECIPE_ID_STRING = RMCChatColor.GRAY + "RecipeManager #";

    // Recipe index
    protected Map<BaseRecipe, RMCRecipeInfo> index = new HashMap<>();

    // Quick-find index
    protected Map<String, BaseRecipe> indexName = new HashMap<>();
    private Map<String, Map<String, List<BaseRecipe>>> indexRecipesSimple = new HashMap<>();
    private Map<String, Map<String, List<BaseRecipe>>> indexRecipes = new HashMap<>();
    private Map<String, Map<String, List<BaseRecipe>>> indexRemovedRecipes = new HashMap<>();
    private Map<String, Boolean> recipeTypeContainsOverride = new HashMap<>();

    public Recipes() {
    }

    public void clean() {
        index.clear();

        indexName.clear();
        indexRecipesSimple.clear();
        indexRecipes.clear();
        indexRemovedRecipes.clear();
        recipeTypeContainsOverride.clear();
    }

    /**
     * Alias for RecipeManager.getRecipes()
     *
     * @return
     */
    public static Recipes getInstance() {
        return RecipeManager.getRecipes();
    }

    public int getNumRecipesSimple() {
        int numRecipes = 0;
        for (Map<String, List<BaseRecipe>> recipeMap: indexRecipesSimple.values()) {
            for (List<BaseRecipe> baseRecipes : recipeMap.values()) {
                for (BaseRecipe baseRecipe : baseRecipes) {
                    if (!Vanilla.initialRecipes.containsKey(baseRecipe)) {
                        numRecipes ++;
                    }
                }
            }
        }
        return numRecipes;
    }

    public int getNumRecipesRequireRecipeManager() {
        int numRecipes = 0;
        for (Map<String, List<BaseRecipe>> recipeMap: indexRecipes.values()) {
            for (List<BaseRecipe> baseRecipes : recipeMap.values()) {
                for (BaseRecipe baseRecipe : baseRecipes) {
                    if (!Vanilla.initialRecipes.containsKey(baseRecipe)) {
                        numRecipes ++;
                    }
                }
            }
        }
        return numRecipes;
    }
    /**
     * Checks if result is part of a recipe added by RecipeManager by checking item's lore.
     *
     * @param result
     *            must be the actual result from recipe.
     * @return
     */
    public boolean isCustomWorkbenchRecipe(ItemStack result) {
        if (result == null || !result.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = result.getItemMeta();

        if (!meta.hasLore()) {
            return false;
        }

        List<String> lore = meta.getLore();

        return lore.get(lore.size() - 1).startsWith(RECIPE_ID_STRING);
    }

    /**
     * Checks if recipe is added by RecipeManager, works for workbench and furnace recipes.<br>
     * Does not work for fuels because they do not exist in Bukkit API, they're a custom system by RecipeManager.
     *
     * @param recipe
     *            must be the actual recipe, because it checks for result's lore.
     * @return
     */
    public boolean isCustomRecipe(Recipe recipe) {
        if (recipe == null) {
            return false;
        }

        if (recipe instanceof FurnaceRecipe) {
            if (Version.has1_13Support()) {
                RecipeChoice choice = ((FurnaceRecipe) recipe).getInputChoice();

                if (choice instanceof RecipeChoice.MaterialChoice) {
                    RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

                    return (getRecipe(RMCRecipeType.SMELT, new ItemStack(materialChoice.getChoices().get(0))) != null);
                }
            } else {
                return (getRecipe(RMCRecipeType.SMELT, ((FurnaceRecipe) recipe).getInput()) != null);
            }
        }

        return isCustomWorkbenchRecipe(recipe.getResult());
    }

    public List<BaseRecipe> getRecipesOfType(RMCRecipeType type) {
        return getRecipesOfType(type.getDirective());
    }

    public List<BaseRecipe> getRecipesOfType(String type) {
        List<BaseRecipe> recipes = new ArrayList<>();
        if (indexRecipes.containsKey(type)) {
            Map<String, List<BaseRecipe>> mappedRecipes = indexRecipes.get(type);
            Collection<List<BaseRecipe>> recipeCollection = mappedRecipes.values();
            for (List<BaseRecipe> recipeList : recipeCollection) {
                recipes.addAll(recipeList);
            }
        }

        return recipes;
    }

    public boolean getRecipeTypeHasOverride(RMCRecipeType type) {
        return getRecipeTypeHasOverride(type.getDirective());
    }

    public boolean getRecipeTypeHasOverride(String type) {
        return recipeTypeContainsOverride.containsKey(type) && recipeTypeContainsOverride.get(type);
    }

    public BaseRecipe getRecipe(RMCRecipeType type, ItemStack ingredient) {
        return getRecipe(type, ingredient, null);
    }

    public BaseRecipe getRecipe(RMCRecipeType type, ItemStack ingredient, ItemStack result) {
        return getRecipe(type, Collections.singletonList(ingredient), result);
    }

    public BaseRecipe getRecipe(RMCRecipeType type, List<ItemStack> ingredients, ItemStack result) {
        return getRecipe(type.getDirective(), ingredients, result);
    }

    public BaseRecipe getRecipe(String type, List<ItemStack> ingredients, ItemStack result) {
        List<BaseRecipe> potentialRecipes = new ArrayList<>();
        replaceNullItemsWithAir(ingredients);

        BaseRecipe blankBaseRecipe = RecipeTypeFactory.getInstance().getRecipeType(type);
        List<String> recipeIndexes = blankBaseRecipe.getRecipeIndexesForInput(ingredients, result);
        if (recipeIndexes != null) {
            if (indexRecipes.containsKey(type)) {
                Map<String, List<BaseRecipe>> recipes = indexRecipes.get(type);

                for (String recipeIndex : recipeIndexes) {
                    if (recipes.containsKey(recipeIndex)) {
                        potentialRecipes.addAll(recipes.get(recipeIndex));
                    }
                }
            }
        }

        if (potentialRecipes.isEmpty()) {
            return null;
        } else if (Version.has1_13BasicSupport()) {
            int matchQuality = 0;
            BaseRecipe closestRecipe = null;

            for (BaseRecipe recipe : potentialRecipes) {
                int quality = recipe.getIngredientMatchQuality(ingredients);
                if (quality > matchQuality) {
                    matchQuality = quality;
                    closestRecipe = recipe;
                }
            }

            return closestRecipe;
        } else {
            return potentialRecipes.get(0);
        }
    }

    public BaseRecipe getRemovedRecipe(RMCRecipeType type, ItemStack ingredient) {
        return getRemovedRecipe(type, ingredient, null);
    }

    public BaseRecipe getRemovedRecipe(RMCRecipeType type, ItemStack ingredient, ItemStack result) {
        return getRemovedRecipe(type, Collections.singletonList(ingredient), result);
    }

    public BaseRecipe getRemovedRecipe(RMCRecipeType type, List<ItemStack> ingredients, ItemStack result) {
        return getRemovedRecipe(type.getDirective(), ingredients, result);
    }

    public BaseRecipe getRemovedRecipe(String type, List<ItemStack> ingredients, ItemStack result) {
        List<BaseRecipe> potentialRecipes = new ArrayList<>();
        replaceNullItemsWithAir(ingredients);

        BaseRecipe blankBaseRecipe = RecipeTypeFactory.getInstance().getRecipeType(type);
        List<String> recipeIndexes = blankBaseRecipe.getRecipeIndexesForInput(ingredients, result);
        if (recipeIndexes != null) {
            if (indexRemovedRecipes.containsKey(type)) {
                Map<String, List<BaseRecipe>> recipes = indexRemovedRecipes.get(type);

                for (String recipeIndex : recipeIndexes) {
                    if (recipes.containsKey(recipeIndex)) {
                        potentialRecipes.addAll(recipes.get(recipeIndex));
                    }
                }
            }
        }

        if (potentialRecipes.isEmpty()) {
            return null;
        } else {
            int matchQuality = 0;
            BaseRecipe closestRecipe = null;

            for (BaseRecipe recipe : potentialRecipes) {
                int quality = recipe.getIngredientMatchQuality(ingredients);
                if (quality > matchQuality) {
                    matchQuality = quality;
                    closestRecipe = recipe;
                }
            }

            return closestRecipe;
        }
    }

    private void replaceNullItemsWithAir(List<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack ingredient = items.get(i);
            if (ingredient == null) {
                items.set(i, new ItemStack(Material.AIR));
            }
        }
    }

    /**
     * Get the RecipeManager workbench recipe for the Bukkit recipe inputted.<br>
     * Can be either craft or combine recipe.<br>
     *
     * @param recipe
     * @return Workbench recipe, otherwise it can be null if doesn't exist or you inputted a furnace recipe
     */
    public PreparableResultRecipe getWorkbenchRecipe(Recipe recipe, ItemStack[] contents) {
        BaseRecipe baseRecipe = null;

        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 1; i < contents.length; i++) {
            if (contents[i] != null && contents[i].getType() != Material.AIR) {
                ingredients.add(contents[i].clone());
            }
        }

        if (recipe instanceof ShapedRecipe) {
            baseRecipe = getRecipe(RMCRecipeType.CRAFT, ingredients, recipe.getResult());
        } else if (recipe instanceof ShapelessRecipe) {
            baseRecipe = getRecipe(RMCRecipeType.COMBINE, ingredients, recipe.getResult());
        }

        PreparableResultRecipe resultRecipe = null;
        if (baseRecipe instanceof PreparableResultRecipe) {
            resultRecipe = (PreparableResultRecipe) baseRecipe;
        }

        return resultRecipe;
    }

    /**
     * Gets a recipe by its name.<br>
     * The name can be an auto-generated name or a custom name.
     *
     * @param name
     *            recipe name
     * @return the recipe matching name
     */
    public BaseRecipe getRecipeByName(String name) {
        return indexName.get(name.toLowerCase());
    }

    /**
     * Gets the recipe's information (owner, adder, status, etc).<br>
     * You can use this on Bukkit recipes by converting them to RecipeManager format using:<br>
     * <code>new BaseRecipe(bukkitRecipe);</code>
     *
     * @param recipe
     *            a RecipeManager recipe
     * @return Recipe's info or null if doesn't exist
     */
    public RMCRecipeInfo getRecipeInfo(BaseRecipe recipe) {
        return index.get(recipe);
    }

    /**
     * Gets a copy of RecipeManager's recipe list.<br>
     * Returned values are mutable so you can edit individual recipes.<br>
     * Removing from this list does nothing, see {@link BaseRecipe #remove()} method instead.
     *
     * @return copy of HashMap
     */
    public Map<BaseRecipe, RMCRecipeInfo> getRecipeList() {
        return ImmutableMap.copyOf(index);
    }

    /**
     * Register a recipe.
     *
     * @param recipe
     */
    public void registerRecipe(BaseRecipe recipe) {
        String adder = RecipeManager.getPlugin().getPluginCaller("registerRecipe");

        registerRecipe(recipe, new RMCRecipeInfo(RecipeOwner.RECIPEMANAGER, adder));
    }

    /**
     * Registers a recipe with custom recipe info object.<br>
     * NOTE: You should not use this if you don't know what the recipe info object REALLY does.
     *
     * @param recipe
     * @param info
     */
    public void registerRecipe(BaseRecipe recipe, RMCRecipeInfo info) {
        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Recipe is invalid! Check ingredients and results.");
        }

        if (index.remove(recipe) != null) {
            recipe.remove();
        }

        index.put(recipe, info); // Add to main index

        String recipeDirective = recipe.getType().getDirective();
        if (recipe.hasFlag(FlagType.REMOVE)) {
            if (!indexRemovedRecipes.containsKey(recipeDirective)) {
                indexRemovedRecipes.put(recipeDirective, new HashMap<>());
            }
            for (String index : recipe.getIndexes()) {
                if (!indexRemovedRecipes.get(recipeDirective).containsKey(index)) {
                    indexRemovedRecipes.get(recipeDirective).put(index, new ArrayList<>());
                }

                indexRemovedRecipes.get(recipeDirective).get(index).add(recipe);
            }
        } else { // Add to quickfind index if it's not removed
            addRecipeToQuickfindIndex(recipeDirective, recipe);
        }

        // Remove original recipe - Special case for 1.12 below
        if (recipe.hasFlag(FlagType.REMOVE) || ((Version.has1_15Support() || !Version.has1_12Support()) && recipe.hasFlag(FlagType.OVERRIDE))) {
            recipe.setBukkitRecipe(Vanilla.removeCustomRecipe(recipe));
        }

        boolean isBasicRecipe = recipe instanceof BaseCraftRecipe || recipe instanceof BaseCombineRecipe;
        if (!Version.has1_15Support()) {
            // For 1.12 AND NEWER, we'll use replacement instead; we never remove, just alter the result to point to our recipe.
            if (Version.has1_12Support() && recipe.hasFlag(FlagType.OVERRIDE)) {
                if (isBasicRecipe) {
                    Vanilla.replaceCustomRecipe(recipe);
                } else { // 'cept for this.
                    recipe.setBukkitRecipe(Vanilla.removeCustomRecipe(recipe));
                }
            }

            // For 1.12 AND NEWER, we don't actually _remove_ the recipe. So, we nullify the Bukkit binding to
            //  ensure the RM recipe is added.
            if (Version.has1_12Support() && recipe.hasFlag(FlagType.OVERRIDE)) {
                recipe.setBukkitRecipe(null);
            }
        }

        // Add to server if applicable
        if (!recipe.hasFlag(FlagType.REMOVE)) {
            Recipe bukkitRecipe = recipe.getBukkitRecipe(false);

            // For 1.12, we don't actually add our overrides, they exist in the index. The replaceCustomRecipe
            //  handler puts as the recipe result the ID index "special item" that RM uses to tell itself that
            //  this is a recipe to manage.
            if (bukkitRecipe != null) {
                // 1.12 AND NEWER

                // Note that since we don't "replace" smelt recipes, we need special handling here.
                if (Version.has1_15Support() || !(Version.has1_12Support() && recipe.hasFlag(FlagType.OVERRIDE) && isBasicRecipe)) {
                    try {
                        Bukkit.addRecipe(bukkitRecipe);
                    } catch (IllegalStateException e) {
                        ErrorReporter.getInstance().warning("Duplicate recipe found while adding. Cannot add: " + recipe.toString());
                    }
                }
            }
        }

        if (recipe.hasFlags()) {
            recipe.getFlags().sendRegistered();
        }

        if (recipe instanceof SingleResultRecipe) {
            SingleResultRecipe rec = (SingleResultRecipe) recipe;
            ItemResult result = rec.getResult();

            if (result != null && result.hasFlags()) {
                result.getFlags().sendRegistered();
            }
        } else if (recipe instanceof MultiResultRecipe) {
            MultiResultRecipe rec = (MultiResultRecipe) recipe;

            for (ItemResult result : rec.getResults()) {
                if (result != null && result.hasFlags()) {
                    result.getFlags().sendRegistered();
                }
            }
        }
    }

    public void addRecipeToQuickfindIndex(String recipeDirective, BaseRecipe recipe) {
        indexName.put(recipe.getName().toLowerCase(), recipe); // Add to name index

        if (recipe.requiresRecipeManagerModification()) {
            if (!indexRecipes.containsKey(recipeDirective)) {
                indexRecipes.put(recipeDirective, new HashMap<>());
            }
            for (String index : recipe.getIndexes()) {
                if (!indexRecipes.get(recipeDirective).containsKey(index)) {
                    indexRecipes.get(recipeDirective).put(index, new ArrayList<>());
                }
                indexRecipes.get(recipeDirective).get(index).add(recipe);
            }
        } else {
            if (!indexRecipesSimple.containsKey(recipeDirective)) {
                indexRecipesSimple.put(recipeDirective, new HashMap<>());
            }
            for (String index : recipe.getIndexes()) {
                if (!indexRecipesSimple.get(recipeDirective).containsKey(index)) {
                    indexRecipesSimple.get(recipeDirective).put(index, new ArrayList<>());
                }
                indexRecipesSimple.get(recipeDirective).get(index).add(recipe);
            }
        }

        if (!recipeTypeContainsOverride.containsKey(recipeDirective)) {
            recipeTypeContainsOverride.put(recipeDirective, false);
        }

        if (!recipeTypeContainsOverride.get(recipeDirective) && recipe.hasFlag(FlagType.OVERRIDE)) {
            recipeTypeContainsOverride.put(recipeDirective, true);
        }
    }

    /**
     * Removes a recipe from the server.
     *
     * @param recipe
     * @return removed recipe or null if not found
     */
    public Recipe removeRecipe(BaseRecipe recipe) {
        // In 1.12, adding the RM recipe is handled elsewhere; we just need to remove indexing here.
        //  if we did try to add, we'd get a fatal error and this whole mess would fail.
        if (!Version.has1_12Support() && (recipe.hasFlag(FlagType.REMOVE) || recipe.hasFlag(FlagType.OVERRIDE))) {
            Bukkit.addRecipe(recipe.getBukkitRecipe(false));
        }

        if (Version.has1_15Support() && recipe.hasFlag(FlagType.OVERRIDE)) {
            Bukkit.addRecipe(recipe.getBukkitRecipe(false));
        }

        index.remove(recipe); // Remove from main index
        indexName.remove(recipe.getName().toLowerCase()); // Remove from name index

        String recipeDirective = recipe.getType().getDirective();
        if (!indexRecipesSimple.containsKey(recipeDirective)) {
            indexRecipesSimple.put(recipeDirective, new HashMap<>());
        }

        if (!indexRecipes.containsKey(recipeDirective)) {
            indexRecipes.put(recipeDirective, new HashMap<>());
        }

        // Remove from quickfind index
        for (String index : recipe.getIndexes()) {
            indexRecipesSimple.get(recipeDirective).remove(index);
            indexRecipes.get(recipeDirective).remove(index);
        }

        // Remove from server if applicable
        if (recipe.hasFlag(FlagType.REMOVE) || recipe.hasFlag(FlagType.OVERRIDE)) {
            return null;
        }

        return Vanilla.removeCustomRecipe(recipe);
    }

    public Map<BaseRecipe, RMCRecipeInfo> getIndex() {
        return index;
    }
}
