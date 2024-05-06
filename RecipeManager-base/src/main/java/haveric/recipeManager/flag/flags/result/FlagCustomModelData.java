package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
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
            "{flag} 1234567", };
    }


    private Integer customModelData;

    public FlagCustomModelData() {
    }

    public FlagCustomModelData(FlagCustomModelData flag) {
        super(flag);
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
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
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
            if (meta != null && customModelData != null) {
                meta.setCustomModelData(customModelData);
                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "customModelData: " + customModelData;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String argLower, boolean noMeta) {
        Integer value = null;
        String conditionName = getConditionName();
        if (argLower.startsWith("!" + conditionName) || argLower.startsWith("no" + conditionName)) {
            value = Integer.MIN_VALUE;
        } else if (argLower.startsWith(conditionName)) {
            String argTrimmed = argLower.substring(conditionName.length()).trim();

            try {
                value = Integer.parseInt(argTrimmed);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has '" + conditionName + "' argument with invalid number: " + argTrimmed);
            }
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            Integer finalValue = value;
            return new ConditionInteger(conditionName, finalValue, (item, meta, condition) -> {
                ConditionInteger conditionInteger = (ConditionInteger) condition;
                if (noMeta || finalValue == Integer.MIN_VALUE) {
                    return !meta.hasCustomModelData();
                }

                if (condition.hasValue()) {
                    return true;
                }

                if (meta.hasCustomModelData()) {
                    return !conditionInteger.hasValue() || meta.getCustomModelData() == conditionInteger.getValue();
                }

                return false;
            });
        }
    }

    @Override
    public String getConditionName() {
        return "custommodeldata";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  custommodeldata <number> = Ingredient must have custom model data",
            "  nocustommodeldata or !custommodeldata = Ingredient must not have custom model data",
        };
    }
}
