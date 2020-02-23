package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.flags.any.FlagBroadcast;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FlagBroadcastTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagBroadcast/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            FlagBroadcast flag = (FlagBroadcast) result.getFlag(FlagType.BROADCAST);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals("{playerdisplay} <green>crafted something!", flag.getMessage());
                assertNull(flag.getPermission());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals("'{player}' crafted '{recipename}' at {world}: {x}, {y}, {z}", flag.getMessage());
                assertEquals("ranks.admins", flag.getPermission());
            } else if (resultType == Material.STONE) {
                assertEquals("One", flag.getMessage());
            } else if (resultType == Material.COBBLESTONE) {
                assertEquals("   Two   ", flag.getMessage());
            } else if (resultType == Material.BRICK) {
                assertEquals("  '{player}' crafted '{recipename}' at {world}: {x}, {y}, {z}  ", flag.getMessage());
                assertEquals("ranks.admins", flag.getPermission());
            }
        }
    }
}