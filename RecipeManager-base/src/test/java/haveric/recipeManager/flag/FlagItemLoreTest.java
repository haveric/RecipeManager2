package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagItemLore;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagItemLoreTest extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagItemLore/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            ItemResult result = recipe.getResult(a);

            FlagItemLore flag = (FlagItemLore) result.getFlag(FlagType.ITEM_LORE);
            flag.onPrepare(a);

            List<String> lores = result.getItemMeta().getLore();

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(lores.contains("One"));
                assertTrue(lores.contains("Two"));
                assertEquals(lores.size(), 2);
            } else if (resultType == Material.COBBLESTONE) {
                assertTrue(lores.contains("One"));
                assertTrue(lores.contains("   Two   "));
                assertEquals(lores.size(), 2);
            }
        }

        // TODO: Finish
    }
}
