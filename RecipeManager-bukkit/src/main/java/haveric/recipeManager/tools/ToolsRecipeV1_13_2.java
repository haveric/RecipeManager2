package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.Map;

/**
 * Minecraft v1.13 NMS solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_13_2 extends BaseToolsRecipe {
    @Override
    public boolean matchesShaped(Recipe bukkitRecipe, String[] shape, Map<Character, List<Material>> materialChoiceMap) {
        if (bukkitRecipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) bukkitRecipe;

            return RMBukkitTools.compareShapedRecipeToChoice(shapedRecipe, shape, materialChoiceMap);
        }

        return false;
    }

    @Override
    public boolean matchesShapeless(Recipe bukkitRecipe, List<List<Material>> materialsList) {
        if (bukkitRecipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) bukkitRecipe;

            List<RecipeChoice> choices = shapelessRecipe.getChoiceList();

            return RMBukkitTools.compareMaterialChoiceList(choices, materialsList);
        }

        return false;
    }


    @Override
    public boolean matchesFurnace(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack furnaceIngredient) {
        if (bukkitRecipe instanceof org.bukkit.inventory.FurnaceRecipe) {
            org.bukkit.inventory.FurnaceRecipe furnaceRecipe = (org.bukkit.inventory.FurnaceRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(furnaceRecipe.getInputChoice(), furnaceIngredient);
        }

        return false;
    }
}
