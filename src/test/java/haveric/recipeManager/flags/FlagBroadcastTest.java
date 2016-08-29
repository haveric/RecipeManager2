package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FlagBroadcastTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagBroadcast/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagBroadcast flag = (FlagBroadcast) result.getFlag(FlagType.BROADCAST);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals("{playerdisplay} <green>crafted something!", flag.getMessage());
                assertNull(flag.getPermission());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals("'{player}' crafted '{recipename}' at {world}: {x}, {y}, {z}", flag.getMessage());
                assertEquals("ranks.admins", flag.getPermission());
            }
        }
    }
}