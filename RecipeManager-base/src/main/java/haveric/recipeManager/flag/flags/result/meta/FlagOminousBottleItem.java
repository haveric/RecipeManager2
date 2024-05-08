package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.inventory.ItemStack;
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
    public Condition parseCondition(String arg, boolean noMeta) {
        ConditionInteger returnCondition = new ConditionInteger(getConditionName(), getFlagType(), arg, noMeta);
        if (returnCondition.skipCondition()) {
            return null;
        }

        returnCondition.setCheckCallback((item, meta, condition) -> {
            ConditionInteger callbackCondition = (ConditionInteger) condition;
            boolean isOminousBottleMeta = meta instanceof OminousBottleMeta;
            if (callbackCondition.shouldHaveNoMeta()) {
                return !isOminousBottleMeta || !((OminousBottleMeta) meta).hasAmplifier();
            }

            if (isOminousBottleMeta &&  ((OminousBottleMeta) meta).hasAmplifier()) {
                return !callbackCondition.hasValue() || callbackCondition.contains(((OminousBottleMeta) meta).getAmplifier());
            }

            return false;
        });

        return returnCondition;
    }

    @Override
    public String getConditionName() {
        return "ominousbottleamplifier";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  ominousbottleamplifier <number> = Ingredient must have an ominous bottle amplifier",
            "    <number> supports ranges: <min>-<max>",
            "    <number> supports multiple values that are comma separated: <number1>, <number2>, <number3>",
            "    <number> supports negative matching by preceding a number (or range) with an exclamation mark `!`: !<min>-<max>, !<number>",
            "    <number> any combination of the above can be combined together: <min>-<max>, <number1>, !<number2>, !<min>-<max>",
            "      Matching for <number> must match ANY of the non-negative values and NONE of the negative values",
            "  noominousbottleamplifier or !ominousbottleamplifier = Ingredient must not have an ominous bottle amplifier",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@ominousbottle ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | ominousbottle ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta instanceof OminousBottleMeta ominousBottleMeta) {
            if (ominousBottleMeta.hasAmplifier()) {
                builder.append(prefix).append(ominousBottleMeta.getAmplifier());
            }
        }
    }
}
