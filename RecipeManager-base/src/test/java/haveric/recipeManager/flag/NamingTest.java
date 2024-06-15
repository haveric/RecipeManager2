package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.combine.CombineRecipe1_13;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe1_13;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class NamingTest extends FlagBaseTest {
    //@Test TODO: Rewrite test
    public void testNamingCraft() {
        File file = new File(baseRecipePath + "naming/namingCraft.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                String name = recipe.getName();

                Material resultType = result.getType();
                if (resultType == Material.STONE_SWORD) {
                    assertTrue(recipe.hasCustomName());
                    assertEquals("Boomstick", name);
                } else if (resultType == Material.IRON_SWORD) {
                    assertFalse(recipe.hasCustomName());
                }
            }
        }
    }

    //@Test TODO: Rewrite test
    public void testNamingCombine() {
        File file = new File(baseRecipePath + "naming/namingCombine.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe1_13 recipe = (CombineRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                String name = recipe.getName();

                Material resultType = result.getType();
                if (resultType == Material.STONE_SWORD) {
                    assertTrue(recipe.hasCustomName());
                    assertEquals("Random Stuff", name);
                } else if (resultType == Material.IRON_SWORD) {
                    assertFalse(recipe.hasCustomName());
                }
            }
        }
    }

    //@Test TODO: Rewrite test
    public void testNamingSmelt() {
        File file = new File(baseRecipePath + "naming/namingSmelt.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe1_13 recipe = (RMFurnaceRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                mockedBukkit.when(() -> Bukkit.getOfflinePlayer(testUUID)).thenReturn(player);
                Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

                ItemResult result = recipe.getResult(a);

                String name = recipe.getName();

                Material resultType = result.getType();
                if (resultType == Material.STONE_SWORD) {
                    assertTrue(recipe.hasCustomName());
                    assertEquals("Diamond Sword", name);
                } else if (resultType == Material.IRON_SWORD) {
                    assertFalse(recipe.hasCustomName());
                }
            }
        }
    }
}
