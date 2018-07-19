package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagBiome;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagBiomeTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagBiome/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagBiome flag = (FlagBiome) result.getFlag(FlagType.BIOME);

            Material resultType = result.getType();
            Map<Biome, Boolean> biomes = flag.getBiomes();
            if (resultType == Material.DIRT) {
                assertEquals(1, biomes.size());
                assertTrue(biomes.containsKey(Biome.JUNGLE));
                assertTrue(biomes.get(Biome.JUNGLE));
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(2, biomes.size());
                assertTrue(biomes.containsKey(Biome.JUNGLE));
                assertTrue(biomes.get(Biome.JUNGLE));
                assertTrue(biomes.containsKey(Biome.JUNGLE_HILLS));
                assertTrue(biomes.get(Biome.JUNGLE_HILLS));
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(2, biomes.size());
                assertTrue(biomes.containsKey(Biome.MUSHROOM_FIELDS));
                assertFalse(biomes.get(Biome.MUSHROOM_FIELDS));
                assertTrue(biomes.containsKey(Biome.MUSHROOM_FIELD_SHORE));
                assertFalse(biomes.get(Biome.MUSHROOM_FIELD_SHORE));
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(2, biomes.size());
                assertTrue(biomes.containsKey(Biome.MUSHROOM_FIELDS));
                assertFalse(biomes.get(Biome.MUSHROOM_FIELDS));
                assertTrue(biomes.containsKey(Biome.MUSHROOM_FIELD_SHORE));
                assertFalse(biomes.get(Biome.MUSHROOM_FIELD_SHORE));
                assertFalse(biomes.containsKey(Biome.JUNGLE));
                assertFalse(biomes.containsKey(Biome.JUNGLE_HILLS));
            }
        }
    }
}
