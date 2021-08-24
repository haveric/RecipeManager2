package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class FlagStoreEnchantment extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.STORE_ENCHANTMENT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments>", };
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
            "  onlyitems                 = Only copies enchantments from items with enchantments. Without this, all enchanted books will be copied as well",
            "",
            "Actions include:",
            "  largest = Use the largest of the two enchantments if two are merged (Sharpness I + Sharpness III = Sharpness III)",
            "  combine = Add the enchantment levels together (Sharpness I + Sharpness III = Sharpness IV)",
            "  anvil   = Combine enchantments similar to anvils (Sharpness I + Sharpness II = Sharpness II) and (Sharpness II + Sharpness II = Sharpness III)", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}",
            "{flag} resultaction combine // Combines the levels with the resulting item's enchants",
            "{flag} ingredientaction combine | ignorelevels | onlyitems // Combines all ingredients levels from items while allowing higher than vanilla allowed enchants", };
    }

    public enum ApplyEnchantmentAction {
        LARGEST,
        COMBINE,
        ANVIL
    }

    private ApplyEnchantmentAction ingredientAction = ApplyEnchantmentAction.LARGEST;
    private ApplyEnchantmentAction resultAction = ApplyEnchantmentAction.LARGEST;
    private boolean ignoreLevelRestriction = false;
    private int maxLevel = -1;
    private boolean onlyItems = false;


    public FlagStoreEnchantment() { }

    public FlagStoreEnchantment(FlagStoreEnchantment flag) {
        super(flag);
        ingredientAction = flag.ingredientAction;
        resultAction = flag.resultAction;
        ignoreLevelRestriction = flag.ignoreLevelRestriction;
        maxLevel = flag.maxLevel;
        onlyItems = flag.onlyItems;
    }

    @Override
    public FlagStoreEnchantment clone() {
        return new FlagStoreEnchantment((FlagStoreEnchantment) super.clone());
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

    public boolean getOnlyItems() {
        return onlyItems;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof EnchantmentStorageMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        if (Version.has1_13BasicSupport()) {
            FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();

            if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), EnchantmentStorageMeta.class)) {
                validFlaggable = true;
            }
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an enchanted book!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
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
                } else if (value.equals("anvil")) {
                    ingredientAction = ApplyEnchantmentAction.ANVIL;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has ingredientaction argument with invalid action: " + value);
                }
            } else if (arg.startsWith("resultaction")) {
                value = arg.substring("resultaction".length()).trim();

                if (value.equals("largest")) {
                    resultAction = ApplyEnchantmentAction.LARGEST;
                } else if (value.equals("combine")) {
                    resultAction = ApplyEnchantmentAction.COMBINE;
                } else if (value.equals("anvil")) {
                    resultAction = ApplyEnchantmentAction.ANVIL;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has resultaction argument with invalid action: " + value);
                }
            } else if (arg.startsWith("ignorelevel")) {
                ignoreLevelRestriction = true;
            } else if  (arg.startsWith("maxLevel")) {
                value = arg.substring("maxlevel".length()).trim();

                try {
                    maxLevel = Integer.parseInt(value);

                    if (maxLevel <= 1) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid maxLevel value: " + maxLevel + ". Value must be > 1.");
                        maxLevel = -1;
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid maxLevel value: " + value + ". Value must be an integer > 1.");
                }
            } else if (arg.startsWith("onlyitems")) {
                onlyItems = true;
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
            }
        }
        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
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

            Map<Enchantment, Integer> enchantments = new HashMap<>();

            if (a.inventory() instanceof CraftingInventory) {
                CraftingInventory inv = (CraftingInventory) a.inventory();

                enchantments = copyEnchantments(inv.getMatrix());
            } else if (a.inventory() instanceof FurnaceInventory) {
                FurnaceInventory inv = (FurnaceInventory) a.inventory();

                enchantments = copyEnchantments(inv.getSmelting());
            } else if (a.inventory() instanceof AnvilInventory || (Version.has1_14Support() && (a.inventory() instanceof CartographyInventory || a.inventory() instanceof GrindstoneInventory)) || (Version.has1_16Support() && a.inventory() instanceof SmithingInventory)) {
                ItemStack[] anvilIngredients = new ItemStack[2];
                anvilIngredients[0] = a.inventory().getItem(0);
                anvilIngredients[1] = a.inventory().getItem(1);

                enchantments = copyEnchantments(anvilIngredients);
            } else if (a.inventory() instanceof BrewerInventory) {
                BrewerInventory inv = (BrewerInventory) a.inventory();

                enchantments = copyEnchantments(inv.getIngredient());
            } else {
                a.addCustomReason(getFlagType() + " has unsupported inventory type: " + a.inventory().getType());
            }

            if (enchantments.size() == 0) {
                return;
            }

            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                if (enchantmentStorageMeta.hasStoredEnchant(enchantment)) {
                    int currentLevel = enchantmentStorageMeta.getStoredEnchantLevel(enchantment);
                    if (resultAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
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

    private Map<Enchantment, Integer> copyEnchantments(ItemStack item) {
        return copyEnchantments(new ItemStack[]{item});
    }

    private Map<Enchantment, Integer> copyEnchantments(ItemStack[] items) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (ItemStack i : items) {
            if (i != null) {
                ItemMeta meta = i.getItemMeta();

                if (!onlyItems) {
                    if (meta instanceof EnchantmentStorageMeta) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;

                        for (Map.Entry<Enchantment, Integer> entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                            evaluateEnchantments(enchantments, entry.getKey(), entry.getValue());
                        }
                    }
                }

                if (meta != null && meta.hasEnchants()) {
                    for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                        evaluateEnchantments(enchantments, entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return enchantments;
    }

    private void evaluateEnchantments(Map<Enchantment, Integer> enchantments, Enchantment enchantment, int level) {
        if (enchantments.containsKey(enchantment)) {
            int currentLevel = enchantments.get(enchantment);
            if (ingredientAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
                if (maxLevel > 1) {
                    level = Math.min(level, maxLevel);
                }
                enchantments.put(enchantment, level);
            } else if (ingredientAction == ApplyEnchantmentAction.COMBINE) {
                level += currentLevel;
                if (maxLevel > 1) {
                    level = Math.min(level, maxLevel);
                }
                enchantments.put(enchantment, level);
            } else if (ingredientAction == ApplyEnchantmentAction.ANVIL) {
                int newLevel;
                if (level == currentLevel) {
                    newLevel = level + 1;
                } else {
                    newLevel = Math.max(level, currentLevel);
                }

                if (maxLevel > 1) {
                    newLevel = Math.min(newLevel, maxLevel);
                }

                enchantments.put(enchantment, newLevel);
            }
        } else {
            enchantments.put(enchantment, level);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "ingredientAction: " + ingredientAction.toString();
        toHash += "resultAction: " + resultAction.toString();
        toHash += "ignoreLevelRestriction: " + ignoreLevelRestriction;
        toHash += "onlyItems: " + onlyItems;

        return toHash.hashCode();
    }
}
