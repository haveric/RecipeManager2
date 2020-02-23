package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.flags.any.FlagNeedLevel;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

public class FlagNeedLevelTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagNeedLevel/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            FlagNeedLevel flag = (FlagNeedLevel) result.getFlag(FlagType.NEED_LEVEL);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(1, flag.getMinLevel());
                assertEquals(1, flag.getMaxLevel());
                assertFalse(flag.getSetBoth());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(5, flag.getMinLevel());
                assertEquals(5, flag.getMaxLevel());
                assertTrue(flag.getSetBoth());
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(25, flag.getMinLevel());
                assertEquals(100, flag.getMaxLevel());
                assertTrue(flag.getSetBoth());
                assertEquals("<red>Need level 25 to 100!", flag.getFailMessage());
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(5, flag.getMinLevel());
                assertEquals(5, flag.getMaxLevel());
                assertFalse(flag.getSetBoth());
            }
        }
    }
}