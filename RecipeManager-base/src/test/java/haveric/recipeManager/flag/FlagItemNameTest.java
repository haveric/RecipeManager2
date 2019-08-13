package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.Settings;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagItemName;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
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
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

            FlagItemName flag = (FlagItemName) result.getFlag(FlagType.ITEM_NAME);
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
