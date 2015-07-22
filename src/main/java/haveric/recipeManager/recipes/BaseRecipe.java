package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.flags.Args;
import haveric.recipeManager.flags.Flag;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flaggable;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;

import org.bukkit.inventory.Recipe;

public class BaseRecipe implements Flaggable {
    protected String name;
    protected boolean customName;
    private Flags flags;
    protected int hash;
    protected Recipe recipe;

    public BaseRecipe() {
    }

    public BaseRecipe(BaseRecipe newRecipe) {
        if (newRecipe.hasFlags()) {
            flags = newRecipe.getFlags().clone(this);
        } else {
            flags = null;
        }
        name = newRecipe.name;
        customName = newRecipe.customName;
        hash = newRecipe.hash;

        recipe = newRecipe.recipe;
    }

    public BaseRecipe(Flags newFlags) {
        flags = newFlags.clone(this);
    }

    /**
     * See: {@link Recipes #getRecipeInfo(BaseRecipe)}
     *
     * @return Recipe info or null if doesn't exist
     */
    public RMCRecipeInfo getInfo() {
        return RecipeManager.getRecipes().getRecipeInfo(this);
    }

    public RMCRecipeType getType() {
        return null;
    }

    /**
     * Returns the auto-generated name or the custom name (if set) of the recipe.
     *
     * @return recipe name, never null.
     */
    public String getName() {
        if (name == null) {
            resetName();
        }

        return name;
    }

    /**
     * @return true if recipe has custom name or false if it's auto-generated.
     */
    public boolean hasCustomName() {
        return customName;
    }

    /**
     * Set the name of this recipe.
     *
     * @param name
     *            should be a UNIQUE name
     */
    public void setName(String newName) {
        newName = newName.trim();

        if (newName.isEmpty()) {
            ErrorReporter.error("Recipe names can not be empty!");
            return;
        }

        while (newName.charAt(0) == '+') {
            ErrorReporter.error("Recipe names can not start with '+' character, removed!");
            newName = newName.substring(1);
        }

        name = newName;
        customName = true;
    }

    /**
     * Reset name to the auto-generated one.
     */
    public void resetName() {
        name = "unknown recipe";
        customName = false;
    }

    public boolean isValid() {
        return false; // empty recipe, invalid!
    }

    public int getIndex() {
        return hash;
    }

    @Override
    public String toString() {
        return getType() + "{" + getName() + "}";
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof BaseRecipe)) {
            return false;
        }

        return obj.hashCode() == hashCode();
    }

    /**
     * Register recipe with the server and RecipeManager.<br>
     * Alias for RecipeManager.getRecipes().registerRecipe(this);
     */
    public void register() {
        RecipeManager.getRecipes().registerRecipe(this);
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

    public boolean hasFlag(FlagType type) {
        boolean hasFlag = false;

        if (flags != null) {
            hasFlag = flags.hasFlag(type);
        }

        return hasFlag;
    }

    public boolean hasFlags() {
        return flags != null;
    }

    public boolean hasNoShiftBit() {
        boolean hasNoShiftBit = true;

        if (flags != null) {
            hasNoShiftBit = flags.hasNoShiftBit();
        }

        return hasNoShiftBit;
    }

    public Flag getFlag(FlagType type) {
        Flag flag = null;

        if (flags != null) {
            flag = flags.getFlag(type);
        }

        return flag;
    }

    public <T extends Flag> T getFlag(Class<T> flagClass) {
        T t = null;

        if (flags != null) {
            t = flags.getFlag(flagClass);
        }

        return t;
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
        getFlags().addFlag(flag);
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

    /**
     * @return Recipe short string for book contents index
     */
    public String printBookIndex() {
        return RMCChatColor.RED + "(undefined)";
    }

    /**
     * @return Recipe detail string that can fit inside a book.
     */
    public String printBook() {
        return RMCChatColor.RED + "(undefined)";
    }

    /**
     * @return Recipe detail string that can fit in the chat.
     */
    public String printChat() {
        String print = printBook();

        print = print.replace(RMCChatColor.WHITE.toString(), RMCChatColor.MAGIC.toString());
        print = print.replace(RMCChatColor.BLACK.toString(), RMCChatColor.WHITE.toString());
        print = print.replace(RMCChatColor.MAGIC.toString(), RMCChatColor.BLACK.toString());

        return print;
    }
}
