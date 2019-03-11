package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagNeedExp;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import junit.framework.Assert;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagNeedExpTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagNeedExp/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagNeedExp flag = (FlagNeedExp) result.getFlag(FlagType.NEED_EXP);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(100, flag.getMinExp());
                assertEquals(100, flag.getMaxExp());
                Assert.assertFalse(flag.getSetBoth());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(250, flag.getMinExp());
                assertEquals(250, flag.getMaxExp());
                assertTrue(flag.getSetBoth());
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(0, flag.getMinExp());
                assertEquals(500, flag.getMaxExp());
                assertTrue(flag.getSetBoth());
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(1000, flag.getMinExp());
                assertEquals(1000, flag.getMaxExp());
                Assert.assertFalse(flag.getSetBoth());
                assertEquals("<red>Need {exp} exp!", flag.getFailMessage());
            } else if (resultType == Material.DIAMOND_SWORD) {
                assertEquals(1000, flag.getMinExp());
                assertEquals(1000, flag.getMaxExp());
                Assert.assertFalse(flag.getSetBoth());
            }
        }
    }
}