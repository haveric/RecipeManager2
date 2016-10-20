package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagCommand;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlagCommandTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagCommand/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagCommand flag = (FlagCommand) result.getFlag(FlagType.COMMAND);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("/say I crafted {result}!", flag.getCommands().get(0));
            } else if (resultType == Material.STONE_SWORD) {
                assertEquals(1, flag.getCommands().size());
                assertEquals("kick {player}", flag.getCommands().get(0));
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(2, flag.getCommands().size());
                assertEquals("/say I crafted {result}!", flag.getCommands().get(0));
                assertEquals("kick {player}", flag.getCommands().get(1));
            }
        }
    }
}