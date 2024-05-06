package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.recipe.FlagFailMessage;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class FlagFailMessageTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagFailMessage/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagFailMessage flag = (FlagFailMessage) recipe.getFlag(FlagType.FAIL_MESSAGE);

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertEquals(RMCChatColor.COLOR_CHAR + "cYOU FAILED, MWaHahahah!", flag.getMessage());
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals("Test", flag.getMessage());
                } else if (resultType == Material.STONE) {
                    assertEquals("One", flag.getMessage());
                } else if (resultType == Material.COBBLESTONE) {
                    assertEquals("   Two   ", flag.getMessage());
                }
            }
        }
    }
}