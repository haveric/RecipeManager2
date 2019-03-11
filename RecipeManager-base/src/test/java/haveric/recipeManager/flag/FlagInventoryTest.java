package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagInventory;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import junit.framework.Assert;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class FlagInventoryTest extends FlagBaseTest {

    Inventory custom;
    Inventory second;
    Inventory third;
    Inventory workbench;

    @Before
    public void prepare() {
        mockStatic(Inventory.class);

        custom = mock(Inventory.class);
        when(custom.getType()).thenReturn(InventoryType.CRAFTING);
        when(custom.getTitle()).thenReturn("Custom");

        second = mock(Inventory.class);
        when(second.getType()).thenReturn(InventoryType.CRAFTING);
        when(second.getTitle()).thenReturn("Second");

        third = mock(Inventory.class);
        when(third.getType()).thenReturn(InventoryType.CRAFTING);
        when(third.getTitle()).thenReturn("  Third  ");

        workbench = mock(Inventory.class);
        when(workbench.getType()).thenReturn(InventoryType.WORKBENCH);
        when(workbench.getTitle()).thenReturn("Anything");
    }

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagInventory/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();

            a.setPlayerUUID(testUUID);

            ItemResult result = recipe.getResult(a);

            FlagInventory flag = (FlagInventory) result.getFlag(FlagType.INVENTORY);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                Assert.assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));

                a.setInventory(custom);
                flag.onCheck(a);
                Assert.assertFalse(a.hasReasons());

                a.setInventory(workbench);
                flag.onCheck(a);
                Assert.assertTrue(a.hasReasons());
            } else if (resultType == Material.COBBLESTONE) {
                Assert.assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                Assert.assertTrue(flag.getInventories().contains(InventoryType.WORKBENCH));

                a.setInventory(custom);
                flag.onCheck(a);
                Assert.assertFalse(a.hasReasons());

                a.setInventory(workbench);
                flag.onCheck(a);
                Assert.assertFalse(a.hasReasons());
            } else if (resultType == Material.STONE) {
                Assert.assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                Assert.assertTrue(flag.getInventories().contains(InventoryType.WORKBENCH));
            } else if (resultType == Material.STONE_SWORD) {
                Assert.assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                Assert.assertTrue(flag.getAllowedTitles().contains("Custom"));
                Assert.assertTrue(flag.getUnallowedTitles().isEmpty());

                a.setInventory(custom);
                flag.onCheck(a);
                Assert.assertFalse(a.hasReasons());

                a.setInventory(second);
                flag.onCheck(a);
                Assert.assertTrue(a.hasReasons());

            } else if (resultType == Material.IRON_SWORD) {
                Assert.assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                Assert.assertTrue(flag.getAllowedTitles().contains("Custom"));
                Assert.assertTrue(flag.getAllowedTitles().contains("Second"));
                Assert.assertTrue(flag.getUnallowedTitles().contains("  Third  "));

                a.setInventory(custom);
                flag.onCheck(a);
                Assert.assertFalse(a.hasReasons());

                a.setInventory(second);
                flag.onCheck(a);
                Assert.assertFalse(a.hasReasons());

                a.setInventory(third);
                flag.onCheck(a);
                Assert.assertTrue(a.hasReasons());

                a.setInventory(workbench);
                flag.onCheck(a);
                Assert.assertTrue(a.hasReasons());
            }
        }
    }
}
