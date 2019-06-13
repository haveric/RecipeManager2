package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.smelt.SmeltRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestSmelt extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "smelt/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            SmeltRecipe recipe = (SmeltRecipe) entry.getKey();
            ItemStack ingredient = recipe.getIngredient();
            ItemResult result = recipe.getResult();

            Material ingredientType = ingredient.getType();

            if (ingredientType == Material.STONE_SWORD) {
                assertEquals(Vanilla.FURNACE_RECIPE_TIME, recipe.getCookTime(), .001);
            } else if (ingredientType == Material.IRON_SWORD) {
                assertEquals(5, recipe.getCookTime(), .001);
            } else if (ingredientType == Material.GOLDEN_SWORD) {
                assertEquals(20, recipe.getCookTime(), .001);
            } else if (ingredientType == Material.DIAMOND_SWORD) {
                assertEquals(25, recipe.getCookTime(), .001);
            }
        }
    }
}
