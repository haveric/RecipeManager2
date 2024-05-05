package haveric.recipeManager.flag.flags.result.applyEnchantment;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class FlagStoreEnchantment extends BaseFlagApplyStoreEnchantment {

    @Override
    public String getFlagType() {
        return FlagType.STORE_ENCHANTMENT;
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Stores the enchantments from items into the resulting enchanted book",
            "  If you want to apply usable enchantments to an item, use: " + FlagType.APPLY_ENCHANTMENT,
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
            "{flag} ingredientaction combine | ignorelevels | onlyitems // Combines all ingredients levels from items while allowing higher than vanilla allowed enchants", };
    }

    public FlagStoreEnchantment() { }

    public FlagStoreEnchantment(FlagStoreEnchantment flag) {
        super(flag);
    }

    @Override
    public FlagStoreEnchantment clone() {
        return new FlagStoreEnchantment((FlagStoreEnchantment) super.clone());
    }


    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof EnchantmentStorageMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), EnchantmentStorageMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an enchanted book!");
        }

        return true;
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasResult() || !a.hasInventoryView()) {
            a.addCustomReason("Needs inventory and result!");
            return;
        }

        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof EnchantmentStorageMeta)) {
                a.addCustomReason("Needs enchanted book!");
                return;
            }

            Map<Enchantment, Integer> enchantments = copyEnchantmentsByInventory(a);
            if (enchantments.isEmpty()) {
                return;
            }

            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                if (enchantmentStorageMeta.hasStoredEnchant(enchantment)) {
                    int currentLevel = enchantmentStorageMeta.getStoredEnchantLevel(enchantment);

                    if (resultAction == ApplyEnchantmentAction.SMALLEST && level < currentLevel) {
                        enchantmentStorageMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
                    } else if (resultAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
                        if (maxLevel > 1) {
                            level = Math.min(level, maxLevel);
                        }
                        enchantmentStorageMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
                    } else if (resultAction == ApplyEnchantmentAction.COMBINE) {
                        level += currentLevel;
                        if (maxLevel > 1) {
                            level = Math.min(level, maxLevel);
                        }
                        enchantmentStorageMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
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

                        enchantmentStorageMeta.addStoredEnchant(enchantment, newLevel, ignoreLevelRestriction);
                    }
                } else {
                    enchantmentStorageMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
                }
            }

            a.result().setItemMeta(enchantmentStorageMeta);
        }
    }
}
