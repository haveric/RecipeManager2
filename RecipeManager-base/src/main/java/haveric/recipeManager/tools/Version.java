package haveric.recipeManager.tools;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static org.bukkit.Tag.REGISTRY_BLOCKS;

public class Version {

    private static String supportVersion = null;
    private static boolean spigotSupport = false;

    public static void init() {
        if (supports1_20()) {
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
        } else if (supports1_16()) {
            supportVersion = "1.16";
        } else if (supports1_15()) {
            supportVersion = "1.15";
        } else if (supports1_14()) {
            supportVersion = "1.14";
        } else if (supports1_13_plus()) {
            supportVersion = "1.13+";
        } else if (supports1_13()) {
            supportVersion = "1.13";
        } else if (supports1_12()) {
            supportVersion = "1.12";
        } else if (supports1_11()) {
            supportVersion = "1.11";
        } else if (supports1_10()) {
            supportVersion = "1.10";
        } else if (supports1_9()) {
            supportVersion = "1.9";
        } else {
            supportVersion = "1.8";
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

    private static boolean supports1_16() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            EntityType zombifiedPiglin = EntityType.ZOMBIFIED_PIGLIN;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_15() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material beeNest = Material.BEE_NEST;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_14() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material campfire = Material.CAMPFIRE;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_13_plus() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Iterable<Tag<Material>> blockTags = Bukkit.getTags(REGISTRY_BLOCKS, Material.class);
            supports = true;
        } catch (NoSuchMethodError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_13() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material kelp = Material.KELP;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_12() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            EntityType et = EntityType.PARROT;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_11() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material shulker = Material.SHULKER_SHELL;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_10() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            EntityType et = EntityType.POLAR_BEAR;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports1_9() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material chorus = Material.CHORUS_FLOWER;
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

    public static boolean has1_20Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_19_4Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_19_3Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_19Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_18Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19") || version.equals("1.18")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_17Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19") || version.equals("1.18") || version.equals("1.17")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_16Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19") || version.equals("1.18") || version.equals("1.17") || version.equals("1.16")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_15Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.20") || version.equals("1.19.4") || version.equals("1.19.3") || version.equals("1.19") || version.equals("1.18") || version.equals("1.17") || version.equals("1.16") || version.equals("1.15")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_14Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.13+") && !version.equals("1.13") && !version.equals("1.12") && !version.equals("1.11") && !version.equals("1.10") && !version.equals("1.9") && !version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    /** Later api support for Tags and RecipeChoice */
    public static boolean has1_13Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.13") && !version.equals("1.12") && !version.equals("1.11") && !version.equals("1.10") && !version.equals("1.9") && !version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_13BasicSupport() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.12") && !version.equals("1.11") && !version.equals("1.10") && !version.equals("1.9") && !version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_12Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.11") && !version.equals("1.10") && !version.equals("1.9") && !version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_11Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.10") && !version.equals("1.9") && !version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_10Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.9") && !version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has1_9Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }
}
