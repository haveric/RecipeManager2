package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.flags.recipe.FlagFailMessage;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagFailMessageTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagFailMessage/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            FlagFailMessage flag = (FlagFailMessage) recipe.getFlag(FlagType.FAIL_MESSAGE);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(RMCChatColor.COLOR_CHAR + "cYOU FAILED, MWaHahahah!", flag.getMessage());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals("Test", flag.getMessage());
            } else if (resultType == Material.STONE) {
                assertEquals("One", flag.getMessage());
            } else if (resultType == Material.COBBLESTONE) {
                assertEquals("   Two   ", flag.getMessage());
            }
        }
    }
}