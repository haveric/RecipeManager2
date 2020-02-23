package haveric.recipeManager;

import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.*;

import static org.bukkit.Tag.REGISTRY_BLOCKS;
import static org.bukkit.Tag.REGISTRY_ITEMS;

/**
 * RecipeManager's settings loaded from its config.yml, values are read-only.
 */
public class Settings {
    private static boolean hasBeenInited = false;

    private static final boolean SPECIAL_RECIPE_DEFAULT = true;
    private static final boolean SPECIAL_REPAIR_METADATA_DEFAULT = false;
    private static final String SPECIAL_ANVIL_CUSTOM_DEFAULT = "false";
    private static final String SPECIAL_GRINDSTONE_CUSTOM_DEFAULT = "false";

    private static final boolean SOUNDS_REPAIR_DEFAULT = true;
    private static final boolean SOUNDS_FAILED_DEFAULT = true;
    private static final boolean SOUNDS_FAILED_CLICK_DEFAULT = true;

    private static final boolean FIX_MOD_RESULTS_DEFAULT = false;
    private static final boolean UPDATE_BOOKS_DEFAULT = true;
    private static final boolean COLOR_CONSOLE_DEFAULT = true;

    private static final String FURNACE_SHIFT_CLICK_DEFAULT = "f";

    private static final boolean MULTITHREADING_DEFAULT = true;

    private static final boolean CLEAR_RECIPES_DEFAULT = false;

    private static final boolean UPDATE_CHECK_ENABLED_DEFAULT = true;
    private static final int UPDATE_CHECK_FREQUENCY_DEFAULT = 6;

    private static Material MATERIAL_FAIL_DEFAULT;
    private static final Material MATERIAL_SECRET_DEFAULT = Material.CHEST;
    private static final Material MATERIAL_MULTIPLE_RESULTS_DEFAULT = Material.CHEST;

    private static final boolean DISABLE_OVERRIDE_WARNINGS_DEFAULT = false;

    private static List<String> RECIPE_COMMENT_CHARACTERS_DEFAULT;

    private static FileConfiguration fileConfig;
    private static Settings instance;

    private static Map<Material, Short> itemDatas;

    private static Map<String, List<Material>> choicesAliases;
    private static Map<String, Material> materialNames;
    private static Map<Material, Map<String, Short>> materialDataNames;
    private static Map<String, Enchantment> enchantNames;

    private static Map<Material, String> materialPrint;
    private static Map<Material, Map<Short, String>> materialDataPrint;
    private static Map<Enchantment, String> enchantPrint;

    private static List<Material> anvilCombineItem = new ArrayList<>();
    private static List<Material> anvilMaterialEnchant = new ArrayList<>();
    private static List<Material> anvilRepairMaterial = new ArrayList<>();
    private static List<Material> anvilRenaming = new ArrayList<>();
    private static Map<Enchantment, List<Integer>> anvilEnchantments = new HashMap<>();

    private static List<Material> grindstoneCombineItem = new ArrayList<>();
    private static List<Material> grindstoneItemMaterials = new ArrayList<>();
    private static Map<Enchantment, List<Integer>> grindstoneBookEnchantments = new HashMap<>();
    private static Map<Enchantment, List<Integer>> grindstoneItemEnchantments = new HashMap<>();

    protected Settings() {
       // Exists only to defeat instantiation.
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();

            init();
        }

        return instance;
    }

    public static void clean() {
        instance = null;
    }

    public static void clearInit() {
        hasBeenInited = false;
    }

    private static void init() {
        if (!hasBeenInited) {
            RECIPE_COMMENT_CHARACTERS_DEFAULT = new ArrayList<>();
            RECIPE_COMMENT_CHARACTERS_DEFAULT.add("//");
            RECIPE_COMMENT_CHARACTERS_DEFAULT.add("#");

            MATERIAL_FAIL_DEFAULT = Material.BARRIER;

            itemDatas = new EnumMap<>(Material.class);
            choicesAliases = new HashMap<>();
            materialNames = new HashMap<>();
            materialDataNames = new EnumMap<>(Material.class);
            enchantNames = new HashMap<>();
            materialPrint = new EnumMap<>(Material.class);
            materialDataPrint = new EnumMap<>(Material.class);
            enchantPrint = new HashMap<>();

            // Load/reload/generate config.yml
            fileConfig = loadYML(Files.FILE_CONFIG);

            hasBeenInited = true;
        }
    }

    public void reload(CommandSender sender) {
        init();

        MessageSender.init(getColorConsole());

        String lastChanged = fileConfig.getString("lastchanged");

        if (!Files.LASTCHANGED_CONFIG.equals(lastChanged)) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_CONFIG + "' file is outdated, please delete it to allow it to be generated again.");
        }

        MessageSender.getInstance().log("config.yml settings:");
        MessageSender.getInstance().log("    special-recipes.anvil.enchant.enabled: " + getSpecialAnvilEnchant());
        MessageSender.getInstance().log("    special-recipes.anvil.combine-item.enabled: " + getSpecialAnvilCombineItem());
        MessageSender.getInstance().log("    special-recipes.anvil.repair-material.enabled: " + getSpecialAnvilRepairMaterial());
        MessageSender.getInstance().log("    special-recipes.anvil.renaming.enabled: " + getSpecialAnvilRenaming());
        MessageSender.getInstance().log("    special-recipes.banner: " + getSpecialBanner());
        MessageSender.getInstance().log("    special-recipes.banner-duplicate: " + getSpecialBannerDuplicate());
        MessageSender.getInstance().log("    special-recipes.book-cloning: " + getSpecialBookCloning());
        MessageSender.getInstance().log("    special-recipes.cartography.clone: " + getSpecialCartographyClone());
        MessageSender.getInstance().log("    special-recipes.cartography.extend: " + getSpecialCartographyExtend());
        MessageSender.getInstance().log("    special-recipes.cartography.lock: " + getSpecialCartographyLock());
        MessageSender.getInstance().log("    special-recipes.fireworks: " + getSpecialFireworks());
        MessageSender.getInstance().log("    special-recipes.firework-star: " + getSpecialFireworkStar());
        MessageSender.getInstance().log("    special-recipes.firework-star-fade: " + getSpecialFireworkStarFade());
        MessageSender.getInstance().log("    special-recipes.grindstone.combine-item.enabled: " + getSpecialGrindstoneCombineItem());
        MessageSender.getInstance().log("    special-recipes.grindstone.disenchant.book.enabled: " + getSpecialGrindstoneDisenchantBook());
        MessageSender.getInstance().log("    special-recipes.grindstone.disenchant.item.enabled: " + getSpecialGrindstoneDisenchantItem());
        MessageSender.getInstance().log("    special-recipes.leather-armor-dye: " + getSpecialLeatherDye());
        MessageSender.getInstance().log("    special-recipes.map-cloning: " + getSpecialMapCloning());
        MessageSender.getInstance().log("    special-recipes.map-extending: " + getSpecialMapExtending());
        MessageSender.getInstance().log("    special-recipes.repair: " + getSpecialRepair());
        MessageSender.getInstance().log("    special-recipes.repair-metadata: " + getSpecialRepairMetadata());
        MessageSender.getInstance().log("    special-recipes.shield-banner: " + getSpecialShieldBanner());
        MessageSender.getInstance().log("    special-recipes.shulker-dye: " + getSpecialShulkerDye());
        MessageSender.getInstance().log("    special-recipes.suspicious-stew: " + getSpecialSuspiciousStew());
        MessageSender.getInstance().log("    special-recipes.tipped-arrows: " + getSpecialTippedArrows());
        MessageSender.getInstance().log("    sounds.failed: " + getSoundsFailed());
        MessageSender.getInstance().log("    sounds.failed_click: " + getSoundsFailedClick());
        MessageSender.getInstance().log("    sounds.repair: " + getSoundsRepair());
        MessageSender.getInstance().log("    update-books: " + getUpdateBooks());
        MessageSender.getInstance().log("    color-console: " + getColorConsole());
        MessageSender.getInstance().log("    furnace-shift-click: " + getFurnaceShiftClick());
        MessageSender.getInstance().log("    multithreading: " + getMultithreading());
        MessageSender.getInstance().log("    fix-mod-results: " + getFixModResults());
        MessageSender.getInstance().log("    clear-recipes: " + getClearRecipes());
        MessageSender.getInstance().log("    update-check.enabled: " + getUpdateCheckEnabled());
        MessageSender.getInstance().log("    update-check.frequency: " + getUpdateCheckFrequency());
        MessageSender.getInstance().log("    material.fail: " + getFailMaterial());
        MessageSender.getInstance().log("    material.secret: " + getSecretMaterial());
        MessageSender.getInstance().log("    material.multiple-results: " + getMultipleResultsMaterial());
        MessageSender.getInstance().log("    disable-override-warnings: " + getDisableOverrideWarnings());
        MessageSender.getInstance().log("    recipe-comment-characters: " + getRecipeCommentCharacters());


        FileConfiguration itemAliasesConfig = loadYML(Files.FILE_ITEM_ALIASES);

        if (!Files.LASTCHANGED_ITEM_ALIASES.equals(itemAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ITEM_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : itemAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = Material.matchMaterial(arg);

            if (material == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid material definition: " + arg);
                continue;
            }

            Object value = itemAliasesConfig.get(arg);

            if (value instanceof String) {
                parseMaterialNames(sender, (String) value, material);
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) value;

                for (String key : section.getKeys(false)) {
                    if (key.equals("names")) {
                        parseMaterialNames(sender, section.getString(key), material);
                    } else {
                        try {
                            parseMaterialDataNames(sender, section.getString(key), Short.parseShort(key), material);
                        } catch (NumberFormatException e) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data value number: " + key + " for material: " + material);
                        }
                    }
                }
            } else {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data type at: " + arg);
            }
        }

        FileConfiguration choiceAliasesConfig = loadYML(Files.FILE_CHOICE_ALIASES);

        if (!Files.LASTCHANGED_CHOICE_ALIASES.equals(choiceAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_CHOICE_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : choiceAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            String aliases = choiceAliasesConfig.getString(arg);
            if (aliases != null) {
                List<Material> materials = new ArrayList<>();
                String[] materialSplit = aliases.split(",");
                for (String materialString : materialSplit) {
                    materialString = materialString.trim();

                    if (materialString.startsWith("tag:") || materialString.startsWith("t:")) {
                        String tagString = materialString.substring(materialString.indexOf(':') + 1);

                        String[] tagSplit = tagString.split(":");
                        String namespace;
                        String material;
                        if (tagSplit.length > 1) {
                            namespace = tagSplit[0].trim();
                            material = tagSplit[1].trim();
                        } else {
                            namespace = NamespacedKey.MINECRAFT;
                            material = tagSplit[0].trim();
                        }

                        NamespacedKey key = new NamespacedKey(namespace, material); // If this deprecated constructor goes away, Loop through Bukkit.getPluginManager().getPlugins() to check any potential namespace?
                        Tag<Material> tag = Bukkit.getTag(REGISTRY_BLOCKS, key, Material.class);

                        if (tag == null || tag.getValues().isEmpty()) {
                            tag = Bukkit.getTag(REGISTRY_ITEMS, key, Material.class);
                        }

                        if (tag == null || tag.getValues().isEmpty()) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_CHOICE_ALIASES + "' has invalid tag definition: " + arg);
                        } else {
                            materials.addAll(tag.getValues());
                        }
                    } else if (materialString.startsWith("alias:") || materialString.startsWith("a:")) {
                        String aliasString = materialString.substring(materialString.indexOf(':') + 1);

                        List<Material> choiceMaterials = getChoicesAlias(aliasString);
                        if (choiceMaterials == null) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_CHOICE_ALIASES + "' has invalid choice alias definition: " + arg);
                        } else {
                            materials.addAll(choiceMaterials);
                        }
                    } else {
                        Material material = getMaterial(materialString);

                        if (material == null) {
                            material = Material.matchMaterial(materialString);
                        }

                        if (material == null) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_CHOICE_ALIASES + "' has invalid material (or item alias) definition: " + arg);
                        } else {
                            materials.add(material);
                        }
                    }
                }

                if (!materials.isEmpty()) {
                    choicesAliases.put(arg.toLowerCase(), materials);
                }
            }
        }

        FileConfiguration itemDatasConfig = loadYML(Files.FILE_ITEM_DATAS);

        if (!Files.LASTCHANGED_ITEM_DATAS.equals(itemDatasConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ITEM_DATAS + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : itemDatasConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = getMaterial(arg);

            if (material == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_DATAS + "' has invalid material definition: " + arg);
                continue;
            }

            String value = itemDatasConfig.getString(arg);
            try {
                itemDatas.put(material, Short.parseShort(value));
            } catch (NumberFormatException e) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_DATAS + "' has invalid data value number: " + value + " for material: " + material);
            }
        }

        FileConfiguration enchantAliasesConfig = loadYML(Files.FILE_ENCHANT_ALIASES);

        if (!Files.LASTCHANGED_ENCHANT_ALIASES.equals(enchantAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : enchantAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Enchantment enchant = Enchantment.getByName(arg.toUpperCase());

            if (enchant == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has invalid enchant definition: " + arg);
                continue;
            }

            String names = enchantAliasesConfig.getString(arg);
            String[] split = names.split(",");

            for (String str : split) {
                str = str.trim();
                String parsed = RMCUtil.parseAliasName(str);

                if (enchantNames.containsKey(parsed)) {
                    MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has duplicate enchant alias '" + str + "' for enchant " + enchant);
                    continue;
                }

                enchantNames.put(parsed, enchant);

                if (!enchantPrint.containsKey(enchant)) {
                    enchantPrint.put(enchant, Tools.parseAliasPrint(str));
                }
            }
        }


        anvilCombineItem.clear();
        String combineItemMaterials = fileConfig.getString("special-recipes.anvil.combine-item.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (combineItemMaterials != null && !combineItemMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(combineItemMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilCombineItem.addAll(materials);
            }
        }

        grindstoneCombineItem.clear();
        String grindstoneCombineItemMaterials = fileConfig.getString("special-recipes.grindstone.combine-item.materials", SPECIAL_GRINDSTONE_CUSTOM_DEFAULT);
        if (grindstoneCombineItemMaterials != null && !grindstoneCombineItemMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(grindstoneCombineItemMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                grindstoneCombineItem.addAll(materials);
            }
        }

        grindstoneItemMaterials.clear();
        String grindstoneDisenchantItemMaterials = fileConfig.getString("special-recipes.grindstone.disenchant.item.materials", SPECIAL_GRINDSTONE_CUSTOM_DEFAULT);
        if (grindstoneDisenchantItemMaterials != null && !grindstoneDisenchantItemMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(grindstoneDisenchantItemMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                grindstoneItemMaterials.addAll(materials);
            }
        }

        anvilMaterialEnchant.clear();
        String enchantMaterials = fileConfig.getString("special-recipes.anvil.enchant.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (enchantMaterials != null && !enchantMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(enchantMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilMaterialEnchant.addAll(materials);
            }
        }

        anvilEnchantments.clear();
        String enchantEnchantments = fileConfig.getString("special-recipes.anvil.enchant.enchantments", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (enchantEnchantments != null && !enchantEnchantments.equals("false")) {
            String[] enchantments = enchantEnchantments.split(",");
            for (String enchantString : enchantments) {
                String[] levelsSplit = enchantString.split(":");
                Enchantment enchant = getEnchantment(RMCUtil.parseAliasName(levelsSplit[0]));

                if (enchant != null) {
                    if (levelsSplit.length > 1) {
                        String levelsString = levelsSplit[1];
                        String[] split = levelsString.split("-");
                        int minLevel = 1;
                        try {
                            minLevel = Integer.parseInt(split[0].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Invalid special recipe anvil enchantment level set: " + split[0] + " for " + levelsSplit[0]);
                        }

                        int maxLevel = minLevel;
                        if (split.length > 1) {
                            try {
                                maxLevel = Integer.parseInt(split[1].trim());
                            } catch (NumberFormatException e) {
                                ErrorReporter.getInstance().warning("Invalid special recipe anvil enchantment max level set: " + split[1] + " for " + levelsSplit[0]);
                            }
                        }

                        List<Integer> levels = new ArrayList<>();
                        for (int i = minLevel; i <= maxLevel; i++) {
                            levels.add(i);
                        }

                        anvilEnchantments.put(enchant, levels);
                    } else {
                        List<Integer> levels = new ArrayList<>();
                        for (int i = enchant.getStartLevel(); i <= enchant.getMaxLevel(); i++) {
                            levels.add(i);
                        }

                        anvilEnchantments.put(enchant, levels);
                    }
                }
            }
        }

        grindstoneBookEnchantments.clear();
        String grindstoneDisenchantBookEnchantments = fileConfig.getString("special-recipes.grindstone.disenchant.book.enchantments", SPECIAL_GRINDSTONE_CUSTOM_DEFAULT);
        if (grindstoneDisenchantBookEnchantments != null && !grindstoneDisenchantBookEnchantments.equals("false")) {
            String[] enchantments = grindstoneDisenchantBookEnchantments.split(",");
            for (String enchantString : enchantments) {
                String[] levelsSplit = enchantString.split(":");
                Enchantment enchant = getEnchantment(RMCUtil.parseAliasName(levelsSplit[0]));

                if (enchant != null) {
                    if (levelsSplit.length > 1) {
                        String levelsString = levelsSplit[1];
                        String[] split = levelsString.split("-");
                        int minLevel = 1;
                        try {
                            minLevel = Integer.parseInt(split[0].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Invalid special recipe grindstone disenchant book enchantment level set: " + split[0] + " for " + levelsSplit[0]);
                        }

                        int maxLevel = minLevel;
                        if (split.length > 1) {
                            try {
                                maxLevel = Integer.parseInt(split[1].trim());
                            } catch (NumberFormatException e) {
                                ErrorReporter.getInstance().warning("Invalid special recipe grindstone disenchant book enchantment max level set: " + split[1] + " for " + levelsSplit[0]);
                            }
                        }

                        List<Integer> levels = new ArrayList<>();
                        for (int i = minLevel; i <= maxLevel; i++) {
                            levels.add(i);
                        }

                        grindstoneBookEnchantments.put(enchant, levels);
                    } else {
                        List<Integer> levels = new ArrayList<>();
                        for (int i = enchant.getStartLevel(); i <= enchant.getMaxLevel(); i++) {
                            levels.add(i);
                        }

                        grindstoneBookEnchantments.put(enchant, levels);
                    }
                }
            }
        }

        grindstoneItemEnchantments.clear();
        String grindstoneDisenchantItemEnchantments = fileConfig.getString("special-recipes.grindstone.disenchant.item.enchantments", SPECIAL_GRINDSTONE_CUSTOM_DEFAULT);
        if (grindstoneDisenchantItemEnchantments != null && !grindstoneDisenchantItemEnchantments.equals("false")) {
            String[] enchantments = grindstoneDisenchantItemEnchantments.split(",");
            for (String enchantString : enchantments) {
                String[] levelsSplit = enchantString.split(":");
                Enchantment enchant = getEnchantment(RMCUtil.parseAliasName(levelsSplit[0]));

                if (enchant != null) {
                    if (levelsSplit.length > 1) {
                        String levelsString = levelsSplit[1];
                        String[] split = levelsString.split("-");
                        int minLevel = 1;
                        try {
                            minLevel = Integer.parseInt(split[0].trim());
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Invalid special recipe grindstone disenchant item enchantment level set: " + split[0] + " for " + levelsSplit[0]);
                        }

                        int maxLevel = minLevel;
                        if (split.length > 1) {
                            try {
                                maxLevel = Integer.parseInt(split[1].trim());
                            } catch (NumberFormatException e) {
                                ErrorReporter.getInstance().warning("Invalid special recipe grindstone disenchant item enchantment max level set: " + split[1] + " for " + levelsSplit[0]);
                            }
                        }

                        List<Integer> levels = new ArrayList<>();
                        for (int i = minLevel; i <= maxLevel; i++) {
                            levels.add(i);
                        }

                        grindstoneItemEnchantments.put(enchant, levels);
                    } else {
                        List<Integer> levels = new ArrayList<>();
                        for (int i = enchant.getStartLevel(); i <= enchant.getMaxLevel(); i++) {
                            levels.add(i);
                        }

                        grindstoneItemEnchantments.put(enchant, levels);
                    }
                }
            }
        }

        anvilRepairMaterial.clear();
        String repairMaterials = fileConfig.getString("special-recipes.anvil.repair-material.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (repairMaterials != null && !repairMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(repairMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilRepairMaterial.addAll(materials);
            }
        }

        anvilRenaming.clear();
        String renameMaterials = fileConfig.getString("special-recipes.anvil.renaming.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (renameMaterials != null && !renameMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(renameMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilRenaming.addAll(materials);
            }
        }

        String failString = fileConfig.getString("material.fail", MATERIAL_FAIL_DEFAULT.toString());
        if (failString != null) {
            Material failMaterial = Material.matchMaterial(failString);
            if (failMaterial == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.fail has invalid material definition: " + failString + ". Defaulting to " + MATERIAL_FAIL_DEFAULT.toString() + ".");
            }
        }

        String secretString = fileConfig.getString("material.secret", MATERIAL_SECRET_DEFAULT.toString());
        if (secretString != null) {
            Material secretMaterial = Material.matchMaterial(secretString);
            if (secretMaterial == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.secret has invalid material definition: " + secretString + ". Defaulting to " + MATERIAL_SECRET_DEFAULT.toString() + ".");
            }
        }

        String multipleResultsString = fileConfig.getString("material.multiple-results", MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());
        if (multipleResultsString != null) {
            Material multipleResultsMaterial = Material.matchMaterial(multipleResultsString);
            if (multipleResultsMaterial == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.multiple-results has invalid material definition: " + multipleResultsString + ". Defaulting to " + MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString() + ".");
            }
        }
    }

    private void parseMaterialNames(CommandSender sender, String names, Material material) {
        if (names == null) {
            return;
        }

        String[] split = names.split(",");

        for (String str : split) {
            str = str.trim();
            String parsed = RMCUtil.parseAliasName(str);

            if (materialNames.containsKey(parsed)) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate material alias '" + str + "' for material " + material);
                continue;
            }

            materialNames.put(parsed, material);

            if (!materialPrint.containsKey(material)) {
                materialPrint.put(material, Tools.parseAliasPrint(str));
            }
        }
    }

    private void parseMaterialDataNames(CommandSender sender, String names, short data, Material material) {
        if (names == null) {
            return;
        }

        String[] split = names.split(",");

        for (String str : split) {
            str = str.trim();
            Map<String, Short> dataMap = materialDataNames.computeIfAbsent(material, k -> new HashMap<>());

            String parsed = RMCUtil.parseAliasName(str);

            if (dataMap.containsKey(parsed)) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate data alias '" + str + "' for material " + material + " and data value " + data);
                continue;
            }

            dataMap.put(parsed, data);

            Map<Short, String> printMap = materialDataPrint.computeIfAbsent(material, k -> new HashMap<>());

            if (!printMap.containsKey(data)) {
                printMap.put(data, Tools.parseAliasPrint(str));
            }
        }
    }

    private static FileConfiguration loadYML(String fileName) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + fileName);

        if (!file.exists()) {
            RecipeManager.getPlugin().saveResource(fileName, false);
            MessageSender.getInstance().log("Generated and loaded '" + fileName + "' file.");
        } else {
            MessageSender.getInstance().log("Loaded '" + fileName + "' file.");
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public boolean getSpecialRepair() {
        return fileConfig.getBoolean("special-recipes.repair", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialRepairMetadata() {
        return fileConfig.getBoolean("special-recipes.repair-metadata", SPECIAL_REPAIR_METADATA_DEFAULT);
    }

    public boolean getSpecialLeatherDye() {
        return fileConfig.getBoolean("special-recipes.leather-armor-dye", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialFireworks() {
        return fileConfig.getBoolean("special-recipes.fireworks", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialFireworkStar() {
        return fileConfig.getBoolean("special-recipes.firework-star", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialFireworkStarFade() {
        return fileConfig.getBoolean("special-recipes.firework-star-fade", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialMapCloning() {
        return fileConfig.getBoolean("special-recipes.map-cloning", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialMapExtending() {
        return fileConfig.getBoolean("special-recipes.map-extending", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialBookCloning() {
        return fileConfig.getBoolean("special-recipes.book-cloning", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialAnvilCombineItem() {
        return fileConfig.getBoolean("special-recipes.anvil.combine-item.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialAnvilEnchant() {
        return fileConfig.getBoolean("special-recipes.anvil.enchant.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialAnvilRepairMaterial() {
        return fileConfig.getBoolean("special-recipes.anvil.repair-material.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialAnvilRenaming() {
        return fileConfig.getBoolean("special-recipes.anvil.renaming.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialGrindstoneCombineItem() {
        return fileConfig.getBoolean("special-recipes.grindstone.combine-item.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialGrindstoneDisenchantBook() {
        return fileConfig.getBoolean("special-recipes.grindstone.disenchant.book.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialGrindstoneDisenchantItem() {
        return fileConfig.getBoolean("special-recipes.grindstone.disenchant.item.enabled", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialCartographyClone() {
        return fileConfig.getBoolean("special-recipes.cartography.clone", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialCartographyExtend() {
        return fileConfig.getBoolean("special-recipes.cartography.extend", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialCartographyLock() {
        return fileConfig.getBoolean("special-recipes.cartography.lock", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialBanner() {
        return fileConfig.getBoolean("special-recipes.banner", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialBannerDuplicate() {
        return fileConfig.getBoolean("special-recipes.banner-duplicate", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialShieldBanner() {
        return fileConfig.getBoolean("special-recipes.shield-banner", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialTippedArrows() {
        return fileConfig.getBoolean("special-recipes.tipped-arrows", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialShulkerDye() {
        return fileConfig.getBoolean("special-recipes.shulker-dye", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSuspiciousStew() {
        return fileConfig.getBoolean("special-recipes.suspicious-stew", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSoundsRepair() {
        return fileConfig.getBoolean("sounds.repair", SOUNDS_REPAIR_DEFAULT);
    }

    public boolean getSoundsFailed() {
        return fileConfig.getBoolean("sounds.failed", SOUNDS_FAILED_DEFAULT);
    }

    public boolean getSoundsFailedClick() {
        return fileConfig.getBoolean("sounds.failed_click", SOUNDS_FAILED_CLICK_DEFAULT);
    }

    public boolean getFixModResults() {
        return fileConfig.getBoolean("fix-mod-results", FIX_MOD_RESULTS_DEFAULT);
    }

    public boolean getUpdateBooks() {
        return fileConfig.getBoolean("update-books", UPDATE_BOOKS_DEFAULT);
    }

    public boolean getColorConsole() {
        return fileConfig.getBoolean("color-console", COLOR_CONSOLE_DEFAULT);
    }

    public char getFurnaceShiftClick() {
        return fileConfig.getString("furnace-shift-click", FURNACE_SHIFT_CLICK_DEFAULT).charAt(0);
    }

    public boolean getMultithreading() {
        return fileConfig.getBoolean("multithreading", MULTITHREADING_DEFAULT);
    }

    public boolean getClearRecipes() {
        return fileConfig.getBoolean("clear-recipes", CLEAR_RECIPES_DEFAULT);
    }

    public boolean getUpdateCheckEnabled() {
        return fileConfig.getBoolean("update-check.enabled", UPDATE_CHECK_ENABLED_DEFAULT);
    }

    public int getUpdateCheckFrequency() {
        return Math.max(fileConfig.getInt("update-check.frequency", UPDATE_CHECK_FREQUENCY_DEFAULT), 0);
    }

    public Material getFailMaterial() {
        String failString = fileConfig.getString("material.fail", MATERIAL_FAIL_DEFAULT.toString());

        Material failMaterial = Material.matchMaterial(failString);

        if (failMaterial == null) {
            failMaterial = MATERIAL_FAIL_DEFAULT;
        }

        return failMaterial;
    }

    public Material getSecretMaterial() {
        String secretString = fileConfig.getString("material.secret", MATERIAL_SECRET_DEFAULT.toString());

        Material secretMaterial = Material.matchMaterial(secretString);

        if (secretMaterial == null) {
            secretMaterial = MATERIAL_SECRET_DEFAULT;
        }

        return secretMaterial;
    }

    public Material getMultipleResultsMaterial() {
        String multipleResultsString = fileConfig.getString("material.multiple-results", MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());

        Material multipleResultsMaterial = Material.matchMaterial(multipleResultsString);

        if (multipleResultsMaterial == null) {
            multipleResultsMaterial = MATERIAL_MULTIPLE_RESULTS_DEFAULT;
        }

        return multipleResultsMaterial;
    }

    public boolean getDisableOverrideWarnings() {
        return fileConfig.getBoolean("disable-override-warnings", DISABLE_OVERRIDE_WARNINGS_DEFAULT);
    }

    public String getRecipeCommentCharacters() {
        String allComments = "";
        List<String> comments = getRecipeCommentCharactersAsList();

        for (int i = 0; i < comments.size(); i++) {
            if (i > 0) {
                allComments += ",";
            }

            allComments += comments.get(i);
        }

        return allComments;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRecipeCommentCharactersAsList() {
        return (List<String>) fileConfig.getList("recipe-comment-characters", RECIPE_COMMENT_CHARACTERS_DEFAULT);
    }


    public Enchantment getEnchantment(String name) {
        return enchantNames.get(name);
    }

    public static Short getCustomData(Material material) {
        Short data = null;

        if (itemDatas.containsKey(material)) {
            data = itemDatas.get(material);
        }

        if (data == null) {
            data = material.getMaxDurability();
        }

        return data;
    }

    public List<Material> getChoicesAlias(String alias) {
        return choicesAliases.get(alias.trim().toLowerCase());
    }

    public Material getMaterial(String name) {
        return materialNames.get(RMCUtil.parseAliasName(name));
    }

    public Map<String, Short> getMaterialDataNames(Material material) {
        return materialDataNames.get(material);
    }

    public String getMaterialPrint(Material material) {
        return materialPrint.get(material);
    }

    public Map<Short, String> getMaterialDataPrint(Material material) {
        return materialDataPrint.get(material);
    }

    public String getEnchantPrint(Enchantment enchant) {
        return enchantPrint.get(enchant);
    }

    public List<Material> getAnvilCombineItem() {
        return anvilCombineItem;
    }

    public List<Material> getAnvilMaterialEnchant() {
        return anvilMaterialEnchant;
    }

    public List<Material> getAnvilRepairMaterial() {
        return anvilRepairMaterial;
    }

    public List<Material> getAnvilRenaming() {
        return anvilRenaming;
    }

    public Map<Enchantment, List<Integer>> getAnvilEnchantments() {
        return anvilEnchantments;
    }

    public List<Material> getGrindstoneCombineItem() {
        return grindstoneCombineItem;
    }

    public Map<Enchantment, List<Integer>> getGrindstoneBookEnchantments() {
        return grindstoneBookEnchantments;
    }

    public List<Material> getGrindstoneItemMaterials() {
        return grindstoneItemMaterials;
    }

    public Map<Enchantment, List<Integer>> getGrindstoneItemEnchantments() {
        return grindstoneItemEnchantments;
    }
}
