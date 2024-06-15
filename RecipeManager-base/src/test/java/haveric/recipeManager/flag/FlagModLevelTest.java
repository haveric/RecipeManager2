package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.FlagModLevel;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class FlagModLevelTest extends FlagBaseTest {

    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagModLevel/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagModLevel flag = (FlagModLevel) result.getFlag(FlagType.MOD_LEVEL);

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertEquals(1, flag.getAmount(), .1);
                    assertEquals('+', flag.getModifier());
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals(2, flag.getAmount(), .1);
                    assertEquals('-', flag.getModifier());
                    assertEquals("<red>You lost {amount} levels.", flag.getCraftMessage());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals(0, flag.getAmount(), .1);
                    assertEquals('=', flag.getModifier());
                    assertEquals("<red>You've been set to level 0!", flag.getCraftMessage());
                } else if (resultType == Material.GOLDEN_SWORD) {
                    assertEquals(2, flag.getAmount(), .1);
                    assertEquals('-', flag.getModifier());
                }
            }
        }
    }
}