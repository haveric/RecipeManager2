package haveric.recipeManager.nms;

import haveric.recipeManager.nms.tools.NMSTools;
import haveric.recipeManager.nms.v1_12.RecipeIteratorV1_12;
import haveric.recipeManager.nms.v1_12.ToolsRecipeV1_12;
import haveric.recipeManagerCommon.recipes.AbstractBaseRecipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Iterator;
import java.util.List;

public class NMSVersionHandler {

    private static Iterator<Recipe> recipeIterator;

    public static Iterator<Recipe> getRecipeIterator() {
        if (recipeIterator == null) {
            String serverVersion = getServerVersion();

            if (serverVersion.equals("v1_13_R2")) {

            } else if (serverVersion.equals("v1_13_R1")) {

            } else if (serverVersion.equals("v1_12_R1")) {
                recipeIterator = new RecipeIteratorV1_12(getBukkitRecipeIterator());
            } else {
                recipeIterator = getBukkitRecipeIterator();
            }
        }

        return recipeIterator;
    }

    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private static Iterator<Recipe> getBukkitRecipeIterator() {
        return Bukkit.recipeIterator();
    }

    public static boolean matchesShaped(ShapedRecipe r, ItemStack[] matrix, ItemStack[] matrixMirror, int width, int height) {
        String serverVersion = getServerVersion();

        if (serverVersion.equals("v1_13_R2")) {

        } else if (serverVersion.equals("v1_13_R1")) {

        } else if (serverVersion.equals("v1_12_R1")) {
            return ToolsRecipeV1_12.matchesShaped(r, matrix, width, height) || ToolsRecipeV1_12.matchesShaped(r, matrixMirror, width, height);
        } else {
            return NMSTools.compareShapedRecipeToMatrix(r, matrix, matrixMirror);
        }

        return false;
    }

    public static boolean matchesShapeless(Recipe r, List<ItemStack> items, List<ItemStack> ingredientList) {
        String serverVersion = getServerVersion();

        if (serverVersion.equals("v1_13_R2")) {

        } else if (serverVersion.equals("v1_13_R1")) {

        } else if (serverVersion.equals("v1_12_R1")) {
            return ToolsRecipeV1_12.matchesShapeless(r, ingredientList);
        } else {
            return NMSTools.compareIngredientList(items, ingredientList);
        }

        return false;
    }

    public static boolean matchesFurnace(Recipe r, ItemStack furnaceIngredient) {
        String serverVersion = getServerVersion();

        if (serverVersion.equals("v1_13_R2")) {

        } else if (serverVersion.equals("v1_13_R1")) {

        } else if (serverVersion.equals("v1_12_R1")) {
            return ToolsRecipeV1_12.matchesFurnace(r, furnaceIngredient);
        }

        return false;
    }

    public static void replace(AbstractBaseRecipe recipe, ItemStack overrideItem) {
        Iterator<Recipe> iterator = getRecipeIterator();

        if (iterator instanceof RecipeIteratorV1_12) {
            ((RecipeIteratorV1_12) iterator).replace(recipe, overrideItem);
        }
    }

    public static void finish() {
        Iterator<Recipe> iterator = getRecipeIterator();

        if (iterator instanceof RecipeIteratorV1_12) {
            ((RecipeIteratorV1_12) iterator).finish();
        }
    }
}
