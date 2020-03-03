package haveric.recipeManager.tools;

import org.bukkit.inventory.*;

/**
 * Minecraft v1.14 solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_14_R1 extends ToolsRecipeV1_13_2 {
    @Override
    public boolean matchesBlasting(Recipe bukkitRecipe, ItemStack blastingIngredient) {
        if (bukkitRecipe instanceof BlastingRecipe) {
            BlastingRecipe blastingRecipe = (BlastingRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(blastingRecipe.getInputChoice(), blastingIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesSmoking(Recipe bukkitRecipe, ItemStack smokingIngredient) {
        if (bukkitRecipe instanceof SmokingRecipe) {
            SmokingRecipe smokingRecipe = (SmokingRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(smokingRecipe.getInputChoice(), smokingIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesCampfire(Recipe bukkitRecipe, ItemStack campfireIngredient) {
        if (bukkitRecipe instanceof CampfireRecipe) {
            CampfireRecipe campfireRecipe = (CampfireRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(campfireRecipe.getInputChoice(), campfireIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesStonecutting(Recipe bukkitRecipe, ItemStack stonecuttingIngredient, ItemStack stonecuttingResult) {
        if (bukkitRecipe instanceof StonecuttingRecipe) {
            StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(stonecuttingRecipe.getInputChoice(), stonecuttingIngredient) && RMBukkitTools.isSameItemPlusDur(stonecuttingRecipe.getResult(), stonecuttingResult);
        }

        return false;
    }
}
