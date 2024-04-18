package haveric.recipeManager.settings;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
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
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Tag.REGISTRY_BLOCKS;
import static org.bukkit.Tag.REGISTRY_ITEMS;

/**
 * RecipeManager's settings loaded from its config.yml, values are read-only.
 */
public class SettingsYaml extends BaseSettings {
    private FileConfiguration fileConfig;
    private File DEFAULT_DATA_FOLDER;

    public SettingsYaml(boolean loadDefaultConfig) {
        super(loadDefaultConfig);
    }

    protected void init(boolean loadDefaultConfig) {
        if (!hasBeenInited) {
            super.init(loadDefaultConfig);

            if (loadDefaultConfig) {
                DEFAULT_DATA_FOLDER = RecipeManager.getPlugin().getDataFolder();

                // Load/reload/generate config.yml
                loadFileConfig(DEFAULT_DATA_FOLDER, Files.FILE_CONFIG);
            }
        }
    }

    public void loadFileConfig(File dataFolder, String fileName) {
        fileConfig = loadYML(dataFolder, fileName);
    }

    public void reload(CommandSender sender) {
        String lastChanged = fileConfig.getString("lastchanged");

        super.reload(sender);

        if (!Files.LASTCHANGED_CONFIG.equals(lastChanged)) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_CONFIG + "' file is outdated, please delete it to allow it to be generated again.");
        }

        loadItemAliases(sender, DEFAULT_DATA_FOLDER, Files.FILE_ITEM_ALIASES);
        loadChoiceAliases(sender, DEFAULT_DATA_FOLDER, Files.FILE_CHOICE_ALIASES);
        loadItemDatas(sender, DEFAULT_DATA_FOLDER, Files.FILE_ITEM_DATAS);

        anvilCombineItem.clear();
        String combineItemMaterials = fileConfig.getString("special-recipes.anvil.combine-item.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (!combineItemMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(combineItemMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilCombineItem.addAll(materials);
            }
        }

        grindstoneCombineItem.clear();
        String grindstoneCombineItemMaterials = fileConfig.getString("special-recipes.grindstone.combine-item.materials", SPECIAL_GRINDSTONE_CUSTOM_DEFAULT);
        if (!grindstoneCombineItemMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(grindstoneCombineItemMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                grindstoneCombineItem.addAll(materials);
            }
        }

        grindstoneItemMaterials.clear();
        String grindstoneDisenchantItemMaterials = fileConfig.getString("special-recipes.grindstone.disenchant.item.materials", SPECIAL_GRINDSTONE_CUSTOM_DEFAULT);
        if (!grindstoneDisenchantItemMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(grindstoneDisenchantItemMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                grindstoneItemMaterials.addAll(materials);
            }
        }

        anvilMaterialEnchant.clear();
        String enchantMaterials = fileConfig.getString("special-recipes.anvil.enchant.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (!enchantMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(enchantMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilMaterialEnchant.addAll(materials);
            }
        }

        anvilEnchantments.clear();
        String enchantEnchantments = fileConfig.getString("special-recipes.anvil.enchant.enchantments", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (!enchantEnchantments.equals("false")) {
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
        if (!grindstoneDisenchantBookEnchantments.equals("false")) {
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
        if (!grindstoneDisenchantItemEnchantments.equals("false")) {
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
        if (!repairMaterials.equals("false")) {
            List<Material> materials = Tools.parseChoice(repairMaterials, ParseBit.NO_ERRORS);
            if (materials != null) {
                anvilRepairMaterial.addAll(materials);
            }
        }

        anvilRenaming.clear();
        String renameMaterials = fileConfig.getString("special-recipes.anvil.renaming.materials", SPECIAL_ANVIL_CUSTOM_DEFAULT);
        if (!renameMaterials.equals("false")) {
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
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.secret has invalid material definition: " + secretString + ". Defaulting to " + MATERIAL_SECRET_DEFAULT + ".");
            }
        }

        String multipleResultsString = fileConfig.getString("material.multiple-results", MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());
        if (multipleResultsString != null) {
            Material multipleResultsMaterial = Material.matchMaterial(multipleResultsString);
            if (multipleResultsMaterial == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.multiple-results has invalid material definition: " + multipleResultsString + ". Defaulting to " + MATERIAL_MULTIPLE_RESULTS_DEFAULT + ".");
            }
        }
    }

    public void loadItemAliases(CommandSender sender, File dataFolder, String fileName) {
        FileConfiguration itemAliasesConfig = loadYML(dataFolder, fileName);

        if (!Files.LASTCHANGED_ITEM_ALIASES.equals(itemAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + fileName + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : itemAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = Material.matchMaterial(arg);

            if (material == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid material definition: " + arg);
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
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid data value number: " + key + " for material: " + material);
                        }
                    }
                }
            } else {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid data type at: " + arg);
            }
        }
    }

    public void loadChoiceAliases(CommandSender sender, File dataFolder, String fileName) {
        FileConfiguration choiceAliasesConfig = loadYML(dataFolder, fileName);

        if (!Files.LASTCHANGED_CHOICE_ALIASES.equals(choiceAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + fileName + "' file is outdated, please delete it to allow it to be generated again.");
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
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid tag definition: " + arg);
                        } else {
                            materials.addAll(tag.getValues());
                        }
                    } else if (materialString.startsWith("alias:") || materialString.startsWith("a:")) {
                        String aliasString = materialString.substring(materialString.indexOf(':') + 1);

                        List<Material> choiceMaterials = getChoicesAlias(aliasString);
                        if (choiceMaterials == null) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid choice alias definition: " + arg);
                        } else {
                            materials.addAll(choiceMaterials);
                        }
                    } else {
                        Material material = getMaterial(materialString);

                        if (material == null) {
                            material = Material.matchMaterial(materialString);
                        }

                        if (material == null) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid material (or item alias) definition: " + arg);
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
    }

    public void loadItemDatas(CommandSender sender, File dataFolder, String fileName) {
        FileConfiguration itemDatasConfig = loadYML(dataFolder, fileName);

        if (!Files.LASTCHANGED_ITEM_DATAS.equals(itemDatasConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + fileName + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : itemDatasConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = getMaterial(arg);

            if (material == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid material definition: " + arg);
                continue;
            }

            String value = itemDatasConfig.getString(arg);
            try {
                itemDatas.put(material, Short.parseShort(value));
            } catch (NumberFormatException e) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + fileName + "' has invalid data value number: " + value + " for material: " + material);
            }
        }
    }

    private static FileConfiguration loadYML(File dataFolder, String fileName) {
        File file = new File(dataFolder + File.separator + fileName);

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

    public boolean getSpecialDecoratedPot() {
        return fileConfig.getBoolean("special-recipes.decorated-pot", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimCoast() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.coast", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimDune() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.dune", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimEye() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.eye", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimHost() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.host", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimRaiser() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.raiser", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimRib() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.rib", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimSentry() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.sentry", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimShaper() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.shaper", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimSilence() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.silence", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimSnout() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.snout", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimSpire() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.spire", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimTide() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.tide", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimVex() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.vex", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimWard() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.ward", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimWayfinder() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.wayfinder", SPECIAL_RECIPE_DEFAULT);
    }

    public boolean getSpecialSmithingArmorTrimWild() {
        return fileConfig.getBoolean("special-recipes.smithing.armor-trim.wild", SPECIAL_RECIPE_DEFAULT);
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

    public boolean getUpdateCheckLogNewOnly() {
        return fileConfig.getBoolean("update-check.log-new-only", UPDATE_CHECK_LOG_NEW_ONLY_DEFAULT);
    }

    public int getSaveFrequencyForBrewingStands() {
        return getSaveFrequency("brewingstands");
    }

    public int getSaveFrequencyForCampfires() {
        return getSaveFrequency("campfires");
    }

    public int getSaveFrequencyForComposters() {
        return getSaveFrequency("composters");
    }

    public int getSaveFrequencyForCooldowns() {
        return getSaveFrequency("cooldowns");
    }

    public int getSaveFrequencyForFurnaces() {
        return getSaveFrequency("furnaces");
    }

    private int getSaveFrequency(String type) {
        int minutes = Math.max(fileConfig.getInt("save-frequency." + type, SAVE_FREQUENCY_DEFAULT), 1);
        return 1200 * minutes; // Convert to ticks: 20t * 60s * m
    }

    public Material getFailMaterial() {
        return getMaterial("fail", MATERIAL_FAIL_DEFAULT);
    }

    public Material getSecretMaterial() {
        return getMaterial("secret", MATERIAL_SECRET_DEFAULT);
    }

    public Material getMultipleResultsMaterial() {
        return getMaterial("multiple-results", MATERIAL_MULTIPLE_RESULTS_DEFAULT);
    }

    private Material getMaterial(String type, Material defaultMaterial) {
        String materialString = fileConfig.getString("material." + type, defaultMaterial.toString());

        Material material = Material.matchMaterial(materialString);

        if (material == null) {
            material = defaultMaterial;
        }

        return material;
    }

    public boolean getDisableOverrideWarnings() {
        return fileConfig.getBoolean("disable-override-warnings", DISABLE_OVERRIDE_WARNINGS_DEFAULT);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRecipeCommentCharactersAsList() {
        return (List<String>) fileConfig.getList("recipe-comment-characters", RECIPE_COMMENT_CHARACTERS_DEFAULT);
    }
}
