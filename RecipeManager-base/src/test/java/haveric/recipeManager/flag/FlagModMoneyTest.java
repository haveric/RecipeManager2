package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagModMoney;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagModMoneyTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagModMoney/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagModMoney flag = (FlagModMoney) result.getFlag(FlagType.MOD_MONEY);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(.5, flag.getAmount(), .001);
                assertEquals('+', flag.getModifier());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(2.5, flag.getAmount(), .001);
                assertEquals('-', flag.getModifier());
                assertEquals("<red>You lost {money}!", flag.getFailMessage());
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(0, flag.getAmount(), .001);
                assertEquals('=', flag.getModifier());
                assertEquals("<red>You lost all your money!", flag.getFailMessage());
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(2.5, flag.getAmount(), .001);
                assertEquals('-', flag.getModifier());
            }
        }
    }
}