package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagNeedMoneyTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagNeedMoney/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagNeedMoney flag = (FlagNeedMoney) result.getFlag(FlagType.NEED_MONEY);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(.25, flag.getMinMoney(), .01);
                assertEquals(.25, flag.getMaxMoney(), .01);
                assertFalse(flag.getSetBoth());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(.1, flag.getMinMoney(), .01);
                assertEquals(1000, flag.getMaxMoney(), .01);
                assertTrue(flag.getSetBoth());
                assertEquals("<red>Need {money}!", flag.getFailMessage());
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(.25, flag.getMinMoney(), .01);
                assertEquals(.25, flag.getMaxMoney(), .01);
                assertFalse(flag.getSetBoth());
            }
        }
    }
}