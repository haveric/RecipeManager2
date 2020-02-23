package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.flags.any.FlagModExp;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagModExpTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagModExp/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

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
