package haveric.recipeManager.flag.flags.recipe;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.Tools;

public class FlagDisplayResult extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.DISPLAY_RESULT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <item or first> | [silentfail]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the display result of the recipe.",
            "Using this flag more than once will overwrite the previous message.",
            "",
            "As 'item' argument you can define an item like in a result, material:data:amount.",
            "Or you can set the item as 'first' to use the first display result available, very useful for multiple results having " + FlagType.INGREDIENT_CONDITION + " flag on them.",
            "",
            "Optionally, using 'silentfail' argument you can make the recipe print no result if it wouldn't give anything in the case of no results being allowed to craft (by other flags, like " + FlagType.INGREDIENT_CONDITION + ").",
            "",
            "NOTE: If there is no item to be displayed (all are secret or unavailable), using this with 'first' will not do anything.",
            "NOTE: Can only be used on workbench recipes because it can not have effect on other recipes.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} first // displays the first available result",
            "{flag} diamond_helmet:120 // damaged diamond helmet", };
    }


    private ItemStack displayItem;
    private boolean silentFail;

    public FlagDisplayResult() {
    }

    public FlagDisplayResult(FlagDisplayResult flag) {
        super(flag);
        displayItem = flag.displayItem;
        silentFail = flag.silentFail;
    }

    @Override
    public FlagDisplayResult clone() {
        return new FlagDisplayResult((FlagDisplayResult) super.clone());
    }

    /**
     * @return the item or null if it's set to use the first available from the recipe
     */
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    /**
     * @param newDisplayItem
     *            item or null to use first available of recipe
     */
    public void setDisplayItem(ItemStack newDisplayItem) {
        displayItem = newDisplayItem;
    }

    public boolean isSilentFail() {
        return silentFail;
    }

    public void setSilentFail(boolean newSilentFail) {
        silentFail = newSilentFail;
    }

    @Override
    public boolean onValidate() {
        BaseRecipe recipe = getRecipe();

        if (!(recipe instanceof PreparableResultRecipe)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " can only be used on workbench recipes.");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.toLowerCase().split("\\|");

        value = args[0].trim();

        if (!value.equals("first")) {
            ItemStack item = Tools.parseItem(value, 0);

            if (item == null || item.getType() == Material.AIR) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid item defined!");
                return false;
            }

            displayItem = item;
        }

        if (args.length > 1) {
            value = args[1].trim();

            if (value.equals("silentfail")) {
                silentFail = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + value);
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "displayItem: " + displayItem.hashCode();
        toHash += "silentFail: " + silentFail;

        return toHash.hashCode();
    }
}
