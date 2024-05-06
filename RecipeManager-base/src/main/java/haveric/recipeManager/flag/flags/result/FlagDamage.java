package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionInteger;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagDamage extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.DAMAGE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <damage>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the result's remaining damage/durability", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 50", };
    }


    private Integer damage = null;

    public FlagDamage() {
    }

    public FlagDamage(FlagDamage flag) {
        super(flag);
        damage = flag.damage;
    }

    @Override
    public FlagDamage clone() {
        return new FlagDamage((FlagDamage) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int newDamage) {
        damage = newDamage;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        try {
            damage = Integer.parseInt(value);

            // TODO: Check if durability is above max durability?
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

            if (meta instanceof Damageable && damage != null) {
                Damageable damageable = (Damageable) meta;
                damageable.setDamage(damage);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "damage: " + damage;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String argLower, boolean noMeta) {
        Integer value = null;
        if (argLower.startsWith("!damage") || argLower.startsWith("nodamage")) {
            value = Integer.MIN_VALUE;
        } else if (argLower.startsWith("damage")) {
            String argTrimmed = argLower.substring("damage".length()).trim();

            try {
                value = Integer.parseInt(argTrimmed);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'damage' argument with invalid number: " + argTrimmed);
            }
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            Integer finalValue = value;
            return new ConditionInteger("damage", finalValue, (item, meta, condition) -> {
                ConditionInteger conditionInteger = (ConditionInteger) condition;
                boolean isDamageableMeta = meta instanceof Damageable;
                if (noMeta || finalValue == Integer.MIN_VALUE) {
                    return !isDamageableMeta || !((Damageable) meta).hasDamage();
                }

                if (condition.hasValue()) {
                    return true;
                }

                if (isDamageableMeta && ((Damageable) meta).hasDamage()) {
                    return !conditionInteger.hasValue() || ((Damageable) meta).getDamage() == conditionInteger.getValue();
                }

                return false;
            });
        }
    }
}
