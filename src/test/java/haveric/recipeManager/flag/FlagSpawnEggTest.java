package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaSpawnEgg;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagSpawnEgg;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagSpawnEggTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagSpawnEgg/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

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
