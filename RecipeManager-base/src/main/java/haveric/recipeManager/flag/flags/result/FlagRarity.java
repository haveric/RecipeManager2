package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.condition.Condition;
import haveric.recipeManager.flag.conditions.condition.ConditionString;
import org.bukkit.inventory.ItemRarity;
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
    public Condition parseCondition(String argLower, boolean noMeta) {
        String value = null;
        String conditionName = getConditionName();
        if (argLower.startsWith("!" + conditionName) || argLower.startsWith("no" + conditionName)) {
            value = "NO_VALUE";
        } else if (argLower.startsWith(conditionName)) {
            String argTrimmed = argLower.substring(conditionName.length()).trim();
            try {
                value = String.valueOf(ItemRarity.valueOf(argTrimmed));
            } catch (IllegalArgumentException e) {
                ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid '" + conditionName + "' name: " + argTrimmed, "Valid rarity values: " + RMCUtil.collectionToString(Arrays.asList(ItemRarity.values())).toLowerCase());
            }
        }

        if (!noMeta && value == null) {
            return null;
        } else {
            String finalValue = value;
            return new ConditionString(conditionName, finalValue, (item, meta, condition) -> {
                ConditionString conditionString = (ConditionString) condition;
                if (noMeta || finalValue.equals("NO_VALUE")) {
                    return !meta.hasRarity();
                }

                if (meta.hasRarity()) {
                    return !conditionString.hasValue() || meta.getRarity() == ItemRarity.valueOf(conditionString.getValue());
                }

                return false;
            });
        }
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
            "  norarity or !rarity = Ingredient must not have a rarity",
        };
    }
}
