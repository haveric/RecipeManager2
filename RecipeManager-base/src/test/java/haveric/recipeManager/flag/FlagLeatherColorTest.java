package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaLeatherArmor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagLeatherColor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Color;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagLeatherColorTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagLeatherColor/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

            FlagLeatherColor flag = (FlagLeatherColor) result.getFlag(FlagType.LEATHER_COLOR);
            flag.onPrepare(a);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                TestMetaLeatherArmor meta = (TestMetaLeatherArmor) result.getItemMeta();
                assertEquals(Color.fromRGB(255,100,50), meta.getColor());
            } else if (resultType == Material.STONE_SWORD) {
                TestMetaLeatherArmor meta = (TestMetaLeatherArmor) result.getItemMeta();
                assertEquals(Color.fromRGB(255,255,255), meta.getColor());
            }
        }
    }
}