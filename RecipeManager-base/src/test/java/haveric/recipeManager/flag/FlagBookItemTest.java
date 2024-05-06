package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaBook;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagBookItem;
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

public class FlagBookItemTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagBookItem/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

                FlagBookItem flag = (FlagBookItem) result.getFlag(FlagType.BOOK_ITEM);

                if (flag != null) {
                    flag.onPrepare(a);
                }

                Material resultType = result.getType();
                if (resultType == Material.DIRT) {
                    assertNull(flag);
                } else if (resultType == Material.WRITTEN_BOOK) {
                    TestMetaBook meta = (TestMetaBook) result.getItemMeta();
                    String name = recipe.getName();

                    switch (name) {
                        case "all":
                            assertFalse(a.hasReasons());
                            assertEquals("The Art of Stealing", meta.getTitle());
                            assertEquals("Gray Fox", meta.getAuthor());
                            assertEquals(3, meta.getPages().size());
                            break;
                        case "spaces":
                            assertEquals("The Art of Stealing", meta.getTitle());
                            assertEquals("Gray Fox", meta.getAuthor());
                            assertEquals(1, meta.getPages().size());
                            assertEquals("Once upon a time...", meta.getPages().get(0));
                            break;
                        case "quotes":
                            assertEquals("   The Art of Stealing   ", meta.getTitle());
                            assertEquals("   Gray Fox   ", meta.getAuthor());
                            assertEquals(1, meta.getPages().size());
                            assertEquals("   Once upon a time...   ", meta.getPages().get(0));
                            break;
                    }
                }
            }
        }
    }
}