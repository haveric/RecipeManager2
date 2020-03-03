package haveric.recipeManager.tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.Map;

public abstract class BaseToolsRecipe {
    public boolean matchesFurnace(Recipe bukkitRecipe, ItemStack furnaceIngredient) {
        return false;
    }

    public boolean matchesShapedLegacy(Recipe bukkitRecipe, ItemStack[] matrix, ItemStack[] matrixMirror) {
        return false;
    }

    public boolean matchesShaped(Recipe bukkitRecipe, String[] shape, Map<Character, RecipeChoice> materialChoiceMap) {
        return false;
    }

    public boolean matchesShapeless(Recipe bukkitRecipe, List<List<Material>> materialsList) {
        return false;
    }

    public boolean matchesShapelessLegacy(Recipe bukkitRecipe, List<ItemStack> itemsList) {
        return false;
    }

    public boolean matchesBlasting(Recipe bukkitRecipe, ItemStack blastingIngredient) {
        return false;
    }

    public boolean matchesSmoking(Recipe bukkitRecipe, ItemStack smokingIngredient) {
        return false;
    }

    public boolean matchesCampfire(Recipe bukkitRecipe, ItemStack campfireIngredient) {
        return false;
    }

    public boolean matchesStonecutting(Recipe bukkitRecipe, ItemStack stoneCuttingIngredient, ItemStack stonecuttingResult) {
        return false;
    }
}
