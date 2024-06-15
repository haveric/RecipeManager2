package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;

public class FlagOminousBottleItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.OMINOUS_BOTTLE_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <amplifier>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the amplifier amount for an Ominous Bottle's bad omen effect",
            "  Value must be between 0 and 4", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 4", };
    }


    private Integer amplifier = null;

    public FlagOminousBottleItem() {
    }

    public FlagOminousBottleItem(FlagOminousBottleItem flag) {
        super(flag);
        amplifier = flag.amplifier;
    }

    @Override
    public FlagOminousBottleItem clone() {
        return new FlagOminousBottleItem((FlagOminousBottleItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(int newAmplifier) {
        amplifier = newAmplifier;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (!Version.has1_20_5Support()) {
            return false;
        }

        if (result != null && (result.getItemMeta() instanceof OminousBottleMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();

        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), OminousBottleMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an ominous bottle!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        try {
            amplifier = Integer.parseInt(value);

            if (amplifier < 0 || amplifier > 4) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid amplifier value: " + value, "Amplifier is limited from 0 - 4.");
                return false;
            }
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

            if (!(meta instanceof OminousBottleMeta)) {
                a.addCustomReason("Needs ominous bottle");
                return;
            }
            if (amplifier != null) {
                OminousBottleMeta bottleMeta = (OminousBottleMeta) meta;
                bottleMeta.setAmplifier(amplifier);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "amplifier: " + amplifier;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String argLower, boolean noMeta) {
        Integer value = null;
        if (argLower.startsWith("!ominousbottleamplifier") || argLower.startsWith("noominousbottleamplifier")) {
            value = Integer.MIN_VALUE;
        } else if (argLower.startsWith("ominousbottleamplifier")) {
            String argTrimmed = argLower.substring("ominousbottleamplifier".length()).trim();

            try {
                value = Integer.parseInt(argTrimmed);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'ominousbottleamplifier' argument with invalid number: " + argTrimmed);
            }
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            Integer finalValue = value;
            return new ConditionInteger("ominousbottleamplifier", finalValue, (item, meta, condition) -> {
                ConditionInteger conditionInteger = (ConditionInteger) condition;
                boolean isOminousBottleMeta = meta instanceof OminousBottleMeta;
                if (noMeta || finalValue == Integer.MIN_VALUE) {
                    return !isOminousBottleMeta || !((OminousBottleMeta) meta).hasAmplifier();
                }

                if (isOminousBottleMeta && ((OminousBottleMeta) meta).hasAmplifier()) {
                    return !conditionInteger.hasValue() || ((OminousBottleMeta) meta).getAmplifier() == conditionInteger.getValue();
                }

                return false;
            });
        }
    }
}
