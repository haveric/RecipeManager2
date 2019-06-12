package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.Settings;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagItemName;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Settings.class, MessageSender.class, RecipeManager.class})
public class FlagItemNameTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagItemName/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).build();

            ItemResult result = recipe.getResult(a);

            FlagItemName flag = (FlagItemName) result.getFlag(FlagType.ITEM_NAME);
            flag.onPrepare(a);

            Material resultType = result.getType();
            if (resultType == Material.STONE_SWORD) {
                assertEquals(flag.getItemName(), "Weird Item");
                assertEquals(result.getItemMeta().getDisplayName(), "Weird Item");
            } else if (resultType == Material.IRON_SWORD) {
                assertEquals(flag.getItemName(), "{player}'s Sword");
                assertEquals(result.getItemMeta().getDisplayName(), "TestPlayer's Sword");
            } else if (resultType == Material.GOLDEN_SWORD) {
                assertEquals(flag.getItemName(), "<gold> Gold");
                assertEquals(result.getItemMeta().getDisplayName(), RMCChatColor.COLOR_CHAR + "6 Gold");
            } else if (resultType == Material.DIAMOND_SWORD) {
                assertEquals(flag.getItemName(), "Second");
                assertEquals(result.getItemMeta().getDisplayName(), "Second");
            } else if (resultType == Material.COBBLESTONE) {
                assertEquals(flag.getItemName(), "First");
                assertEquals(result.getItemMeta().getDisplayName(), "First");
            } else if (resultType == Material.BRICK) {
                assertEquals(flag.getItemName(), "   Second   ");
                assertEquals(result.getItemMeta().getDisplayName(), "   Second   ");
            }
        }
    }
}
