package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionBoolean;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagHideTooltip extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.HIDE_TOOLTIP;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
                "{flag} [false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
                "Hides the result's tooltip",
                "",
                "Optionally, adding false will make the result show its tooltip again", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
                "{flag} // Hides the result's tooltip",
                "{flag} false // Disable this flag, allowing the result to have a tooltip again", };
    }

    private boolean hideTooltip;

    public FlagHideTooltip() {
        hideTooltip = true;
    }

    public FlagHideTooltip(FlagHideTooltip flag) {
        super(flag);
        hideTooltip = flag.hideTooltip;
    }

    public void setHideTooltip(boolean isHideTooltip) {
        hideTooltip = isHideTooltip;
    }

    public boolean isHideTooltip() {
        return hideTooltip;
    }

    @Override
    public FlagHideTooltip clone() {
        return new FlagHideTooltip((FlagHideTooltip) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value != null && value.equalsIgnoreCase("false")) {
            hideTooltip = false;
        } else {
            hideTooltip = true;
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
                meta.setHideTooltip(hideTooltip);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "hideTooltip: " + hideTooltip;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String argLower, boolean noMeta) {
        Boolean value = null;
        if (argLower.startsWith("!hidetooltip") || argLower.startsWith("nohidetooltip")) {
            value = false;
        } else if (argLower.startsWith("hidetooltip")) {
            value = true;
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            return new ConditionBoolean("hidetooltip", value, (item, meta, condition) -> {
                ConditionBoolean conditionBoolean = (ConditionBoolean) condition;
                boolean isHideTooltip = meta.isHideTooltip();
                if (noMeta) {
                    return !isHideTooltip;
                } else {
                    return !conditionBoolean.hasValue() || isHideTooltip == conditionBoolean.getValue();
                }
            });
        }
    }
}
