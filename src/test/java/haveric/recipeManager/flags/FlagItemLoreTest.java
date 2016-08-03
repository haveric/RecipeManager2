package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagItemLoreTest extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagItemLore/flagItemLore.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(1, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            ItemResult result = recipe.getResult(a);

            FlagItemLore flag = (FlagItemLore) result.getFlag(FlagType.ITEM_LORE);
            flag.onPrepare(a);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                List<String> lores = result.getItemMeta().getLore();

                assertTrue(lores.contains("One"));
                assertTrue(lores.contains("Two"));
                assertEquals(lores.size(), 2);
            }
        }

        // TODO: Finish
    }
}
