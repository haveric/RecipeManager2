package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Rabbit;

public class Version {

    private static String supportVersion = null;

    public static void init() {
        if (supports1_11()) {
            supportVersion = "1.11";
        } else if (supports19()) {
            supportVersion = "1.9";
        } else if (supports18Plus()) {
            supportVersion = "1.8+";
        } else if (supports18()) {
            supportVersion = "1.8";
        } else {
            supportVersion = "1.7";
        }
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
    private static boolean supports19() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Sound sound = Sound.BLOCK_NOTE_BASS;
            supports = true;
        } catch (NoSuchFieldError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports18Plus() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Rabbit.Type rabbit = Rabbit.Type.SALT_AND_PEPPER;
            supports = true;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            supports = false;
        }

        return supports;
    }

    private static boolean supports18() {
        boolean supports;

        try {
            @SuppressWarnings("unused")
            Material slime = Material.SLIME_BLOCK;
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

    public static boolean has1_11Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.11")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has19Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.11") || version.equals("1.9")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has18PlusSupport() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.8") && !version.equals("1.7")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has18Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (!version.equals("1.7")) {
            hasSupport = true;
        }

        return hasSupport;
    }
}
