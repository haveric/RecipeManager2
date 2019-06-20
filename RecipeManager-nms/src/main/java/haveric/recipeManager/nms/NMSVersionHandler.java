package haveric.recipeManager.nms;

import haveric.recipeManager.nms.tools.BaseRecipeIterator;
import haveric.recipeManager.nms.tools.BaseToolsRecipe;
import haveric.recipeManager.nms.v1_12.RecipeIteratorV1_12;
import haveric.recipeManager.nms.v1_12.ToolsRecipeV1_12;
import haveric.recipeManager.nms.v1_13_2.RecipeIteratorV1_13_2;
import haveric.recipeManager.nms.v1_13_2.ToolsRecipeV1_13_2;
import haveric.recipeManager.nms.v1_14_R1.RecipeIteratorV1_14_R1;
import haveric.recipeManager.nms.v1_14_R1.ToolsRecipeV1_14_R1;
import haveric.recipeManager.nms.vOld.RecipeIteratorOld;
import haveric.recipeManager.nms.vOld.ToolsRecipeOld;
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
            default:
                recipeIterator = new RecipeIteratorOld();
                break;
        }

        return recipeIterator;
    }

    public static BaseToolsRecipe getToolsRecipe() {
        if (toolsRecipe == null) {
            String serverVersion = getServerVersion();

            switch (serverVersion) {
                case "v1_14_R1":
                    toolsRecipe = new ToolsRecipeV1_14_R1();
                    break;
                case "v1_13_R2":
                    toolsRecipe = new ToolsRecipeV1_13_2();
                    break;
                case "v1_12_R1":
                    toolsRecipe = new ToolsRecipeV1_12();
                    break;
                default:
                    toolsRecipe = new ToolsRecipeOld();
                    break;
            }
        }

        return toolsRecipe;
    }


    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
