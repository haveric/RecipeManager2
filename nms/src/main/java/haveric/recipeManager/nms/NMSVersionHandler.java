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
                recipeIterator = new haveric.recipeManager.nms.RecipeIteratorV1_12();
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
            if (minorVersion >= 14) {
                toolsRecipe = new ToolsRecipeV1_14_R1();
            } else {
                switch (serverVersion) {
                    case "v1_13_R2":
                        toolsRecipe = new ToolsRecipeV1_13_2();
                        break;
                    case "v1_12_R1":
                    default:
                        toolsRecipe = new ToolsRecipeOld();
                        break;
                }
            }
        }

        return toolsRecipe;
    }


    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private static int getMinorVersion(String serverVersion) {
        int minorVersion = 0;
        String[] versionSplit = serverVersion.split("_");

        if (versionSplit.length == 3) {
            try {
                minorVersion = Integer.parseInt(versionSplit[1]);
            } catch (NumberFormatException ignored) {}
        }

        return minorVersion;
    }
}
