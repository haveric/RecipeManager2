package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.meta.FlagDisplayName;
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

public class FlagDisplayNameTest extends FlagBaseTest {

    //@Test TODO: Rewrite test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagItemName/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
                mockedBukkit.when(() -> Bukkit.getOfflinePlayer(testUUID)).thenReturn(player);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

                FlagDisplayName flag = (FlagDisplayName) result.getFlag(FlagType.DISPLAY_NAME);

                flag.onPrepare(a);

                Material resultType = result.getType();
                if (resultType == Material.STONE_SWORD) {
                    assertEquals("Weird Item", flag.getResultName());
                    assertEquals("Weird Item", result.getItemMeta().getDisplayName());
                } else if (resultType == Material.IRON_SWORD) {
                    assertEquals("{player}'s Sword", flag.getResultName());
                    assertEquals("TestPlayer's Sword", result.getItemMeta().getDisplayName());
                } else if (resultType == Material.GOLDEN_SWORD) {
                    assertEquals(RMCChatColor.COLOR_CHAR + "6 Gold", flag.getResultName());
                    assertEquals(RMCChatColor.COLOR_CHAR + "6 Gold", result.getItemMeta().getDisplayName());
                } else if (resultType == Material.DIAMOND_SWORD) {
                    assertEquals("Second", flag.getResultName());
                    assertEquals("Second", result.getItemMeta().getDisplayName());
                } else if (resultType == Material.COBBLESTONE) {
                    assertEquals("First", flag.getResultName());
                    assertEquals("First", result.getItemMeta().getDisplayName());
                } else if (resultType == Material.BRICK) {
                    assertEquals("   Second   ", flag.getResultName());
                    assertEquals("   Second   ", result.getItemMeta().getDisplayName());
                }
            }
        }
    }
}
