package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.recipes.BaseRecipe;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmptyFileTest extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "emptyFile/emptyFile.txt");
        reloadRecipeProcessor(true, file);
        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(0, queued.size());
    }
}
