package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.*;
import haveric.recipeManager.recipes.anvil.AnvilRecipe;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.compost.CompostRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.recipes.furnace.RMBlastingRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManager.recipes.furnace.RMSmokingRecipe;
import haveric.recipeManager.recipes.grindstone.GrindstoneRecipe;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.recipes.RMCRecipeInfo.RecipeOwner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, CraftRecipe> indexCraftLegacy = new HashMap<>();
    private Map<Integer, CraftRecipe1_13> indexCraft = new HashMap<>();
    private Map<Integer, CombineRecipe> indexCombine = new HashMap<>();
    private Map<String, RMFurnaceRecipe> indexSmeltLegacy = new HashMap<>();
    private Map<String, RMFurnaceRecipe> indexSmeltLegacyFuels = new HashMap<>();
    private Map<String, RMFurnaceRecipe1_13> indexSmelt = new HashMap<>();
    private Map<String, RMFurnaceRecipe1_13> indexSmeltFuels = new HashMap<>();
    private Map<String, RMBlastingRecipe> indexBlasting = new HashMap<>();
    private Map<String, RMBlastingRecipe> indexBlastingFuels = new HashMap<>();
    private Map<String, RMSmokingRecipe> indexSmoking = new HashMap<>();
    private Map<String, RMSmokingRecipe> indexSmokingFuels = new HashMap<>();
    private Map<String, RMCampfireRecipe> indexCampfire = new HashMap<>();
    private Map<String, RMStonecuttingRecipe> indexStonecutting = new HashMap<>();
    protected Map<String, FuelRecipe> indexFuels = new HashMap<>();
    private Map<String, BrewRecipe> indexBrew = new HashMap<>();
    protected Map<String, CompostRecipe> indexCompost = new HashMap<>();
    protected Map<String, CompostRecipe> indexRemovedCompost = new HashMap<>();
    public static boolean hasAnyOverridenCompostRecipe = false;
    private Map<String, AnvilRecipe> indexAnvil = new HashMap<>();
    private Map<String, GrindstoneRecipe> indexGrindstone = new HashMap<>();
    private Map<String, CartographyRecipe> indexCartography = new HashMap<>();
    protected Map<String, BaseRecipe> indexName = new HashMap<>();

    public Recipes() {
    }

    public void clean() {
        index.clear();
        indexCraftLegacy.clear();
        indexCraft.clear();
        indexCombine.clear();
        indexSmeltLegacy.clear();
        indexSmeltLegacyFuels.clear();
        indexSmelt.clear();
        indexSmeltFuels.clear();
        indexFuels.clear();
        indexBlasting.clear();
        indexBlastingFuels.clear();
        indexSmoking.clear();
        indexSmokingFuels.clear();
        indexCampfire.clear();
        indexStonecutting.clear();
        indexBrew.clear();
        indexCompost.clear();
        indexRemovedCompost.clear();
        indexAnvil.clear();
        indexGrindstone.clear();
        indexCartography.clear();
        indexName.clear();
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
            if (Version.has1_13Support()) {
                RecipeChoice choice = ((FurnaceRecipe) recipe).getInputChoice();

                if (choice instanceof RecipeChoice.MaterialChoice) {
                    RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;

                    return (getSmeltRecipe(new ItemStack(materialChoice.getChoices().get(0))) != null);
                }
            } else {
                return (getSmeltLegacyRecipe(((FurnaceRecipe) recipe).getInput()) != null);
            }
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

    public List<CraftRecipe1_13> getCraftRecipes() {
        List<CraftRecipe1_13> recipes = new ArrayList<>();
        for (Map.Entry<Integer, CraftRecipe1_13> entry : indexCraft.entrySet()) {
            CraftRecipe1_13 recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<CraftRecipe> getCraftLegacyRecipes() {
        List<CraftRecipe> recipes = new ArrayList<>();
        for (Map.Entry<Integer, CraftRecipe> entry : indexCraftLegacy.entrySet()) {
            CraftRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<CombineRecipe> getCombineRecipes() {
        List<CombineRecipe> recipes = new ArrayList<>();
        for (Map.Entry<Integer, CombineRecipe> entry : indexCombine.entrySet()) {
            CombineRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<RMFurnaceRecipe> getLegacyFurnaceRecipes() {
        List<RMFurnaceRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, RMFurnaceRecipe> entry : indexSmeltLegacy.entrySet()) {
            RMFurnaceRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<RMFurnaceRecipe1_13> getFurnaceRecipes() {
        List<RMFurnaceRecipe1_13> recipes = new ArrayList<>();
        for (Map.Entry<String, RMFurnaceRecipe1_13> entry : indexSmelt.entrySet()) {
            RMFurnaceRecipe1_13 recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<RMBlastingRecipe> getBlastingRecipes() {
        List<RMBlastingRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, RMBlastingRecipe> entry : indexBlasting.entrySet()) {
            RMBlastingRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<RMSmokingRecipe> getSmokingRecipes() {
        List<RMSmokingRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, RMSmokingRecipe> entry : indexSmoking.entrySet()) {
            RMSmokingRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<RMCampfireRecipe> getCampfireRecipes() {
        List<RMCampfireRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, RMCampfireRecipe> entry : indexCampfire.entrySet()) {
            RMCampfireRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<RMStonecuttingRecipe> getStonecuttingRecipes() {
        List<RMStonecuttingRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, RMStonecuttingRecipe> entry : indexStonecutting.entrySet()) {
            RMStonecuttingRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<BrewRecipe> getBrewingRecipes() {
        List<BrewRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, BrewRecipe> entry : indexBrew.entrySet()) {
            BrewRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<FuelRecipe> getFuelRecipes() {
        List<FuelRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, FuelRecipe> entry : indexFuels.entrySet()) {
            FuelRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    public List<CompostRecipe> getCompostRecipes() {
        List<CompostRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, CompostRecipe> entry : indexCompost.entrySet()) {
            CompostRecipe recipe = entry.getValue();
            recipes.add(recipe);
        }

        return recipes;
    }

    /**
     * Get the RecipeManager workbench recipe for the Bukkit recipe inputted.<br>
     * Can be either craft or combine recipe.<br>
     * If you know the specific type you can use {@link #getCraftRecipe(ItemStack)} or {@link #getCombineRecipe(ItemStack)}
     *
     * @param recipe
     * @return Workbench recipe, otherwise it can be null if doesn't exist or you inputted a furnace recipe
     */
    public PreparableResultRecipe getWorkbenchRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            if (Version.has1_13Support()) {
                return getCraftRecipe(recipe.getResult());
            } else {
                return getCraftLegacyRecipe(recipe.getResult());
            }
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
    public CraftRecipe getCraftLegacyRecipe(ItemStack result) {
        CraftRecipe recipe = null;

        if (result != null) {
            recipe = indexCraftLegacy.get(Tools.getRecipeIdFromItem(result));
        }

        return recipe;
    }

    /**
     * Get the RecipeManager craft recipe for the result inputted.<br>
     * The result must be the one from the Bukkit recipe retrieved as it needs to check for result lore for the ID.
     *
     * @param result
     * @return Craft recipe or null if doesn't exist
     */
    public CraftRecipe1_13 getCraftRecipe(ItemStack result) {
        CraftRecipe1_13 recipe = null;

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
    public RMFurnaceRecipe getSmeltLegacyRecipe(ItemStack ingredient) {
        RMFurnaceRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexSmeltLegacy.get(ingredient.getType().toString() + ":" + ingredient.getDurability());

            if (recipe == null) {
                recipe = indexSmeltLegacy.get(ingredient.getType().toString() + ":" + RMCVanilla.DATA_WILDCARD);
            }
        }

        return recipe;
    }

    public RMFurnaceRecipe getSmeltLegacyRecipeWithFuel(ItemStack fuel) {
        if (fuel == null) {
            return null;
        }

        RMFurnaceRecipe recipe = indexSmeltLegacyFuels.get(String.valueOf(fuel.getType().toString()));

        if (recipe == null) {
            return indexSmeltLegacyFuels.get(fuel.getType().toString() + ":" + fuel.getDurability());
        }

        return recipe;
    }

    /**
     * Get RecipeManager's furnace smelt recipe for the specified ingredient
     *
     * @param ingredient
     * @return Smelt recipe or null if doesn't exist
     */
    public RMFurnaceRecipe1_13 getSmeltRecipe(ItemStack ingredient) {
        RMFurnaceRecipe1_13 recipe = null;

        if (ingredient != null) {
            recipe = indexSmelt.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public RMFurnaceRecipe1_13 getSmeltRecipeWithFuel(ItemStack fuel) {
        if (fuel == null) {
            return null;
        }

        RMFurnaceRecipe1_13 recipe = indexSmeltFuels.get(String.valueOf(fuel.getType().toString()));

        if (recipe == null) {
            return indexSmeltFuels.get(fuel.getType().toString() + ":" + fuel.getDurability());
        }

        return recipe;
    }

    public BrewRecipe getBrewRecipe(ItemStack ingredient) {
        BrewRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexBrew.get(ingredient.getType().toString() + ":" + ingredient.getDurability());

            if (recipe == null) {
                recipe = indexBrew.get(ingredient.getType().toString() + ":" + RMCVanilla.DATA_WILDCARD);
            }
        }

        return recipe;
    }

    public CompostRecipe getCompostRecipe(ItemStack ingredient) {
        CompostRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexCompost.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public CompostRecipe getRemovedCompostRecipe(ItemStack ingredient) {
        CompostRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexRemovedCompost.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public AnvilRecipe getAnvilRecipe(ItemStack primary, ItemStack secondary) {
        if (primary == null) {
            primary = new ItemStack(Material.AIR);
        }
        if (secondary == null) {
            secondary = new ItemStack(Material.AIR);
        }

        return indexAnvil.get(primary.getType().toString() + "-" + secondary.getType().toString());
    }

    public RMBlastingRecipe getRMBlastingRecipe(ItemStack ingredient) {
        RMBlastingRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexBlasting.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public RMBlastingRecipe getRMBlastingRecipeWithFuel(ItemStack fuel) {
        if (fuel == null) {
            return null;
        }

        RMBlastingRecipe recipe = indexBlastingFuels.get(String.valueOf(fuel.getType().toString()));

        if (recipe == null) {
            return indexBlastingFuels.get(fuel.getType().toString() + ":" + fuel.getDurability());
        }

        return recipe;
    }

    public RMSmokingRecipe getRMSmokingRecipe(ItemStack ingredient) {
        RMSmokingRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexSmoking.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public RMSmokingRecipe getRMSmokingRecipeWithFuel(ItemStack fuel) {
        if (fuel == null) {
            return null;
        }

        RMSmokingRecipe recipe = indexSmokingFuels.get(String.valueOf(fuel.getType().toString()));

        if (recipe == null) {
            return indexSmokingFuels.get(fuel.getType().toString() + ":" + fuel.getDurability());
        }

        return recipe;
    }

    public RMCampfireRecipe getRMCampfireRecipe(ItemStack ingredient) {
        RMCampfireRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexCampfire.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public RMStonecuttingRecipe getRMStonecuttingRecipe(ItemStack ingredient) {
        RMStonecuttingRecipe recipe = null;

        if (ingredient != null) {
            recipe = indexStonecutting.get(ingredient.getType().toString());
        }

        return recipe;
    }

    public GrindstoneRecipe getGrindstoneRecipe(ItemStack primary, ItemStack secondary) {
        if (primary == null) {
            primary = new ItemStack(Material.AIR);
        }
        if (secondary == null) {
            secondary = new ItemStack(Material.AIR);
        }

        return indexGrindstone.get(primary.getType().toString() + "-" + secondary.getType().toString());
    }

    public CartographyRecipe getCartographyRecipe(ItemStack primary, ItemStack secondary) {
        if (primary == null) {
            primary = new ItemStack(Material.AIR);
        }
        if (secondary == null) {
            secondary = new ItemStack(Material.AIR);
        }

        return indexCartography.get(primary.getType().toString() + "-" + secondary.getType().toString());
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

        FuelRecipe recipe = indexFuels.get(String.valueOf(fuel.getType().toString()));

        if (recipe == null) {
            return indexFuels.get(fuel.getType().toString() + ":" + fuel.getDurability());
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

        if (recipe.hasFlag(FlagType.REMOVE)) {
            if (recipe instanceof CompostRecipe){
                for (String index : ((CompostRecipe) recipe).getIndexString()) {
                    indexRemovedCompost.put(index, (CompostRecipe) recipe);
                }
            }
        } else { // Add to quickfind index if it's not removed
            indexName.put(recipe.getName().toLowerCase(), recipe); // Add to name index

            if (recipe instanceof CraftRecipe) {
                indexCraftLegacy.put(recipe.getIndex(), (CraftRecipe) recipe);
            } else if (recipe instanceof CraftRecipe1_13) {
                indexCraft.put(recipe.getIndex(), (CraftRecipe1_13) recipe);
            } else if (recipe instanceof CombineRecipe) {
                indexCombine.put(recipe.getIndex(), (CombineRecipe) recipe);
            } else if (recipe instanceof RMFurnaceRecipe) {
                RMFurnaceRecipe r = (RMFurnaceRecipe) recipe;

                for (String index : ((RMFurnaceRecipe) recipe).getIndexString()) {
                    indexSmeltLegacy.put(index, r);
                }

                if (r.hasFuel()) {
                    indexSmeltLegacyFuels.put(r.getFuelIndex(), r);
                }
            } else if (recipe instanceof RMFurnaceRecipe1_13) {
                RMFurnaceRecipe1_13 r = (RMFurnaceRecipe1_13) recipe;

                for (String index : ((RMFurnaceRecipe1_13) recipe).getIndexString()) {
                    indexSmelt.put(index, r);
                }

                if (r.hasFuel()) {
                    indexSmeltFuels.put(r.getFuelIndex(), r);
                }
            } else if (recipe instanceof RMBlastingRecipe) {
                RMBlastingRecipe r = (RMBlastingRecipe) recipe;

                for (String index : ((RMBlastingRecipe) recipe).getIndexString()) {
                    indexBlasting.put(index, r);
                }

                if (r.hasFuel()) {
                    indexBlastingFuels.put(r.getFuelIndex(), r);
                }
            } else if (recipe instanceof RMSmokingRecipe) {
                RMSmokingRecipe r = (RMSmokingRecipe) recipe;

                for (String index : ((RMSmokingRecipe) recipe).getIndexString()) {
                    indexSmoking.put(index, r);
                }

                if (r.hasFuel()) {
                    indexSmokingFuels.put(r.getFuelIndex(), r);
                }
            } else if (recipe instanceof RMCampfireRecipe) {
                for (String index : ((RMCampfireRecipe) recipe).getIndexString()) {
                    indexCampfire.put(index, (RMCampfireRecipe) recipe);
                }
            } else if (recipe instanceof RMStonecuttingRecipe) {
                for (String index : ((RMStonecuttingRecipe) recipe).getIndexString()) {
                    indexStonecutting.put(index, (RMStonecuttingRecipe) recipe);
                }
            } else if (recipe instanceof BrewRecipe) {
                indexBrew.put(((BrewRecipe) recipe).getIndexString(), (BrewRecipe) recipe);
            } else if (recipe instanceof FuelRecipe) {
                indexFuels.put(((FuelRecipe) recipe).getIndexString(), (FuelRecipe) recipe);
            } else if (recipe instanceof CompostRecipe) {
                if (!hasAnyOverridenCompostRecipe && recipe.hasFlag(FlagType.OVERRIDE)) {
                    hasAnyOverridenCompostRecipe = true;
                }

                for (String index : ((CompostRecipe) recipe).getIndexString()) {
                    indexCompost.put(index, (CompostRecipe) recipe);
                }
            } else if (recipe instanceof AnvilRecipe) {
                for (String index : ((AnvilRecipe) recipe).getIndexString()) {
                    indexAnvil.put(index, (AnvilRecipe) recipe);
                }
            } else if (recipe instanceof GrindstoneRecipe) {
                for (String index : ((GrindstoneRecipe) recipe).getIndexString()) {
                    indexGrindstone.put(index, (GrindstoneRecipe) recipe);
                }
            } else if (recipe instanceof CartographyRecipe) {
                for (String index : ((CartographyRecipe) recipe).getIndexString()) {
                    indexCartography.put(index, (CartographyRecipe) recipe);
                }
            }
        }

        // Remove original recipe - Special case for 1.12 below
        if (recipe.hasFlag(FlagType.REMOVE) || ((Version.has1_15Support() || !Version.has1_12Support()) && recipe.hasFlag(FlagType.OVERRIDE))) {
            recipe.setBukkitRecipe(Vanilla.removeCustomRecipe(recipe));
        }

        boolean isBasicRecipe = recipe instanceof CraftRecipe || recipe instanceof CombineRecipe;
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
                    Bukkit.addRecipe(bukkitRecipe);
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

        // Remove from quickfind index
        if (recipe instanceof CraftRecipe) {
            indexCraftLegacy.remove(recipe.getIndex());
        } else if (recipe instanceof CraftRecipe1_13) {
            indexCraft.remove(recipe.getIndex());
        } else if (recipe instanceof CombineRecipe) {
            indexCombine.remove(recipe.getIndex());
        } else if (recipe instanceof RMFurnaceRecipe) {
            for (String index : ((RMFurnaceRecipe) recipe).getIndexString()) {
                indexSmeltLegacy.remove(index);
            }
        } else if (recipe instanceof RMFurnaceRecipe1_13) {
            for (String index : ((RMFurnaceRecipe1_13) recipe).getIndexString()) {
                indexSmelt.remove(index);
            }
        } else if (recipe instanceof RMBlastingRecipe) {
            for (String index : ((RMBlastingRecipe) recipe).getIndexString()) {
                indexBlasting.remove(index);
            }
        } else if (recipe instanceof RMSmokingRecipe) {
            for (String index : ((RMSmokingRecipe) recipe).getIndexString()) {
                indexSmoking.remove(index);
            }
        } else if (recipe instanceof RMCampfireRecipe) {
            for (String index : ((RMCampfireRecipe) recipe).getIndexString()) {
                indexCampfire.remove(index);
            }
        } else if (recipe instanceof RMStonecuttingRecipe) {
            for (String index : ((RMStonecuttingRecipe) recipe).getIndexString()) {
                indexStonecutting.remove(index);
            }
        } else if (recipe instanceof BrewRecipe) {
            indexBrew.remove(((BrewRecipe) recipe).getIndexString());
        } else if (recipe instanceof FuelRecipe) {
            indexFuels.remove(((FuelRecipe) recipe).getIndexString());
        } else if (recipe instanceof CompostRecipe) {
            for (String index : ((CompostRecipe) recipe).getIndexString()) {
                indexCompost.remove(index);
            }
        } else if (recipe instanceof AnvilRecipe) {
            for (String index : ((AnvilRecipe) recipe).getIndexString()) {
                indexAnvil.remove(index);
            }
        } else if (recipe instanceof GrindstoneRecipe) {
            for (String index : ((GrindstoneRecipe) recipe).getIndexString()) {
                indexGrindstone.remove(index);
            }
        } else if (recipe instanceof CartographyRecipe) {
            for (String index : ((CartographyRecipe) recipe).getIndexString()) {
                indexCartography.remove(index);
            }
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
