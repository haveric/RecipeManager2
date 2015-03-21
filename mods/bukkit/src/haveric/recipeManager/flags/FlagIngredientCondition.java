package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.Messages;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.ParseBit;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;


public class FlagIngredientCondition extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.INGREDIENTCONDITION;
    protected static final String[] A = new String[] {
        "{flag} <item> | <conditions>", };

    protected static final String[] D = new String[] {
        "Adds conditions for individual ingredients like ranged data values, enchantments or using stacks.",
        "This flag can be called more than once to add more ingredients with conditions.",
        "",
        "The <item> argument must be an item that is in the recipe, 'material:data' format.",
        "If you're planning to add ranged data values the data value must be the wildcard '*' or not set at all in order to work.",
        "",
        "For <conditions> argument you must specify at least one condition.",
        "Conditions must be separated by | and can be specified in any order.",
        "Condition list:",
        "",
        "  data <[!][&]num or min-max or all or vanilla or damaged or new>, [...]",
        "    Condition for data/damage/durability, as argument you can specify data values separated by , character.",
        "    One number is required, you can add another number separated by - character to make a number range.",
        "    Additionally instead of the number you can specify 'item:data' to use the named data value.",
        "    Special data values:",
        "      all: Flips the data check to allow all data values instead of none initially.",
        "      vanilla: Only allow data values within the vanilla ranges.",
        "      new: Equivalent to 0, or an undamaged item.",
        "      damaged: On weapons and armor, this is everything within vanilla limits that is considered a damaged item.",
        "    Prefixing with '&' would make a bitwise operation on the data value.",
        "    Prefixing with '!' would reverse the statement's meaning making it not work with the value specified.",
        "    Optionally you can add more data conditions separated by ',' that the ingredient must match against one to proceed.",
        "    Defaults to the equivalent of !all.",

        "",
        "  enchant <name> [[!]num or min-max], [...]",
        "    Condition for applied enchantments (not stored in books).",
        "    This argument can be used more than once to add more enchantments as conditions.",
        "    The name must be an enchantment name, see '" + Files.FILE_INFO_NAMES + "' at 'ENCHANTMENTS' section.",
        "    The 2nd argument is the levels, it's optional",
        "    A number can be used as level to set that level as requirement.",
        "    You can also use 'max' to use the max supported level for that enchantment.",
        "    Additionally a second number separated by - can be added to specify a level range, 'max' is also supported in ranged value.",
        "    Prefixing with '!' would ban the level or level range.",
        "",
        "  noenchant or !enchant",
        "    Ingredient must have no enchantment",
        "    Overrides enchant condition if set",
        "",
        "  amount <num>                     = stack amount, this will also subtract from the ingredient when crafted!",
        "  name <text or regex:pattern>     = check the item name against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
        "  noname or !name",
        "    Ingredient must have no/default name",
        "    Overrides name condition if set",
        "",
        "  lore <text or regex:pattern>     = checks each lore line for a specific text or if prefixed with 'regex:' it will check for a regex pattern.",
        "  nolore or !lore",
        "    Ingredient must have no lore",
        "    Overrides lore condition if set",
        "",
        "  color <colorname or R,G,B>       = only works for leather armor, checks color, the values can be individual values or ranged separated by - char or you can use a color name constant, see '" + Files.FILE_INFO_NAMES + "' at 'DYE COLOR'.",
        "",
        "  nocolor or !color",
        "    Only works for leather armor",
        "    Ingredient must have default/vanilla color",
        "    Overrides color condition if set",
        "",
        "  nometa or !meta",
        "    Ingredient must have no metadata (enchants, name, lore, color)",
        "    Overrides enchant, name, lore, color conditions if set",
        "    Equivalent to noenchant | noname | nolore | nocolor",
        "",
        // TODO mark
        // "  recipebook <name> [volume <num>] = checks if ingredient is a recipebook generated by this plugin, partial name matching; optionally you can require a specific volume, accepts any volume by default.",
        // "  extinctrecipebook                = checks if the ingredient is a recipe book generated by this plugin but no longer exists, useful to give players a chance to recycle their extinct recipe books.",
        "  failmsg <text>                   = overwrite message sent to crafter when failing to provide required ingredient.",
        "",
        "This flag can be used on recipe results to determine a specific outcome for the recipe depending on the ingredients, however in that case you would need 'failmsg false' along with " + FlagType.DISPLAYRESULT + " flag too, see 'advanced recipes.html' file for example.",
        "",
        "NOTE: if an ingredient exists more than once in the recipe then the conditions will apply to all of them.",
        "NOTE: this flag can not be used in recipe header, needs to be defined on individual results or recipes.", };

    protected static final String[] E = new String[] {
        "{flag} wood | data 3 // pointless use of this flag, just use wood:3 as ingredient.",
        "{flag} wood | data 1-3, 39, 100 // this overwrites the data condition to the previous one.",
        "{flag} dirt | amount 64 // needs a full stack of dirt to work.",
        "{flag} iron_sword | data 0-25 // only accepts iron swords that have 0 to 25 damage.",
        "{flag} wool | data vanilla, !wool:red // no red wool",
        "{flag} wool | data all, !vanilla // only modded data values",
        "{flag} iron_sword | data new // Only allow undamaged iron swords",
        "{flag} gold_sword | data damaged // Only allow damaged gold swords",
        "{flag} potion | data &16384, !&64 // checks if potion is splash and NOT extended (see http://www.minecraftwiki.net/wiki/Data_value#Potions)",
        "{flag} diamond_helmet | enchant fire_resistance 1-3 | enchant thorns | data 0, 5, 50-100 // makes ingredient require 2 enchantments and some specific data values.",
        "{flag} stick | nometa // makes ingredient require a vanilla stick.",
        "{flag} stick | !meta  // Same as above.",
        "{flag} stick | name Crafted Stick | nolore | noenchant // makes ingredient require a stick with a name of 'Crafted Stick', but no lore or enchantments.",
       };

    // Flag code

    // TODO written book title, author, page num, chars per page, etc

    public class Conditions implements Cloneable {
        private ItemStack ingredient;
        private String failMessage;
        private Map<Short, Boolean> dataValues = new HashMap<Short, Boolean>();
        private Map<Short, Boolean> dataBits = new HashMap<Short, Boolean>();
        private int amount;
        private Map<Enchantment, Map<Short, Boolean>> enchants = new HashMap<Enchantment, Map<Short, Boolean>>();
        private String name;
        private List<String> lores = new ArrayList<String>();
        private Color minColor;
        private Color maxColor;
        private boolean noMeta = false;
        private boolean noName = false;
        private boolean noLore = false;
        private boolean noEnchant = false;
        private boolean noColor = false;
        private boolean allSet = false;

        // TODO mark
        // private boolean extinctRecipeBook;
        // private String recipeBook;
        // private int recipeBookVolume;

        public Conditions() {
        }

        public Conditions(Conditions original) {
            ingredient = original.ingredient.clone();

            failMessage = original.failMessage;

            dataValues.putAll(original.dataValues);
            dataBits.putAll(original.dataBits);

            amount = original.amount;

            for (Entry<Enchantment, Map<Short, Boolean>> e : original.enchants.entrySet()) {
                Map<Short, Boolean> map = new HashMap<Short, Boolean>(e.getValue().size());
                map.putAll(e.getValue());
                enchants.put(e.getKey(), map);
            }

            name = original.name;

            lores = original.lores;

            minColor = original.minColor;
            maxColor = original.maxColor;

            noMeta = original.noMeta;
            noName = original.noName;
            noLore = original.noLore;
            noEnchant = original.noEnchant;
            noColor = original.noColor;

            allSet = original.allSet;
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
                    short d = e.getKey().shortValue();

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
                    return allSet;
                }

                return is.booleanValue();
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
         * @param enchants
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
                levels = new HashMap<Short, Boolean>();
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
            if (noMeta || noEnchant) {
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
                        Boolean is = e.getValue().get(level.shortValue());

                        if (is == null) {
                            return false;
                        }

                        return is.booleanValue();
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

        public String getName() {
            return name;
        }

        public void setName(String newName) {
            if (newName == null) {
                name = null;
            } else {
                name = Tools.parseColors(newName, false);
            }
        }

        public boolean hasName() {
            return name != null;
        }

        public boolean checkName(String nameToCheck) {
            if (noMeta || noName) {
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
                        ErrorReporter.error("Flag " + getType() + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'http://regexpal.com' (or something similar) to test your regex code before using it.");
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
                lores.add(Tools.parseColors(newLore, false));
            }

        }

        public boolean hasLore() {
            return lores != null && !lores.isEmpty();
        }

        public boolean checkLore(List<String> loreToCheck) {
            if (noMeta || noLore) {
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
                        ErrorReporter.error("Flag " + getType() + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'http://regexpal.com' (or something similar) to test your regex code before using it.");
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

            if (matchedLoreChecks == totalLoreChecks) {
                return true;
            }

            return false;
        }

        /**
         * Set the color ranges.
         *
         * @param minColor
         *            color for min-range or null to disable color checking.
         * @param maxColor
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
            if (color != null && (noColor || noMeta)) {
                Color defaultColor = Bukkit.getItemFactory().getDefaultLeatherColor();
                return color == defaultColor;
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
        public boolean checkIngredient(ItemStack item, Args a) {
            boolean ok = true;

            if (!checkData(item.getDurability())) {
                if (a == null) {
                    return false;
                }

                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NODATA, getFailMessage(), "{item}", ToolsItem.print(item), "{data}", getDataString());
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }

            if (!checkAmount(item.getAmount())) {
                if (a == null) {
                    return false;
                }

                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOAMOUNT, getFailMessage(), "{item}", ToolsItem.print(item), "{amount}", getAmount());
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }

            if (!checkEnchants(item.getEnchantments())) {
                if (a == null) {
                    return false;
                }

                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOENCHANTS, getFailMessage(), "{item}", ToolsItem.print(item), "{enchants}", getEnchantsString());
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

                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NONAME, getFailMessage(), "{item}", ToolsItem.print(item), "{name}", getName());
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }

            if (!checkLore(meta.getLore())) {
                if (a == null) {
                    return false;
                }

                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOLORE, getFailMessage(), "{item}", ToolsItem.print(item), "{lore}", getLores());
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

                    a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOCOLOR, getFailMessage(), "{item}", ToolsItem.print(item), "{color}", getColorString());
                    ok = false;

                    if (getFailMessage() != null) {
                        return false;
                    }
                }
            }

            return ok;
        }
    }

    private Map<String, Conditions> conditions = new HashMap<String, Conditions>();

    public FlagIngredientCondition() {
    }

    public FlagIngredientCondition(FlagIngredientCondition flag) {
        for (Entry<String, Conditions> e : flag.conditions.entrySet()) {
            conditions.put(e.getKey(), e.getValue().clone());
        }
    }

    @Override
    public FlagIngredientCondition clone() {
        super.clone();
        return new FlagIngredientCondition(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    @Override
    protected boolean onParse(String value) {
        String[] args = value.split("\\|");

        if (args.length <= 1) {
            return ErrorReporter.error("Flag " + getType() + " needs an item and some arguments for conditions!", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        ItemStack item = Tools.parseItem(args[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null) {
            return false;
        }

        Conditions cond = getIngredientConditions(item);

        if (cond == null) {
            cond = new Conditions();
            setIngredientConditions(item, cond);
        }

        cond.setIngredient(item);

        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

            if (arg.startsWith("data")) {
                if (item.getDurability() != Vanilla.DATA_WILDCARD) {
                    ErrorReporter.warning("Flag " + getType() + " has 'data' argument but ingredient has specific data!", "The ingredient must have the 'any' data value set.");
                    continue;
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
                        cond.allSet = !not;
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
                            // ErrorReporter.warning("Flag " + getType() + " has 'data' argument with unknown material:data combination: " + val);
                            continue;
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
                                ErrorReporter.warning("Flag " + getType() + " has 'data' argument with invalid numbers: " + val);
                                continue;
                            }

                            if (min > max) {
                                ErrorReporter.warning("Flag " + getType() + " has 'data' argument with invalid number range: " + min + " to " + max);
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
                                ErrorReporter.warning("Flag " + getType() + " has 'data' argument with invalid number: " + val);
                                continue;
                            }
                        }
                    }
                }
            } else if (arg.startsWith("amount")) {
                value = arg.substring("amount".length()).trim();

                try {
                    cond.setAmount(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    ErrorReporter.warning("Flag " + getType() + " has 'amount' argument with invalid number: " + value);
                    continue;
                }
            } else if (arg.startsWith("!enchant") || arg.startsWith("noenchant")) {
                cond.noEnchant = true;
            } else if (arg.startsWith("enchant")) {
                value = arg.substring("enchant".length()).trim();

                String[] list = value.split(" ", 2);

                value = list[0].trim();

                Enchantment enchant = Tools.parseEnchant(value);

                if (enchant == null) {
                    ErrorReporter.warning("Flag " + getType() + " has 'enchant' argument with invalid name: " + value);
                    continue;
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
                                ErrorReporter.warning("Flag " + getType() + " has 'enchant' argument with invalid numbers: " + s);
                                continue;
                            }

                            if (min > max) {
                                ErrorReporter.warning("Flag " + getType() + " has 'enchant' argument with invalid number range: " + min + " to " + max);
                                continue;
                            }

                            cond.addEnchantLevelRange(enchant, min, max, !not);
                        } else {
                            try {
                                cond.addEnchantLevel(enchant, Short.valueOf(s.trim()), !not);
                            } catch (NumberFormatException e) {
                                ErrorReporter.warning("Flag " + getType() + " has 'enchant' argument with invalid number: " + s);
                                continue;
                            }
                        }
                    }
                } else {
                    cond.addEnchant(enchant);
                }
            } else if (arg.startsWith("!color") || arg.startsWith("nocolor")) {
                if (item.getItemMeta() instanceof LeatherArmorMeta) {
                    cond.noColor = true;
                }
            } else if (arg.startsWith("color")) {
                if (!(item.getItemMeta() instanceof LeatherArmorMeta)) {
                    ErrorReporter.warning("Flag " + getType() + " has 'color' argument for an item that is not leather armor.", "RGB can only be applied to leather, for wool and dye use the 'data' argument.");
                    continue;
                }

                value = arg.substring("color".length()).trim();

                DyeColor dye = Tools.parseEnum(value, DyeColor.values());

                if (dye == null) {
                    String[] split = value.split(",", 3);

                    if (split.length != 3) {
                        ErrorReporter.warning("Flag " + getType() + " has 'color' argument with less than 3 colors separated by comma: " + value);
                        continue;
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
                                ErrorReporter.warning("Flag " + getType() + " has 'color' argument with invalid range: " + min + " to " + max, "Numbers must be from 0 to 255 and min must be less or equal to max!");
                                break;
                            }

                            //minColor[c] = min;
                            //maxColor[c] = max;
                        } catch (NumberFormatException e) {
                            ErrorReporter.warning("Flag " + getType() + " has 'color' argument with invalid number: " + value);
                            continue;
                        }
                    }
                } else {
                    cond.setColor(dye.getColor(), null);
                }
            } else if (arg.startsWith("!name") || arg.startsWith("noname")) {
                cond.noName = true;
            } else if (arg.startsWith("name")) {
                value = args[i].trim().substring("name".length()).trim(); // preserve case for regex

                cond.setName(value);
            } else if (arg.startsWith("!lore") || arg.startsWith("nolore")) {
                cond.noLore = true;
            } else if (arg.startsWith("lore")) {
                value = args[i].trim().substring("lore".length()).trim(); // preserve case for regex

                cond.addLore(value);
            } else if (arg.startsWith("!meta") || arg.startsWith("nometa")) {
                cond.noMeta = true;
            } else if (arg.startsWith("failmsg")) {
                value = args[i].trim().substring("failmsg".length()).trim(); // preserve case... because it's a message

                cond.setFailMessage(value);
            } else {
                ErrorReporter.warning("Flag " + getType() + " has unknown argument: " + args[i]);
            }
        }

        return true;
    }

    @Override
    protected void onRegistered() {
        Iterator<Conditions> it = conditions.values().iterator();
        BaseRecipe recipe = getRecipeDeep();

        while (it.hasNext()) {
            Conditions c = it.next();

            if (c.ingredient != null && Tools.findItemInIngredients(recipe, c.ingredient.getType(), c.ingredient.getDurability()) == 0) {
                ErrorReporter.error("Flag " + getType() + " has couldn't find ingredient: " + ToolsItem.print(c.ingredient));
                it.remove();
            }
        }
    }
    // TODO: Better handle conditions to allow multiple recipes per item:dur
    public void setIngredientConditions(ItemStack item, Conditions cond) {
        Validate.notNull(item, "item argument must not be null!");
        Validate.notNull(cond, "cond argument must not be null!");
        conditions.put(Tools.convertItemToStringId(item), cond);
    }

    public Conditions getIngredientConditions(ItemStack item) {
        if (item == null) {
            return null;
        }

        Conditions cond = conditions.get(String.valueOf(item.getTypeId() + ":" + item.getDurability()));

        if (cond == null) {
            cond = conditions.get(String.valueOf(item.getTypeId()));
        }

        return cond;
    }

    /**
     * @param item
     *            returns false if null.
     * @param a
     *            arguments to store reasons or null to just use return value.
     * @return true if passed, false otherwise
     */
    public boolean checkIngredientConditions(ItemStack item, Args a) {
        if (item == null) {
            return false;
        }

        Conditions cond = getIngredientConditions(item);

        if (cond == null) {
            return true;
        }

        return cond.checkIngredient(item, a);
    }

    @Override
    protected void onCheck(Args a) {
        if (!a.hasInventory()) {
            a.addCustomReason("Needs inventory!");
            return;
        }

        if (a.inventory() instanceof CraftingInventory) {
            for (int i = 1; i < 10; i++) {
                ItemStack item = a.inventory().getItem(i);

                if (item != null) {
                    checkIngredientConditions(item, a);
                }
            }

            return;
        } else if (a.inventory() instanceof FurnaceInventory) {
            ItemStack smelting = ToolsItem.nullIfAir((ItemStack) a.extra());

            if (smelting != null) {
                checkIngredientConditions(smelting, a);
            }

            return;
        } else if (a.inventory() instanceof BrewerInventory) {
            ItemStack ingredient = a.inventory().getItem(3);

            if (ingredient != null) {
                checkIngredientConditions(ingredient, a);
            }

            return;
        }

        a.addCustomReason("Unknown inventory type: " + a.inventory());
    }
}
