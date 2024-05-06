package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.FlagNeedExp;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class FlagNeedExpTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagNeedExp/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagNeedExp flag = (FlagNeedExp) result.getFlag(FlagType.NEED_EXP);

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertEquals(100, flag.getMinExp());
                    assertEquals(100, flag.getMaxExp());
                    assertFalse(flag.getSetBoth());
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals(250, flag.getMinExp());
                    assertEquals(250, flag.getMaxExp());
                    assertTrue(flag.getSetBoth());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals(0, flag.getMinExp());
                    assertEquals(500, flag.getMaxExp());
                    assertTrue(flag.getSetBoth());
                } else if (resultType == Material.GOLDEN_SWORD) {
                    assertEquals(1000, flag.getMinExp());
                    assertEquals(1000, flag.getMaxExp());
                    assertFalse(flag.getSetBoth());
                    assertEquals("<red>Need {exp} exp!", flag.getFailMessage());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertEquals(1000, flag.getMinExp());
                    assertEquals(1000, flag.getMaxExp());
                    assertFalse(flag.getSetBoth());
                }
            }
        }
    }
}