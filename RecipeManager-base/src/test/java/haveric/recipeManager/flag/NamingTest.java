package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class NamingTest extends FlagBaseTest {
    @Test
    public void testNamingCraft() {
        File file = new File(baseRecipePath + "naming/namingCraft.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

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

    @Test
    public void testNamingCombine() {
        File file = new File(baseRecipePath + "naming/namingCombine.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();

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

    @Test
    public void testNamingSmelt() {
        File file = new File(baseRecipePath + "naming/namingSmelt.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe recipe = (RMFurnaceRecipe) entry.getKey();

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
