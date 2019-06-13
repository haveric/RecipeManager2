package haveric.recipeManager.nms.tools;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public abstract class BaseToolsRecipe {
    public boolean matchesFurnace(Recipe bukkitRecipe, ItemStack furnaceIngredient) {
        return false;
    }

    public boolean matchesShaped(Recipe bukkitRecipe, ItemStack[] matrix, ItemStack[] matrixMirror, int width, int height) {
        return matchesShapedMatrix(bukkitRecipe, matrix, width, height) || matchesShapedMatrix(bukkitRecipe, matrixMirror, width, height);
    }

    protected boolean matchesShapedMatrix(Recipe bukkitRecipe, ItemStack[] ingredients, int width, int height) {
        return false;
    }

    public boolean matchesShapeless(Recipe bukkitRecipe, List<ItemStack> ingredients, List<ItemStack> ingredientList) {
        return false;
    }

    public boolean matchesBlasting(Recipe bukkitRecipe, ItemStack blastingIngredient) {
        return false;
    }

    public boolean matchesSmoking(Recipe bukkitRecipe, ItemStack smokingIngredient) {
        return false;
    }

    public boolean matchesCampfire(Recipe bukkitRecipe, ItemStack campfireIngredient) {
        return false;
    }

    public boolean matchesStonecutting(Recipe bukkitRecipe, ItemStack stoneCuttingIngredient, ItemStack stonecuttingResult) {
        return false;
    }
}
