package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.TestMessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.WorkbenchEvents;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class FlagKeepItemTest extends FlagBaseYamlTest {
    private TestCraftingInventory inventory;
    @Mock
    private CraftItemEvent craftEvent;
    private static WorkbenchEvents workbenchEvents;

    private ItemStack ironSword;
    private ItemStack goldSword;
    private ItemStack diamondSword;
    private ItemStack stoneSword;
    private ItemStack dirtStack;
    private ItemStack stoneStack;
    private ItemStack grassStack;

    @BeforeEach
    public void prepare() {
        try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
            mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

            settings.loadItemDatas(null, new File(baseDataPath), "item datas.yml");

            ironSword = new ItemStack(Material.IRON_SWORD);
            goldSword = new ItemStack(Material.GOLDEN_SWORD);
            diamondSword = new ItemStack(Material.DIAMOND_SWORD);
            stoneSword = new ItemStack(Material.STONE_SWORD);
            dirtStack = new ItemStack(Material.DIRT, 3);
            stoneStack = new ItemStack(Material.STONE, 3);
            grassStack = new ItemStack(Material.GRASS, 20);

            File booksDir = new File(workDir.getPath() + "/books/");
            booksDir.mkdirs();

            RecipeBooks.getInstance().init(booksDir);

            RecipeBooks.getInstance().reload(null);
        }

        inventory = new TestCraftingInventory();

        workbenchEvents = new WorkbenchEvents();


        PlayerInventory playerInventory = new TestPlayerInventory();

        Player player = mock(Player.class);
        when(player.hasPermission(Perms.FLAG_ALL)).thenReturn(true);
        when(player.getInventory()).thenReturn(playerInventory);

        InventoryView view = mock(InventoryView.class);
        when(view.getPlayer()).thenReturn(player);
        when(view.getTopInventory()).thenReturn(inventory);

        when(craftEvent.getInventory()).thenReturn(inventory);
        when(craftEvent.getView()).thenReturn(view);
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagKeepItem/");
        reloadRecipeProcessor(false, file);

        Map<BaseRecipe, RMCRecipeInfo> indexedRecipes = Recipes.getInstance().getIndex();

        assertEquals(3, indexedRecipes.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : indexedRecipes.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
                ItemResult result = recipe.getResults().get(0);
                Material resultType = result.getType();

                Recipe bukkitRecipe = recipe.getBukkitRecipe(false);
                when(craftEvent.getRecipe()).thenReturn(bukkitRecipe);

                try (MockedStatic<RecipeManager> mockedRecipeManager = mockStatic(RecipeManager.class)) {
                    mockedRecipeManager.when(RecipeManager::getSettings).thenReturn(settings);
                    mockedRecipeManager.when(RecipeManager::getRecipes).thenReturn(recipes);

                    try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
                        mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

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
                            workbenchEvents.craftFinish(craftEvent);
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
                            workbenchEvents.craftFinish(craftEvent);
                            assertNull(craftEvent.getCurrentItem());

                            ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                            int count = 0;
                            for (ItemStack item : shiftContents) {
                                if (item != null && item.getType() != Material.AIR) {
                                    count += item.getAmount();
                                }
                            }

                            assertEquals(2, count);
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
                            workbenchEvents.craftFinish(craftEvent);
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
        }
    }
}
