package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaCharge;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagFireworkChargeItem;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagFireworkChargeItemTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagFireworkChargeItem/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagFireworkChargeItem flag = (FlagFireworkChargeItem) result.getFlag(FlagType.FIREWORK_CHARGE_ITEM);
            flag.onPrepare(a);

            TestMetaCharge meta = (TestMetaCharge) result.getItemMeta();
            String name = recipe.getName();

            if (name.equals("first")) {
                assertEquals(2, meta.getEffect().getColors().size());
                assertTrue(meta.getEffect().getColors().contains(Color.fromRGB(255, 0, 0)));
                assertTrue(meta.getEffect().getColors().contains(Color.fromRGB(0, 255, 0)));
                assertTrue(meta.getEffect().getFadeColors().isEmpty());

                assertTrue(meta.getEffect().hasTrail());
                assertFalse(meta.getEffect().hasFlicker());

                assertEquals(FireworkEffect.Type.BALL_LARGE, meta.getEffect().getType());
            } else if (name.equals("second") || name.equals("two")) {
                assertEquals(1, meta.getEffect().getColors().size());
                assertFalse(meta.getEffect().getColors().contains(Color.fromRGB(255, 0, 0)));
                assertTrue(meta.getEffect().getColors().contains(Color.fromRGB(0, 255, 0)));

                assertEquals(2, meta.getEffect().getFadeColors().size());
                assertTrue(meta.getEffect().getFadeColors().contains(Color.fromRGB(255, 0, 0)));
                assertTrue(meta.getEffect().getFadeColors().contains(Color.fromRGB(0, 255, 0)));

                assertFalse(meta.getEffect().hasTrail());
                assertTrue(meta.getEffect().hasFlicker());

                assertEquals(FireworkEffect.Type.CREEPER, meta.getEffect().getType());
            }
        }
    }
}