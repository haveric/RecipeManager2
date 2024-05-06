package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.flags.any.FlagMessage;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class FlagMessageTest extends FlagBaseTest {

    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagMessage/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                FlagMessage flag = (FlagMessage) result.getFlag(FlagType.MESSAGE);

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertEquals(1, flag.getMessages().size());
                    assertTrue(flag.getMessages().contains("<green>Good job!"));
                } else if (resultType == Material.STONE_SWORD) {
                    assertEquals(2, flag.getMessages().size());
                    assertTrue(flag.getMessages().contains("<green>Good job!"));
                    assertTrue(flag.getMessages().contains("<gray>Now you can die&c happy<gray> that you crafted that."));
                } else if (resultType == Material.COBBLESTONE) {
                    assertEquals(1, flag.getMessages().size());
                    assertTrue(flag.getMessages().contains("<green>Good job!"));
                } else if (resultType == Material.STONE) {
                    assertEquals(1, flag.getMessages().size());
                    assertTrue(flag.getMessages().contains("   <green>Good job!   "));
                }
            }
        }
    }
}