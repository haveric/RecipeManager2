package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.any.FlagInventory;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlagInventoryTest extends FlagBaseTest {

    @Mock
    InventoryView custom;
    @Mock
    InventoryView second;
    @Mock
    InventoryView third;
    @Mock
    InventoryView workbench;

    @BeforeEach
    public void prepare() {
        when(custom.getType()).thenReturn(InventoryType.CRAFTING);
        when(custom.getTitle()).thenReturn("Custom");

        when(second.getType()).thenReturn(InventoryType.CRAFTING);
        when(second.getTitle()).thenReturn("Second");

        when(third.getType()).thenReturn(InventoryType.CRAFTING);
        when(third.getTitle()).thenReturn("  Third  ");

        when(workbench.getType()).thenReturn(InventoryType.WORKBENCH);
        when(workbench.getTitle()).thenReturn("Anything");
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagInventory/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(5, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

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
}
