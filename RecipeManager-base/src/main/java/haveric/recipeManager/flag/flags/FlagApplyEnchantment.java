package haveric.recipeManager.flag.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class FlagApplyEnchantment extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.APPLY_ENCHANTMENT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Applies the enchantments from books onto the result",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "As '<arguments>' you must define at least one feature to copy from the ingredient to the result.",
            "Arguments can be one or more of the following, separated by | character:",
            "  ingredientaction <action> = (default largest) merge action for all of the ingredients",
            "  resultaction <action>     = (default largest) merge action applied to the result",
            "  ignorelevel               = Ignore enchantment level restrictions",
            "  onlybooks                 = Only copies enchantments from Enchanted Books. Without this, all item enchantments will be copied",
            "",
            "Actions include:",
            "  largest = Use the largest of the two enchantments if two are merged (Sharpness I + Sharpness III = Sharpness III)",
            "  combine = Add the enchantment levels together (Sharpness I + Sharpness III = Sharpness IV)",
            "", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}",
            "{flag} resultaction combine // Combines the levels with the resulting item's enchants",
            "{flag} ingredientaction combine | ignorelevels | onlybooks // Combines all ingredients levels from books while allowing higher than vanilla allowed enchants"};
    }

    public enum ApplyEnchantmentAction {
        LARGEST,
        COMBINE
    }

    private ApplyEnchantmentAction ingredientAction = ApplyEnchantmentAction.LARGEST;
    private ApplyEnchantmentAction resultAction = ApplyEnchantmentAction.LARGEST;
    private boolean ignoreLevelRestriction = false;
    private boolean onlyBooks = false;


    public FlagApplyEnchantment() { }

    public FlagApplyEnchantment(FlagApplyEnchantment flag) {
        ingredientAction = flag.ingredientAction;
        resultAction = flag.resultAction;
        ignoreLevelRestriction = flag.ignoreLevelRestriction;
        onlyBooks = flag.onlyBooks;
    }

    @Override
    public FlagApplyEnchantment clone() {
        return new FlagApplyEnchantment((FlagApplyEnchantment) super.clone());
    }

    public ApplyEnchantmentAction getIngredientAction() {
        return ingredientAction;
    }

    public ApplyEnchantmentAction getResultAction() {
        return resultAction;
    }

    public boolean getIgnoreLevelRestriction() {
        return ignoreLevelRestriction;
    }

    public boolean getOnlyBooks() {
        return onlyBooks;
    }

    @Override
    public boolean onParse(String value) {
        if (value == null) {
            return true; // accepts null value
        }

        String[] args = value.toLowerCase().split("\\|");

        for (String arg : args) {
            arg = arg.trim();

            if (arg.startsWith("ingredientaction")) {
                value = arg.substring("ingredientaction".length()).trim();

                if (value.equals("largest")) {
                    ingredientAction = ApplyEnchantmentAction.LARGEST;
                } else if (value.equals("combine")) {
                    ingredientAction = ApplyEnchantmentAction.COMBINE;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has ingredientaction argument with invalid action: " + value);
                }
            } else if (arg.startsWith("resultaction")) {
                value = arg.substring("resultaction".length()).trim();

                if (value.equals("largest")) {
                    resultAction = ApplyEnchantmentAction.LARGEST;
                } else if (value.equals("combine")) {
                    resultAction = ApplyEnchantmentAction.COMBINE;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has resultaction argument with invalid action: " + value);
                }
            } else if (arg.startsWith("ignorelevel")) {
                ignoreLevelRestriction = true;
            } else if (arg.startsWith("onlybooks")) {
                onlyBooks = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
            }
        }
        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (!a.hasResult() || !a.hasInventoryView()) {
            a.addCustomReason("Needs inventory and result!");
            return;
        }

        Map<Enchantment, Integer> enchantments = new HashMap<>();

        if (a.inventory() instanceof CraftingInventory) {
            CraftingInventory inv = (CraftingInventory) a.inventory();

            enchantments = copyEnchantments(inv.getMatrix());
        } else if (a.inventory() instanceof FurnaceInventory) {
            FurnaceInventory inv = (FurnaceInventory) a.inventory();

            enchantments = copyEnchantments(new ItemStack[] { inv.getSmelting() });
        } else {
            a.addCustomReason("Unknown inventory type: " + a.inventory());
        }

        if (enchantments.size() == 0) {
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
                if (resultAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
                    resultMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
                } else if (resultAction == ApplyEnchantmentAction.COMBINE){
                    resultMeta.addEnchant(enchantment, level + currentLevel, ignoreLevelRestriction);
                }
            } else {
                resultMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
            }
        }

        a.result().setItemMeta(resultMeta);
    }

    private Map<Enchantment, Integer> copyEnchantments(ItemStack[] items) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (ItemStack i : items) {
            if (i != null) {
                ItemMeta meta = i.getItemMeta();

                if (meta instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;

                    for (Map.Entry<Enchantment, Integer> entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                        Enchantment enchantment = entry.getKey();
                        int level = entry.getValue();

                        if (enchantments.containsKey(enchantment)) {
                            int currentLevel = enchantments.get(enchantment);
                            if (ingredientAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
                                enchantments.put(enchantment, level);
                            } else if (ingredientAction == ApplyEnchantmentAction.COMBINE) {
                                enchantments.put(enchantment, level + currentLevel);
                            }
                        } else {
                            enchantments.put(enchantment, level);
                        }
                    }
                }

                if(!onlyBooks) {
                    if (meta != null && meta.hasEnchants()) {
                        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                            Enchantment enchantment = entry.getKey();
                            int level = entry.getValue();

                            if (enchantments.containsKey(enchantment)) {
                                int currentLevel = enchantments.get(enchantment);
                                if (ingredientAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
                                    enchantments.put(enchantment, level);
                                } else if (ingredientAction == ApplyEnchantmentAction.COMBINE) {
                                    enchantments.put(enchantment, level + currentLevel);
                                }
                            } else {
                                enchantments.put(enchantment, level);
                            }
                        }
                    }
                }
            }
        }

        return enchantments;
    }
}
