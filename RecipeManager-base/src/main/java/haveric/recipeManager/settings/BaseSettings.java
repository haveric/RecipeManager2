package haveric.recipeManager.settings;

import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class BaseSettings {
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
        MessageSender.getInstance().log("    update-check.log-new-only: " + getUpdateCheckLogNewOnly());
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


    public boolean getSpecialRepair() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialRepairMetadata() {
        return SPECIAL_REPAIR_METADATA_DEFAULT;
    }

    public boolean getSpecialLeatherDye() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialFireworks() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialFireworkStar() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialFireworkStarFade() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialMapCloning() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialMapExtending() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialBookCloning() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialAnvilCombineItem() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialAnvilEnchant() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialAnvilRepairMaterial() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialAnvilRenaming() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialGrindstoneCombineItem() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialGrindstoneDisenchantBook() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialGrindstoneDisenchantItem() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialCartographyClone() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialCartographyExtend() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialCartographyLock() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialBanner() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialBannerDuplicate() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialShieldBanner() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialTippedArrows() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialShulkerDye() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSpecialSuspiciousStew() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    public boolean getSoundsRepair() {
        return SOUNDS_REPAIR_DEFAULT;
    }

    public boolean getSoundsFailed() {
        return SOUNDS_FAILED_DEFAULT;
    }

    public boolean getSoundsFailedClick() {
        return SOUNDS_FAILED_CLICK_DEFAULT;
    }

    public boolean getFixModResults() {
        return FIX_MOD_RESULTS_DEFAULT;
    }

    public boolean getUpdateBooks() {
        return UPDATE_BOOKS_DEFAULT;
    }

    public boolean getColorConsole() {
        return COLOR_CONSOLE_DEFAULT;
    }

    public char getFurnaceShiftClick() {
        return FURNACE_SHIFT_CLICK_DEFAULT.charAt(0);
    }

    public boolean getMultithreading() {
        return MULTITHREADING_DEFAULT;
    }

    public boolean getClearRecipes() {
        return CLEAR_RECIPES_DEFAULT;
    }

    public boolean getUpdateCheckEnabled() {
        return UPDATE_CHECK_ENABLED_DEFAULT;
    }

    public int getUpdateCheckFrequency() {
        return UPDATE_CHECK_FREQUENCY_DEFAULT;
    }

    public boolean getUpdateCheckLogNewOnly() {
        return UPDATE_CHECK_LOG_NEW_ONLY_DEFAULT;
    }

    public Material getFailMaterial() {
        return MATERIAL_FAIL_DEFAULT;
    }

    public Material getSecretMaterial() {
        return MATERIAL_SECRET_DEFAULT;
    }

    public Material getMultipleResultsMaterial() {
        return MATERIAL_MULTIPLE_RESULTS_DEFAULT;
    }

    public boolean getDisableOverrideWarnings() {
        return DISABLE_OVERRIDE_WARNINGS_DEFAULT;
    }

    public List<String> getRecipeCommentCharactersAsList() {
        return RECIPE_COMMENT_CHARACTERS_DEFAULT;
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
