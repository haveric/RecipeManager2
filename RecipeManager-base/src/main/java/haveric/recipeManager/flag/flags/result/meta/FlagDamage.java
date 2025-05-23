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

            if (meta instanceof Damageable damageable && damage != null) {
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
    public Condition parseCondition(String arg, boolean noMeta) {
        ConditionInteger returnCondition = new ConditionInteger(getConditionName(), getFlagType(), arg, noMeta);
        if (returnCondition.skipCondition()) {
            return null;
        }

        returnCondition.setCheckCallback((item, meta, condition) -> {
            ConditionInteger callbackCondition = (ConditionInteger) condition;
            boolean isDamageableMeta = meta instanceof Damageable;
            if (callbackCondition.shouldHaveNoMeta()) {
                return !isDamageableMeta || !((Damageable) meta).hasDamage();
            }

            if (isDamageableMeta &&  ((Damageable) meta).hasDamage()) {
                return !callbackCondition.hasValue() || callbackCondition.contains(((Damageable) meta).getDamage());
            }

            return false;
        });

        return returnCondition;
    }

    @Override
    public String getConditionName() {
        return "damage";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  damage <number> = Ingredient must have damage/durability",
            "    <number> supports ranges: <min>-<max>",
            "    <number> supports multiple values that are comma separated: <number1>, <number2>, <number3>",
            "    <number> supports negative matching by preceding a number (or range) with an exclamation mark `!`: !<min>-<max>, !<number>",
            "    <number> any combination of the above can be combined together: <min>-<max>, <number1>, !<number2>, !<min>-<max>",
            "      Matching for <number> must match ANY of the non-negative values and NONE of the negative values",
            "  nodamage or !damage = Ingredient must not have damage/durability",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@damage ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | damage ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta instanceof Damageable damageable) {
            if (damageable.hasDamage()) {
                builder.append(prefix).append(damageable.getDamage());
            }
        }
    }
}
