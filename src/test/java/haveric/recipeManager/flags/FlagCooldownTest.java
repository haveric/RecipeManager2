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

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FlagCooldownTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagCooldown/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagCooldown flag = (FlagCooldown) result.getFlag(FlagType.COOLDOWN);

            Material resultType = result.getType();

            int cooldownTime = flag.getCooldownTime();
            String failMessage = flag.getFailMessage();
            String message = flag.getCraftMessage();
            if (resultType == Material.DIRT) {
                assertEquals(30, cooldownTime);
                assertNull(failMessage);
                assertNull(message);
                assertFalse(flag.isGlobal());
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(30, cooldownTime);
                assertNull(failMessage);
                assertNull(message);
                assertFalse(flag.isGlobal());
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(105, cooldownTime);
                assertEquals("<red>Usable in: {time}", failMessage);
                assertNull(message);
                assertFalse(flag.isGlobal());
            } else if (resultType == Material.GOLD_SWORD) {
                assertEquals(1800, cooldownTime);
                assertEquals("<red>Someone used this recently, wait: {time}", failMessage);
                assertEquals("<yellow>Cooldown time: {time}", message);
                assertTrue(flag.isGlobal());
            } else if (resultType == Material.DIAMOND_SWORD) {
                assertEquals(600, cooldownTime);
                assertNull(failMessage);
                assertNull(message);
                assertFalse(flag.isGlobal());
            }
        }
    }
}