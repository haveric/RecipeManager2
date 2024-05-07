package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagMaxDamage extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MAX_DAMAGE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <damage>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the result's max damage/durability", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 2000", };
    }


    private Integer maxDamage = null;

    public FlagMaxDamage() {
    }

    public FlagMaxDamage(FlagMaxDamage flag) {
        super(flag);
        maxDamage = flag.maxDamage;
    }

    @Override
    public FlagMaxDamage clone() {
        return new FlagMaxDamage((FlagMaxDamage) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int newMaxDamage) {
        maxDamage = newMaxDamage;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        try {
            maxDamage = Integer.parseInt(value);
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

            if (meta instanceof Damageable && maxDamage != null) {
                Damageable damageable = (Damageable) meta;
                damageable.setMaxDamage(maxDamage);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "maxDamage: " + maxDamage;

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
                boolean isDamageableMeta = meta instanceof Damageable;
                if (noMeta || finalValue == Integer.MIN_VALUE) {
                    return !isDamageableMeta || !((Damageable) meta).hasMaxDamage();
                }

                if (condition.hasValue()) {
                    return true;
                }

                if (isDamageableMeta && ((Damageable) meta).hasMaxDamage()) {
                    return !conditionInteger.hasValue() || ((Damageable) meta).getMaxDamage() == conditionInteger.getValue();
                }

                return false;
            });
        }
    }

    @Override
    public String getConditionName() {
        return "maxdamage";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  maxdamage <amount> = Ingredient must have max damage/durability",
            "  nomaxdamage or !maxdamage = Ingredient must not have max damage/durability",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@maxdamage ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | maxdamage ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            if (damageable.hasMaxDamage()) {
                builder.append(prefix).append(damageable.getMaxDamage());
            }
        }
    }
}
