package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Perms;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class Flag implements Cloneable {
    protected Flags flagsContainer;
    private static final String[] EMPTY_STRING = {};

    protected Flag() { }

    /*
     * Public tools/final methods
     */

    /**
     * @return The Flags object that holds this flag
     */
    public final Flags getFlagsContainer() {
        return flagsContainer;
    }

    /**
     * Parses a string to get the values for this flag.<br>
     * Has different effects for each extension of Flag object.
     *
     * @param value
     *            the flag's value (not containing the <code>@flag</code> string)
     * @return
     * @return false if an error occurred and the flag should not be added
     */
    public final boolean parse(String value) {
        return onParse(value);
    }

    /**
     * Check if player has the required permissions to skip this flag
     *
     * @param player
     * @return
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

    /**
     * Apply the flag's effects - triggered when recipe is prepared or result is displayed
     *
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public final void prepare(Args a) {
        if (hasFlagPermission(a.player())) {
            onPrepare(a);
        }
    }

    /**
     * Apply the flag's effects to the arguments.<br>
     * Any and all arguments can be null if you don't have values for them.<br>
     * To make the check fail you <b>must</b> add a reason to the argument!
     *
     * @param a
     *            the arguments class for easily maintainable argument class
     */
    public final void crafted(Args a) {
        if (hasFlagPermission(a.player())) {
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

    /*
     * Non-public tools/final methods
     */

    protected String[] getArguments() {
        return EMPTY_STRING;
    }

    protected String[] getDescription() {
        return EMPTY_STRING;
    }

    protected String[] getExamples() {
        return EMPTY_STRING;
    }

    protected String getFlagType() {
        return null;
    }

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

    protected final ItemResult getResult() {
        Flaggable flaggable = getFlaggable();
        ItemResult itemResult = null;
        if (flaggable instanceof ItemResult) {
            itemResult = (ItemResult) flaggable;
        }
        return itemResult;
    }

    protected final boolean validateParse(String value) {
        Validate.notNull(getFlagType(), "This can't be used on a blank flag!");

        FlagDescriptor desc = FlagFactory.getInstance().getFlagByName(getFlagType());
        if (!desc.hasBit(FlagBit.NO_VALUE) && value == null) {
            ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " needs a value!");
            return false;
        }

        if (!desc.hasBit(FlagBit.NO_FALSE) && value != null && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("remove"))) {
            remove();
            return false;
        }

        return validate();
    }

    protected final boolean validate() {
        Flaggable flaggable = getFlaggable();

        FlagDescriptor desc = FlagFactory.getInstance().getFlagByName(getFlagType());
        if (desc.hasBit(FlagBit.RESULT) && !(flaggable instanceof ItemResult)) {
            ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " only works on results!");
            return false;
        }

        if (desc.hasBit(FlagBit.RECIPE) && !(flaggable instanceof BaseRecipe) && flaggable instanceof ItemResult) {
            ErrorReporter.getInstance().error("Flag " + desc.getNameDisplay() + " only works on recipes!");
            return false;
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

    protected boolean onValidate() {
        return (getFlagType() != null);
    }

    protected boolean onParse(String value) {
        return false; // it didn't parse anything
    }

    protected void onRegistered() { }

    protected void onRemove() { }

    protected void onCheck(Args a) { }

    protected void onPrepare(Args a) { }

    protected void onCrafted(Args a) { }

    protected void onFailed(Args a) { }

    protected void onFuelEnd(Args a) { }

    protected void onFuelRandom(Args a) { }

    public String getResultLore() {
        return null;
    }
}
