package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagCustomModelData extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.CUSTOM_MODEL_DATA;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <number>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes result's custom model data.",
            "Used with custom datapacks", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 7",
            "{flag} 1234567" , };
    }


    private int customModelData = Integer.MIN_VALUE;

    public FlagCustomModelData() {
    }

    public FlagCustomModelData(FlagCustomModelData flag) {
        customModelData = flag.customModelData;
    }

    @Override
    public FlagCustomModelData clone() {
        return new FlagCustomModelData((FlagCustomModelData) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int newData) {
        customModelData = newData;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        try {
            customModelData = Integer.parseInt(value);
        } catch(NumberFormatException e) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid number: " + value);
            return false;
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
                int modelData = customModelData;
                if (modelData != Integer.MIN_VALUE) {
                    meta.setCustomModelData(modelData);
                    a.result().setItemMeta(meta);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "customModelData: " + customModelData;

        return toHash.hashCode();
    }
}
