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
        Boolean condition = null;
        if (argLower.startsWith("!fireresistant") || argLower.startsWith("nofireresistant")) {
            condition = false;
        } else if (argLower.startsWith("fireresistant")) {
            condition = true;
        }

        if (!noMeta && condition == null) {
            return null;
        } else {
            return new ConditionBoolean("fireresistant", condition, (item, meta, hasValue, value) -> {
                boolean isFireResistant = meta.isFireResistant();
                if (noMeta) {
                    return !isFireResistant;
                } else {
                    return !hasValue || isFireResistant == value;
                }
            });
        }
    }
}
