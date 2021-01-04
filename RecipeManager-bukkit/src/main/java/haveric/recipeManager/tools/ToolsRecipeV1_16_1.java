package haveric.recipeManager.tools;

import org.bukkit.inventory.*;

/**
 * Minecraft v1.16 solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_16_1 extends ToolsRecipeV1_14_R1 {

    @Override
    public boolean matchesSmithing(Recipe bukkitRecipe, ItemStack baseIngredient, ItemStack addIngredient) {
        if (bukkitRecipe instanceof SmithingRecipe) {
            SmithingRecipe smithingRecipe = (SmithingRecipe) bukkitRecipe;

            boolean isBaseSame = RMBukkitTools.isSameItemFromChoice(smithingRecipe.getBase(), baseIngredient);
            boolean isAddSame = RMBukkitTools.isSameItemFromChoice(smithingRecipe.getAddition(), addIngredient);
            return isBaseSame && isAddSame;
        }

        return false;
    }
}
