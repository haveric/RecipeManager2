package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.Map;

/**
 * Minecraft v1.16 solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_16_1 extends BaseToolsRecipe {
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
