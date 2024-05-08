package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionString;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FlagRarity extends Flag {
    @Override
    public String getFlagType() {
        return FlagType.RARITY;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <rarity>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets the item's rarity",
            "",
            "Rarity values: " + RMCUtil.collectionToString(Arrays.asList(ItemRarity.values())).toLowerCase(), };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} epic // Sets the rarity to epic with a light purple name",
            "{flag} uncommon // Sets the rarity to uncommon with a yellow name", };
    }

    private ItemRarity itemRarity;

    public FlagRarity() {

    }

    public FlagRarity(FlagRarity flag) {
        super(flag);
        itemRarity = flag.itemRarity;
    }

    public void setItemRarity(ItemRarity rarity) {
        itemRarity = rarity;
    }

    public ItemRarity getItemRarity() {
        return itemRarity;
    }

    @Override
    public FlagRarity clone() {
        return new FlagRarity((FlagRarity) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|");

        value = split[0].trim().toUpperCase();

        try {
            itemRarity = ItemRarity.valueOf(value);
        } catch (IllegalArgumentException e) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid rarity name: " + value, "Valid rarity values: " + RMCUtil.collectionToString(Arrays.asList(ItemRarity.values())).toLowerCase());
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
                meta.setRarity(itemRarity);

                a.result().setItemMeta(meta);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "itemRarity: " + itemRarity;

        return toHash.hashCode();
    }

    @Override
    public Condition parseCondition(String arg, boolean noMeta) {
        String conditionName = getConditionName();
        ConditionString returnCondition = new ConditionString(conditionName, getFlagType(), arg, noMeta);

        String lastValue = "";
        try {
            for (String value : returnCondition.getValues()) {
                lastValue = value;
                ItemRarity.valueOf(value);
            }

            for (String value : returnCondition.getNegativeValues()) {
                lastValue = value;
                ItemRarity.valueOf(value);
            }
        } catch (IllegalArgumentException e) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid '" + conditionName + "' name: " + lastValue, "Valid rarity values: " + RMCUtil.collectionToString(Arrays.asList(ItemRarity.values())).toLowerCase());
            return null;
        }

        if (returnCondition.skipCondition()) {
            return null;
        }

        returnCondition.setCheckCallback((item, meta, condition) -> {
            ConditionString callbackCondition = (ConditionString) condition;
            if (callbackCondition.shouldHaveNoMeta()) {
                return !meta.hasRarity();
            }

            if (meta.hasRarity()) {
                return !callbackCondition.hasValue() || callbackCondition.contains(meta.getRarity().name());
            }

            return false;
        });

        return returnCondition;
    }

    @Override
    public String getConditionName() {
        return "rarity";
    }

    @Override
    public String[] getConditionDescription() {
        return new String[] {
            "  rarity <rarity> = Ingredient must have a specific rarity",
            "    Rarity values: " + RMCUtil.collectionToString(Arrays.asList(ItemRarity.values())).toLowerCase(),
            "    <rarity> supports multiple values that are comma separated: <rarity1>, <rarity2>, <rarity3>",
            "    <rarity> supports negative matching by preceding a rarity with an exclamation mark `!`: !<rarity1>, !<rarity2>",
            "    <rarity> any combination of the above can be combined together: <rarity1>, !<rarity2>",
            "      Matching for <rarity> must match ANY of the non-negative values and NONE of the negative values",
            "  norarity or !rarity = Ingredient must not have a rarity",
        };
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        parse(meta, recipeString, Files.NL + "@rarity ");
    }

    @Override
    public void parseIngredientForConditions(ItemStack item, ItemMeta meta, StringBuilder ingredientCondition) {
        parse(meta, ingredientCondition, " | rarity ");
    }

    private void parse(ItemMeta meta, StringBuilder builder, String prefix) {
        if (meta != null && meta.hasRarity()) {
            builder.append(prefix).append(meta.getRarity());
        }
    }
}
