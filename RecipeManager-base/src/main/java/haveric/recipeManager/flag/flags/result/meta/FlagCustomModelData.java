package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
import org.bukkit.inventory.ItemStack;
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
        ConditionInteger returnCondition = new ConditionInteger(getConditionName(), getFlagType(), argLower, noMeta);
        if (returnCondition.skipCondition()) {
            return null;
        }

        returnCondition.setCheckCallback((item, meta, condition) -> {
            ConditionInteger callbackCondition = (ConditionInteger) condition;
            if (callbackCondition.shouldHaveNoMeta()) {
                return !meta.hasCustomModelData();
            }

            if (meta.hasCustomModelData()) {
                return !callbackCondition.hasValue() || callbackCondition.contains(meta.getCustomModelData());
            }

            return false;
        });

        return returnCondition;
    }

    @Override
    public String getConditionName() {
        return "custommodeldata";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  custommodeldata <number> = Ingredient must have custom model data",
            "    <number> supports ranges: <min>-<max>",
            "    <number> supports multiple values that are comma separated: <number1>, <number2>, <number3>",
            "    <number> supports negative matching by preceding a number (or range) with an exclamation mark `!`: !<min>-<max>, !<number>",
            "    <number> any combination of the above can be combined together: <min>-<max>, <number1>, !<number2>, !<min>-<max>",
            "      Matching for <number> must match ANY of the non-negative values and NONE of the negative values",
            "  nocustommodeldata or !custommodeldata = Ingredient must not have custom model data",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@custommodeldata ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | custommodeldata ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta != null && meta.hasCustomModelData()) {
            builder.append(prefix).append(meta.getCustomModelData());
        }
    }
}
