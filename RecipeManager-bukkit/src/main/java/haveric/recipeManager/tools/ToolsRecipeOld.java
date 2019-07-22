package haveric.recipeManager.tools;

import org.bukkit.inventory.*;

import java.util.List;

public class ToolsRecipeOld extends BaseToolsRecipe {
    @Override
    public boolean matchesShapedLegacy(Recipe bukkitRecipe, ItemStack[] matrix, ItemStack[] matrixMirror, int width, int height) {
        if (bukkitRecipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) bukkitRecipe;

            return RMBukkitTools.compareShapedRecipeToMatrix(shapedRecipe, matrix, matrixMirror);
        }

        return false;
    }

    @Override
    public boolean matchesShapelessLegacy(Recipe bukkitRecipe, List<ItemStack> ingredientItems) {
        if (bukkitRecipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) bukkitRecipe;

            return RMBukkitTools.compareIngredientList(ingredientItems, shapelessRecipe.getIngredientList());
        }

        return false;
    }


    @Override
    public boolean matchesFurnace(Recipe bukkitRecipe, ItemStack furnaceIngredient) {
        if (bukkitRecipe instanceof FurnaceRecipe) {
            FurnaceRecipe furnaceRecipe = (FurnaceRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemPlusDur(furnaceRecipe.getInput(), furnaceIngredient);
        }

        return false;
    }
}
