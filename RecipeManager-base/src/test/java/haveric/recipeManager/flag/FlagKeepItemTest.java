package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class FlagKeepItemTest extends FlagBaseTest {
    private File booksDir;

    private TestCraftingInventory inventory;
    private CraftItemEvent craftEvent;
    private static Events events;

    private Recipe bukkitRecipe;

    private ItemStack ironSword;
    private ItemStack goldSword;
    private ItemStack diamondSword;
    private ItemStack stoneSword;
    private ItemStack dirtStack;
    private ItemStack stoneStack;
    private ItemStack grassStack;

    @Before
    public void prepare() {
        when(Settings.getCustomData(Material.STONE_SWORD)).thenReturn((short)132);

        ironSword = new ItemStack(Material.IRON_SWORD);
        goldSword = new ItemStack(Material.GOLDEN_SWORD);
        diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        stoneSword = new ItemStack(Material.STONE_SWORD);
        dirtStack = new ItemStack(Material.DIRT, 3);
        stoneStack = new ItemStack(Material.STONE, 3);
        grassStack = new ItemStack(Material.GRASS, 20);

        booksDir = new File(workDir.getPath() + "/books/");
        booksDir.mkdirs();

        RecipeBooks.getInstance().init(booksDir);
        RecipeBooks.getInstance().reload(null);

        mockStatic(Inventory.class);

        inventory = new TestCraftingInventory();

        events = new Events();


        PlayerInventory playerInventory = new TestPlayerInventory();

        Player player = mock(Player.class);
        when(player.hasPermission(Perms.FLAG_ALL)).thenReturn(true);
        when(player.getInventory()).thenReturn(playerInventory);

        InventoryView view = mock(InventoryView.class);
        when(view.getPlayer()).thenReturn(player);
        when(view.getTopInventory()).thenReturn(inventory);

        craftEvent = mock(CraftItemEvent.class);
        when(craftEvent.getInventory()).thenReturn(inventory);
        when(craftEvent.getView()).thenReturn(view);
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagKeepItem/");
        RecipeProcessor.reload(null, false, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> indexedRecipes = Recipes.getInstance().getIndex();

        assertEquals(3, indexedRecipes.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : indexedRecipes.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            bukkitRecipe = recipe.getBukkitRecipe(false);
            when(craftEvent.getRecipe()).thenReturn(bukkitRecipe);

            if (resultType == Material.IRON_SWORD) {
                ItemStack[] matrix = new ItemStack[3];
                matrix[0] = ironSword.clone();
                matrix[1] = stoneSword.clone();
                matrix[2] = dirtStack.clone();

                inventory.setMatrix(matrix);
                inventory.setResult(ironSword.clone());

                // Switch to shift click
                craftEvent.getView().getPlayer().getInventory().clear();
                when(craftEvent.isShiftClick()).thenReturn(true);
                events.craftFinish(craftEvent);
                assertNull(craftEvent.getCurrentItem());

                ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                    }
                }

                assertEquals(3, count);
            } else if (resultType == Material.GOLDEN_SWORD) {
                ItemStack[] matrix = new ItemStack[3];
                matrix[0] = goldSword.clone();
                matrix[1] = stoneSword.clone();
                matrix[2] = stoneStack.clone();

                inventory.setMatrix(matrix);
                inventory.setResult(goldSword.clone());

                // Switch to shift click
                craftEvent.getView().getPlayer().getInventory().clear();
                when(craftEvent.isShiftClick()).thenReturn(true);
                events.craftFinish(craftEvent);
                assertNull(craftEvent.getCurrentItem());

                ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                    }
                }

                assertEquals(1, count);
            } else if (resultType == Material.DIAMOND_SWORD) {
                ItemStack[] matrix = new ItemStack[3];
                matrix[0] = diamondSword.clone();
                matrix[1] = stoneSword.clone();
                matrix[2] = grassStack.clone();

                inventory.setMatrix(matrix);
                inventory.setResult(diamondSword.clone());

                // Switch to shift click
                craftEvent.getView().getPlayer().getInventory().clear();
                when(craftEvent.isShiftClick()).thenReturn(true);
                events.craftFinish(craftEvent);
                assertNull(craftEvent.getCurrentItem());

                ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                    }
                }

                assertEquals(3, count);
            }
        }
    }
}
