package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaBook;
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
import static org.junit.Assert.assertNull;

public class FlagBookItemTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagBookItem/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagBookItem flag = (FlagBookItem) result.getFlag(FlagType.BOOK_ITEM);

            if (flag != null) {
                flag.onPrepare(a);
            }

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertNull(flag);
            } else if (resultType == Material.WRITTEN_BOOK) {
                assertFalse(a.hasReasons());
                TestMetaBook meta = (TestMetaBook) result.getItemMeta();
                assertEquals("The Art of Stealing", meta.getTitle());
                assertEquals("Gray Fox", meta.getAuthor());
                assertEquals(3, meta.getPages().size());
            }
        }
    }
}