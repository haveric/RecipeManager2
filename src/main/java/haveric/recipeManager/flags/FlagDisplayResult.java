package haveric.recipeManager.flags;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.WorkbenchRecipe;
import haveric.recipeManager.tools.Tools;

public class FlagDisplayResult extends Flag {

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
            "Or you can set the item as 'first' to use the first display result available, very useful for multiple results having " + FlagType.INGREDIENTCONDITION + " flag on them.",
            "",
            "Optionally, using 'silentfail' argument you can make the recipe print no result if it wouldn't give anything in the case of no results being allowed to craft (by other flags, like " + FlagType.INGREDIENTCONDITION + ").",
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
     * @param displayItem
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
    protected boolean onValidate() {
        BaseRecipe recipe = getRecipe();

        if (!(recipe instanceof WorkbenchRecipe)) {
            ErrorReporter.error("Flag " + getType() + " can only be used on workbench recipes.");
            return false;
        }

        return true;
    }

    @Override
    public boolean onParse(String value) {
        String[] args = value.toLowerCase().split("\\|");

        value = args[0].trim();

        if (!value.equals("first")) {
            ItemStack item = Tools.parseItem(value, 0);

            if (item == null || item.getType() == Material.AIR) {
                ErrorReporter.warning("Flag " + getType() + " has invalid item defined!");
                return false;
            }

            setDisplayItem(item);
        }

        if (args.length > 1) {
            value = args[1].trim();

            if (value.equals("silentfail")) {
                setSilentFail(true);
            } else {
                ErrorReporter.warning("Flag " + getType() + " has unknown argument: " + value);
            }
        }

        return true;
    }
}
