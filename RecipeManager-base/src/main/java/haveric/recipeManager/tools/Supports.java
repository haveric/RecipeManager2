package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

public class Supports {
    static boolean tropicalFishBucketMeta = false;
    static boolean knowledgeBookMeta = false;
    static boolean suspiciousStewMeta = false;
    static boolean compassMeta = false;
    static boolean itemFlagHideDye = false;

    public static void init() {
        checkTropicalFishBucketMeta();
        checkKnowledgeBookMeta();
        checkSuspiciousStewMeta();
        checkCompassMetaSupport();
        checkItemFlagHideDyeSupport();
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
}
