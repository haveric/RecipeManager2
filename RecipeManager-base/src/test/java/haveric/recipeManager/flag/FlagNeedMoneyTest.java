package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagNeedMoney;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

public class FlagNeedMoneyTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagNeedMoney/");
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