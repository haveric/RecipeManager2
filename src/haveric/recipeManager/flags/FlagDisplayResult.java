package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.WorkbenchRecipe;
import haveric.recipeManager.tools.Tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class FlagDisplayResult extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.DISPLAYRESULT;

        A = new String[] { "{flag} <item or first> | [silentfail]", };

        D = new String[] { "Sets the display result of the recipe.", "Using this flag more than once will overwrite the previous message.", "", "As 'item' argument you can define an item like in a result, material:data:amount.", "Or you can set the item as 'first' to use the first display result available, very useful for multiple results having " + FlagType.INGREDIENTCONDITION + " flag on them.", "", "Optionally, using 'silentfail' argument you can make the recipe print no result if it wouldn't give anything in the case of no results being allowed to craft (by other flags, like " + FlagType.INGREDIENTCONDITION + ").", "", "NOTE: If there is no item to be displayed (all are secret or unavailable), using this with 'first' will not do anything.", "NOTE: Can only be used on workbench recipes because it can not have effect on other recipes.", };

        E = new String[] { "{flag} first // displays the first available result", "{flag} diamond_helmet:120 // damaged diamond helmet", };
    }

    // Flag code

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
        return new FlagDisplayResult(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
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
    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public boolean isSilentFail() {
        return silentFail;
    }

    public void setSilentFail(boolean silentFail) {
        this.silentFail = silentFail;
    }

    @Override
    protected boolean onValidate() {
        BaseRecipe recipe = getRecipe();

        if (recipe instanceof WorkbenchRecipe == false) {
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
