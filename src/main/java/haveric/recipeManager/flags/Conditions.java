package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.util.ParseBit;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

//TODO written book title, author, page num, chars per page, etc

public class Conditions implements Cloneable {
    private String flagType;
    private ItemStack ingredient;
    private String failMessage;
    private Map<Short, Boolean> dataValues = new HashMap<>();
    private Map<Short, Boolean> dataBits = new HashMap<>();
    private int amount;
    private Map<Enchantment, Map<Short, Boolean>> enchants = new HashMap<>();
    private Map<Enchantment, Map<Short, Boolean>> bookEnchants = new HashMap<>();
    private String name;
    private List<String> lores = new ArrayList<>();
    private Color minColor;
    private Color maxColor;
    private boolean noMeta = false;
    private boolean noName = false;
    private boolean noLore = false;
    private boolean noEnchant = false;
    private boolean noBookEnchant = false;
    private boolean noColor = false;
    private boolean allSet = false;

    // TODO mark
    // private boolean extinctRecipeBook;
    // private String recipeBook;
    // private int recipeBookVolume;

    public Conditions() {
    }

    public Conditions(Conditions original) {
        setFlagType(original.getFlagType());
        setIngredient(original.getIngredient().clone());

        failMessage = original.failMessage;

        dataValues.putAll(original.dataValues);
        dataBits.putAll(original.dataBits);

        amount = original.amount;

        for (Entry<Enchantment, Map<Short, Boolean>> e : original.enchants.entrySet()) {
            Map<Short, Boolean> map = new HashMap<>(e.getValue().size());
            map.putAll(e.getValue());
            enchants.put(e.getKey(), map);
        }

        for (Entry<Enchantment, Map<Short, Boolean>> e : original.bookEnchants.entrySet()) {
            Map<Short, Boolean> map = new HashMap<>(e.getValue().size());
            map.putAll(e.getValue());
            bookEnchants.put(e.getKey(), map);
        }

        name = original.name;

        lores = original.lores;

        minColor = original.minColor;
        maxColor = original.maxColor;

        setNoMeta(original.isNoMeta());
        setNoName(original.isNoName());
        setNoLore(original.isNoLore());
        setNoEnchant(original.isNoEnchant());
        setNoBookEnchant(original.isNoBookEnchant());
        setNoColor(original.isNoColor());

        setAllSet(original.isAllSet());
    }

    @Override
    public Conditions clone() {
        return new Conditions(this);
    }

    protected void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        failMessage = message;
    }

    /**
     * @return a map that contains data values and if they should or not be in the ingredient's data (the '!' char in the definition); never null.
     */
    public Map<Short, Boolean> getDataValues() {
        return dataValues;
    }

    /**
     * Sets the new data values map.<br>
     * If the map is null the values will be cleared.
     *
     * @param map
     */
    public void setDataValues(Map<Short, Boolean> map) {
        if (map == null) {
            dataValues.clear();
        } else {
            dataValues = map;
        }
    }

    /**
     * Adds data value as requirement.
     *
     * @param data
     */
    public void addDataValue(short data) {
        addDataValue(data, true);
    }

    /**
     * Adds data value as requirement/restriction.
     *
     * @param data
     * @param allow
     *            true if requirement, false if restricted
     */
    public void addDataValue(short data, boolean allow) {
        dataValues.put(data, allow);
    }

    /**
     * Adds data values range as requirement.<br>
     * Note: max >= min
     *
     * @param min
     * @param max
     */
    public void addDataValueRange(short min, short max) {
        addDataValueRange(min, max, true);
    }

    /**
     * Adds data values range as requirement/restriction.<br>
     * Note: max >= min
     *
     * @param min
     * @param max
     * @param allow
     *            true if requirement, false if restricted
     */
    public void addDataValueRange(short min, short max, boolean allow) {
        if (min > max) {
            throw new IllegalArgumentException("Invalid number range: " + min + " to " + max);
        }

        for (short i = min; i <= max; i++) {
            addDataValue(i, allow);
        }
    }

    public boolean hasDataValues() {
        return !dataValues.isEmpty();
    }

    /**
     * @return a map that contains data bits and if they should or not be in the ingredient's data (the '!' char in the definition); never null.
     */
    public Map<Short, Boolean> getDataBits() {
        return dataBits;
    }

    /**
     * Sets the new data bits map.<br>
     * If the map is null the values will be cleared.
     *
     * @param map
     */
    public void setDataBits(Map<Short, Boolean> map) {
        if (map == null) {
            dataBits.clear();
        } else {
            dataBits = map;
        }
    }

    /**
     * Adds data bit as requirement.
     *
     * @param data
     */
    public void addDataBit(short data) {
        addDataBit(data, true);
    }

    /**
     * Adds data bit as requirement/restriction.
     *
     * @param data
     * @param allow
     *            true if requirement, false if restricted
     */
    public void addDataBit(short data, boolean allow) {
        dataBits.put(data, allow);
    }

    public boolean hasDataBits() {
        return !dataBits.isEmpty();
    }

    /**
     * @return human-friendly list of data values and bits
     */
    public String getDataString() {
        StringBuilder s = new StringBuilder();

        for (Entry<Short, Boolean> e : dataValues.entrySet()) {
            if (s.length() > 0) {
                s.append(", ");
            }

            if (!e.getValue()) {
                s.append("! ");
            }

            s.append(e.getKey());
        }

        for (Entry<Short, Boolean> e : dataBits.entrySet()) {
            if (s.length() > 0) {
                s.append(", ");
            }

            if (!e.getValue()) {
                s.append("! ");
            }

            s.append("& ").append(e.getKey());
        }

        return s.toString();
    }

    /**
     * Checks if the supplied data value can be used with this condition.
     *
     * @param data
     *            ingredient's data value
     * @return true if value is permitted, false otherwise.
     */
    public boolean checkData(short data) {
        boolean ok = false;

        if (hasDataBits()) {
            for (Entry<Short, Boolean> e : dataBits.entrySet()) {
                short d = e.getKey();

                if (e.getValue()) {
                    if (!ok && (data & d) == d) {
                        ok = true;
                    }
                } else if ((data & d) == d) {
                    return false;
                }
            }

            if (!ok) {
                return false;
            }
        }

        if (hasDataValues()) {
            Boolean is = dataValues.get(data);

            // If value not found return false otherwise return if value should be there
            if (is == null) {
                return isAllSet();
            }

            return is;
        }

        return true;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int newAmount) {
        amount = newAmount;
    }

    public boolean hasAmount() {
        return amount > 0;
    }

    public boolean checkAmount(int amountToCheck) {
        return amountToCheck >= amount;
    }

    /**
     * @return enchantments map, never null.
     */
    public Map<Enchantment, Map<Short, Boolean>> getEnchants() {
        return enchants;
    }

    /**
     * Set the enchants map.<br>
     * Setting to null will clear the map contents.
     *
     * @param newEnchants
     */
    public void setEnchants(Map<Enchantment, Map<Short, Boolean>> newEnchants) {
        if (newEnchants == null) {
            enchants.clear();
        } else {
            enchants = newEnchants;
        }
    }

    public void addEnchant(Enchantment enchant) {
        enchants.put(enchant, new HashMap<Short, Boolean>(0));
    }

    public void addEnchantLevel(Enchantment enchant, short level) {
        addEnchantLevel(enchant, level, true);
    }

    public void addEnchantLevel(Enchantment enchant, short level, boolean allow) {
        addEnchantLevelRange(enchant, level, level, allow);
    }

    public void addEnchantLevelRange(Enchantment enchant, short min, short max) {
        addEnchantLevelRange(enchant, min, max, true);
    }

    public void addEnchantLevelRange(Enchantment enchant, short min, short max, boolean allow) {
        Map<Short, Boolean> levels = enchants.get(enchant);

        if (levels == null) {
            levels = new HashMap<>();
            enchants.put(enchant, levels);
        }

        for (short i = min; i <= max; i++) {
            levels.put(i, allow);
        }
    }

    public boolean hasEnchants() {
        return !enchants.isEmpty();
    }

    public boolean checkEnchants(Map<Enchantment, Integer> enchantsToCheck) {
        if (isNoMeta() || isNoEnchant()) {
            return enchantsToCheck == null || enchantsToCheck.isEmpty();
        }

        if (!hasEnchants()) {
            return true;
        }

        if (enchantsToCheck != null && !enchantsToCheck.isEmpty()) {
            for (Entry<Enchantment, Map<Short, Boolean>> e : enchants.entrySet()) {
                Integer level = enchantsToCheck.get(e.getKey());

                // TODO test if proper

                if (level == null) {
                    return false;
                } else if (!e.getValue().isEmpty()) {
                    return e.getValue().get(level);
                }
            }
        }

        return false;
    }

    public String getEnchantsString() {
        StringBuilder s = new StringBuilder();

        for (Entry<Enchantment, Map<Short, Boolean>> e : getEnchants().entrySet()) {
            if (s.length() > 0) {
                s.append("; ");
            }

            s.append(e.getKey().getName());

            if (!e.getValue().isEmpty()) {
                s.append(' ');
                boolean first = true;

                for (Entry<Short, Boolean> l : e.getValue().entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        s.append(", ");
                    }

                    if (!l.getValue()) {
                        s.append("! ");
                    }

                    s.append(l.getKey());
                }
            }
        }

        return s.toString();
    }

    /**
     * @return book enchantments map, never null.
     */
    public Map<Enchantment, Map<Short, Boolean>> getBookEnchants() {
        return bookEnchants;
    }

    /**
     * Set the book enchants map.<br>
     * Setting to null will clear the map contents.
     *
     * @param newEnchants
     */
    public void setBookEnchants(Map<Enchantment, Map<Short, Boolean>> newEnchants) {
        if (newEnchants == null) {
            bookEnchants.clear();
        } else {
            bookEnchants = newEnchants;
        }
    }

    public void addBookEnchant(Enchantment enchant) {
        bookEnchants.put(enchant, new HashMap<Short, Boolean>(0));
    }

    public void addBookEnchantLevel(Enchantment enchant, short level) {
        addBookEnchantLevel(enchant, level, true);
    }

    public void addBookEnchantLevel(Enchantment enchant, short level, boolean allow) {
        addBookEnchantLevelRange(enchant, level, level, allow);
    }

    public void addBookEnchantLevelRange(Enchantment enchant, short min, short max) {
        addBookEnchantLevelRange(enchant, min, max, true);
    }

    public void addBookEnchantLevelRange(Enchantment enchant, short min, short max, boolean allow) {
        Map<Short, Boolean> levels = bookEnchants.get(enchant);

        if (levels == null) {
            levels = new HashMap<>();
            bookEnchants.put(enchant, levels);
        }

        for (short i = min; i <= max; i++) {
            levels.put(i, allow);
        }
    }

    public boolean hasBookEnchants() {
        return !bookEnchants.isEmpty();
    }

    public boolean checkBookEnchants(Map<Enchantment, Integer> enchantsToCheck) {
        if (isNoMeta() || isNoBookEnchant()) {
            return enchantsToCheck == null || enchantsToCheck.isEmpty();
        }

        if (!hasBookEnchants()) {
            return true;
        }

        if (enchantsToCheck != null && !enchantsToCheck.isEmpty()) {
            for (Entry<Enchantment, Map<Short, Boolean>> e : bookEnchants.entrySet()) {
                Integer level = enchantsToCheck.get(e.getKey());

                // TODO test if proper

                if (level == null) {
                    return false;
                } else if (!e.getValue().isEmpty()) {
                    return e.getValue().get(level);
                }
            }
        }

        return false;
    }

    public String getBookEnchantsString() {
        StringBuilder s = new StringBuilder();

        for (Entry<Enchantment, Map<Short, Boolean>> e : getBookEnchants().entrySet()) {
            if (s.length() > 0) {
                s.append("; ");
            }

            s.append(e.getKey().getName());

            if (!e.getValue().isEmpty()) {
                s.append(' ');
                boolean first = true;

                for (Entry<Short, Boolean> l : e.getValue().entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        s.append(", ");
                    }

                    if (!l.getValue()) {
                        s.append("! ");
                    }

                    s.append(l.getKey());
                }
            }
        }

        return s.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        if (newName == null) {
            name = null;
        } else {
            name = RMCUtil.parseColors(newName, false);
        }
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean checkName(String nameToCheck) {
        if (isNoMeta() || isNoName()) {
            return nameToCheck == null;
        }

        if (!hasName()) {
            return true;
        }

        if (nameToCheck != null) {
            if (name.startsWith("regex:")) {
                try {
                    Pattern pattern = Pattern.compile(name.substring("regex:".length()));
                    return pattern.matcher(nameToCheck).matches();
                } catch (PatternSyntaxException e) {
                    ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'http://regexpal.com' (or something similar) to test your regex code before using it.");
                    return false;
                }
            }

            return name.equalsIgnoreCase(nameToCheck);
        }

        return false;
    }

    public List<String> getLores() {
        return lores;
    }

    public void addLore(String newLore) {
        if (newLore != null) {
            lores.add(RMCUtil.parseColors(newLore, false));
        }
    }

    public boolean hasLore() {
        return lores != null && !lores.isEmpty();
    }

    public boolean checkLore(List<String> loreToCheck) {
        if (isNoMeta() || isNoLore()) {
            return loreToCheck == null || loreToCheck.isEmpty();
        }

        if (!hasLore()) {
            return true;
        }

        Pattern pattern = null;

        int matchedLoreChecks = 0;
        int totalLoreChecks = lores.size();
        for (String lore : lores) {
            if (lore.startsWith("regex:")) {
                try {
                    pattern = Pattern.compile(lore.substring("regex:".length()));
                } catch (PatternSyntaxException e) {
                    ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'http://regexpal.com' (or something similar) to test your regex code before using it.");
                    return false;
                }
            }

            if (loreToCheck != null && !loreToCheck.isEmpty()) {
                for (String line : loreToCheck) {
                    if (line != null) {
                        if (lore.startsWith("regex:")) {
                            if (pattern.matcher(line).matches()) {
                                matchedLoreChecks++;
                                break;
                            }
                        } else {
                            if (lore.equalsIgnoreCase(line)) {
                                matchedLoreChecks++;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return matchedLoreChecks == totalLoreChecks;
    }

    /**
     * Set the color ranges.
     *
     * @param newMinColor
     *            color for min-range or null to disable color checking.
     * @param newMaxColor
     *            color for max-range or null to disable range.
     */
    public void setColor(Color newMinColor, Color newMaxColor) {
        minColor = newMinColor;
        maxColor = newMaxColor;
    }

    /**
     * Sets the color required.<br>
     * NOTE: This sets maxColor to null.
     *
     * @param r
     *            0-255
     * @param g
     *            0-255
     * @param b
     *            0-255
     */
    public void setColor(int r, int g, int b) {
        minColor = Color.fromRGB(r, g, b);
        maxColor = null;
    }

    /**
     * Sets the color range required.
     *
     * @param minR
     *            0 to 255
     * @param maxR
     *            minR to 255
     * @param minG
     *            0 to 255
     * @param maxG
     *            minG to 255
     * @param minB
     *            0 to 255
     * @param maxB
     *            minG to 255
     */
    public void setColor(int minR, int maxR, int minG, int maxG, int minB, int maxB) {
        Validate.isTrue(maxR >= minR, "minR is bigger than maxR!");
        Validate.isTrue(maxG >= minG, "minG is bigger than maxG!");
        Validate.isTrue(maxB >= minB, "minB is bigger than maxB!");

        minColor = Color.fromRGB(minR, minG, minB);
        maxColor = Color.fromRGB(maxR, maxG, maxB);
    }

    /**
     * @return color or null if color checking is disabled.
     */
    public Color getMinColor() {
        return minColor;
    }

    /**
     * @return color or null if range is disabled.
     */
    public Color getMaxColor() {
        return maxColor;
    }

    /**
     * @return user-friendly color info or null if disabled
     */
    public String getColorString() {
        if (!hasColor()) {
            return null;
        }

        StringBuilder s = new StringBuilder();

        if (maxColor == null) {
            s.append(minColor.getRed()).append(", ");
            s.append(minColor.getGreen()).append(", ");
            s.append(minColor.getBlue());
        } else {
            s.append(minColor.getRed()).append('-').append(maxColor.getRed()).append(", ");
            s.append(minColor.getGreen()).append('-').append(maxColor.getGreen()).append(", ");
            s.append(minColor.getBlue()).append('-').append(maxColor.getBlue());
        }

        return s.toString();
    }

    /**
     * @return if minColor != null
     */
    public boolean hasColor() {
        return minColor != null;
    }

    public boolean checkColor(Color color) {
        if (color != null && (isNoColor() || isNoMeta())) {
            Color defaultColor = Bukkit.getItemFactory().getDefaultLeatherColor();
            return color.equals(defaultColor);
        }

        if (!hasColor()) {
            return true;
        }

        if (color != null) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            if (maxColor == null) {
                return (minColor.getRed() == r && minColor.getGreen() == g && minColor.getBlue() == b);
            }

            return (minColor.getRed() <= r && maxColor.getRed() >= r && minColor.getGreen() <= g && maxColor.getGreen() >= g && minColor.getBlue() <= b && maxColor.getBlue() >= b);
        }

        return false;
    }

    /**
     * Check the supplied item with supplied arguments against this condition class.
     *
     * @param item
     *            the ingredient, must not be null.
     * @param a
     *            use {@link ArgBuilder} to build arguments, must not be null.
     * @return
     */
    public boolean checkIngredient(ItemStack item, Args a, boolean addReasons) {
        boolean ok = true;

        if (!checkData(item.getDurability())) {
            if (a == null) {
                return false;
            }

            if (addReasons) {
                a.addReason("flag.ingredientconditions.nodata", getFailMessage(), "{item}", ToolsItem.print(item), "{data}", getDataString());
            }
            ok = false;

            if (getFailMessage() != null) {
                return false;
            }
        }

        if (!checkAmount(item.getAmount())) {
            if (a == null) {
                return false;
            }

            if (addReasons) {
                a.addReason("flag.ingredientconditions.noamount", getFailMessage(), "{item}", ToolsItem.print(item), "{amount}", getAmount());
            }
            ok = false;

            if (getFailMessage() != null) {
                return false;
            }
        }
        if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

            if (!checkBookEnchants(meta.getStoredEnchants())) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.noenchants", getFailMessage(), "{item}", ToolsItem.print(item), "{enchants}", getBookEnchantsString());
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        if (!checkEnchants(item.getEnchantments())) {
            if (a == null) {
                return false;
            }

            if (addReasons) {
                a.addReason("flag.ingredientconditions.noenchants", getFailMessage(), "{item}", ToolsItem.print(item), "{enchants}", getEnchantsString());
            }
            ok = false;

            if (getFailMessage() != null) {
                return false;
            }
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        if (!checkName(meta.getDisplayName())) {
            if (a == null) {
                return false;
            }

            if (addReasons) {
                a.addReason("flag.ingredientconditions.noname", getFailMessage(), "{item}", ToolsItem.print(item), "{name}", getName());
            }
            ok = false;

            if (getFailMessage() != null) {
                return false;
            }
        }

        if (!checkLore(meta.getLore())) {
            if (a == null) {
                return false;
            }

            if (addReasons) {
                a.addReason("flag.ingredientconditions.nolore", getFailMessage(), "{item}", ToolsItem.print(item), "{lore}", getLores());
            }
            ok = false;

            if (getFailMessage() != null) {
                return false;
            }
        }

        if (hasColor()) {
            boolean failed = true;

            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leather = (LeatherArmorMeta) meta;

                if (checkColor(leather.getColor())) {
                    failed = false;
                }
            }

            if (failed) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.nocolor", getFailMessage(), "{item}", ToolsItem.print(item), "{color}", getColorString());
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        return ok;
    }

    public boolean isAllSet() {
        return allSet;
    }

    public void setAllSet(boolean allSet) {
        this.allSet = allSet;
    }

    public boolean isNoEnchant() {
        return noEnchant;
    }

    public void setNoEnchant(boolean noEnchant) {
        this.noEnchant = noEnchant;
    }

    public boolean isNoBookEnchant() {
        return noBookEnchant;
    }

    public void setNoBookEnchant(boolean noBookEnchant) {
        this.noBookEnchant = noBookEnchant;
    }

    public boolean isNoColor() {
        return noColor;
    }

    public void setNoColor(boolean noColor) {
        this.noColor = noColor;
    }

    public boolean isNoName() {
        return noName;
    }

    public void setNoName(boolean noName) {
        this.noName = noName;
    }

    public boolean isNoLore() {
        return noLore;
    }

    public void setNoLore(boolean noLore) {
        this.noLore = noLore;
    }

    public boolean isNoMeta() {
        return noMeta;
    }

    public void setNoMeta(boolean noMeta) {
        this.noMeta = noMeta;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public String getFlagType() {
        return flagType;
    }

    public void setFlagType(String newFlagType) {
        flagType = newFlagType;
    }

    public static void parseArg(String value, String arg, Conditions cond) {
        ItemStack item = cond.getIngredient();

        if (arg.startsWith("data")) {
            if (item.getDurability() != Vanilla.DATA_WILDCARD) {
                ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'data' argument but ingredient has specific data!", "The ingredient must have the 'any' data value set.");
                return;
            }

            value = arg.substring("data".length()).trim();

            String[] list = value.split(",");

            for (String val : list) {
                val = val.trim();
                boolean not = val.charAt(0) == '!';

                if (not) {
                    val = val.substring(1).trim();
                }

                short maxDurability = item.getType().getMaxDurability();
                if (val.equals("all")) {
                    cond.setAllSet(!not);
                } else if (val.equals("vanilla")) {
                    cond.addDataValueRange((short) 0, maxDurability, !not);
                } else if (val.equals("damaged")) {
                    if ((maxDurability - 1) > 0) {
                        cond.addDataValueRange((short) 1, maxDurability, !not);
                    }
                } else if (val.equals("new")) {
                    cond.addDataValueRange((short) 0, (short) 0, !not);
                } else if (val.matches("(.*):(.*)")) {
                    ItemStack match = Tools.parseItem(val, Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

                    if (match != null && match.getDurability() != Vanilla.DATA_WILDCARD) {
                        cond.addDataValue(match.getDurability(), !not);
                    } else {
                        // ErrorReporter.getInstance().warning("Flag " + getType() + " has 'data' argument with unknown material:data combination: " + val);
                    }
                } else {
                    String[] split = val.split("-");

                    if (split.length > 1) {
                        short min;
                        short max;

                        try {
                            min = Short.valueOf(split[0].trim());
                            max = Short.valueOf(split[1].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'data' argument with invalid numbers: " + val);
                            continue;
                        }

                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'data' argument with invalid number range: " + min + " to " + max);
                            break;
                        }

                        cond.addDataValueRange(min, max, !not);
                    } else {
                        val = val.trim();
                        boolean bitwise = val.charAt(0) == '&';

                        if (bitwise) {
                            val = val.substring(1).trim();
                        }

                        try {
                            if (bitwise) {
                                cond.addDataBit(Short.valueOf(val), !not);
                            } else {
                                cond.addDataValue(Short.valueOf(val), !not);
                            }
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'data' argument with invalid number: " + val);
                        }
                    }
                }
            }
        } else if (arg.startsWith("amount")) {
            value = arg.substring("amount".length()).trim();

            try {
                cond.setAmount(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'amount' argument with invalid number: " + value);
            }
        } else if (arg.startsWith("!enchant") || arg.startsWith("noenchant")) {
            cond.setNoEnchant(true);
        } else if (arg.startsWith("enchant")) {
            value = arg.substring("enchant".length()).trim();

            String[] list = value.split(" ", 2);

            value = list[0].trim();

            Enchantment enchant = Tools.parseEnchant(value);

            if (enchant == null) {
                ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'enchant' argument with invalid name: " + value);
                return;
            }

            if (list.length > 1) {
                list = list[1].split(",");

                for (String s : list) {
                    s = s.trim();
                    boolean not = s.charAt(0) == '!';

                    if (not) {
                        s = s.substring(1).trim();
                    }

                    String[] split = s.split("-", 2);

                    if (split.length > 1) {
                        short min;
                        short max;

                        try {
                            min = Short.valueOf(split[0].trim());
                            max = Short.valueOf(split[1].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'enchant' argument with invalid numbers: " + s);
                            continue;
                        }

                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'enchant' argument with invalid number range: " + min + " to " + max);
                            continue;
                        }

                        cond.addEnchantLevelRange(enchant, min, max, !not);
                    } else {
                        try {
                            cond.addEnchantLevel(enchant, Short.valueOf(s.trim()), !not);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'enchant' argument with invalid number: " + s);
                        }
                    }
                }
            } else {
                cond.addEnchant(enchant);
            }
        } else if (arg.startsWith("!bookenchant") || arg.startsWith("nobookenchant")) {
            if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
                cond.setNoBookEnchant(true);
            }
        } else if (arg.startsWith("bookenchant")) {
            if (!(item.getItemMeta() instanceof EnchantmentStorageMeta)) {
                ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'bookenchant' argument for an item that is not an enchanted book.");
                return;
            }

            value = arg.substring("bookenchant".length()).trim();

            String[] list = value.split(" ", 2);

            value = list[0].trim();

            Enchantment enchant = Tools.parseEnchant(value);

            if (enchant == null) {
                ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'bookenchant' argument with invalid name: " + value);
                return;
            }

            if (list.length > 1) {
                list = list[1].split(",");

                for (String s : list) {
                    s = s.trim();
                    boolean not = s.charAt(0) == '!';

                    if (not) {
                        s = s.substring(1).trim();
                    }

                    String[] split = s.split("-", 2);

                    if (split.length > 1) {
                        short min;
                        short max;

                        try {
                            min = Short.valueOf(split[0].trim());
                            max = Short.valueOf(split[1].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'bookenchant' argument with invalid numbers: " + s);
                            continue;
                        }

                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'bookenchant' argument with invalid number range: " + min + " to " + max);
                            continue;
                        }

                        cond.addBookEnchantLevelRange(enchant, min, max, !not);
                    } else {
                        try {
                            cond.addBookEnchantLevel(enchant, Short.valueOf(s.trim()), !not);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'bookenchant' argument with invalid number: " + s);
                        }
                    }
                }
            } else {
                cond.addBookEnchant(enchant);
            }
        } else if (arg.startsWith("!color") || arg.startsWith("nocolor")) {
            if (item.getItemMeta() instanceof LeatherArmorMeta) {
                cond.setNoColor(true);
            }
        } else if (arg.startsWith("color")) {
            if (!(item.getItemMeta() instanceof LeatherArmorMeta)) {
                ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'color' argument for an item that is not leather armor.", "RGB can only be applied to leather, for wool and dye use the 'data' argument.");
                return;
            }

            value = arg.substring("color".length()).trim();

            DyeColor dye = RMCUtil.parseEnum(value, DyeColor.values());

            if (dye == null) {
                String[] split = value.split(",", 3);

                if (split.length != 3) {
                    ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'color' argument with less than 3 colors separated by comma: " + value);
                    return;
                }

                // TODO: Figure out if these are needed
                //short[] minColor = new short[3];
                //short[] maxColor = new short[3];

                for (String element : split) {
                    String[] range = element.split("-", 2);

                    try {
                        short min = Short.valueOf(range[0].trim());
                        short max = min;

                        if (range.length > 1) {
                            max = Short.valueOf(range[1].trim());
                        }

                        if (min < 0 || min > 255 || min > max || max > 255) {
                            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'color' argument with invalid range: " + min + " to " + max, "Numbers must be from 0 to 255 and min must be less or equal to max!");
                            break;
                        }

                        //minColor[c] = min;
                        //maxColor[c] = max;
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'color' argument with invalid number: " + value);
                    }
                }
            } else {
                cond.setColor(dye.getColor(), null);
            }
        } else if (arg.startsWith("!name") || arg.startsWith("noname")) {
            cond.setNoName(true);
        } else if (arg.startsWith("name")) {
            value = arg.substring("name".length()).trim(); // preserve case for regex

            cond.setName(value);
        } else if (arg.startsWith("!lore") || arg.startsWith("nolore")) {
            cond.setNoLore(true);
        } else if (arg.startsWith("lore")) {
            value = arg.substring("lore".length()).trim(); // preserve case for regex

            cond.addLore(value);
        } else if (arg.startsWith("!meta") || arg.startsWith("nometa")) {
            cond.setNoMeta(true);
        } else if (arg.startsWith("failmsg")) {
            value = arg.substring("failmsg".length()).trim(); // preserve case... because it's a message

            cond.setFailMessage(value);
        } else {
            ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has unknown argument: " + arg);
        }
    }
}