package haveric.recipeManager.tools;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.Recipes;
import haveric.recipeManager.Settings;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.ItemResult;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.bukkit.Tag.REGISTRY_BLOCKS;
import static org.bukkit.Tag.REGISTRY_ITEMS;

/**
 * Collection of conversion and useful methods
 */
public class Tools {
    public static Enchantment parseEnchant(String value) {
        value = RMCUtil.parseAliasName(value);

        Enchantment enchant = Settings.getInstance().getEnchantment(value);

        if (enchant != null) {
            return enchant;
        }

        for (Enchantment e : Enchantment.values()) {
            String s = e.getName().toLowerCase().replaceAll("[_\\s]+", "");

            if (s.equals(value)) {
                return e;
            }
        }

        return null;
    }

    public static boolean playerCanAddItem(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        int amount = item.getAmount();
        int available = 0;

        int itemMaxStackSize = item.getType().getMaxStackSize();
        int inventoryMaxStackSize = inv.getMaxStackSize();
        for (ItemStack i : inv.getStorageContents()) {
            if (i == null || i.getType() == Material.AIR) {
                available += itemMaxStackSize;
            } else if (item.isSimilar(i)) {
                available += Math.max(Math.min(i.getMaxStackSize(), inventoryMaxStackSize) - i.getAmount(), 0);
            }

            if (available >= amount) {
                return true;
            }
        }

        return false;
    }

    public static String parseAliasPrint(String name) {
        return WordUtils.capitalize(name.toLowerCase().replace('_', ' ').trim());
    }

    public static String printLocation(Location l) {
        return l.getWorld().getName() + ":" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
    }

    public static ItemResult parseItemResult(String string, int defaultData) {
        return parseItemResult(string, defaultData, 0);
    }

    public static ItemResult parseItemResult(String string, int defaultData, int settings) {
        String[] split = string.substring(1).trim().split("%");
        ItemResult result = new ItemResult();
        result.setChance(-1);

        if (split.length >= 2) {
            string = split[0].trim();

            if (!string.equals("*") && !string.equalsIgnoreCase("calc")) {
                try {
                    result.setChance(Math.min(Math.max(Float.parseFloat(string), 0), 100));
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Invalid percentage number: " + string);
                }
            }

            string = split[1];
        } else {
            string = split[0];
        }

        ItemStack item = parseItem(string, defaultData, settings);

        if (item == null) {
            return null;
        }

        result.setItemStack(item);

        return result;
    }

    public static List<Material> parseChoice(String value, int settings) {
        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String[] args = value.split(";");
        if (args.length > 1) {
            if ((settings & ParseBit.NO_ERRORS) != ParseBit.NO_ERRORS) {
                ErrorReporter.getInstance().warning("Inline name, lore, enchant no longer supported in 1.13 or newer. Ignoring them.");
            }
        }

        String[] split = args[0].split(",");
        if (split.length <= 0 || split[0].isEmpty()) {
            return null;
        }

        List<Material> choices = new ArrayList<>();
        for (String s : split) {
            String[] durSplit = s.trim().split(":");
            value = durSplit[0].trim();

            if (durSplit.length > 1 && value.equals("tag") || value.equals("t")) {
                String namespace;
                String material;
                if (durSplit.length > 2) {
                    namespace = durSplit[1].trim();
                    material = durSplit[2].trim();
                } else {
                    namespace = NamespacedKey.MINECRAFT;
                    material = durSplit[1].trim();
                }

                NamespacedKey key = new NamespacedKey(namespace, material); // If this deprecated constructor goes away, Loop through Bukkit.getPluginManager().getPlugins() to check any potential namespace?
                Tag<Material> tag = Bukkit.getTag(REGISTRY_BLOCKS, key, Material.class);

                if (tag == null || tag.getValues().isEmpty()) {
                    tag = Bukkit.getTag(REGISTRY_ITEMS, key, Material.class);
                }

                if (tag == null || tag.getValues().isEmpty()) {
                    ErrorReporter.getInstance().warning("Invalid tag: " + s);
                } else {
                    choices.addAll(tag.getValues());
                }
            } else if (durSplit.length > 1 && value.equals("alias") || value.equals("a")) {
                String alias = durSplit[1].trim();
                List<Material> materials = Settings.getInstance().getChoicesAlias(alias);

                if (materials == null) {
                    ErrorReporter.getInstance().warning("Invalid alias: " + s);
                } else {
                    choices.addAll(materials);
                }
            } else {
                if (durSplit.length > 1) {
                    ErrorReporter.getInstance().warning("Ingredient data is no longer supported in 1.13 or newer. Ignoring data.", "Try using " + FlagType.INGREDIENT_CONDITION + " with the data attribute. Use the needed attribute if multiple of the same ingredient exist and need to be unique.");
                }

                Material material = Settings.getInstance().getMaterial(value);

                if (material == null) {
                    material = Material.matchMaterial(value);
                }

                if (material == null) {
                    ErrorReporter.getInstance().warning("Material '" + value + "' does not exist!", "Name could be different, look in '" + Files.FILE_INFO_NAMES + "' or '" + Files.FILE_ITEM_ALIASES + "' for material names.");
                } else {
                    choices.add(material);
                }
            }
        }

        return choices;
    }

    public static Map<List<Material>, Integer> parseChoiceWithAmount(String value, int settings) {
        value = value.trim();

        if (value.length() == 0) {
            return null;
        }


        String[] args = value.split(";");
        if (args.length > 1) {
            ErrorReporter.getInstance().warning("Inline name, lore, enchant no longer supported in 1.13 or newer. Ignoring them.");
        }

        String[] split = args[0].split(",");
        if (split.length <= 0 || split[0].isEmpty()) {
            return null;
        }

        List<Material> choices = new ArrayList<>();
        int amount = 1;
        int choicesSize = split.length;
        String amountString = null;
        for (int i = 0; i < choicesSize; i++) {
            String[] durSplit = split[i].trim().split(":");
            value = durSplit[0];

            // Formats
            // tag:namespace:tagname:amount
            // tag:tagname:amount
            // tag:namespace:tagname
            // tag:tagname
            if (durSplit.length > 1 && value.equals("tag") || value.equals("t")) {
                String namespace;
                String material;
                if (durSplit.length > 4) {
                    ErrorReporter.getInstance().warning("Invalid tag format: " + split[i]);
                    continue;
                } else if (durSplit.length == 4) {
                    namespace = durSplit[1].trim();
                    material = durSplit[2].trim();
                    amountString = durSplit[3].trim();
                } else if (durSplit.length == 3) {
                    try {
                        Integer.parseInt(durSplit[2].trim());
                        amountString = durSplit[2].trim();
                        namespace = NamespacedKey.MINECRAFT;
                        material = durSplit[1].trim();
                    } catch (NumberFormatException e) {
                        namespace = durSplit[1].trim();
                        material = durSplit[2].trim();
                    }
                } else {
                    namespace = NamespacedKey.MINECRAFT;
                    material = durSplit[1].trim();
                }

                NamespacedKey key = new NamespacedKey(namespace, material); // If this deprecated constructor goes away, Loop through Bukkit.getPluginManager().getPlugins() to check any potential namespace?
                Tag<Material> tag = Bukkit.getTag(REGISTRY_BLOCKS, key, Material.class);

                if (tag == null || tag.getValues().isEmpty()) {
                    tag = Bukkit.getTag(REGISTRY_ITEMS, key, Material.class);
                }

                if (tag == null || tag.getValues().isEmpty()) {
                    ErrorReporter.getInstance().warning("Invalid tag: " + split[i]);
                } else {
                    choices.addAll(tag.getValues());
                }
            } else if (durSplit.length > 1 && value.equals("alias") || value.equals("a")) {
                String alias = durSplit[1].trim();
                List<Material> materials = Settings.getInstance().getChoicesAlias(alias);

                if (materials == null) {
                    ErrorReporter.getInstance().warning("Invalid alias: " + split[i]);
                } else {
                    choices.addAll(materials);
                }

                if (durSplit.length == 3) {
                    amountString = durSplit[2].trim();
                }
            } else {
                Material material = Settings.getInstance().getMaterial(value);

                if (material == null) {
                    material = Material.matchMaterial(value);
                }

                if (material == null) {
                    if ((settings & ParseBit.NO_ERRORS) != ParseBit.NO_ERRORS) {
                        ErrorReporter.getInstance().error("Material '" + value + "' does not exist!", "Name could be different, look in '" + Files.FILE_INFO_NAMES + "' or '" + Files.FILE_ITEM_ALIASES + "' for material names.");
                    }

                    return null;
                }

                choices.add(material);

                if (durSplit.length == 2) {
                    amountString = durSplit[1].trim();
                } else if (durSplit.length == 3) {
                    ErrorReporter.getInstance().warning("Data is no longer supported on ingredients. " + durSplit[1].trim() + " ignored. Using " + durSplit[2].trim() + " for amount.");
                    amountString = durSplit[2].trim();
                }
            }

            if (amountString != null) {
                if (i + 1 < choicesSize) {
                    ErrorReporter.getInstance().warning("Amount is only allowed on the last ingredient of a set. Ignoring amount.");
                } else {
                    try {
                        amount = Integer.parseInt(amountString);
                    } catch (NumberFormatException e) {
                        if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                            ErrorReporter.getInstance().warning("Item '" + split[i] + "' has amount value that is not a number: " + amountString + ", defaulting to 1");
                        }
                    }
                }
            }
        }

        return Collections.singletonMap(choices, amount);
    }

    public static ItemStack parseItem(String value, int defaultData) {
        return parseItem(value, defaultData, 0);
    }

    public static ItemStack parseItem(String value, int defaultData, int settings) {
        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String[] args = value.split(";");
        String[] split = args[0].trim().split(":");

        if (split.length <= 0 || split[0].isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        value = split[0].trim();

        Material material = Settings.getInstance().getMaterial(value);

        if (material == null) {
            material = Material.matchMaterial(value);
        }

        if (material == null) {
            if ((settings & ParseBit.NO_ERRORS) != ParseBit.NO_ERRORS) {
                ErrorReporter.getInstance().error("Item '" + value + "' does not exist!", "Name could be different, look in '" + Files.FILE_INFO_NAMES + "' or '" + Files.FILE_ITEM_ALIASES + "' for material names.");
            }

            return null;
        }

        int data = defaultData;

        if (split.length > 1) {
            if ((settings & ParseBit.NO_DATA) != ParseBit.NO_DATA) {
                value = split[1].toLowerCase().trim();

                if (value.charAt(0) == '*' || value.equals("any")) {
                    data = RMCVanilla.DATA_WILDCARD;
                } else {
                    Map<String, Short> dataMap = Settings.getInstance().getMaterialDataNames(material);
                    Short dataValue;
                    if (dataMap == null) {
                        dataValue = null;
                    } else {
                        dataValue = dataMap.get(RMCUtil.parseAliasName(value));
                    }

                    if (dataValue == null) {
                        try {
                            data = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                                ErrorReporter.getInstance().warning("Item '" + material + " has unknown data number/alias: '" + value + "', defaulting to " + defaultData);
                            }
                        }
                    } else {
                        data = dataValue;
                    }

                    if (data == -1) {
                        if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                            ErrorReporter.getInstance().warning("Item '" + material + "' has data value -1, use * instead!", "The -1 value no longer works since Minecraft 1.5, for future compatibility use * instead or don't define a data value.");
                        }
                    }
                }
            } else {
                if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                    ErrorReporter.getInstance().warning("Item '" + material + "' can't have data value defined here, data value ignored.");
                }
            }
        }

        int amount = 1;

        if (split.length > 2) {
            if ((settings & ParseBit.NO_AMOUNT) != ParseBit.NO_AMOUNT) {
                value = split[2].trim();

                try {
                    amount = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                        ErrorReporter.getInstance().warning("Item '" + material + "' has amount value that is not a number: " + value + ", defaulting to 1");
                    }
                }
            } else {
                if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                    ErrorReporter.getInstance().warning("Item '" + material + "' can't have amount defined here, amount ignored.");
                }
            }
        }

        ItemStack item;

        if (Version.has1_13BasicSupport() && data == RMCVanilla.DATA_WILDCARD) {
            item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(data);
                item.setItemMeta(meta);
            }
        } else {
            item = new ItemStack(material, amount, (short) data);
        }

        if (args.length > 1) {
            ItemMeta meta = item.getItemMeta();

            if (meta == null && (settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                ErrorReporter.getInstance().warning("The " + material.toString() + " material doesn't support item meta, name/lore/enchants ignored.");
                return item;
            }

            String original;

            int argsLength = args.length;
            for (int i = 1; i < argsLength; i++) {
                original = args[i].trim();
                value = original.toLowerCase();

                if (value.startsWith("name")) {
                    value = original.substring("name".length()).trim();

                    meta.setDisplayName(RMCUtil.parseColors(value, false));
                } else if (value.startsWith("lore")) {
                    value = original.substring("lore".length()).trim();

                    List<String> lore = meta.getLore();

                    if (lore == null) {
                        lore = new ArrayList<>();
                    }

                    lore.add(RMCUtil.parseColors(value, false));
                    meta.setLore(lore);
                } else if (value.startsWith("enchant")) {
                    split = value.substring("enchant".length()).trim().split(" ");
                    value = split[0].trim();

                    Enchantment enchant = Tools.parseEnchant(value);

                    if (enchant == null) {
                        if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                            ErrorReporter.getInstance().error("Invalid enchantment: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for enchantment names.");
                        }
                        continue;
                    }

                    int level = enchant.getStartLevel();

                    if (split.length > 1) {
                        value = split[1].trim();

                        if (value.equals("max")) {
                            level = enchant.getMaxLevel();
                        } else {
                            try {
                                level = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                if ((settings & ParseBit.NO_WARNINGS) != ParseBit.NO_WARNINGS) {
                                    ErrorReporter.getInstance().error("Invalid enchantment level number: " + value);
                                    continue;
                                }
                            }
                        }
                    }

                    item.addUnsafeEnchantment(enchant, level);
                }
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static Potion parsePotion18(String value, String type) {
        String[] split = value.toLowerCase().split("\\|");

        if (split.length == 0) {
            ErrorReporter.getInstance().error("Flag " + type + " doesn't have any arguments!", "It must have at least 'type' argument, read '" + Files.FILE_INFO_NAMES + "' for potion types list.");
            return null;
        }

        Potion potion = new Potion(null);
        boolean splash = false;
        boolean extended = false;
        int level = 1;

        for (String s : split) {
            s = s.trim();

            if (s.equals("splash")) {
                splash = true;
            } else if (s.equals("extended")) {
                extended = true;
            } else if (s.startsWith("type")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'type' argument with no type!", "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                    return null;
                }

                value = split[1].trim();

                try {
                    potion.setType(PotionType.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    ErrorReporter.getInstance().error("Flag " + type + " has invalid 'type' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                    return null;
                }
            } else if (s.startsWith("level")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'level' argument with no level!");
                    continue;
                }

                value = split[1].trim();

                if (value.equals("max")) {
                    level = 9999;
                } else {
                    try {
                        level = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().error("Flag " + type + " has invalid 'level' number: " + value);
                    }
                }
            } else {
                ErrorReporter.getInstance().error("Flag " + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in '" + Files.FILE_INFO_FLAGS + "' file.");
            }
        }

        if (potion.getType() == null) {
            ErrorReporter.getInstance().error("Flag " + type + " is missing 'type' argument!", "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
            return null;
        }

        if (potion.getType().getMaxLevel() > 0) {
            potion.setLevel(Math.min(Math.max(level, 1), potion.getType().getMaxLevel()));
        }

        if (!potion.getType().isInstant()) {
            potion.setHasExtendedDuration(extended);
        }

        potion.setSplash(splash);

        return potion;
    }

    public static ItemStack parsePotion19(String value, String type) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        String[] split = value.toLowerCase().split("\\|");

        if (split.length == 0) {
            ErrorReporter.getInstance().error("Flag " + type + " doesn't have any arguments!", "It must have at least 'type' argument, read '" + Files.FILE_INFO_NAMES + "' for potion types list.");
            return null;
        }

        boolean extended = false;
        int level = 1;
        PotionType potionType = null;
        boolean needsPotionType = false;

        for (String s : split) {
            s = s.trim();

            if (s.equals("splash")) {
                potion.setType(Material.SPLASH_POTION);
            } else if (s.equals("lingering")) {
                potion.setType(Material.LINGERING_POTION);
            } else if (s.equals("extended")) {
                needsPotionType = true;
                extended = true;
            } else if (s.startsWith("type")) {
                needsPotionType = true;
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'type' argument with no type!", "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                    return null;
                }

                value = split[1].trim();

                try {
                    potionType = PotionType.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    ErrorReporter.getInstance().error("Flag " + type + " has invalid 'type' argument value: " + value, "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                    return null;
                }
            } else if (s.startsWith("level")) {
                needsPotionType = true;
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'level' argument with no level!");
                    continue;
                }

                value = split[1].trim();

                if (value.equals("max")) {
                    level = 9999;
                } else {
                    try {
                        level = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().error("Flag " + type + " has invalid 'level' number: " + value);
                    }
                }
            } else if (s.startsWith("color")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'color' argument with no colors!");
                    continue;
                }

                Color color = Tools.parseColor(split[1]);
                if (color == null) {
                    ErrorReporter.getInstance().error("Flag " + type + " has invalid color numbers!", "Use 3 numbers ranging from 0 to 255, e.g. 255 128 0 for orange.");
                } else {
                    potionMeta.setColor(color);
                }
            } else {
                ErrorReporter.getInstance().error("Flag " + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in '" + Files.FILE_INFO_FLAGS + "' file.");
            }
        }

        if (needsPotionType) {
            if (potionType == null) {
                ErrorReporter.getInstance().error("Flag " + type + " is missing 'type' argument!", "Read '" + Files.FILE_INFO_NAMES + "' for potion types.");
                return null;
            }

            boolean upgraded = false;
            if (potionType.getMaxLevel() > 0) {
                int newLevel = Math.min(Math.max(level, 1), potionType.getMaxLevel());
                if (newLevel == 2) {
                    upgraded = true;
                }
            }
            PotionData potionData = new PotionData(potionType, extended, upgraded);

            potionMeta.setBasePotionData(potionData);
        }

        potion.setItemMeta(potionMeta);

        return potion;
    }

    public static PotionEffect parsePotionEffect(String value, String flagType) {
        String[] split = value.toLowerCase().split("\\|");

        if (split.length == 0) {
            ErrorReporter.getInstance().error("Flag " + flagType + " doesn't have any arguments!", "It must have at least 'type' argument, read '" + Files.FILE_INFO_NAMES + "' for potion effect types list.");
            return null;
        }

        String firstArg = split[0].trim();
        PotionEffectType effectType = PotionEffectType.getByName(firstArg);
        if (effectType == null) {
            ErrorReporter.getInstance().error("Flag " + flagType + " has invalid effect type: " + firstArg, "Read '" + Files.FILE_INFO_NAMES + "' for potion effect types.");
            return null;
        }

        int amplifier = 0;
        float duration = 1;
        boolean ambient = true;
        boolean particles = true;
        boolean icon = true;


        for (int i = 1; i < split.length; i++) {
            String s = split[i].trim();

            if (s.startsWith("ambient")) {
                s = s.substring("ambient".length()).trim();

                if (s.equals("false")) {
                    ambient = false;
                } else if (s.equals("true") || s.isEmpty()) {
                    ambient = true;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has invalid value for ambient: " + s + ". Defaulting to true.");
                    ambient = true;
                }
            } else if (s.startsWith("particles")) {
                s = s.substring("particles".length()).trim();

                if (s.equals("false")) {
                    particles = false;
                } else if (s.equals("true") || s.isEmpty()) {
                    particles = true;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has invalid value for particles: " + s + ". Defaulting to true.");
                    particles = true;
                }
            } else if (s.startsWith("icon")) {
                s = s.substring("icon".length()).trim();

                if (value.equals("false")) {
                    icon = false;
                } else if (s.equals("true") || s.isEmpty()) {
                    icon = true;
                } else {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has invalid value for icon: " + s + ". Defaulting to true.");
                    icon = true;
                }
            } else if (s.equals("!ambient")) {
                ambient = false;
            } else if (s.equals("!particles")) {
                particles = false;
            } else if (s.equals("!icon")) {
                icon = false;
            } else if (s.startsWith("duration")) {
                if (effectType.isInstant()) {
                    ErrorReporter.getInstance().warning("Flag " + flagType + " has effect type '" + effectType.toString() + "' which is instant, it can't have duration, ignored.");
                    continue;
                }

                split = s.split(" ", 2);


                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + flagType + " has 'duration' argument with no number!");
                    continue;
                }

                value = split[1].trim();

                try {
                    duration = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Flag " + flagType + " has invalid 'duration' number: " + value);
                }
            } else if (s.startsWith("amplifier")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + flagType + " has 'amplify' argument with no number!");
                    continue;
                }

                value = split[1].trim();

                try {
                    amplifier = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Flag " + flagType + " has invalid 'amplifier' number: " + value);
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + flagType + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in '" + Files.FILE_INFO_FLAGS + "' file.");
            }
        }

        if (duration != 1 && effectType.isInstant()) {
            ErrorReporter.getInstance().warning("Flag " + flagType + " can't have duration on instant effect: " + effectType.toString());
        }

        PotionEffect effect;
        if (Version.has1_13BasicSupport()) {
            effect = new PotionEffect(effectType, (int) Math.ceil(duration * 20.0), amplifier, ambient, particles, icon);
        } else {
            effect = new PotionEffect(effectType, (int) Math.ceil(duration * 20.0), amplifier, ambient, particles);
        }

        return effect;
    }

    public static FireworkEffect parseFireworkEffect(String value, String type) {
        String[] split = value.toLowerCase().split("\\|");

        if (split.length == 0) {
            ErrorReporter.getInstance().error("Flag " + type + " doesn't have any arguments!", "It must have at least one 'color' argument, read '" + Files.FILE_INFO_FLAGS + "' for syntax.");
            return null;
        }

        Builder build = FireworkEffect.builder();

        for (String s : split) {
            s = s.trim();

            if (s.equals("trail")) {
                build.withTrail();
            } else if (s.equals("flicker")) {
                build.withFlicker();
            } else if (s.startsWith("color")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'color' argument with no colors!", "Add colors separated by , in RGB format (3 numbers ranged 0-255)");
                    return null;
                }

                split = split[1].split(",");
                List<Color> colors = new ArrayList<>();
                Color color;

                for (String c : split) {
                    color = Tools.parseColor(c.trim());

                    if (color == null) {
                        ErrorReporter.getInstance().warning("Flag " + type + " has an invalid color!");
                    } else {
                        colors.add(color);
                    }
                }

                if (colors.isEmpty()) {
                    ErrorReporter.getInstance().error("Flag " + type + " doesn't have any valid colors, they are required!");
                    return null;
                }

                build.withColor(colors);
            } else if (s.startsWith("fadecolor")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'fadecolor' argument with no colors!", "Add colors separated by , in RGB format (3 numbers ranged 0-255)");
                    return null;
                }

                split = split[1].split(",");
                List<Color> colors = new ArrayList<>();
                Color color;

                for (String c : split) {
                    color = Tools.parseColor(c.trim());

                    if (color == null) {
                        ErrorReporter.getInstance().warning("Flag " + type + " has an invalid fade color! Moving on...");
                    } else {
                        colors.add(color);
                    }
                }

                if (colors.isEmpty()) {
                    ErrorReporter.getInstance().error("Flag " + type + " doesn't have any valid fade colors! Moving on...");
                } else {
                    build.withFade(colors);
                }
            } else if (s.startsWith("type")) {
                split = s.split(" ", 2);

                if (split.length <= 1) {
                    ErrorReporter.getInstance().error("Flag " + type + " has 'type' argument with no value!", "Read " + Files.FILE_INFO_NAMES + " for list of firework effect types.");
                    return null;
                }

                value = split[1].trim();

                try {
                    build.with(FireworkEffect.Type.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    ErrorReporter.getInstance().error("Flag " + type + " has invalid 'type' setting value: " + value, "Read " + Files.FILE_INFO_NAMES + " for list of firework effect types.");
                    return null;
                }
            } else {
                ErrorReporter.getInstance().warning("Flag " + type + " has unknown argument: " + s, "Maybe it's spelled wrong, check it in " + Files.FILE_INFO_FLAGS + " file.");
            }
        }

        return build.build();
    }

    public static Color parseColor(String rgbString) {
        String[] split = rgbString.split(" ");

        if (split.length == 3) {
            try {
                int r = Integer.parseInt(split[0].trim());
                int g = Integer.parseInt(split[1].trim());
                int b = Integer.parseInt(split[2].trim());

                return Color.fromRGB(r, g, b);
            } catch (Throwable e) {
                // TODO: Handle error
            }
        }

        return null;
    }

    /**
     * For use in furnace smelting and fuel recipes HashMap
     */
    public static String convertItemToStringId(ItemStack item) {
        String stringId = "" + item.getType().toString();

        if (item.getDurability() != RMCVanilla.DATA_WILDCARD) {
            stringId += ":" + item.getDurability();
        }

        return stringId;
    }

    /**
     * For use in shaped/shapeless recipe's result
     */
    public static ItemStack createItemRecipeId(ItemStack result, int id) {
        result = result.clone();
        ItemMeta meta = result.getItemMeta();

        if (meta == null) {
            MessageSender.getInstance().error(null, new IllegalAccessError(), "Can't mark result because it doesn't support item meta!");
            return result;
        }

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(Recipes.RECIPE_ID_STRING + id);
        meta.setLore(lore);
        result.setItemMeta(meta);

        return result;
    }

    public static String getRecipeIdFromItem(ItemStack result) {
        if (!result.hasItemMeta()) {
            return "-1";
        }

        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            return "-1";
        }

        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) {
            return "-1";
        }

        for (String s : lore) {
            if (s != null && s.startsWith(Recipes.RECIPE_ID_STRING)) {
                try {
                    return s.substring(Recipes.RECIPE_ID_STRING.length());
                } catch (Throwable e) {
                    MessageSender.getInstance().debug("Invalid recipe identifier found: " + s);
                    break;
                }
            }
        }

        return "-1";
    }

    public static ItemStack[] mirrorItemMatrix(ItemStack[] matrix) {
        ItemStack[] m = new ItemStack[9];

        for (int r = 0; r < 3; r++) {
            m[(r * 3)] = matrix[(r * 3) + 2];
            m[(r * 3) + 1] = matrix[(r * 3) + 1];
            m[(r * 3) + 2] = matrix[(r * 3)];
        }

        RMBukkitTools.trimItemMatrix(m);

        return m;
    }

    public static boolean isDifferentMatrix(ItemStack[] original, ItemStack[] current) {
        boolean different = false;

        if (original.length == current.length) {
            for (int i = 0; i < original.length; i++) {
                ItemStack originalStack = original[i];
                ItemStack currentStack = current[i];

                if (originalStack != null) {
                    if (currentStack == null && originalStack.getType() == Material.AIR) {
                        // Null == AIR
                    } else if (currentStack == null || currentStack.getType() != originalStack.getType()) {
                        different = true;
                        break;
                    }
                }
            }
        }

        return different;
    }

    public static boolean saveTextToFile(String text, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            BufferedWriter stream = new BufferedWriter(new FileWriter(file, false));
            stream.write(text);
            stream.close();
            return true;
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }

        return false;
    }

    public static Sound getSound(String newSound) {
        Sound sound = null;

        if (Version.has1_13BasicSupport()) {
            // set known sounds to make sure Enum isn't changing on us
            switch (newSound) {
                case "BLOCK_NOTE_BASS":
                    sound = Sound.BLOCK_NOTE_BLOCK_BASS;
                    break;
                case "BLOCK_NOTE_PLING":
                    sound = Sound.BLOCK_NOTE_BLOCK_PLING;
                    break;
                default:
                    sound = Sound.valueOf(newSound);
                    break;
            }
        } else if (Version.has1_9Support()) {
            // set known sounds to make sure Enum isn't changing on us
            switch (newSound) {
                case "BLOCK_ANVIL_USE":
                    sound = Sound.BLOCK_ANVIL_USE;
                    break;
                case "ENTITY_ITEM_BREAK":
                    sound = Sound.ENTITY_ITEM_BREAK;
                    break;
                default:
                    sound = Sound.valueOf(newSound);
                    break;
            }
        } else {
            ArrayList<String> oldSounds = new ArrayList<>();
            switch (newSound) {
                case "BLOCK_NOTE_BASS":
                    oldSounds.add("NOTE_BASS");
                    break;
                case "BLOCK_NOTE_PLING":
                    oldSounds.add("NOTE_PLING");
                    break;
                case "BLOCK_ANVIL_USE":
                    oldSounds.add("ANVIL_USE");
                    break;
                case "ENTITY_ITEM_BREAK":
                    oldSounds.add("ITEM_BREAK");
                    break;
                default:
                    break;
            }

            for (String oldSound : oldSounds) {
                try {
                    sound = Sound.valueOf(oldSound);
                } catch (IllegalArgumentException e2) {
                    // Sound is missing
                }
            }
        }

        return sound;
    }
}
