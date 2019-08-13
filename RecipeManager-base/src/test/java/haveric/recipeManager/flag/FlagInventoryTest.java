package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagInventory;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class FlagInventoryTest extends FlagBaseTest {

    InventoryView custom;
    InventoryView second;
    InventoryView third;
    InventoryView workbench;

    @Before
    public void prepare() {
        mockStatic(InventoryView.class);

        custom = mock(InventoryView.class);
        when(custom.getType()).thenReturn(InventoryType.CRAFTING);
        when(custom.getTitle()).thenReturn("Custom");

        second = mock(InventoryView.class);
        when(second.getType()).thenReturn(InventoryType.CRAFTING);
        when(second.getTitle()).thenReturn("Second");

        third = mock(InventoryView.class);
        when(third.getType()).thenReturn(InventoryType.CRAFTING);
        when(third.getTitle()).thenReturn("  Third  ");

        workbench = mock(InventoryView.class);
        when(workbench.getType()).thenReturn(InventoryType.WORKBENCH);
        when(workbench.getTitle()).thenReturn("Anything");
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagInventory/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

            FlagInventory flag = (FlagInventory) result.getFlag(FlagType.INVENTORY);

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));

                a.setInventoryView(custom);
                flag.onCheck(a);
                assertFalse(a.hasReasons());

                a.setInventoryView(workbench);
                flag.onCheck(a);
                assertTrue(a.hasReasons());
            } else if (resultType == Material.COBBLESTONE) {
                assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                assertTrue(flag.getInventories().contains(InventoryType.WORKBENCH));

                a.setInventoryView(custom);
                flag.onCheck(a);
                assertFalse(a.hasReasons());

                a.setInventoryView(workbench);
                flag.onCheck(a);
                assertFalse(a.hasReasons());
            } else if (resultType == Material.STONE) {
                assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                assertTrue(flag.getInventories().contains(InventoryType.WORKBENCH));
            } else if (resultType == Material.STONE_SWORD) {
                assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                assertTrue(flag.getAllowedTitles().contains("Custom"));
                assertTrue(flag.getUnallowedTitles().isEmpty());

                a.setInventoryView(custom);
                flag.onCheck(a);
                assertFalse(a.hasReasons());

                a.setInventoryView(second);
                flag.onCheck(a);
                assertTrue(a.hasReasons());

            } else if (resultType == Material.IRON_SWORD) {
                assertTrue(flag.getInventories().contains(InventoryType.CRAFTING));
                assertTrue(flag.getAllowedTitles().contains("Custom"));
                assertTrue(flag.getAllowedTitles().contains("Second"));
                assertTrue(flag.getUnallowedTitles().contains("  Third  "));

                a.setInventoryView(custom);
                flag.onCheck(a);
                assertFalse(a.hasReasons());

                a.setInventoryView(second);
                flag.onCheck(a);
                assertFalse(a.hasReasons());

                a.setInventoryView(third);
                flag.onCheck(a);
                assertTrue(a.hasReasons());

                a.setInventoryView(workbench);
                flag.onCheck(a);
                assertTrue(a.hasReasons());
            }
        }
    }
}
