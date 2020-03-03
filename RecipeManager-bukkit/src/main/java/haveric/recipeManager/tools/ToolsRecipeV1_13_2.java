package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.Map;

/**
 * Minecraft v1.13 solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_13_2 extends BaseToolsRecipe {
    @Override
    public boolean matchesShaped(Recipe bukkitRecipe, String[] shape, Map<Character, RecipeChoice> choice) {
        if (bukkitRecipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) bukkitRecipe;

            return RMBukkitTools.compareShapedRecipeToChoice(shapedRecipe, shape, choice);
        }

        return false;
    }

    @Override
    public boolean matchesShapeless(Recipe bukkitRecipe, List<List<Material>> materialsList) {
        if (bukkitRecipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) bukkitRecipe;

            List<RecipeChoice> choices = shapelessRecipe.getChoiceList();

            return RMBukkitTools.compareShapelessChoiceList(choices, materialsList);
        }

        return false;
    }


    @Override
    public boolean matchesFurnace(Recipe bukkitRecipe, ItemStack furnaceIngredient) {
        if (bukkitRecipe instanceof FurnaceRecipe) {
            FurnaceRecipe furnaceRecipe = (FurnaceRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(furnaceRecipe.getInputChoice(), furnaceIngredient);
        }

        return false;
    }
}
