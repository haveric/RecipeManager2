package haveric.recipeManager.tools;

import org.bukkit.Bukkit;

public class VersionHandler {

    private static BaseToolsRecipe toolsRecipe;

    public static RecipeIterator getRecipeIterator() {
        return new RecipeIterator();
    }

    public static BaseToolsRecipe getToolsRecipe() {
        if (toolsRecipe == null) {
            String serverVersion = getServerVersion();

            int minorVersion = getMinorVersion(serverVersion);
            int patchVersion = getPatchVersion(serverVersion);
            if (minorVersion > 19 || (minorVersion == 19 && patchVersion >= 4)) {
                toolsRecipe = new ToolsRecipeV1_19_4();
            } else {
                toolsRecipe = new ToolsRecipeV1_16_1();
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
