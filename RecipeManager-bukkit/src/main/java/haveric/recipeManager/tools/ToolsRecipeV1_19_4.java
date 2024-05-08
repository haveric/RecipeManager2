package haveric.recipeManager.tools;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;

/**
 * Minecraft v1.19.4 solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_19_4 extends ToolsRecipeV1_16_1 {

    @Override
    public boolean matchesSmithingTransform(Recipe bukkitRecipe, ItemStack templateIngredient, ItemStack baseIngredient, ItemStack addIngredient) {
        if (bukkitRecipe instanceof SmithingTransformRecipe smithingTransformRecipe) {
            boolean isTemplateSame = RMBukkitTools.isSameItemFromChoice(smithingTransformRecipe.getTemplate(), templateIngredient);
            boolean isBaseSame = RMBukkitTools.isSameItemFromChoice(smithingTransformRecipe.getBase(), baseIngredient);
            boolean isAddSame = RMBukkitTools.isSameItemFromChoice(smithingTransformRecipe.getAddition(), addIngredient);
            return isTemplateSame && isBaseSame && isAddSame;
        }

        return false;
    }
}
