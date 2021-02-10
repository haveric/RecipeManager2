package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.FlagBiome;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class FlagBiomeTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagBiome/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

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
}
