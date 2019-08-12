package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaSpawnEgg;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagSpawnEgg;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagSpawnEggTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagSpawnEgg/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();
            FlagSpawnEgg flag = (FlagSpawnEgg) result.getFlag(FlagType.SPAWN_EGG);
            flag.onPrepare(a);

            TestMetaSpawnEgg meta = (TestMetaSpawnEgg) result.getItemMeta();
            String name = recipe.getName();

            if (name.equals("one")) {
                assertEquals(EntityType.CREEPER, meta.getSpawnedType());
            } else if (name.equals("two") || name.equals("three")) {
                assertEquals(EntityType.HORSE, meta.getSpawnedType());
            }
        }
    }
}
