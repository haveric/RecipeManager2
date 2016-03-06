package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.Sound;

public class Version {

    private static String supportVersion = null;

    public static void init() {
        if (supports19()) {
            supportVersion = "1.9";
        } else if (supports18()) {
            supportVersion = "1.8";
        } else {
            supportVersion = "1.7";
        }
    }

    private static boolean supports19() {
        boolean supports = false;

        try {
            @SuppressWarnings("unused")
            Sound sound = Sound.BLOCK_NOTE_BASS;
            supports = true;
        } catch (NoSuchFieldError e) {
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

    public static boolean has19Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.9")) {
            hasSupport = true;
        }

        return hasSupport;
    }

    public static boolean has18Support() {
        boolean hasSupport = false;
        String version = getVersion();

        if (version.equals("1.9") || version.equals("1.8")) {
            hasSupport = true;
        }

        return hasSupport;
    }
}
