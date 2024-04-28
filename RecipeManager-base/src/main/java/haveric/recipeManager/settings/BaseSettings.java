package haveric.recipeManager.settings;

import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public abstract class BaseSettings {
    protected boolean hasBeenInited = false;

    protected final boolean SPECIAL_RECIPE_DEFAULT = true;
    protected final boolean SPECIAL_REPAIR_METADATA_DEFAULT = false;
    protected final String SPECIAL_ANVIL_CUSTOM_DEFAULT = "false";
    protected final String SPECIAL_GRINDSTONE_CUSTOM_DEFAULT = "false";

    protected final boolean SOUNDS_REPAIR_DEFAULT = true;
    protected final boolean SOUNDS_FAILED_DEFAULT = true;
    protected final boolean SOUNDS_FAILED_CLICK_DEFAULT = true;

    protected final boolean FIX_MOD_RESULTS_DEFAULT = false;
    protected final boolean UPDATE_BOOKS_DEFAULT = true;
    protected final boolean COLOR_CONSOLE_DEFAULT = true;

    protected final String FURNACE_SHIFT_CLICK_DEFAULT = "f";

    protected final boolean MULTITHREADING_DEFAULT = true;

    protected final boolean CLEAR_RECIPES_DEFAULT = false;

    protected final boolean UPDATE_CHECK_ENABLED_DEFAULT = true;
    protected final int UPDATE_CHECK_FREQUENCY_DEFAULT = 6;
    protected final boolean UPDATE_CHECK_LOG_NEW_ONLY_DEFAULT = true;
    protected final int SAVE_FREQUENCY_DEFAULT = 30;

    protected Material MATERIAL_FAIL_DEFAULT;
    protected final Material MATERIAL_SECRET_DEFAULT = Material.CHEST;
    protected final Material MATERIAL_MULTIPLE_RESULTS_DEFAULT = Material.CHEST;

    protected final boolean DISABLE_OVERRIDE_WARNINGS_DEFAULT = false;

    protected List<String> RECIPE_COMMENT_CHARACTERS_DEFAULT;

    protected Map<Material, Short> itemDatas;

    protected Map<String, List<Material>> choicesAliases;
    protected Map<String, Material> materialNames;
    protected Map<Material, Map<String, Short>> materialDataNames;
    protected Map<String, Enchantment> enchantNames;

    protected Map<Material, String> materialPrint;
    protected Map<Material, Map<Short, String>> materialDataPrint;
    protected Map<Enchantment, String> enchantPrint;

    protected List<Material> anvilCombineItem = new ArrayList<>();
    protected List<Material> anvilMaterialEnchant = new ArrayList<>();
    protected List<Material> anvilRepairMaterial = new ArrayList<>();
    protected List<Material> anvilRenaming = new ArrayList<>();
    protected Map<Enchantment, List<Integer>> anvilEnchantments = new HashMap<>();

    protected List<Material> grindstoneCombineItem = new ArrayList<>();
    protected List<Material> grindstoneItemMaterials = new ArrayList<>();
    protected Map<Enchantment, List<Integer>> grindstoneBookEnchantments = new HashMap<>();
    protected Map<Enchantment, List<Integer>> grindstoneItemEnchantments = new HashMap<>();

    public BaseSettings(boolean loadDefaultConfig) {
        init(loadDefaultConfig);
    }

    public void clearInit() {
        hasBeenInited = false;
    }

    protected void init(boolean loadDefaultConfig) {
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

            hasBeenInited = true;
        }
    }

    public void reload(CommandSender sender) {
        init(true);

        MessageSender.init(getColorConsole());

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
        MessageSender.getInstance().log("    special-recipes.decorated-pot: " + getSpecialDecoratedPot());
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
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.bolt: " + getSpecialSmithingArmorTrimBolt());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.coast: " + getSpecialSmithingArmorTrimCoast());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.dune: " + getSpecialSmithingArmorTrimDune());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.eye: " + getSpecialSmithingArmorTrimEye());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.flow: " + getSpecialSmithingArmorTrimFlow());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.host: " + getSpecialSmithingArmorTrimHost());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.raiser: " + getSpecialSmithingArmorTrimRaiser());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.rib: " + getSpecialSmithingArmorTrimRib());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.sentry: " + getSpecialSmithingArmorTrimSentry());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.shaper: " + getSpecialSmithingArmorTrimShaper());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.silence: " + getSpecialSmithingArmorTrimSilence());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.snout: " + getSpecialSmithingArmorTrimSnout());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.spire: " + getSpecialSmithingArmorTrimSpire());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.tide: " + getSpecialSmithingArmorTrimTide());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.vex: " + getSpecialSmithingArmorTrimVex());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.ward: " + getSpecialSmithingArmorTrimWard());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.wayfinder: " + getSpecialSmithingArmorTrimWayfinder());
        MessageSender.getInstance().log("    special-recipes.smithing.armor-trim.wild: " + getSpecialSmithingArmorTrimWild());
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
        MessageSender.getInstance().log("    update-check.log-new-only: " + getUpdateCheckLogNewOnly());
        MessageSender.getInstance().log("    save-frequency.brewingstands: " + getSaveFrequencyForBrewingStands());
        MessageSender.getInstance().log("    save-frequency.campfires: " + getSaveFrequencyForCampfires());
        MessageSender.getInstance().log("    save-frequency.composters: " + getSaveFrequencyForComposters());
        MessageSender.getInstance().log("    save-frequency.cooldowns: " + getSaveFrequencyForCooldowns());
        MessageSender.getInstance().log("    save-frequency.furnaces: " + getSaveFrequencyForFurnaces());
        MessageSender.getInstance().log("    material.fail: " + getFailMaterial());
        MessageSender.getInstance().log("    material.secret: " + getSecretMaterial());
        MessageSender.getInstance().log("    material.multiple-results: " + getMultipleResultsMaterial());
        MessageSender.getInstance().log("    disable-override-warnings: " + getDisableOverrideWarnings());
        MessageSender.getInstance().log("    recipe-comment-characters: " + getRecipeCommentCharacters());
    }


    public void addEnchantName(String name, Enchantment enchantment) {
        enchantNames.put(name, enchantment);
    }

    protected void parseMaterialNames(CommandSender sender, String names, Material material) {
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

    protected void parseMaterialDataNames(CommandSender sender, String names, short data, Material material) {
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


    public abstract boolean getSpecialRepair();

    public abstract boolean getSpecialRepairMetadata();

    public abstract boolean getSpecialLeatherDye();

    public abstract boolean getSpecialFireworks();

    public abstract boolean getSpecialFireworkStar();

    public abstract boolean getSpecialFireworkStarFade();

    public abstract boolean getSpecialMapCloning();

    public abstract boolean getSpecialMapExtending();

    public abstract boolean getSpecialBookCloning();

    public abstract boolean getSpecialAnvilCombineItem();

    public abstract boolean getSpecialAnvilEnchant();

    public abstract boolean getSpecialAnvilRepairMaterial();

    public abstract boolean getSpecialAnvilRenaming();

    public abstract boolean getSpecialGrindstoneCombineItem();

    public abstract boolean getSpecialGrindstoneDisenchantBook();

    public abstract boolean getSpecialGrindstoneDisenchantItem();

    public abstract boolean getSpecialCartographyClone();

    public abstract boolean getSpecialCartographyExtend();

    public abstract boolean getSpecialCartographyLock();

    public abstract boolean getSpecialBanner();

    public abstract boolean getSpecialBannerDuplicate();

    public abstract boolean getSpecialShieldBanner();

    public abstract boolean getSpecialTippedArrows();

    public abstract boolean getSpecialShulkerDye();

    public abstract boolean getSpecialSuspiciousStew();

    public abstract boolean getSpecialDecoratedPot();

    public abstract boolean getSpecialSmithingArmorTrimBolt();
    public abstract boolean getSpecialSmithingArmorTrimCoast();
    public abstract boolean getSpecialSmithingArmorTrimDune();
    public abstract boolean getSpecialSmithingArmorTrimEye();
    public abstract boolean getSpecialSmithingArmorTrimFlow();
    public abstract boolean getSpecialSmithingArmorTrimHost();
    public abstract boolean getSpecialSmithingArmorTrimRaiser();
    public abstract boolean getSpecialSmithingArmorTrimRib();
    public abstract boolean getSpecialSmithingArmorTrimSentry();
    public abstract boolean getSpecialSmithingArmorTrimShaper();
    public abstract boolean getSpecialSmithingArmorTrimSilence();
    public abstract boolean getSpecialSmithingArmorTrimSnout();
    public abstract boolean getSpecialSmithingArmorTrimSpire();
    public abstract boolean getSpecialSmithingArmorTrimTide();
    public abstract boolean getSpecialSmithingArmorTrimVex();
    public abstract boolean getSpecialSmithingArmorTrimWard();
    public abstract boolean getSpecialSmithingArmorTrimWayfinder();
    public abstract boolean getSpecialSmithingArmorTrimWild();

    public abstract boolean getSoundsRepair();

    public abstract boolean getSoundsFailed();

    public abstract boolean getSoundsFailedClick();

    public abstract boolean getFixModResults();

    public abstract boolean getUpdateBooks();

    public abstract boolean getColorConsole();

    public abstract char getFurnaceShiftClick();

    public abstract boolean getMultithreading();

    public abstract boolean getClearRecipes();

    public abstract boolean getUpdateCheckEnabled();

    public abstract int getUpdateCheckFrequency();

    public abstract boolean getUpdateCheckLogNewOnly();

    public abstract int getSaveFrequencyForBrewingStands();

    public abstract int getSaveFrequencyForCampfires();

    public abstract int getSaveFrequencyForComposters();

    public abstract int getSaveFrequencyForCooldowns();

    public abstract int getSaveFrequencyForFurnaces();

    public abstract Material getFailMaterial();

    public abstract Material getSecretMaterial();

    public abstract Material getMultipleResultsMaterial();

    public abstract boolean getDisableOverrideWarnings();

    public abstract List<String> getRecipeCommentCharactersAsList();

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


    public Enchantment getEnchantment(String name) {
        return enchantNames.get(name);
    }

    public Short getCustomData(Material material) {
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
