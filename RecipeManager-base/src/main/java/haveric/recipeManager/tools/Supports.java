package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;

public class Supports {
    static boolean suspiciousStewMeta = false;
    static boolean compassMeta = false;
    static boolean itemFlagHideDye = false;

    public static void init() {
        checkSuspiciousStewMeta();
        checkCompassMetaSupport();
        checkItemFlagHideDyeSupport();
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
