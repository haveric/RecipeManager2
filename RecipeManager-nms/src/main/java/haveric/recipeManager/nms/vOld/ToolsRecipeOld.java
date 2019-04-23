package haveric.recipeManager.nms.vOld;

import haveric.recipeManager.nms.tools.BaseToolsRecipe;
import haveric.recipeManager.nms.tools.NMSTools;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class ToolsRecipeOld extends BaseToolsRecipe {
    @Override
    public boolean matchesShaped(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack[] matrix, org.bukkit.inventory.ItemStack[] matrixMirror, int width, int height) {
        return NMSTools.compareShapedRecipeToMatrix((ShapedRecipe) bukkitRecipe, matrix, matrixMirror);
    }

    @Override
    public boolean matchesShapeless(Recipe bukkitRecipe, List<ItemStack> ingredientItems, List<ItemStack> ingredientList) {
        return NMSTools.compareIngredientList(ingredientItems, ingredientList);
    }
}
