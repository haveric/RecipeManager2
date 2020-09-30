package haveric.recipeManager.tools;

import org.bukkit.inventory.ItemFlag;

public class Supports {
    static boolean itemFlagHideDye = false;

    public static void init() {
        checkItemFlagHideDyeSupport();
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

    public static boolean itemFlagHideDye() {
        return itemFlagHideDye;
    }
}
