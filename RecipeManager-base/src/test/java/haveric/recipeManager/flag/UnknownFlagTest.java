package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UnknownFlagTest extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "unknownFlag/unknownFlag.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            if (resultType == Material.IRON_SWORD) {
                assertEquals(0, result.getFlags().get().size());
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(1, result.getFlags().get().size());
            } else if (resultType == Material.DIAMOND_SWORD) {
                assertEquals(1, result.getFlags().get().size());
            }
        }
    }
}
