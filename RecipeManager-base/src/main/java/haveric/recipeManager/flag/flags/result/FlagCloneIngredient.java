package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlagCloneIngredient extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.CLONE_INGREDIENT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <arguments>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Clones the ingredient matching material of the result used on.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "As '<arguments>' you must define at least one feature to copy from the ingredient to the result.",
            "Arguments can be one or more of the following, separated by | character:",
            "  data [<mod> <value>]                   = copy data value with optional modifier, <mod> can be +,-,/,* or % as math operator and <value> a number.",
            "  amount [<mod> <value>]                 = copy stack amount with optional modifier, <mod> can be +,-,/,* or % as math operator and <value> a number.",
            "  enchants                               = copies the enchantments.",
            "  name                                   = copies the custom item name.",
            "  lore [number or text or regex:pattern] = copies all the custom item lore/description unless conditions are added to copy specific lines.",
            "    [number]                             = copies only the lore on line number. More lore conditions can be added to copy more lines",
            "    [text]                               = copies any lore lines that contain the text",
            "    [regex:pattern]                      = copies any lore lines that match the regex pattern. Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  special                                = copies item's special feature like leather armor color, firework effects, book contents, skull owner, etc.",
            "  allmeta                                = copies enchants, name, lore and special.",
            "  all                                    = copies entire item (data, amount, enchants, name, lore, special)",
            "  allowedtypes <type, ...>               = set material types other than the result type that can be cloned from",
            "",
            "NOTE: If the result's material is present in the ingredients more than once, when using the recipe it will clone the details of first item in the grid.",
            "",
            "To apply conditions for ingredients (ranged data values, specific names, etc) then you can use the " + FlagType.INGREDIENT_CONDITION + " flag too.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} data // just copy data value",
            "{flag} data +2 // copy data value and add 2 to it",
            "{flag} amount * 2 // copy amount and multiply it by 2",
            "{flag} data % 2 // get the remainder from data divided by 2.",
            "{flag} data | amount | lore // only copy these things",
            "{flag} all // copy entire ingredient",
            "{flag} all | allowedtypes stone_axe, iron_axe // copy ingredients from the first ingredient that matches a stone axe, iron axe, or the result type"};
    }


    private byte copyBitsum;
    private int[] dataModifier = new int[2];
    private int[] amountModifier = new int[2];
    private List<String> loreConditions = new ArrayList<>();
    private List<Integer> loreLines = new ArrayList<>();
    private List<Material> allowedTypes = new ArrayList<>();

    /**
     * Contains static constants that are usable in the 'copy' methods of {@link FlagCloneIngredient}.
     */
    public static class Bit {
        public static final byte NONE = 0;
        public static final byte DATA = 1;
        public static final byte AMOUNT = 1 << 1;
        public static final byte ENCHANTS = 1 << 2;
        public static final byte NAME = 1 << 3;
        public static final byte LORE = 1 << 4;
        public static final byte SPECIAL = 1 << 5;
        public static final byte ALLMETA = ENCHANTS | NAME | LORE | SPECIAL;
        public static final byte ALL = DATA | AMOUNT | ALLMETA;
    }

    public FlagCloneIngredient() {
    }

    public FlagCloneIngredient(FlagCloneIngredient flag) {
        copyBitsum = flag.copyBitsum;
        System.arraycopy(flag.dataModifier, 0, dataModifier, 0, dataModifier.length);
        System.arraycopy(flag.amountModifier, 0, amountModifier, 0, amountModifier.length);
        loreConditions = flag.loreConditions;
        loreLines = flag.loreLines;
        allowedTypes = flag.allowedTypes;
    }

    @Override
    public FlagCloneIngredient clone() {
        return new FlagCloneIngredient((FlagCloneIngredient) super.clone());
    }

    /**
     * @return the bitsum of the copyable arguments.
     */
    public byte getCopyBitsum() {
        return copyBitsum;
    }

    /**
     * Checks if the copy bitsum has the requested bit.
     *
     * @param bit
     *            use {@link Bit} enums
     * @return true if bit is present, false otherwise
     */
    public boolean hasCopyBit(byte bit) {
        return (copyBitsum & bit) == bit;
    }

    /**
     * Set what to copy from the ingredient to the result.<br>
     * You should use {@link Bit} class for values!
     *
     * @param bitsum
     *            use {@link Bit} enums
     */
    public void setCopyBitsum(byte bitsum) {
        copyBitsum = bitsum;
    }

    /**
     * Pick something to copy from the ingredient to the result.<br>
     * You should use {@link Bit} class for values!
     *
     * @param bit
     *            use {@link Bit} enums
     */
    private void addCopyBit(byte bit) {
        copyBitsum |= bit;
    }

    /**
     * Data value modifier for final result.
     *
     * @return integer array of exactly 2 elements, first is the +/-/= char and second is the data value
     */
    public int[] getDataModifier() {
        return dataModifier.clone();
    }

    /**
     * Modify the final result's data value by using symbol as math operator.
     *
     * @param symbol
     *            can be: +, -, *, /, %
     * @param data
     *            the data amount to change
     */
    public void setDataModifier(char symbol, int data) {
        dataModifier[0] = symbol;
        dataModifier[1] = data;
    }

    /**
     * Amount modifier for final result.
     *
     * @return integer array of exactly 2 elements, first is the +/-/= char and second is the amount
     */
    public int[] getAmountModifier() {
        return amountModifier.clone();
    }

    /**
     * Modify the final result's amount by using symbol as math operator.
     *
     * @param symbol
     *            can be: +, -, *, /, %
     * @param data
     *            the amount amount to change
     */
    public void setAmountModifier(char symbol, int data) {
        amountModifier[0] = symbol;
        amountModifier[1] = data;
    }

    private void addLoreCondition(String newLore) {
        if (newLore != null) {
            loreConditions.add(RMCUtil.parseColors(newLore, false));
        }
    }

    private void addLoreLine(int line) {
        loreLines.add(line);
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || result.getType() == Material.AIR) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " can not be used on AIR results!", "The type of result defines the type of ingredient it searches for");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        // Match on single pipes '|', but not double '||'
        // Double pipes will be replaced by single pipes for each arg
        String[] args = value.toLowerCase().split("(?<!\\|)\\|(?!\\|)");

        for (String arg : args) {
            arg = arg.trim();

            // Replace double pipes with single pipe: || -> |
            arg = arg.replaceAll("\\|\\|", "|");

            if (arg.equals("all")) {
                copyBitsum = Bit.ALL;
                continue;
            } else if (arg.equals("allmeta")) {
                addCopyBit(Bit.ALLMETA);
                continue;
            } else if (arg.equals("enchants")) {
                addCopyBit(Bit.ENCHANTS);
                continue;
            } else if (arg.equals("name")) {
                addCopyBit(Bit.NAME);
                continue;
            } else if (arg.startsWith("lore")) {
                addCopyBit(Bit.LORE);

                String loreValue = arg.substring("lore".length()).trim();
                if (!loreValue.isEmpty()) {
                    try {
                        int loreLine = Integer.parseInt(loreValue);
                        addLoreLine(loreLine);
                    } catch (NumberFormatException e) {
                        addLoreCondition(loreValue);
                    }
                }
                continue;
            } else if (arg.equals("special")) {
                addCopyBit(Bit.SPECIAL);
                continue;
            } else if (arg.startsWith("allowedtypes")) {
                value = arg.substring("allowedtypes".length()).trim();
                String[] types = value.split(",");

                for (String type : types) {
                    Material material = RecipeManager.getSettings().getMaterial(type);
                    if (material == null) {
                        material = Material.matchMaterial(type);
                    }

                    if (material == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid allowedtypes value: " + type + ". Value must be a material name.");
                    } else {
                        allowedTypes.add(material);
                    }
                }

                continue;
            }

            boolean isDataArg = arg.startsWith("data");

            if (isDataArg || arg.startsWith("amount")) {
                byte copyBit;
                if (isDataArg) {
                    copyBit = Bit.DATA;
                } else {
                    copyBit = Bit.AMOUNT;
                }
                addCopyBit(copyBit);

                Pattern pattern = Pattern.compile("[-+*/%]");
                Matcher match = pattern.matcher(arg);

                if (match.find()) {
                    args = pattern.split(arg, 2);
                    value = args[1].trim();

                    try {
                        if (isDataArg) {
                            setDataModifier(match.group(0).charAt(0), Math.abs(Integer.parseInt(value)));
                        } else {
                            setAmountModifier(match.group(0).charAt(0), Math.abs(Integer.parseInt(value)));
                        }
                    } catch (Throwable e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has '" + (isDataArg ? "data" : "amount") + "' argument with invalid number: " + value);
                        continue;
                    }
                }

                continue;
            }

            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
        }

        List<Material> allTypes = new ArrayList<>();
        allTypes.add(getResult().getType());
        allTypes.addAll(allowedTypes);

        int found = 0;
        Material foundMaterial = null;
        for (Material material : allTypes) {
            found = getResult().getRecipe().findItemInIngredients(material, null);

            if (found > 0) {
                foundMaterial = material;
                break;
            }
        }

        if (found == 0) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " couldn't find ingredient of type: " + allTypes.toString());
        } else if (found > 1) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has found the " + foundMaterial + " ingredient more than once, only data from the first one will be cloned!");
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

        boolean cloned = cloneIngredientToResult(a.result(), a);

        if (!cloned) {
            a.addCustomReason("Failed to clone ingredient."); // TODO remove ?
        }
    }

    private boolean cloneIngredientToResult(ItemStack result, Args a) {
        List<Material> allTypes = new ArrayList<>();
        allTypes.add(result.getType());
        allTypes.addAll(allowedTypes);

        ItemStack ingredient = null;

        if (a.inventory() instanceof CraftingInventory) {
            CraftingInventory inv = (CraftingInventory) a.inventory();

            for (ItemStack i : inv.getMatrix()) {
                if (i != null && allTypes.contains(i.getType())) {
                    ingredient = i.clone();
                    break;
                }
            }
        } else if (a.inventory() instanceof FurnaceInventory) {
            FurnaceInventory inv = (FurnaceInventory) a.inventory();
            ItemStack i = inv.getSmelting();

            if (i != null && allTypes.contains(i.getType())) {
                ingredient = i.clone();
            }
        } else if (a.inventory() instanceof AnvilInventory || (Version.has1_14Support() && (a.inventory() instanceof CartographyInventory || a.inventory() instanceof GrindstoneInventory))) {
            ItemStack first = a.inventory().getItem(0);
            ItemStack second = a.inventory().getItem(1);
            if (first != null && allTypes.contains(first.getType())) {
                ingredient = first.clone();
            } else if (second != null && allTypes.contains(second.getType())) {
                ingredient = second.clone();
            }
        } else {
            a.addCustomReason("Unknown inventory type: " + a.inventory());
            return false;
        }

        if (ingredient == null) {
            a.addCustomReason("Couldn't find target ingredient!");
            return false;
        }

        // 1.12 version of handling data
        if (!Version.has1_13BasicSupport() && hasCopyBit(Bit.DATA)) {
            //noinspection deprecation
            int data = ingredient.getDurability();
            int[] mod = getDataModifier();

            data = modValue(data, mod);

            //noinspection deprecation
            result.setDurability((short) data);

            if (hasCopyBit(Bit.SPECIAL)) {
                //noinspection deprecation
                ingredient.setDurability((short) data);
            }
        }

        if (hasCopyBit(Bit.AMOUNT)) {
            int amount = ingredient.getAmount();
            int[] mod = getAmountModifier();

            amount = modValue(amount, mod);

            result.setAmount(amount);
        }

        ItemMeta ingredientMeta = ingredient.getItemMeta();
        ItemMeta resultMeta = result.getItemMeta();

        if (ingredientMeta == null || resultMeta == null) {
            return true;
        }

        // 1.13+ version of handling data
        if (Version.has1_13BasicSupport() && hasCopyBit(Bit.DATA) && ingredientMeta instanceof Damageable && resultMeta instanceof Damageable) {
            Damageable ingredientDamageable = (Damageable) ingredientMeta;
            int data = ingredientDamageable.getDamage();
            int[] mod = getDataModifier();

            data = modValue(data, mod);

            Damageable resultDamageable = (Damageable) resultMeta;
            resultDamageable.setDamage(data);

            if (hasCopyBit(Bit.SPECIAL)) {
                ingredientDamageable.setDamage(data);
            }
        }

        if (hasCopyBit(Bit.ENCHANTS)) {
            for (Entry<Enchantment, Integer> e : ingredientMeta.getEnchants().entrySet()) {
                resultMeta.addEnchant(e.getKey(), e.getValue(), true);
            }
        }

        if (hasCopyBit(Bit.NAME)) {
            resultMeta.setDisplayName(ingredientMeta.getDisplayName());
        }

        if (hasCopyBit(Bit.LORE)) {
            if (loreConditions.isEmpty() && loreLines.isEmpty()) {
                resultMeta.setLore(ingredientMeta.getLore());
            } else {
                List<String> newLores = new ArrayList<>();

                List<String> ingredientLores = ingredientMeta.getLore();
                for (int i = 0; i < ingredientLores.size(); i++) {
                    String loreLine = ingredientLores.get(i);

                    if (loreLines.contains(i+1)) {
                        newLores.add(loreLine);
                        continue;
                    }
                    for (String loreCondition : loreConditions) {
                        if (loreCondition.startsWith("regex:")) {
                            Pattern pattern = Pattern.compile(loreCondition.substring("regex:".length()));
                            if (pattern.matcher(loreLine).matches()) {
                                newLores.add(loreLine);
                                break;
                            }
                        } else {
                            if (loreLine.contains(loreCondition)) {
                                newLores.add(loreLine);
                                break;
                            }
                        }
                    }
                }

                resultMeta.setLore(newLores);
            }
        }

        if (hasCopyBit(Bit.SPECIAL)) {
            ingredientMeta.setDisplayName(resultMeta.getDisplayName());
            ingredientMeta.setLore(resultMeta.getLore());

            if (ingredientMeta.hasEnchants()) {
                for (Enchantment e : ingredientMeta.getEnchants().keySet()) {
                    ingredientMeta.removeEnchant(e);
                }
            }

            for (Entry<Enchantment, Integer> e : resultMeta.getEnchants().entrySet()) {
                ingredientMeta.addEnchant(e.getKey(), e.getValue(), true);
            }

            resultMeta = ingredientMeta;
        }

        result.setItemMeta(resultMeta);

        return true;
    }

    private int modValue(int value, int[] mod) {
        switch (mod[0]) {
            case '-':
                value -= mod[1];
                break;

            case '*':
                value *= mod[1];
                break;

            case '/':
                value /= mod[1];
                break;

            case '%':
                value %= mod[1];
                break;

            default:
                value += mod[1];
                break;
        }
        return value;
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "copyBitsum: " + copyBitsum;
        toHash += "dataModifier: " + dataModifier[0] + dataModifier[1];
        toHash += "amountModifier: " + amountModifier[0] + amountModifier[1];

        for (String loreCondition : loreConditions) {
            toHash += loreCondition + ", ";
        }

        for (Integer loreline : loreLines) {
            toHash += loreline + ", ";
        }

        for (Material material : allowedTypes) {
            toHash += material.name() + ", ";
        }

        return toHash.hashCode();
    }
}
