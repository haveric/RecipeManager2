package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaFirework;
import haveric.recipeManager.flag.flags.any.FlagLaunchFirework;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

public class FlagLaunchFireworkTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagLaunchFirework/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            FlagLaunchFirework flag = (FlagLaunchFirework) result.getFlag(FlagType.LAUNCH_FIREWORK);

            TestMetaFirework meta = (TestMetaFirework) flag.getFirework();
            String name = recipe.getName();

            switch (name) {
                case "first":
                    assertEquals(1, meta.getEffectsSize());
                    assertEquals(1, meta.getEffects().get(0).getColors().size());
                    assertTrue(meta.getEffects().get(0).getColors().contains(Color.fromRGB(0, 255, 0)));
                    assertEquals(0, meta.getEffects().get(0).getFadeColors().size());

                    assertEquals(FireworkEffect.Type.BALL, meta.getEffects().get(0).getType());
                    assertFalse(meta.getEffects().get(0).hasTrail());
                    assertFalse(meta.getEffects().get(0).hasFlicker());

                    assertEquals(2, meta.getPower());
                    break;
                case "second":
                    assertEquals(1, meta.getEffectsSize());
                    assertEquals(1, meta.getEffects().get(0).getColors().size());
                    assertTrue(meta.getEffects().get(0).getColors().contains(Color.fromRGB(255, 0, 0)));
                    assertEquals(0, meta.getEffects().get(0).getFadeColors().size());

                    assertEquals(FireworkEffect.Type.BURST, meta.getEffects().get(0).getType());
                    assertTrue(meta.getEffects().get(0).hasTrail());
                    assertFalse(meta.getEffects().get(0).hasFlicker());

                    assertEquals(2, meta.getPower());
                    break;
                case "third":
                    assertEquals(1, meta.getEffectsSize());
                    assertEquals(3, meta.getEffects().get(0).getColors().size());
                    assertTrue(meta.getEffects().get(0).getColors().contains(Color.fromRGB(255, 0, 200)));
                    assertTrue(meta.getEffects().get(0).getColors().contains(Color.fromRGB(0, 255, 0)));
                    assertTrue(meta.getEffects().get(0).getColors().contains(Color.fromRGB(255, 128, 0)));

                    assertEquals(3, meta.getEffects().get(0).getFadeColors().size());
                    assertTrue(meta.getEffects().get(0).getFadeColors().contains(Color.fromRGB(255, 0, 0)));
                    assertTrue(meta.getEffects().get(0).getFadeColors().contains(Color.fromRGB(0, 0, 255)));
                    assertTrue(meta.getEffects().get(0).getFadeColors().contains(Color.fromRGB(0, 255, 0)));

                    assertEquals(FireworkEffect.Type.BALL_LARGE, meta.getEffects().get(0).getType());
                    assertTrue(meta.getEffects().get(0).hasTrail());
                    assertFalse(meta.getEffects().get(0).hasFlicker());

                    assertEquals(2, meta.getPower());
                    break;
                case "power":
                    assertEquals(0, meta.getEffectsSize());

                    assertEquals(1, meta.getPower());
                    break;
                case "multiple":
                    assertEquals(3, meta.getEffectsSize());

                    // ONE
                    assertEquals(1, meta.getEffects().get(0).getColors().size());
                    assertTrue(meta.getEffects().get(0).getColors().contains(Color.fromRGB(0, 255, 0)));
                    assertEquals(0, meta.getEffects().get(0).getFadeColors().size());

                    assertEquals(FireworkEffect.Type.BALL, meta.getEffects().get(0).getType());
                    assertFalse(meta.getEffects().get(0).hasTrail());
                    assertFalse(meta.getEffects().get(0).hasFlicker());
                    // END ONE

                    // TWO
                    assertEquals(1, meta.getEffects().get(1).getColors().size());
                    assertTrue(meta.getEffects().get(1).getColors().contains(Color.fromRGB(255, 0, 0)));
                    assertEquals(0, meta.getEffects().get(1).getFadeColors().size());

                    assertEquals(FireworkEffect.Type.BURST, meta.getEffects().get(1).getType());
                    assertTrue(meta.getEffects().get(1).hasTrail());
                    assertFalse(meta.getEffects().get(1).hasFlicker());
                    // END TWO

                    // THREE
                    assertEquals(3, meta.getEffects().get(2).getColors().size());
                    assertTrue(meta.getEffects().get(2).getColors().contains(Color.fromRGB(255, 0, 200)));
                    assertTrue(meta.getEffects().get(2).getColors().contains(Color.fromRGB(0, 255, 0)));
                    assertTrue(meta.getEffects().get(2).getColors().contains(Color.fromRGB(255, 128, 0)));

                    assertEquals(3, meta.getEffects().get(2).getFadeColors().size());
                    assertTrue(meta.getEffects().get(2).getFadeColors().contains(Color.fromRGB(255, 0, 0)));
                    assertTrue(meta.getEffects().get(2).getFadeColors().contains(Color.fromRGB(0, 0, 255)));
                    assertTrue(meta.getEffects().get(2).getFadeColors().contains(Color.fromRGB(0, 255, 0)));

                    assertEquals(FireworkEffect.Type.BALL_LARGE, meta.getEffects().get(2).getType());
                    assertTrue(meta.getEffects().get(2).hasTrail());
                    assertFalse(meta.getEffects().get(2).hasFlicker());
                    // END THREE

                    assertEquals(1, meta.getPower());
                    break;
            }
        }
    }
}