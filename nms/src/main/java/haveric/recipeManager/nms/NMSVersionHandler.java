package haveric.recipeManager.nms;

import haveric.recipeManager.tools.*;
import org.bukkit.Bukkit;

public class NMSVersionHandler {

    private static BaseToolsRecipe toolsRecipe;

    public static BaseRecipeIterator getRecipeIterator() {
        BaseRecipeIterator recipeIterator;

        String serverVersion = getServerVersion();

        switch (serverVersion) {
            case "v1_14_R1":
                recipeIterator = new RecipeIteratorV1_14_R1();
                break;
            case "v1_13_R2":
                recipeIterator = new RecipeIteratorV1_13_2();
                break;
            case "v1_12_R1":
                recipeIterator = new RecipeIteratorV1_12();
                break;
            case "v1_15_R1":
            default:
                recipeIterator = new RecipeIteratorOld();
                break;
        }

        return recipeIterator;
    }

    public static BaseToolsRecipe getToolsRecipe() {
        if (toolsRecipe == null) {
            String serverVersion = getServerVersion();

            int minorVersion = getMinorVersion(serverVersion);
            int patchVersion = getPatchVersion(serverVersion);
            if (minorVersion > 19 || (minorVersion == 19 && patchVersion >= 4)) {
                toolsRecipe = new ToolsRecipeV1_19_4();
            } else if (minorVersion >= 16) {
                toolsRecipe = new ToolsRecipeV1_16_1();
            } else if (minorVersion >= 14) {
                toolsRecipe = new ToolsRecipeV1_14_R1();
            } else if (minorVersion == 13 && patchVersion == 2) {
                toolsRecipe = new ToolsRecipeV1_13_2();
            } else {
                toolsRecipe = new ToolsRecipeOld();
            }
        }

        return toolsRecipe;
    }


    // Example return: "1.21-R0.1-SNAPSHOT"
    private static String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion();
    }

    private static int getMinorVersion(String serverVersion) {
        int minorVersion = 0;
        String[] versionSplit = serverVersion.split("-")[0].split("\\.");

        if (versionSplit.length > 1) {
            try {
                minorVersion = Integer.parseInt(versionSplit[1]);
            } catch (NumberFormatException ignored) {}
        }

        return minorVersion;
    }

    private static int getPatchVersion(String serverVersion) {
        int patchVersion = 0;
        String[] versionSplit = serverVersion.split("-")[0].split("\\.");

        if (versionSplit.length > 2) {
            try {
                patchVersion = Integer.parseInt(versionSplit[2]);
            } catch (NumberFormatException ignored) {}
        }

        return patchVersion;
    }
}
