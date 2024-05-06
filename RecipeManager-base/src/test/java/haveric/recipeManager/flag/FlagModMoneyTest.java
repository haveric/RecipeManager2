package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.FlagModMoney;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class FlagModMoneyTest extends FlagBaseTest {

    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagModMoney/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagModMoney flag = (FlagModMoney) result.getFlag(FlagType.MOD_MONEY);

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertEquals(.5, flag.getAmount(), .001);
                    assertEquals('+', flag.getModifier());
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals(2.5, flag.getAmount(), .001);
                    assertEquals('-', flag.getModifier());
                    assertEquals("<red>You lost {money}!", flag.getFailMessage());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals(0, flag.getAmount(), .001);
                    assertEquals('=', flag.getModifier());
                    assertEquals("<red>You lost all your money!", flag.getFailMessage());
                } else if (resultType == Material.GOLDEN_SWORD) {
                    assertEquals(2.5, flag.getAmount(), .001);
                    assertEquals('-', flag.getModifier());
                }
            }
        }
    }
}