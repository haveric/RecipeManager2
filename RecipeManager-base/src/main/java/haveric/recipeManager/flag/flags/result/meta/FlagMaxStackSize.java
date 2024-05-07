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

public class FlagMaxStackSize extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MAX_STACK_SIZE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <number>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes result's stack size (from 1 to 99)", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 99", };
    }


    private Integer maxStackSize = null;

    public FlagMaxStackSize() {
    }

    public FlagMaxStackSize(FlagMaxStackSize flag) {
        super(flag);
        maxStackSize = flag.maxStackSize;
    }

    @Override
    public FlagMaxStackSize clone() {
        return new FlagMaxStackSize((FlagMaxStackSize) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public int getStackSize() {
        return maxStackSize;
    }

    public void setStackSize(int newStackSize) {
        maxStackSize = newStackSize;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        try {
            maxStackSize = Integer.parseInt(value);

            if (maxStackSize < 1 || maxStackSize > 99) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid stack size: " + value, "Stack size is limited from 1 - 99.");
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
            if (meta != null && maxStackSize != null) {
                meta.setMaxStackSize(maxStackSize);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "maxStackSize: " + maxStackSize;

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
                    return !meta.hasMaxStackSize();
                }

                if (meta.hasMaxStackSize()) {
                    return !conditionInteger.hasValue() || meta.getMaxStackSize() == conditionInteger.getValue();
                }

                return false;
            });
        }
    }

    @Override
    public String getConditionName() {
        return "maxstacksize";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  maxstacksize <amount> = Ingredient must have a custom max stack size",
            "  nomaxstacksize or !maxstacksize = Ingredient must not have a custom max stack size",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@maxstacksize ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | maxstacksize ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta != null && meta.hasMaxStackSize()) {
            builder.append(prefix).append(meta.getMaxStackSize());
        }
    }
}
