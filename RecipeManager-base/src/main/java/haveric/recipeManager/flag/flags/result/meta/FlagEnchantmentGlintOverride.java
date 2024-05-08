package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionBoolean;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagEnchantmentGlintOverride extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.ENCHANTMENT_GLINT_OVERRIDE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the result glint, even without enchantments",
            "",
            "Optionally, adding false will remove the glint", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} // Makes the result glint",
            "{flag} false // Removes the glint", };
    }

    private boolean glintOverride;

    public FlagEnchantmentGlintOverride() {
        glintOverride = true;
    }

    public FlagEnchantmentGlintOverride(FlagEnchantmentGlintOverride flag) {
        super(flag);
        glintOverride = flag.glintOverride;
    }

    public void setGlintOverride(boolean glint) {
        glintOverride = glint;
    }

    public boolean hasGlintOverride() {
        return glintOverride;
    }

    @Override
    public FlagEnchantmentGlintOverride clone() {
        return new FlagEnchantmentGlintOverride((FlagEnchantmentGlintOverride) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value != null && value.equalsIgnoreCase("false")) {
            glintOverride = false;
        } else {
            glintOverride = true;
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
                meta.setEnchantmentGlintOverride(glintOverride);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "glintOverride: " + glintOverride;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String arg, boolean noMeta) {
        String argLower = arg.toLowerCase();
        Boolean value = null;
        String conditionName = getConditionName();
        if (argLower.startsWith("!" + conditionName) || argLower.startsWith("no" + conditionName)) {
            value = false;
        } else if (argLower.startsWith(conditionName)) {
            value = true;
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            return new ConditionBoolean(conditionName, value, (item, meta, condition) -> {
                ConditionBoolean conditionBoolean = (ConditionBoolean) condition;
                boolean hasGlintOverride = meta.hasEnchantmentGlintOverride();
                if (noMeta) {
                    return !hasGlintOverride;
                } else {
                    return !conditionBoolean.hasValue() || hasGlintOverride == conditionBoolean.getValue();
                }
            });
        }
    }

    @Override
    public String getConditionName() {
        return "enchantmentglint";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  enchantmentglint = Ingredient must have the enchantmentglint flag",
            "  noenchantmentglint or !enchantmentglint = Ingredient must not have the enchantmentglint flag",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@enchantmentglint ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | enchantmentglint ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta != null && meta.hasEnchantmentGlintOverride()) {
            builder.append(prefix);
        }
    }
}
