package haveric.recipeManager.flag.flags.result.applyEnchantment;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class FlagApplyEnchantment extends BaseFlagApplyStoreEnchantment {

    @Override
    public String getFlagType() {
        return FlagType.APPLY_ENCHANTMENT;
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Applies the enchantments from books onto the result",
            "  If you want to store enchantments in an enchanted book, use: " + FlagType.STORE_ENCHANTMENT,
            "Using this flag more than once will overwrite the previous one.",
            "",
            "As '<arguments>' you must define at least one feature to copy from the ingredient to the result.",
            "Arguments can be one or more of the following, separated by | character:",
            "  ingredientaction <action> = (default largest) merge action for all of the ingredients",
            "  resultaction <action>     = (default largest) merge action applied to the result",
            "  ignorelevel               = Ignore enchantment level restrictions",
            "  maxlevel <level>          = Restrict the maximum level",
            "  onlybooks                 = Only copies enchantments from Enchanted Books. Without this, all item enchantments will be copied",
            "  onlyitems                 = Only copies enchantments from items with enchantments. Without this, all enchanted books will be copied as well",
            "    onlybooks and onlyitems are mutually exclusive. If you use one, the other is set to false. Using both will set the last defined.",
            "",
            "Actions include:",
            "  smallest = Use the smallest of the two enchantments if two are merged (Sharpness I + Sharpness III = Sharpness I)",
            "  largest  = Use the largest of the two enchantments if two are merged (Sharpness I + Sharpness III = Sharpness III)",
            "  combine  = Add the enchantment levels together (Sharpness I + Sharpness III = Sharpness IV)",
            "  anvil    = Combine enchantments similar to anvils (Sharpness I + Sharpness II = Sharpness II) and (Sharpness II + Sharpness II = Sharpness III)", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}",
            "{flag} resultaction combine // Combines the levels with the resulting item's enchants",
            "{flag} ingredientaction combine | ignorelevels | onlybooks // Combines all ingredients levels from books while allowing higher than vanilla allowed enchants", };
    }


    public FlagApplyEnchantment() { }

    public FlagApplyEnchantment(FlagApplyEnchantment flag) {
        super(flag);
    }

    @Override
    public FlagApplyEnchantment clone() {
        return new FlagApplyEnchantment((FlagApplyEnchantment) super.clone());
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasResult() || !a.hasInventoryView()) {
            a.addCustomReason("Needs inventory and result!");
            return;
        }

        Map<Enchantment, Integer> enchantments = copyEnchantmentsByInventory(a);
        if (enchantments.isEmpty()) {
            return;
        }

        ItemMeta resultMeta = a.result().getItemMeta();
        if (resultMeta == null) {
            return;
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (resultMeta.hasEnchant(enchantment)) {
                int currentLevel = resultMeta.getEnchantLevel(enchantment);
                if (resultAction == ApplyEnchantmentAction.SMALLEST && level < currentLevel) {
                    resultMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
                } else if (resultAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
                    if (maxLevel > 1) {
                        level = Math.min(level, maxLevel);
                    }
                    resultMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
                } else if (resultAction == ApplyEnchantmentAction.COMBINE) {
                    level += currentLevel;
                    if (maxLevel > 1) {
                        level = Math.min(level, maxLevel);
                    }
                    resultMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
                } else if (resultAction == ApplyEnchantmentAction.ANVIL) {
                    int newLevel;
                    if (level == currentLevel) {
                        newLevel = level + 1;
                    } else {
                        newLevel = Math.max(level, currentLevel);
                    }

                    if (maxLevel > 1) {
                        newLevel = Math.min(newLevel, maxLevel);
                    }

                    resultMeta.addEnchant(enchantment, newLevel, ignoreLevelRestriction);
                }
            } else {
                resultMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
            }
        }

        a.result().setItemMeta(resultMeta);
    }
}
