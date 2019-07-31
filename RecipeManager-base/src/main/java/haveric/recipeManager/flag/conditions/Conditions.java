package haveric.recipeManager.flag.conditions;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.util.ParseBit;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

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
    private Map<Enchantment, Map<Integer, Boolean>> enchants = new HashMap<>();
    private Map<Enchantment, Map<Integer, Boolean>> bookEnchants = new HashMap<>();
    private String name;
    private List<String> lores = new ArrayList<>();
    private Color minColor;
    private Color maxColor;
    private Boolean unbreakable;
    private Map<PotionType, ConditionPotion> potionConditions = new HashMap<>();
    private Map<PotionEffectType, ConditionPotionEffect> potionEffectConditions = new HashMap<>();
    private DyeColor bannerColor;
    private Map<PatternType, DyeColor> bannerPatterns = new HashMap<>();
    private EntityType spawnEggEntityType;
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

        for (Entry<Enchantment, Map<Integer, Boolean>> e : original.enchants.entrySet()) {
            Map<Integer, Boolean> map = new HashMap<>(e.getValue().size());
            map.putAll(e.getValue());
            enchants.put(e.getKey(), map);
        }

        for (Entry<Enchantment, Map<Integer, Boolean>> e : original.bookEnchants.entrySet()) {
            Map<Integer, Boolean> map = new HashMap<>(e.getValue().size());
            map.putAll(e.getValue());
            bookEnchants.put(e.getKey(), map);
        }

        name = original.name;

        lores = original.lores;

        minColor = original.minColor;
        maxColor = original.maxColor;
        unbreakable = original.unbreakable;

        potionConditions.putAll(original.potionConditions);
        potionEffectConditions.putAll(original.potionEffectConditions);
        bannerColor = original.bannerColor;
        bannerPatterns.putAll(original.bannerPatterns);
        spawnEggEntityType = original.spawnEggEntityType;

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

    public void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        failMessage = RMCUtil.parseColors(message, false);
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
    public Map<Enchantment, Map<Integer, Boolean>> getEnchants() {
        return enchants;
    }

    /**
     * Set the enchants map.<br>
     * Setting to null will clear the map contents.
     *
     * @param newEnchants
     */
    public void setEnchants(Map<Enchantment, Map<Integer, Boolean>> newEnchants) {
        if (newEnchants == null) {
            enchants.clear();
        } else {
            enchants = newEnchants;
        }
    }

    public void addEnchant(Enchantment enchant) {
        enchants.put(enchant, new HashMap<>(0));
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
        Map<Integer, Boolean> levels = enchants.get(enchant);

        if (levels == null) {
            levels = new HashMap<>();
            enchants.put(enchant, levels);
        }

        for (int i = min; i <= max; i++) {
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
            for (Entry<Enchantment, Map<Integer, Boolean>> e : enchants.entrySet()) {
                boolean checkedHasEnchant = enchantsToCheck.containsKey(e.getKey());

                if (checkedHasEnchant) {
                    // All levels of the enchant are allowed
                    if (e.getValue().isEmpty()) {
                        continue;
                    }

                    Integer level = enchantsToCheck.get(e.getKey());

                    boolean hasEnchant = e.getValue().get(level) != null;

                    if (!hasEnchant) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public String getEnchantsString() {
        StringBuilder s = new StringBuilder();

        for (Entry<Enchantment, Map<Integer, Boolean>> e : getEnchants().entrySet()) {
            if (s.length() > 0) {
                s.append("; ");
            }

            s.append(e.getKey().getName());

            if (!e.getValue().isEmpty()) {
                s.append(' ');
                boolean first = true;

                for (Entry<Integer, Boolean> l : e.getValue().entrySet()) {
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
    public Map<Enchantment, Map<Integer, Boolean>> getBookEnchants() {
        return bookEnchants;
    }

    /**
     * Set the book enchants map.<br>
     * Setting to null will clear the map contents.
     *
     * @param newEnchants
     */
    public void setBookEnchants(Map<Enchantment, Map<Integer, Boolean>> newEnchants) {
        if (newEnchants == null) {
            bookEnchants.clear();
        } else {
            bookEnchants = newEnchants;
        }
    }

    public void addBookEnchant(Enchantment enchant) {
        bookEnchants.put(enchant, new HashMap<>(0));
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
        Map<Integer, Boolean> levels = bookEnchants.get(enchant);

        if (levels == null) {
            levels = new HashMap<>();
            bookEnchants.put(enchant, levels);
        }

        for (int i = min; i <= max; i++) {
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
            for (Entry<Enchantment, Map<Integer, Boolean>> e : bookEnchants.entrySet()) {
                boolean checkedHasEnchant = enchantsToCheck.containsKey(e.getKey());

                if (checkedHasEnchant) {
                    // All levels of the enchant are allowed
                    if (e.getValue().isEmpty()) {
                        continue;
                    }

                    Integer level = enchantsToCheck.get(e.getKey());

                    boolean hasEnchant = e.getValue().get(level) != null;

                    if (!hasEnchant) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public String getBookEnchantsString() {
        StringBuilder s = new StringBuilder();

        for (Entry<Enchantment, Map<Integer, Boolean>> e : getBookEnchants().entrySet()) {
            if (s.length() > 0) {
                s.append("; ");
            }

            s.append(e.getKey().getName());

            if (!e.getValue().isEmpty()) {
                s.append(' ');
                boolean first = true;

                for (Entry<Integer, Boolean> l : e.getValue().entrySet()) {
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
                    ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'https://www.regexpal.com/' (or something similar) to test your regex code before using it.");
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
                    ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid regex pattern '" + e.getPattern() + "', error: " + e.getMessage(), "Use 'https://www.regexpal.com/' (or something similar) to test your regex code before using it.");
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

    public boolean hasPotion() {
        return !potionConditions.isEmpty();
    }

    public boolean checkPotion(PotionMeta meta, Material potionMaterial) {
        int conditionsMet = 0;

        for (Map.Entry<PotionType, ConditionPotion> entry : potionConditions.entrySet()) {
            boolean success = true;
            PotionType type = entry.getKey();
            ConditionPotion cond = entry.getValue();

            PotionData data = meta.getBasePotionData();
            if (type == null || type.equals(data.getType())) {
                if (cond.hasExtended()) {
                    if (!cond.getExtended().equals(data.isExtended())) {
                        success = false;
                    }
                }

                if (cond.hasLevel()) {
                    if (cond.getLevel() == 1 && data.isUpgraded() || cond.getLevel() == 2 && !data.isUpgraded()) {
                        success = false;
                    }
                }
            } else {
                success = false;
            }

            if (success) {
                conditionsMet ++;
            }
        }

        return conditionsMet == potionConditions.entrySet().size();
    }

    public boolean hasPotionEffect() {
        return !potionEffectConditions.isEmpty();
    }

    public boolean checkPotionEffect(PotionMeta meta) {
        int conditionsMet = 0;

        for (Map.Entry<PotionEffectType, ConditionPotionEffect> entry : potionEffectConditions.entrySet()) {
            boolean anySuccess = false;
            PotionEffectType type = entry.getKey();
            ConditionPotionEffect cond = entry.getValue();

            List<PotionEffect> effects = meta.getCustomEffects();
            for (PotionEffect effect : effects) {
                boolean success = true;
                if (type == null || type.equals(effect.getType())) {
                    if (cond.hasDuration()) {
                        int duration = effect.getDuration();

                        if (duration < cond.getDurationMinLevel() || duration > cond.getDurationMaxLevel()) {
                            success = false;
                        }
                    }

                    if (cond.hasAmplify()) {
                        int amplifier = effect.getAmplifier();

                        if (amplifier < cond.getAmplifyMinLevel() || amplifier > cond.getAmplifyMaxLevel()) {
                            success = false;
                        }
                    }

                    if (cond.hasAmbient()) {
                        if (!cond.getAmbient().equals(effect.isAmbient())) {
                            success = false;
                        }
                    }
                } else {
                    success = false;
                }

                if (success) {
                    anySuccess = true;
                    break;
                }
            }

            if (anySuccess) {
                conditionsMet ++;
            }
        }

        return conditionsMet == potionConditions.entrySet().size();
    }

    public void setBannerColor(DyeColor color) {
        bannerColor = color;
    }

    public boolean hasBannerColor() {
        return bannerColor != null;
    }

    public boolean checkBannerColor(BannerMeta meta) {
        return meta.getBaseColor().equals(bannerColor);
    }

    public void addBannerPattern(PatternType pattern, DyeColor color) {
        bannerPatterns.put(pattern, color);
    }
    public boolean hasBannerPatterns() {
        return !bannerPatterns.isEmpty();
    }

    public boolean checkBannerPatterns(BannerMeta meta) {
        List<org.bukkit.block.banner.Pattern> patterns = meta.getPatterns();
        int conditionsMet = 0;

        for (Entry<PatternType, DyeColor> entry : bannerPatterns.entrySet()) {

            PatternType type = entry.getKey();
            DyeColor dye = entry.getValue();

            for (org.bukkit.block.banner.Pattern pattern : patterns) {
                if (pattern.getPattern() == type) {
                    if (dye == null || dye == pattern.getColor()) {
                        conditionsMet ++;
                        break;
                    }
                }
            }
        }

        return conditionsMet == bannerPatterns.entrySet().size();
    }

    public void setSpawnEggEntityType(EntityType entityType) {
        spawnEggEntityType = entityType;
    }

    public boolean hasSpawnEggEntityType() {
        return spawnEggEntityType != null;
    }

    public boolean checkSpawnEggEntityType(SpawnEggMeta meta) {
        return meta.getSpawnedType().equals(spawnEggEntityType);
    }

    public Boolean getUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(Boolean newUnbreakable) {
        unbreakable = newUnbreakable;
    }

    public boolean hasUnbreakable() {
        return unbreakable != null;
    }

    public boolean checkUnbreakable(boolean unbreakableToCheck) {
        if (isNoMeta()) {
            return !unbreakableToCheck;
        } else {
            return !hasUnbreakable() || unbreakable == unbreakableToCheck;
        }
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

        if (hasPotion()) {
            boolean failed = true;

            if (meta instanceof PotionMeta) {
                PotionMeta potion = (PotionMeta) meta;

                if (checkPotion(potion, item.getType())) {
                    failed = false;
                }
            }

            if (failed) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.nopotion", getFailMessage(), "{item}", ToolsItem.print(item), "{potion}", potionConditions); // TODO: This probably needs updating
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        if (hasPotionEffect()) {
            boolean failed = true;

            if (meta instanceof PotionMeta) {
                PotionMeta potion = (PotionMeta) meta;

                if (checkPotionEffect(potion)) {
                    failed = false;
                }
            }

            if (failed) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.nopotioneffect", getFailMessage(), "{item}", ToolsItem.print(item), "{effect}", potionEffectConditions); // TODO: This probably needs updating
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        if (hasBannerColor()) {
            boolean failed = true;

            if (meta instanceof BannerMeta) {
                BannerMeta banner = (BannerMeta) meta;

                if (checkBannerColor(banner)) {
                    failed = false;
                }
            }

            if (failed) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.nobannercolor", getFailMessage(), "{item}", ToolsItem.print(item), "color", bannerColor);
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        if (hasBannerPatterns()) {
            boolean failed = true;

            if (meta instanceof BannerMeta) {
                BannerMeta banner = (BannerMeta) meta;

                if (checkBannerPatterns(banner)) {
                    failed = false;
                }
            }

            if (failed) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.nobannerpatterns", getFailMessage(), "{item}", ToolsItem.print(item), "{patterns)", bannerPatterns); // TODO: Check if these messages are actually being used and if so probably fix bannerPatterns output
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        if (hasSpawnEggEntityType()) {
            boolean failed = true;

            if (meta instanceof SpawnEggMeta) {
                SpawnEggMeta spawnEggMeta = (SpawnEggMeta) meta;

                if (checkSpawnEggEntityType(spawnEggMeta)) {
                    failed = false;
                }
            }

            if (failed) {
                if (a == null) {
                    return false;
                }

                if (addReasons) {
                    a.addReason("flag.ingredientconditions.nospawneggentitytype", getFailMessage(), "{item}", ToolsItem.print(item), "{entitytype)", spawnEggEntityType);
                }
                ok = false;

                if (getFailMessage() != null) {
                    return false;
                }
            }
        }

        if (Version.has1_11Support() && !checkUnbreakable(meta.isUnbreakable())) {
            if (a == null) {
                return false;
            }

            if (addReasons) {
                if (hasUnbreakable() && unbreakable) {
                    a.addReason("flag.ingredientconditions.unbreakable", getFailMessage(), "{item}", ToolsItem.print(item));
                } else {
                    a.addReason("flag.ingredientconditions.unbreakable", getFailMessage(), "{item}", ToolsItem.print(item));
                }
            }
            ok = false;

            if (getFailMessage() != null) {
                return false;
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

    public void parseArg(String value, String arg) {
        // Replace double pipes with single pipe: || -> |
        arg = arg.replaceAll("\\|\\|", "|");

        ItemStack item = getIngredient();
        String argLower = arg.toLowerCase();

        if (argLower.startsWith("data")) {
            if (item.getDurability() != RMCVanilla.DATA_WILDCARD) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'data' argument but ingredient has specific data!", "The ingredient must have the 'any' data value set.");
                return;
            }

            value = argLower.substring("data".length()).trim();

            String[] list = value.split(",");

            for (String val : list) {
                val = val.trim();
                boolean not = val.charAt(0) == '!';

                if (not) {
                    val = val.substring(1).trim();
                }

                short maxDurability = item.getType().getMaxDurability();
                if (val.equals("all")) {
                    setAllSet(!not);
                } else if (val.equals("vanilla")) {
                    addDataValueRange((short) 0, maxDurability, !not);
                } else if (val.equals("damaged")) {
                    if ((maxDurability - 1) > 0) {
                        addDataValueRange((short) 1, maxDurability, !not);
                    }
                } else if (val.equals("new")) {
                    addDataValueRange((short) 0, (short) 0, !not);
                } else if (val.matches("(.*):(.*)")) {
                    ItemStack match = Tools.parseItem(val, RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

                    if (match != null && match.getDurability() != RMCVanilla.DATA_WILDCARD) {
                        addDataValue(match.getDurability(), !not);
                    }/* else {
                        // ErrorReporter.getInstance().warning("Flag " + getType() + " has 'data' argument with unknown material:data combination: " + val);
                    }*/
                } else {
                    String[] split = val.split("-");

                    if (split.length > 1) {
                        short min;
                        short max;

                        try {
                            min = Short.valueOf(split[0].trim());
                            max = Short.valueOf(split[1].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'data' argument with invalid numbers: " + val);
                            continue;
                        }

                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'data' argument with invalid number range: " + min + " to " + max);
                            break;
                        }

                        addDataValueRange(min, max, !not);
                    } else {
                        val = val.trim();
                        boolean bitwise = val.charAt(0) == '&';

                        if (bitwise) {
                            val = val.substring(1).trim();
                        }

                        try {
                            if (bitwise) {
                                addDataBit(Short.valueOf(val), !not);
                            } else {
                                addDataValue(Short.valueOf(val), !not);
                            }
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'data' argument with invalid number: " + val);
                        }
                    }
                }
            }
        } else if (argLower.startsWith("amount")) {
            value = arg.substring("amount".length()).trim();

            try {
                setAmount(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'amount' argument with invalid number: " + value);
            }
        } else if (argLower.startsWith("!enchant") || argLower.startsWith("noenchant")) {
            setNoEnchant(true);
        } else if (argLower.startsWith("enchant")) {
            value = argLower.substring("enchant".length()).trim();

            String[] list = value.split(" ", 2);

            value = list[0].trim();

            Enchantment enchant = Tools.parseEnchant(value);

            if (enchant == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'enchant' argument with invalid name: " + value);
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
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'enchant' argument with invalid numbers: " + s);
                            continue;
                        }

                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'enchant' argument with invalid number range: " + min + " to " + max);
                            continue;
                        }

                        addEnchantLevelRange(enchant, min, max, !not);
                    } else {
                        try {
                            addEnchantLevel(enchant, Short.valueOf(s.trim()), !not);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'enchant' argument with invalid number: " + s);
                        }
                    }
                }
            } else {
                addEnchant(enchant);
            }
        } else if (argLower.startsWith("!bookenchant") || argLower.startsWith("nobookenchant")) {
            if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
                setNoBookEnchant(true);
            }
        } else if (argLower.startsWith("bookenchant")) {
            if (!(item.getItemMeta() instanceof EnchantmentStorageMeta)) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'bookenchant' argument for an item that is not an enchanted book.");
                return;
            }

            value = argLower.substring("bookenchant".length()).trim();

            String[] list = value.split(" ", 2);

            value = list[0].trim();

            Enchantment enchant = Tools.parseEnchant(value);

            if (enchant == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'bookenchant' argument with invalid name: " + value);
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
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'bookenchant' argument with invalid numbers: " + s);
                            continue;
                        }

                        if (min > max) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'bookenchant' argument with invalid number range: " + min + " to " + max);
                            continue;
                        }

                        addBookEnchantLevelRange(enchant, min, max, !not);
                    } else {
                        try {
                            addBookEnchantLevel(enchant, Short.valueOf(s.trim()), !not);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'bookenchant' argument with invalid number: " + s);
                        }
                    }
                }
            } else {
                addBookEnchant(enchant);
            }
        } else if (argLower.startsWith("!color") || argLower.startsWith("nocolor")) {
            if (item.getItemMeta() instanceof LeatherArmorMeta) {
                setNoColor(true);
            }
        } else if (argLower.startsWith("color")) {
            if (!(item.getItemMeta() instanceof LeatherArmorMeta)) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument for an item that is not leather armor.", "RGB can only be applied to leather, for wool and dye use the 'data' argument.");
                return;
            }

            value = argLower.substring("color".length()).trim();

            DyeColor dye = RMCUtil.parseEnum(value, DyeColor.values());

            if (dye == null) {
                String[] split = value.split(",", 3);

                if (split.length != 3) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument with less than 3 colors separated by comma: " + value);
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

                        if (min < 0 || min > max || max > 255) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument with invalid range: " + min + " to " + max, "Numbers must be from 0 to 255 and min must be less or equal to max!");
                            break;
                        }

                        //minColor[c] = min;
                        //maxColor[c] = max;
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument with invalid number: " + value);
                    }
                }
            } else {
                setColor(dye.getColor(), null);
            }
        } else if (argLower.startsWith("!name") || argLower.startsWith("noname")) {
            setNoName(true);
        } else if (argLower.startsWith("name")) {
            value = arg.substring("name".length()); // preserve case for regex

            setName(RMCUtil.trimExactQuotes(value));
        } else if (argLower.startsWith("!lore") || argLower.startsWith("nolore")) {
            setNoLore(true);
        } else if (argLower.startsWith("lore")) {
            value = arg.substring("lore".length()); // preserve case for regex

            addLore(RMCUtil.trimExactQuotes(value));
        } else if (argLower.startsWith("!unbreakable") || argLower.startsWith("nounbreakable")) {
            setUnbreakable(false);
        } else if (argLower.startsWith("unbreakable")) {
            setUnbreakable(true);
        } else if (argLower.startsWith("!meta") || argLower.startsWith("nometa")) {
            setNoMeta(true);
        } else if (argLower.startsWith("failmsg")) {
            value = arg.substring("failmsg".length()); // preserve case because it's a message

            setFailMessage(RMCUtil.trimExactQuotes(value));
        } else if (argLower.startsWith("potion")) {
            value = argLower.substring("potion".length()).trim();

            ConditionPotion potionCond = new ConditionPotion();
            PotionType potionType = null;

            String[] split = value.split(",");
            for (String element : split) {
                element = element.trim();

                if (element.equals("extended")) {
                    potionCond.setExtended(true);
                } else if (element.equals("!extended")) {
                    potionCond.setExtended(false);
                } else if (element.startsWith("type")) {
                    String[] typeSplit = element.split(" ");
                    try {
                        potionType = PotionType.valueOf(typeSplit[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'type' argument with invalid potion type: " + typeSplit[1]);
                    }
                } else if (element.startsWith("level")) {
                    String[] levelSplit = element.split(" ");

                    try {
                        if (levelSplit.length > 1) {
                            potionCond.setLevel(Integer.parseInt(levelSplit[1]));
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'level' argument with invalid value: " + levelSplit[1]);
                    }
                }
            }

            potionConditions.put(potionType, potionCond);
        } else if (argLower.startsWith("potioneffect")) {
            value = argLower.substring("potioneffect".length()).trim();

            ConditionPotionEffect effectCond = new ConditionPotionEffect();
            PotionEffectType effectType = null;

            String[] split = value.split(",");
            for (String element : split) {
                if (element.equals("ambient")) {
                    effectCond.setAmbient(true);
                } else if (element.equals("!ambient")) {
                    effectCond.setAmbient(false);
                } else if (element.startsWith("type")) {
                    String[] typeSplit = element.split(" ");
                    try {
                        effectType = PotionEffectType.getByName(typeSplit[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'type' argument with invalid potion effect type: " + typeSplit[1]);
                    }
                } else if (element.startsWith("duration")) {
                    String[] durationSplit = element.split(" ", 2);
                    String durationValue = durationSplit[1].trim();
                    durationSplit = durationValue.split("-");

                    try {
                        if (durationSplit.length > 1) {
                            effectCond.setDurationMinLevel(Integer.parseInt(durationSplit[0]));
                            effectCond.setDurationMaxLevel(Integer.parseInt(durationSplit[1]));
                        } else {
                            int level = Integer.parseInt(durationSplit[0]);
                            effectCond.setDurationMinLevel(level);
                            effectCond.setDurationMaxLevel(level);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'duration' argument with invalid value: " + durationValue);
                    }
                } else if (element.startsWith("amplify")) {
                    String[] amplifySplit = element.split(" ", 2);
                    String amplifyValue = amplifySplit[1].trim();
                    amplifySplit = amplifyValue.split("-");

                    try {
                        if (amplifySplit.length > 1) {
                            effectCond.setAmplifyMinLevel(Integer.parseInt(amplifySplit[0]));
                            effectCond.setAmplifyMaxLevel(Integer.parseInt(amplifySplit[1]));
                        } else {
                            int level = Integer.parseInt(amplifySplit[0]);
                            effectCond.setAmplifyMinLevel(level);
                            effectCond.setAmplifyMaxLevel(level);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'amplify' argument with invalid value: " + amplifyValue);
                    }
                }
            }

            potionEffectConditions.put(effectType, effectCond);
        } else if (argLower.startsWith("banner")) {
            value = argLower.substring("banner".length()).trim();

            String[] split = value.split(",");
            for (String element : split) {
                if (element.startsWith("color")) {
                    String[] colorSplit = element.split(" ", 2);
                    String colorValue = colorSplit[1].trim();

                    try {
                        DyeColor dye = DyeColor.valueOf(colorValue.toUpperCase());
                        setBannerColor(dye);
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'color' argument with invalid dye color: " + colorValue);
                    }
                } else if (element.startsWith("pattern")) {
                    String[] patternSplit = element.split(" ", 3);
                    String patternValue = patternSplit[1].trim();

                    PatternType pattern = null;
                    try {
                        pattern = PatternType.valueOf(patternValue.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pattern' argument with invalid pattern type: " + patternValue);
                    }

                    DyeColor dye = null;
                    if (patternSplit.length > 2) {
                        try {
                            String colorValue = patternSplit[2].trim();
                            dye = DyeColor.valueOf(colorValue.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'pattern' argument with invalid pattern type: " + patternValue);
                        }
                    }

                    if (pattern != null) {
                        addBannerPattern(pattern, dye);
                    }
                }
            }
        } else if (argLower.startsWith("spawnegg")) {
            value = argLower.substring("spawnegg".length()).trim();

            try {
                EntityType entityType = EntityType.valueOf(value.toUpperCase());
                setSpawnEggEntityType(entityType);
            } catch (IllegalArgumentException e) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'spawnegg' argument with invalid entity type: " + value);
            }
        } else {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown argument: " + arg);
        }
    }
}