package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.furnace.RMFurnaceRecipe1_13;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSmelt extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "smelt/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe1_13 recipe = (RMFurnaceRecipe1_13) entry.getKey();
            RecipeChoice recipeChoice = recipe.getIngredientChoice();

            assertTrue(recipeChoice instanceof RecipeChoice.MaterialChoice);

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
