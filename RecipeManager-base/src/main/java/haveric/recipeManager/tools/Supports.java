package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class Supports {
    static boolean compassMeta = false;
    static boolean itemFlagHideDye = false;

    public static void init() {
        checkCompassMetaSupport();
        checkItemFlagHideDyeSupport();
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

    public static boolean compassMeta() {
        return compassMeta;
    }

    public static boolean itemFlagHideDye() {
        return itemFlagHideDye;
    }
}
