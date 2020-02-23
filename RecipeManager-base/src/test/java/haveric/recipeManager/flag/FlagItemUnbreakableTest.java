package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagItemUnbreakable;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

public class FlagItemUnbreakableTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagItemUnbreakable/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();
        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

            FlagItemUnbreakable flag = (FlagItemUnbreakable) result.getFlag(FlagType.ITEM_UNBREAKABLE);
            flag.onPrepare(a);

            Material resultType = result.getType();
            if (resultType == Material.STONE_SWORD) {
                assertTrue(flag.isUnbreakable());
                assertTrue(result.getItemMeta().isUnbreakable());
            } else if (resultType == Material.IRON_SWORD) {
                assertFalse(flag.isUnbreakable());
                assertFalse(result.getItemMeta().isUnbreakable());
            }
        }
    }
}
