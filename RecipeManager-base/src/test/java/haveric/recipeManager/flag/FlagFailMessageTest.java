package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagFailMessage;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
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
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

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