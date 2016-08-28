package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaBanner;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagBannerItemTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagBannerItem/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagBannerItem flag = (FlagBannerItem) result.getFlag(FlagType.BANNER_ITEM);
            flag.onPrepare(a);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(a.hasReasons());
            } else {
                TestMetaBanner meta = (TestMetaBanner) result.getItemMeta();
                String name = recipe.getName();
                assertFalse(a.hasReasons());

                if (name.equals("base")) {
                    assertEquals(DyeColor.BLACK, meta.getBaseColor());
                    assertTrue(meta.getPatterns().isEmpty());
                } else if (name.equals("one")) {
                    assertEquals(DyeColor.RED, meta.getBaseColor());
                    assertEquals(1, meta.numberOfPatterns());
                    assertEquals(PatternType.CIRCLE_MIDDLE, meta.getPattern(0).getPattern());
                    assertEquals(DyeColor.BLUE, meta.getPattern(0).getColor());
                } else if (name.equals("two")) {
                    assertEquals(DyeColor.RED, meta.getBaseColor());
                    assertEquals(2, meta.numberOfPatterns());
                    assertEquals(PatternType.CIRCLE_MIDDLE, meta.getPattern(0).getPattern());
                    assertEquals(DyeColor.BLUE, meta.getPattern(0).getColor());
                    assertEquals(PatternType.SKULL, meta.getPattern(1).getPattern());
                    assertEquals(DyeColor.YELLOW, meta.getPattern(1).getColor());
                } else if (name.equals("override")) {
                    assertEquals(DyeColor.GREEN, meta.getBaseColor());
                    assertEquals(2, meta.numberOfPatterns());
                    assertEquals(PatternType.HALF_HORIZONTAL, meta.getPattern(0).getPattern());
                    assertEquals(DyeColor.YELLOW, meta.getPattern(0).getColor());
                    assertEquals(PatternType.CIRCLE_MIDDLE, meta.getPattern(1).getPattern());
                    assertEquals(DyeColor.ORANGE, meta.getPattern(1).getColor());
                }
            }
        }
    }
}
