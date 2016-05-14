package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import haveric.recipeManager.flags.Args;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.recipes.*;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeOwner;
import org.bukkit.Bukkit;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecipeManager's recipe storage
 */
public class Recipes {
    // constants
    //public static final String FURNACE_OWNER_STRING = RMCChatColor.GRAY + "Placed by: " + RMCChatColor.WHITE;
    public static final String RECIPE_ID_STRING = RMCChatColor.GRAY + "RecipeManager #";

    // Remember results for re-use on failure
    private static final Map<String, ItemResult> staticResults = new HashMap<String, ItemResult>();

    // Recipe index
    protected Map<BaseRecipe, RMCRecipeInfo> index = new HashMap<BaseRecipe, RMCRecipeInfo>();

    // Quick-find index
    protected Map<Integer, CraftRecipe> indexCraft = new HashMap<Integer, CraftRecipe>();
    protected Map<Integer, CombineRecipe> indexCombine = new HashMap<Integer, CombineRecipe>();
    protected Map<String, SmeltRecipe> indexSmelt = new HashMap<String, SmeltRecipe>();
    protected Map<String, SmeltRecipe> indexSmeltFuels = new HashMap<String, SmeltRecipe>();
    protected Map<String, FuelRecipe> indexFuels = new HashMap<String, FuelRecipe>();
    protected Map<String, BrewRecipe> indexBrew = new HashMap<String, BrewRecipe>();
    protected Map<String, BaseRecipe> indexName = new HashMap<String, BaseRecipe>();

    public Recipes() {
    }

    public void clean() {
        index.clear();
        indexCraft.clear();
        indexCombine.clear();
        indexSmelt.clear();
        indexFuels.clear();
        indexBrew.clear();
        indexName.clear();

        staticResults.clear();
    }

    /**
     * Alias for RecipeManager.getRecipes()
     *
     * @return
     */
    public static Recipes getInstance() {
        return RecipeManager.getRecipes();
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
            return (getSmeltRecipe(((FurnaceRecipe) recipe).getInput()) != null);
        }

        return isCustomWorkbenchRecipe(recipe.getResult());
    }

    /**
     * Checks if item can be used as a fuel. Alias for getFuelRecipe(item) != null
     *
     * @param item
     * @return
     */
    public boolean isFuel(ItemStack item) {
        return getFuelRecipe(item) != null;
    }

    /**
     * Get the RecipeManager workbench recipe for the Bukkit recipe inputted.<br>
     * Can be either craft or combine recipe.<br>
     * If you know the specific type you can use {@link #getCraftRecipe(ItemStack)} or {@link #getCombineRecipe(ItemStack)}
     *
     * @param recipe
     * @return Workbench recipe, otherwise it can be null if doesn't exist or you inputted a furnace recipe
     */
    public WorkbenchRecipe getWorkbenchRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            return getCraftRecipe(recipe.getResult());
        }

        if (recipe instanceof ShapelessRecipe) {
            return getCombineRecipe(recipe.getResult());
        }

        return null;
    }

    /**
     * Get the RecipeManager craft recipe for the result inputted.<br>
     * The result must be the one from the Bukkit recipe retrieved as it needs to check for result lore for the ID.
     *
     * @param result
     * @return Craft recipe or null if doesn't exist
     */
    public CraftRecipe getCraftRecipe(ItemStack result) {
        CraftRecipe recipe = null;

        if (result != null) {
            recipe = indexCraft.get(Tools.getRecipeIdFromItem(result));
        }

        return recipe;
    }

    /**
     * Get the RecipeManager combine recipe for the result inputted.<br>
     * The result must be the one from the Bukkit recipe retrieved as it needs to check for result lore for the ID.
     *
     * @param result
     * @return Combine recipe or null if doesn't exist
     */
    public CombineRecipe getCombineRecipe(ItemStack result) {
        CombineRecipe recipe = null;

        if (result != null) {
            recipe = indexCombine.get(Tools.getRecipeIdFromItem(result));
        }

        return recipe;
    }

    /**
     * Get RecipeManager's furnace smelt recipe for the specified ingredient
     *
     * @param ingredient
     * @return Smelt recipe or null if doesn't exist
     */
    public SmeltRecipe getSmeltRecipe(ItemStack ingredient) {
        SmeltRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexSmelt.get(ingredient.getTypeId() + ":" + ingredient.getDurability());

            if (recipe == null) {
                recipe = indexSmelt.get(ingredient.getTypeId() + ":" + Vanilla.DATA_WILDCARD);
            }
        }

        return recipe;
    }

    public BrewRecipe getBrewRecipe(ItemStack ingredient) {
        BrewRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexBrew.get(ingredient.getTypeId() + ":" + ingredient.getDurability());

            if (recipe == null) {
                recipe = indexBrew.get(ingredient.getTypeId() + ":" + Vanilla.DATA_WILDCARD);
            }
        }

        return recipe;
    }

    public SmeltRecipe getSmeltRecipeWithFuel(ItemStack fuel) {
        if (fuel == null) {
            return null;
        }

        SmeltRecipe recipe = indexSmeltFuels.get(String.valueOf(fuel.getTypeId()));

        if (recipe == null) {
            return indexSmeltFuels.get(fuel.getTypeId() + ":" + fuel.getDurability());
        }

        return recipe;
    }

    /**
     * Get RecipeManager's furnace fuel recipe for the specified item.
     *
     * @param fuel
     *            fuel
     * @return Fuel recipe or null if doesn't exist
     */
    public FuelRecipe getFuelRecipe(ItemStack fuel) {
        if (fuel == null) {
            return null;
        }

        FuelRecipe recipe = indexFuels.get(String.valueOf(fuel.getTypeId()));

        if (recipe == null) {
            return indexFuels.get(fuel.getTypeId() + ":" + fuel.getDurability());
        }

        return recipe;
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

        // Add to quickfind index if it's not removed
        if (!recipe.hasFlag(FlagType.REMOVE)) {
            indexName.put(recipe.getName().toLowerCase(), recipe); // Add to name index

            if (recipe instanceof CraftRecipe) {
                indexCraft.put(recipe.getIndex(), (CraftRecipe) recipe);
            } else if (recipe instanceof CombineRecipe) {
                indexCombine.put(recipe.getIndex(), (CombineRecipe) recipe);
            } else if (recipe instanceof SmeltRecipe) {
                SmeltRecipe r = (SmeltRecipe) recipe;

                indexSmelt.put(((SmeltRecipe) recipe).getIndexString(), r);

                if (r.hasFuel()) {
                    indexSmeltFuels.put(r.getFuelIndex(), r);
                }
            } else if (recipe instanceof BrewRecipe) {
                indexBrew.put(((BrewRecipe) recipe).getIndexString(), (BrewRecipe) recipe);
            } else if (recipe instanceof FuelRecipe) {
                indexFuels.put(((FuelRecipe) recipe).getIndexString(), (FuelRecipe) recipe);
            }
        }

        // Remove original recipe
        if (recipe.hasFlag(FlagType.REMOVE) || recipe.hasFlag(FlagType.OVERRIDE)) {
            recipe.setBukkitRecipe(Vanilla.removeCustomRecipe(recipe));
        }

        // Add to server if applicable
        if (!recipe.hasFlag(FlagType.REMOVE)) {
            Recipe bukkitRecipe = recipe.getBukkitRecipe(false);

            if (bukkitRecipe != null) {
                Bukkit.addRecipe(bukkitRecipe);
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

    /**
     * Removes a recipe from the server.
     *
     * @param recipe
     * @return removed recipe or null if not found
     */
    public Recipe removeRecipe(BaseRecipe recipe) {
        if (recipe.hasFlag(FlagType.REMOVE) || recipe.hasFlag(FlagType.OVERRIDE)) {
            Bukkit.getServer().addRecipe(recipe.getBukkitRecipe(false));
        }

        index.remove(recipe); // Remove from main index
        indexName.remove(recipe.getName().toLowerCase()); // Remove from name index

        // Remove from quickfind index
        if (recipe instanceof CraftRecipe) {
            indexCraft.remove(recipe.getIndex());
        } else if (recipe instanceof CombineRecipe) {
            indexCombine.remove(recipe.getIndex());
        } else if (recipe instanceof SmeltRecipe) {
            indexSmelt.remove(((SmeltRecipe) recipe).getIndexString());
        } else if (recipe instanceof BrewRecipe) {
            indexBrew.remove(((BrewRecipe) recipe).getIndexString());
        } else if (recipe instanceof FuelRecipe) {
            indexFuels.remove(((FuelRecipe) recipe).getIndexString());
        }

        // Remove from server if applicable
        if (recipe.hasFlag(FlagType.REMOVE) || recipe.hasFlag(FlagType.OVERRIDE)) {
            return null;
        }

        return Vanilla.removeCustomRecipe(recipe);
    }

    protected static ItemResult recipeGetResult(Args a, WorkbenchRecipe recipe) {
        ItemResult result = staticResults.get(a.playerName());

        if (result == null) {
            result = recipe.getResult(a);
            staticResults.put(a.playerName(), result);
        }

        if (result == null) {
            return null;
        }

        return result.clone();
    }

    protected static void recipeResetResult(String name) {
        staticResults.remove(name);
    }

    public Map<BaseRecipe, RMCRecipeInfo> getIndex() {
        return index;
    }
}
