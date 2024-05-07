package haveric.recipeManager.flag;

import com.google.common.base.Preconditions;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Perms;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Flag implements Cloneable {
    private Flags flagsContainer;
    protected String sourceFileName = "";
    protected int sourceLineNum = -1;

    protected Flag() { }

    protected Flag(Flag flag) {
        this.flagsContainer = flag.flagsContainer;
        this.sourceFileName = flag.sourceFileName;
        this.sourceLineNum = flag.sourceLineNum;
    }

    /*
     * Public tools/final methods
     */

    /**
     * @return The Flags object that holds this flag
     */
    public final Flags getFlagsContainer() {
        return flagsContainer;
    }

    public void setFlagsContainer(Flags flags) {
        flagsContainer = flags;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public int getSourceLineNum() {
        return sourceLineNum;
    }

    public boolean requiresRecipeManagerModification() {
        return true;
    }

    /**
     * Parses a string to get the values for this flag.<br>
     * Has different effects for each extension of Flag object.
     *
     * @param value
     *            the flag's value (not containing the <code>@flag</code> string)
     *
     * @return false if an error occurred and the flag should not be added
     */
    public final boolean parse(String value, String fileName, int lineNum, int restrictedBit) {
        return onParse(value, fileName, lineNum, restrictedBit);
    }

    /**
     * Check if player has the required permissions to skip this flag
     *
     * @param player
     */
    public final boolean hasFlagPermission(Player player) {
        if (player == null) {
            return false; // no player, no skip
        }

        if (Perms.hasFlagAll(player)) {
            return true; // has permission for all flags
        }

        FlagDescriptor desc = FlagFactory.getInstance().getFlagByName(getFlagType());
        for (String name : desc.getNames()) {
            if (Perms.hasFlagPrefix(player, name)) {
                return true; // has permission for this flag
            }
        }

        return false; // no permission for flag
    }

    /**
     * Check if the flag allows to craft with these arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     *
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public final void check(Args a) {
        if (hasFlagPermission(a.player())) {
            onCheck(a);
        }
    }

    public final void prepare(Args a) {
        prepare(a, false);
    }
    /**
     * Apply the flag's effects - triggered when recipe is prepared or result is displayed
     *
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public final void prepare(Args a, boolean ignorePermission) {
        if (ignorePermission || hasFlagPermission(a.player())) {
            onPrepare(a);
        }
    }

    public final void crafted(Args a) {
        crafted(a, false);
    }

    /**
     * Apply the flag's effects to the arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     *
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public final void crafted(Args a, boolean ignorePermission) {
        if (ignorePermission || hasFlagPermission(a.player())) {
            onCrafted(a);
        }
    }

    /**
     * Trigger flag failure as if it failed due to multi-result chance.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * Adding reasons to this will display them to the crafter.
     *
     * @param a
     */
    public final void failed(Args a) {
        onFailed(a);
    }

    public final void fuelEnd(Args a) {
        onFuelEnd(a);
    }

    public final void fuelRandom(Args a) {
        onFuelRandom(a);
    }

    /**
     * Removes the flag from its flag list container.<br>
     * This also notifies the flag of removal, it might do some stuff before removal.<br>
     * If the flag hasn't been added to any flag list, this method won't do anything.
     */
    public final void remove() {
        if (flagsContainer != null) {
            flagsContainer.removeFlag(this);
            onRemove();
        }
    }

    /**
     * Clones the flag and assigns it to a new flag container
     *
     * @param container
     * @return
     */
    public final Flag clone(Flags container) {
        Flag flag = clone();
        flag.flagsContainer = container;
        return flag;
    }

    /**
     * Returns the hashCode of the flag's type enum.
     */
    @Override
    public int hashCode() {
        int hashCode;

        if (getFlagType() == null) {
            hashCode = 0;
        } else {
            hashCode = getFlagType().hashCode();
        }

        return hashCode;
    }

    /**
     * Warning: this method doesn't check flag's values, it only compares flag type!
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Flag && obj.hashCode() == hashCode();
    }

    public abstract String getFlagType();

    protected abstract String[] getArguments();

    protected abstract String[] getDescription();

    protected abstract String[] getExamples();

    protected final Flaggable getFlaggable() {
        Flaggable flaggable = null;
        if (flagsContainer != null) {
            flaggable = flagsContainer.flaggable;
        }
        return flaggable;
    }

    protected final BaseRecipe getRecipe() {
        Flaggable flaggable = getFlaggable();
        BaseRecipe baseRecipe = null;
        if (flaggable instanceof BaseRecipe) {
            baseRecipe = (BaseRecipe) flaggable;
        }

        return baseRecipe;
    }

    protected final BaseRecipe getRecipeDeep() {
        Flaggable flaggable = getFlaggable();

        if (flaggable instanceof BaseRecipe) {
            return (BaseRecipe) flaggable;
        }

        ItemResult result = getResult();

        if (result != null) {
            return result.getRecipe();
        }

        return null;
    }

    protected final FlaggableRecipeChoice getFlaggableRecipeChoice() {
        Flaggable flaggable = getFlaggable();
        FlaggableRecipeChoice flaggableRecipeChoice = null;
        if (flaggable instanceof FlaggableRecipeChoice) {
            flaggableRecipeChoice = (FlaggableRecipeChoice) flaggable;
        }
        return flaggableRecipeChoice;
    }

    protected final ItemResult getResult() {
        Flaggable flaggable = getFlaggable();
        ItemResult itemResult = null;
        if (flaggable instanceof ItemResult) {
            itemResult = (ItemResult) flaggable;
        }
        return itemResult;
    }

    public final boolean validateParse(String value, int restrictedBit) {
        Preconditions.checkNotNull(getFlagType(), "This can't be used on a blank flag!");

        FlagDescriptor desc = FlagFactory.getInstance().getFlagByName(getFlagType());
        if (!desc.hasBit(FlagBit.NO_VALUE_REQUIRED) && value == null) {
            return ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " needs a value!");
        }

        if (!desc.hasBit(FlagBit.NO_FALSE) && value != null && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("remove"))) {
            remove();
            return false;
        }

        return validate(restrictedBit);
    }

    public final boolean validate(int restrictedBit) {
        FlagDescriptor desc = FlagFactory.getInstance().getFlagByName(getFlagType());

        if (restrictedBit == FlagBit.RESULT && !desc.hasBit(FlagBit.RESULT)) {
            return ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " not supported on results!");
        }

        if (restrictedBit == FlagBit.INGREDIENT && !desc.hasBit(FlagBit.INGREDIENT)) {
            return ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " not supported on ingredients!");
        }

        if (restrictedBit == FlagBit.RECIPE && !desc.hasBit(FlagBit.RECIPE)) {
            return ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " not supported on recipes!");
        }

        return onValidate();
    }

    protected final void registered() {
        onRegistered();
    }

    /*
     * Overwritable methods/events
     */

    @Override
    public Flag clone() {
        return this; // pointless to clone an empty flag
    }

    /**
     * Validates if this flag is allowed when parsing the recipe/results.
     *
     * @return whether the flag is allowed to be added to the given recipe
     */
    public boolean onValidate() {
        return (getFlagType() != null);
    }

    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        this.sourceFileName = fileName;
        this.sourceLineNum = lineNum;
        return false; // it didn't parse anything
    }

    public void onRegistered() { }

    public void onRemove() { }

    public void onCheck(Args a) { }

    public void onPrepare(Args a) { }

    public void onCrafted(Args a) { }

    public void onFailed(Args a) { }

    public void onFuelEnd(Args a) { }

    public void onFuelRandom(Args a) { }


    protected boolean canAddMeta(Args a) {
        boolean canAdd;

        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            canAdd = false;
        } else {
            canAdd = a.result().getItemMeta() != null;
        }

        return canAdd;
    }

    protected void addResultLore(Args a, String lore) {
        addResultLores(a, Collections.singletonList(lore));
    }

    protected void addResultLores(Args a, List<String> lores) {
        if (lores.isEmpty()) {
            return;
        }

        ItemMeta meta = a.result().getItemMeta();
        if (meta != null) {
            List<String> newLore = meta.getLore();

            if (newLore == null) {
                newLore = new ArrayList<>();
            }

            newLore.addAll(lores);

            meta.setLore(newLore);
            a.result().setItemMeta(meta);
        }
    }

    protected void addNBTRaw(Args a, String nbtRaw) {
        Bukkit.getUnsafe().modifyItemStack(a.result().getItemStack(), nbtRaw);
    }

    public Condition parseCondition(String argLower, boolean noMeta) {
        return null;
    }

    public String getConditionName() {
        return null;
    }

    public String[] getConditionDescription() {
        return null;
    }

    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {}
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {}
}
