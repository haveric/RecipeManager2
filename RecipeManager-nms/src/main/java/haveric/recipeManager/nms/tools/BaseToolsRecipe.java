package haveric.recipeManager.nms.tools;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public abstract class BaseToolsRecipe {
    public boolean matchesFurnace(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack furnaceIngredient) {
        return false;
    }

    public boolean matchesShaped(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack[] matrix, org.bukkit.inventory.ItemStack[] matrixMirror, int width, int height) {
        return matchesShapedMatrix(bukkitRecipe, matrix, width, height) || matchesShapedMatrix(bukkitRecipe, matrixMirror, width, height);
    }

    protected boolean matchesShapedMatrix(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack[] ingredients, int width, int height) {
        return false;
    }

    public boolean matchesShapeless(Recipe bukkitRecipe, List<ItemStack> ingredients, List<ItemStack> ingredientList) {
        return false;
    }
}
