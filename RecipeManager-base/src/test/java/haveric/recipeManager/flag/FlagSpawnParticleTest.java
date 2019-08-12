package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.flags.any.FlagSpawnParticle;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Particle;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagSpawnParticleTest extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagSpawnParticle/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(10, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            FlagSpawnParticle flag = (FlagSpawnParticle) result.getFlag(FlagType.SPAWN_PARTICLE);

            String name = recipe.getName();

            List<RMParticle> particles = flag.getParticles();

            switch (name) {
                case "first":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(3, particles.get(0).getCount());

                    assertEquals(0, particles.get(0).getDelay());
                    assertEquals(Double.NaN, particles.get(0).getExtra(), .001);
                    break;
                case "second":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(40, particles.get(0).getDelay());


                    assertEquals(1, particles.get(0).getCount());
                    assertEquals(Double.NaN, particles.get(0).getExtra(), .001);
                    break;
                case "third":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(3, particles.get(0).getExtra(), .001);

                    assertEquals(0, particles.get(0).getDelay());
                    assertEquals(1, particles.get(0).getCount());

                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetZ(), .001);

                    assertEquals(RMParticle.DEFAULT_OFFSET_X, particles.get(0).getOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Y, particles.get(0).getOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Z, particles.get(0).getOffsetZ(), .001);
                    break;
                case "offset":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(20, particles.get(0).getCount());
                    assertEquals(80, particles.get(0).getDelay());

                    assertEquals(-1, particles.get(0).getRandomOffsetX(), .001);
                    assertEquals(2, particles.get(0).getRandomOffsetY(), .001);
                    assertEquals(1, particles.get(0).getRandomOffsetZ(), .001);

                    assertEquals(-2, particles.get(0).getOffsetX(), .001);
                    assertEquals(3, particles.get(0).getOffsetY(), .001);
                    assertEquals(2, particles.get(0).getOffsetZ(), .001);
                    break;
                case "offset2":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(20, particles.get(0).getCount());
                    assertEquals(80, particles.get(0).getDelay());

                    assertEquals(-1, particles.get(0).getRandomOffsetX(), .001);
                    assertEquals(2, particles.get(0).getRandomOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetZ(), .001);

                    assertEquals(-2, particles.get(0).getOffsetX(), .001);
                    assertEquals(3, particles.get(0).getOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Z, particles.get(0).getOffsetZ(), .001);
                    break;
                case "offset3":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(20, particles.get(0).getCount());
                    assertEquals(80, particles.get(0).getDelay());

                    assertEquals(-1, particles.get(0).getRandomOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetZ(), .001);

                    assertEquals(-2, particles.get(0).getOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Y, particles.get(0).getOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Z, particles.get(0).getOffsetZ(), .001);
                    break;
                case "repeat":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());

                    assertEquals(5, particles.get(0).getRepeatTimes());
                    assertEquals(40, particles.get(0).getRepeatDelay());
                    break;
                case "repeat2":
                    assertEquals(1, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());

                    assertEquals(7, particles.get(0).getRepeatTimes());
                    assertEquals(20, particles.get(0).getRepeatDelay());
                    break;
                case "multiple":
                    assertEquals(3, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(Particle.HEART, particles.get(1).getParticle());
                    assertEquals(Particle.HEART, particles.get(2).getParticle());

                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(0).getRandomOffsetZ(), .001);

                    assertEquals(RMParticle.DEFAULT_OFFSET_X, particles.get(0).getOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Y, particles.get(0).getOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Z, particles.get(0).getOffsetZ(), .001);

                    assertEquals(3, particles.get(0).getCount());
                    assertEquals(0, particles.get(0).getDelay());

                    assertEquals(5, particles.get(1).getCount());
                    assertEquals(40, particles.get(1).getDelay());

                    assertEquals(20, particles.get(2).getCount());
                    assertEquals(80, particles.get(2).getDelay());

                    assertEquals(-1, particles.get(2).getRandomOffsetX(), .001);
                    assertEquals(2, particles.get(2).getRandomOffsetY(), .001);
                    assertEquals(1, particles.get(2).getRandomOffsetZ(), .001);

                    assertEquals(-2, particles.get(2).getOffsetX(), .001);
                    assertEquals(3, particles.get(2).getOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Z, particles.get(2).getOffsetZ(), .001);
                    break;
                case "multiple2":
                    assertEquals(4, particles.size());
                    assertEquals(Particle.HEART, particles.get(0).getParticle());
                    assertEquals(Particle.SMOKE_NORMAL, particles.get(1).getParticle());
                    assertEquals(Particle.LAVA, particles.get(2).getParticle());
                    assertEquals(Particle.SMOKE_LARGE, particles.get(3).getParticle());

                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(3).getRandomOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(3).getRandomOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_RANDOM_OFFSET, particles.get(3).getRandomOffsetZ(), .001);

                    assertEquals(RMParticle.DEFAULT_OFFSET_X, particles.get(3).getOffsetX(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Y, particles.get(3).getOffsetY(), .001);
                    assertEquals(RMParticle.DEFAULT_OFFSET_Z, particles.get(3).getOffsetZ(), .001);
                    break;
            }
        }
    }
}
