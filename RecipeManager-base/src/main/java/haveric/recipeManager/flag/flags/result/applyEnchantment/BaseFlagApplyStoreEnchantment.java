package haveric.recipeManager.flag.flags.result.applyEnchantment;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseFlagApplyStoreEnchantment extends Flag {
    protected ApplyEnchantmentAction ingredientAction = ApplyEnchantmentAction.LARGEST;
    protected ApplyEnchantmentAction resultAction = ApplyEnchantmentAction.LARGEST;
    protected boolean ignoreLevelRestriction = false;
    protected int maxLevel = -1;
    protected boolean onlyBooks = false;
    protected boolean onlyItems = false;

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments>", };
    }

    public BaseFlagApplyStoreEnchantment() {
        super();
    }

    public BaseFlagApplyStoreEnchantment(BaseFlagApplyStoreEnchantment flag) {
        super(flag);

        ingredientAction = flag.ingredientAction;
        resultAction = flag.resultAction;
        ignoreLevelRestriction = flag.ignoreLevelRestriction;
        maxLevel = flag.maxLevel;
        onlyBooks = flag.onlyBooks;
        onlyItems = flag.onlyItems;
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

    public boolean getOnlyItems() {
        return onlyItems;
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

                if (value.equals("smallest")) {
                    ingredientAction = ApplyEnchantmentAction.SMALLEST;
                } else if (value.equals("largest")) {
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

                if (value.equals("smallest")) {
                    resultAction = ApplyEnchantmentAction.SMALLEST;
                } else if (value.equals("largest")) {
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
            } else if (arg.startsWith("maxLevel")) {
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
            } else if (arg.startsWith("onlybooks")) {
                onlyBooks = true;
                onlyItems = false;
            } else if (arg.startsWith("onlyitems")) {
                onlyItems = true;
                onlyBooks = false;
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

    protected Map<Enchantment, Integer> copyEnchantments(ItemStack item) {
        return copyEnchantments(new ItemStack[]{item});
    }

    protected Map<Enchantment, Integer> copyEnchantments(ItemStack[] items) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (ItemStack i : items) {
            if (i != null) {
                ItemMeta meta = i.getItemMeta();

                if (!onlyItems) {
                    if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
                        for (Map.Entry<Enchantment, Integer> entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                            evaluateEnchantments(enchantments, entry.getKey(), entry.getValue());
                        }
                    }
                }

                if (!onlyBooks) {
                    if (meta != null && meta.hasEnchants()) {
                        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                            evaluateEnchantments(enchantments, entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }

        return enchantments;
    }

    protected void evaluateEnchantments(Map<Enchantment, Integer> enchantments, Enchantment enchantment, int level) {
        if (enchantments.containsKey(enchantment)) {
            int currentLevel = enchantments.get(enchantment);
            if (ingredientAction == ApplyEnchantmentAction.SMALLEST && level < currentLevel) {
                enchantments.put(enchantment, level);
            } else if (ingredientAction == ApplyEnchantmentAction.LARGEST && level > currentLevel) {
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

    protected Map<Enchantment, Integer> copyEnchantmentsByInventory(Args a) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        if (a.inventory() instanceof CraftingInventory inv) {
            enchantments = copyEnchantments(inv.getMatrix());
        } else if (a.inventory() instanceof FurnaceInventory inv) {
            enchantments = copyEnchantments(inv.getSmelting());
        } else if (a.inventory() instanceof AnvilInventory || a.inventory() instanceof CartographyInventory || a.inventory() instanceof GrindstoneInventory || a.inventory() instanceof SmithingInventory) {
            ItemStack[] anvilIngredients = new ItemStack[2];
            anvilIngredients[0] = a.inventory().getItem(0);
            anvilIngredients[1] = a.inventory().getItem(1);

            enchantments = copyEnchantments(anvilIngredients);
        } else if (a.inventory() instanceof BrewerInventory inv) {
            enchantments = copyEnchantments(inv.getIngredient());
        } else {
            a.addCustomReason(getFlagType() + " has unsupported inventory type: " + a.inventory().getType());
        }

        return enchantments;
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "ingredientAction: " + ingredientAction.toString();
        toHash += "resultAction: " + resultAction.toString();
        toHash += "ignoreLevelRestriction: " + ignoreLevelRestriction;
        toHash += "onlyBooks: " + onlyBooks;
        toHash += "onlyItems: " + onlyItems;

        return toHash.hashCode();
    }
}
