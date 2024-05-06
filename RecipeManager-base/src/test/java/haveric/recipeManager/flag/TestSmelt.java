package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestSmelt extends FlagBaseTest {
    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "smelt/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe recipe = (RMFurnaceRecipe) entry.getKey();
            RecipeChoice recipeChoice = recipe.getIngredientChoice();

            assertInstanceOf(RecipeChoice.MaterialChoice.class, recipeChoice);

            RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) recipeChoice;
            List<Material> ingredientMaterials = materialChoice.getChoices();

            if (ingredientMaterials.contains(Material.STONE_SWORD)) {
                assertEquals(Vanilla.FURNACE_RECIPE_TIME, recipe.getCookTime(), .001);
            } else if (ingredientMaterials.contains(Material.IRON_SWORD)) {
                assertEquals(5, recipe.getCookTime(), .001);
            } else if (ingredientMaterials.contains(Material.GOLDEN_SWORD)) {
                assertEquals(20, recipe.getCookTime(), .001);
            } else if (ingredientMaterials.contains(Material.DIAMOND_SWORD)) {
                assertEquals(25, recipe.getCookTime(), .001);
            }
        }
    }
}
