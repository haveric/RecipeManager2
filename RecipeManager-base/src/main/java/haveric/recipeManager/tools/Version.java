package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Version {

    private static String supportVersion = null;
    private static boolean spigotSupport = false;

    public static void init() {
        if (supports1_20_5()) {
            supportVersion = "1.20.5";
        } else if (supports1_20()) {
            supportVersion = "1.20";
        } else if (supports1_19_4()) {
            supportVersion = "1.19.4";
        } else if (supports1_19_3()) {
            supportVersion = "1.19.3";
        } else if (supports1_19()) {
            supportVersion = "1.19";
        } else if (supports1_18()) {
            supportVersion = "1.18";
        } else if (supports1_17()) {
            supportVersion = "1.17";
        } else {
            supportVersion = "1.16";
        }

        spigotSupport = supportsSpigot();
    }

    private static boolean supportsSpigot() {
        boolean supports;

        try {
            ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) item.getItemMeta();
            @SuppressWarnings("unused")
            BookMeta.Spigot spigot = bookMeta.spigot();
            supports = true;
        } catch (NoSuchMethodError e) {
            supports = false;
        }

        return supports;
    }

    public static boolean hasSpigotSupport() {
        return spigotSupport;
    }

    private static boolean supports1_20_5() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material turtleScute = Material.TURTLE_SCUTE;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_20() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material pitcherPod = Material.PITCHER_POD;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_19_4() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material cherryWood = Material.CHERRY_WOOD;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_19_3() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material bookshelf = Material.CHISELED_BOOKSHELF;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_19() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material mangroveBoat = Material.MANGROVE_BOAT;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_18() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material othersideDisk = Material.MUSIC_DISC_OTHERSIDE;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_17() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            EntityType goat = EntityType.GOAT;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static String getVersion() {
        if (supportVersion == null) {
            init();
        }

        return supportVersion;
    }

    public static boolean has1_20_5Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_20Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5") || version.equals("1.20")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_19_4Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5") || version.equals("1.20") || version.equals("1.19.4")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_19_3Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5") || version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_19Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5") || version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_18Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5") || version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19") || version.equals("1.18")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_17Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20.5") || version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19") || version.equals("1.18") || version.equals("1.17")) {
            hasSupport = true;
        }

        return hasSupport;
    }
}
