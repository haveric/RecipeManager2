package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionBoolean;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagFireResistant extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.FIRE_RESISTANT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
                "{flag} [false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
                "Makes the result immune to burning in fire",
                "",
                "Optionally, adding false will remove the fire immunity", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} // Makes the result immune to burning in fire",
                "{flag} false // Removes the fire immunity", };
    }

    private boolean fireResistant;

    public FlagFireResistant() {
        fireResistant = true;
    }

    public FlagFireResistant(FlagFireResistant flag) {
        super(flag);
        fireResistant = flag.fireResistant;
    }

    public void setFireResistant(boolean resistant) {
        fireResistant = resistant;
    }

    public boolean isFireResistant() {
        return fireResistant;
    }

    @Override
    public FlagFireResistant clone() {
        return new FlagFireResistant((FlagFireResistant) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value != null && value.equalsIgnoreCase("false")) {
            fireResistant = false;
        } else {
            fireResistant = true;
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
                meta.setFireResistant(fireResistant);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "fireResistant: " + fireResistant;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String argLower, boolean noMeta) {
        Boolean value = null;
        if (argLower.startsWith("!fireresistant") || argLower.startsWith("nofireresistant")) {
            value = false;
        } else if (argLower.startsWith("fireresistant")) {
            value = true;
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            return new ConditionBoolean("fireresistant", value, (item, meta, condition) -> {
                ConditionBoolean conditionBoolean = (ConditionBoolean) condition;
                boolean isFireResistant = meta.isFireResistant();
                if (noMeta) {
                    return !isFireResistant;
                } else {
                    return !conditionBoolean.hasValue() || isFireResistant == conditionBoolean.getValue();
                }
            });
        }
    }
}
