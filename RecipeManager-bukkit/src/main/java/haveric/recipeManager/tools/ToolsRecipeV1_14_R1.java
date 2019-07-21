package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.List;

/**
 * Minecraft v1.13 NMS solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_14_R1 extends BaseToolsRecipe {
    @Override
    public boolean matchesShaped(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack[] matrix, org.bukkit.inventory.ItemStack[] matrixMirror, int width, int height) {
        if (bukkitRecipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) bukkitRecipe;

            return RMBukkitTools.compareShapedRecipeToMatrix(shapedRecipe, matrix, matrixMirror);
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


    @Override
    public boolean matchesBlasting(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack blastingIngredient) {
        if (bukkitRecipe instanceof BlastingRecipe) {
            BlastingRecipe blastingRecipe = (BlastingRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(blastingRecipe.getInputChoice(), blastingIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesSmoking(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack smokingIngredient) {
        if (bukkitRecipe instanceof SmokingRecipe) {
            SmokingRecipe smokingRecipe = (SmokingRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(smokingRecipe.getInputChoice(), smokingIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesCampfire(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack campfireIngredient) {
        if (bukkitRecipe instanceof CampfireRecipe) {
            CampfireRecipe campfireRecipe = (CampfireRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(campfireRecipe.getInputChoice(), campfireIngredient);
        }

        return false;
    }

    @Override
    public boolean matchesStonecutting(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack stonecuttingIngredient, ItemStack stonecuttingResult) {
        if (bukkitRecipe instanceof StonecuttingRecipe) {
            StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(stonecuttingRecipe.getInputChoice(), stonecuttingIngredient) && RMBukkitTools.isSameItemPlusDur(stonecuttingRecipe.getResult(), stonecuttingResult);
        }

        return false;
    }
}
