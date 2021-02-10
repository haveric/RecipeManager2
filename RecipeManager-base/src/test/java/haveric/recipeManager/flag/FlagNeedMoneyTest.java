package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.FlagNeedMoney;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class FlagNeedMoneyTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagNeedMoney/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagNeedMoney flag = (FlagNeedMoney) result.getFlag(FlagType.NEED_MONEY);

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertEquals(.25, flag.getMinMoney(), .01);
                    assertEquals(.25, flag.getMaxMoney(), .01);
                    assertFalse(flag.getSetBoth());
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals(.1, flag.getMinMoney(), .01);
                    assertEquals(1000, flag.getMaxMoney(), .01);
                    assertTrue(flag.getSetBoth());
                    assertEquals("<red>Need {money}!", flag.getFailMessage());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals(.25, flag.getMinMoney(), .01);
                    assertEquals(.25, flag.getMaxMoney(), .01);
                    assertFalse(flag.getSetBoth());
                }
            }
        }
    }
}