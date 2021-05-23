package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.common.recipes.AbstractBaseRecipe;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.*;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagItemName;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public class BaseRecipe extends AbstractBaseRecipe implements Flaggable {
    private Flags flags;
    protected Recipe recipe;
    private boolean vanillaSpecialRecipe = false;

    public BaseRecipe() {
        super();
    }

    public BaseRecipe(BaseRecipe newRecipe) {
        super(newRecipe);
        if (newRecipe.hasFlags()) {
            flags = newRecipe.getFlags().clone(this);
        } else {
            flags = null;
        }

        recipe = newRecipe.recipe;
        vanillaSpecialRecipe = newRecipe.vanillaSpecialRecipe;
    }

    public BaseRecipe(Flags newFlags) {
        flags = newFlags.clone(this);
    }

    /**
     * Called when a recipe is registered to the server
     */
    public void onRegister() { }

    /**
     * See: {@link Recipes #getRecipeInfo(BaseRecipe)}
     *
     * @return Recipe info or null if doesn't exist
     */
    public RMCRecipeInfo getInfo() {
        return RecipeManager.getRecipes().getRecipeInfo(this);
    }

    /**
     * Set the name of this recipe.
     *
     * @param newName
     *            should be a UNIQUE name
     */
    public void setName(String newName) {
        newName = newName.trim();

        if (newName.isEmpty()) {
            ErrorReporter.getInstance().error("Recipe names can not be empty!");
            return;
        }

        while (newName.charAt(0) == '+') {
            ErrorReporter.getInstance().error("Recipe names can not start with '+' character, removed!");
            newName = newName.substring(1);
        }

        name = newName;
        customName = true;
    }

    public void setVanillaSpecialRecipe(boolean isVanilla) {
        vanillaSpecialRecipe = isVanilla;
    }

    public boolean isVanillaSpecialRecipe() {
        return vanillaSpecialRecipe;
    }


    @Override
    public String toString() {
        return getType() + "{" + getName() + "}";
    }

    protected NamespacedKey getNamespacedKey() {
        String key = Recipes.RECIPE_NAMESPACE_STRING + hashCode();

        RecipeManager plugin = RecipeManager.getPlugin();
        // Used when running tests and plugin doesn't exist
        if (plugin == null) {
            return NamespacedKey.minecraft(key);
        } else {
            return new NamespacedKey(RecipeManager.getPlugin(), key);
        }
    }

    /**
     * Remove this recipe from the server and from RecipeManager.<br>
     * Alias for: RecipeManager.getRecipes().removeRecipe(this);
     *
     * @return removed recipe or null if not found
     */
    public Recipe remove() {
        return RecipeManager.getRecipes().removeRecipe(this);
    }

    /**
     * You usually won't need this, but just in case you do, here it is.
     *
     * @return Bukkit API version of the recipe
     */
    public Recipe getBukkitRecipe(boolean vanilla) {
        Recipe bukkitRecipe;
        if (recipe == null) {
            bukkitRecipe = toBukkitRecipe(vanilla);
        } else {
            bukkitRecipe = recipe;
        }

        return bukkitRecipe;
    }

    public void setBukkitRecipe(Recipe newRecipe) {
        recipe = newRecipe;
    }

    public Recipe toBukkitRecipe(boolean vanilla) {
        return null;
    }

    // From Flaggable interface

    public boolean hasFlag(String type) {
        boolean hasFlag = false;

        if (flags != null) {
            hasFlag = flags.hasFlag(type);
        }

        return hasFlag;
    }

    public boolean hasFlags() {
        return flags != null;
    }

    public Flag getFlag(String type) {
        Flag flag = null;

        if (flags != null) {
            flag = flags.getFlag(type);
        }

        return flag;
    }

    public Flags getFlags() {
        if (flags == null) {
            flags = new Flags(this);
        }

        return flags;
    }

    public void clearFlags() {
        flags = null;
    }

    public void addFlag(Flag flag) {
        getFlags().addFlag(flag, FlagBit.RECIPE);
    }

    public boolean checkFlags(Args a) {
        boolean checkFlags = true;

        if (flags != null) {
            checkFlags = flags.checkFlags(a);
        }

        return checkFlags;
    }

    public boolean sendCrafted(Args a) {
        boolean sendCrafted = true;

        if (flags != null) {
            sendCrafted = flags.sendCrafted(a);
        }

        return sendCrafted;
    }

    public boolean sendPrepare(Args a) {
        boolean sendPrepare = true;

        if (flags != null) {
            sendPrepare = flags.sendPrepare(a);
        }

        return sendPrepare;
    }

    @Override
    public boolean sendFuelRandom(Args a) {
        boolean sendRandom = true;

        if (flags != null) {
            sendRandom = flags.sendFuelRandom(a);
        }

        return sendRandom;
    }

    @Override
    public boolean sendFuelEnd(Args a) {
        boolean sendEnd = true;

        if (flags != null) {
            sendEnd = flags.sendFuelEnd(a);
        }

        return sendEnd;
    }

    /**
     * Notify flags that the recipe failed.
     *
     * @param a
     */
    public void sendFailed(Args a) {
        if (flags != null) {
            flags.sendFailed(a);
        }
    }

    protected String getResultPrintName(ItemResult result) {
        String print;

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            print = RMCUtil.parseColors(flag.getPrintName(), false);
        } else {
            print = ToolsItem.getName(result);
        }

        return print;
    }

    public String printBookResult(ItemResult result) {
        return "";
    }

    public int findItemInIngredients(Material type, Short data) {
        return 0;
    }

    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        return null;
    }

    public boolean isValidBlockMaterial(Material material) {
        return false;
    }

    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        return 0;
    }

    public boolean requiresRecipeManagerModification() {
        return true;
    }
}
