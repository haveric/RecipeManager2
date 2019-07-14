package haveric.recipeManager.nms.vOld;

import haveric.recipeManager.nms.tools.BaseToolsRecipe;
import haveric.recipeManager.nms.tools.NMSTools;
import org.bukkit.inventory.*;

import java.util.List;

public class ToolsRecipeOld extends BaseToolsRecipe {
    @Override
    public boolean matchesShaped(Recipe bukkitRecipe, ItemStack[] matrix, ItemStack[] matrixMirror, int width, int height) {
        if (bukkitRecipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) bukkitRecipe;

            return NMSTools.compareShapedRecipeToMatrix(shapedRecipe, matrix, matrixMirror);
        }

        return false;
    }

    @Override
    public boolean matchesShapeless(Recipe bukkitRecipe, List<ItemStack> ingredientItems) {
        if (bukkitRecipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) bukkitRecipe;

            return NMSTools.compareIngredientList(ingredientItems, shapelessRecipe.getIngredientList());
        }

        return false;
    }


    @Override
    public boolean matchesFurnace(Recipe bukkitRecipe, ItemStack furnaceIngredient) {
        if (bukkitRecipe instanceof FurnaceRecipe) {
            FurnaceRecipe furnaceRecipe = (FurnaceRecipe) bukkitRecipe;

            return NMSTools.isSameItemPlusDur(furnaceRecipe.getInput(), furnaceIngredient);
        }

        return false;
    }


    @Override
    public boolean matchesBlasting(Recipe bukkitRecipe, ItemStack blastingIngredient) {
        if (bukkitRecipe instanceof BlastingRecipe) {
            BlastingRecipe blastingRecipe = (BlastingRecipe) bukkitRecipe;

            return NMSTools.isSameItemPlusDur(blastingRecipe.getInput(), blastingIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesSmoking(Recipe bukkitRecipe, ItemStack smokingIngredient) {
        if (bukkitRecipe instanceof SmokingRecipe) {
            SmokingRecipe smokingRecipe = (SmokingRecipe) bukkitRecipe;

            return NMSTools.isSameItemPlusDur(smokingRecipe.getInput(), smokingIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesCampfire(Recipe bukkitRecipe, ItemStack campfireIngredient) {
        if (bukkitRecipe instanceof CampfireRecipe) {
            CampfireRecipe campfireRecipe = (CampfireRecipe) bukkitRecipe;

            return NMSTools.isSameItemPlusDur(campfireRecipe.getInput(), campfireIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesStonecutting(Recipe bukkitRecipe, ItemStack stonecuttingIngredient, ItemStack stonecuttingResult) {
        if (bukkitRecipe instanceof StonecuttingRecipe) {
            StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) bukkitRecipe;

            return NMSTools.isSameItemPlusDur(stonecuttingRecipe.getInput(), stonecuttingIngredient) && NMSTools.isSameItemPlusDur(stonecuttingRecipe.getResult(), stonecuttingResult);
        }

        return false;
    }
}
