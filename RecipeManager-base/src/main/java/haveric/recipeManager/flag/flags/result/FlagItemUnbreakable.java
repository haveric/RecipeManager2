package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagItemUnbreakable extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.ITEM_UNBREAKABLE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
                "{flag} [false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
                "Makes the result unbreakable",
                "",
                "Optionally, adding false will make the result breakable again", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} // Makes the result unbreakable",
                "{flag} false // Remove the unbreakable status, allowing for the item to be destroyed",};
    }

    private boolean unbreakable;

    public FlagItemUnbreakable() {
        unbreakable = true;
    }

    public FlagItemUnbreakable(FlagItemUnbreakable flag) {
        super(flag);
        unbreakable = flag.unbreakable;
    }

    public void setUnbreakable(boolean isUnbreakable) {
        unbreakable = isUnbreakable;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    @Override
    public FlagItemUnbreakable clone() {
        return new FlagItemUnbreakable((FlagItemUnbreakable) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value != null && value.equalsIgnoreCase("false")) {
            unbreakable = false;
        } else {
            unbreakable = true;
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(unbreakable);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "unbreakable: " + unbreakable;

        return toHash.hashCode();
    }
}
