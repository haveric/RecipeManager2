package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagModExp;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagModExpTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagModExp/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagModExp flag = (FlagModExp) result.getFlag(FlagType.MOD_EXP);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(25, flag.getAmount(), .01);
                assertEquals('+', flag.getModifier());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(50, flag.getAmount(), .01);
                assertEquals('-', flag.getModifier());
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(0, flag.getAmount(), .01);
                assertEquals('=', flag.getModifier());
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(50, flag.getAmount(), .01);
                assertEquals('-', flag.getModifier());
            }
        }
    }
}
