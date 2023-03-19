package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class Supports {
    static boolean axolotlBucketMeta = false;
    static boolean tropicalFishBucketMeta = false;
    static boolean knowledgeBookMeta = false;
    static boolean suspiciousStewMeta = false;
    static boolean compassMeta = false;
    static boolean itemFlagHideDye = false;
    static boolean itemFlagHideArmorTrim = false;
    static boolean allayDuplication = false;
    static boolean categories = false;
    static boolean campfireStartEvent = false;

    public static void init() {
        checkAxolotlBucketMeta();
        checkTropicalFishBucketMeta();
        checkKnowledgeBookMeta();
        checkSuspiciousStewMeta();
        checkCompassMetaSupport();
        checkItemFlagHideDyeSupport();
        checkItemFlagHideArmorTrimSupport();
        checkAllayDuplication();
        checkCategories();
        checkCampfireStartEvent();
    }

    // 1.12
    private static void checkKnowledgeBookMeta() {
        try {
            ItemStack book = new ItemStack(Material.KNOWLEDGE_BOOK);
            @SuppressWarnings("unused")
            KnowledgeBookMeta bookMeta = (KnowledgeBookMeta) book.getItemMeta();
            knowledgeBookMeta = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            knowledgeBookMeta = false;
        }
    }

    // 1.13
    private static void checkTropicalFishBucketMeta() {
        try {
            ItemStack fishBucket = new ItemStack(Material.TROPICAL_FISH_BUCKET);
            @SuppressWarnings("unused")
            TropicalFishBucketMeta fishBucketMeta = (TropicalFishBucketMeta) fishBucket.getItemMeta();
            tropicalFishBucketMeta = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            tropicalFishBucketMeta = false;
        }
    }

    // 1.14.? (Added sometime after initial 1.14 release)
    private static void checkSuspiciousStewMeta() {
        try {
            ItemStack stew = new ItemStack(Material.SUSPICIOUS_STEW);
            @SuppressWarnings("unused")
            SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) stew.getItemMeta();
            suspiciousStewMeta = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            suspiciousStewMeta = false;
        }
    }

    // 1.16.1
    private static void checkCompassMetaSupport() {
        try {
            ItemStack compass = new ItemStack(Material.COMPASS);
            @SuppressWarnings("unused")
            CompassMeta meta = (CompassMeta) compass.getItemMeta();
            compassMeta = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            compassMeta = false;
        }
    }

    // 1.16.2
    private static void checkItemFlagHideDyeSupport() {
        try {
            @SuppressWarnings("unused")
            ItemFlag flag = ItemFlag.HIDE_DYE;
            itemFlagHideDye = true;
        } catch (NoSuchFieldError e) {
            itemFlagHideDye = false;
        }
    }

    // 1.17+
    private static void checkAxolotlBucketMeta() {
        try {
            ItemStack axolotlBucket = new ItemStack(Material.AXOLOTL_BUCKET);
            @SuppressWarnings("unused")
            AxolotlBucketMeta axolotlMeta = (AxolotlBucketMeta) axolotlBucket.getItemMeta();
            axolotlBucketMeta = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            axolotlBucketMeta = false;
        }
    }

    // 1.19.2
    private static void checkAllayDuplication() {
        try {
            @SuppressWarnings("unused")
            CreatureSpawnEvent.SpawnReason spawnReason = CreatureSpawnEvent.SpawnReason.DUPLICATION;
            allayDuplication = true;
        } catch (NoSuchFieldError e) {
            allayDuplication = false;
        }
    }

    // 1.19.3
    private static void checkCategories() {
        try {
            @SuppressWarnings("unused")
            String categoryName = CraftingBookCategory.class.getName();
            categories = true;
        } catch (NoClassDefFoundError e) {
            categories = false;
        }
    }

    // 1.19.3+
    private static void checkCampfireStartEvent() {
        try {
            @SuppressWarnings("unused")
            String eventName = CampfireStartEvent.class.getName();
            campfireStartEvent = true;
        } catch (NoClassDefFoundError e) {
            campfireStartEvent = false;
        }
    }

    // 1.19.4
    private static void checkItemFlagHideArmorTrimSupport() {
        try {
            @SuppressWarnings("unused")
            ItemFlag flag = ItemFlag.HIDE_ARMOR_TRIM;
            itemFlagHideArmorTrim = true;
        } catch (NoSuchFieldError e) {
            itemFlagHideArmorTrim = false;
        }
    }

    public static boolean axolotlBucketMeta() {
        return axolotlBucketMeta;
    }

    public static boolean tropicalFishBucketMeta() {
        return tropicalFishBucketMeta;
    }

    public static boolean knowledgeBookMeta() {
        return knowledgeBookMeta;
    }

    public static boolean suspiciousStewMeta() {
        return suspiciousStewMeta;
    }

    public static boolean compassMeta() {
        return compassMeta;
    }

    public static boolean itemFlagHideDye() {
        return itemFlagHideDye;
    }

    public static boolean itemFlagHideArmorTrim() {
        return itemFlagHideArmorTrim;
    }

    public static boolean allayDuplication() {
        return allayDuplication;
    }
    public static boolean categories() {
        return categories;
    }

    public static boolean campfireStartEvent() {
        return campfireStartEvent;
    }
}
