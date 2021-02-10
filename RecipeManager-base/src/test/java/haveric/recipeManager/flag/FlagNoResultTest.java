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
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class FlagNoResultTest extends FlagBaseTest {
    private CraftItemEvent craftEvent;
    private CraftItemEvent shiftCraftEvent;
    private static WorkbenchEvents workbenchEvents;

    @BeforeEach
    public void prepare() {
        File booksDir = new File(workDir.getPath() + "/books/");
        booksDir.mkdirs();

        RecipeBooks.getInstance().init(booksDir);
        try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
            mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

            RecipeBooks.getInstance().reload(null);
        }

        TestCraftingInventory inventory = new TestCraftingInventory();

        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

            ItemStack dirt = new ItemStack(Material.DIRT);
            ItemStack stone = new ItemStack(Material.STONE);
            ItemMeta stoneMeta = stone.getItemMeta();
            stoneMeta.setDisplayName("Test");
            stone.setItemMeta(stoneMeta);
            ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
            ItemStack air = new ItemStack(Material.AIR);

            ItemStack[] matrix = new ItemStack[10];
            matrix[0] = stoneSword;
            matrix[1] = dirt;
            matrix[2] = stone;
            for (int i = 3; i < 10; i++) {
                matrix[i] = air;
            }

            inventory.setMatrix(matrix);
            inventory.setResult(stoneSword);

            TestCraftingInventory shiftInventory = new TestCraftingInventory();
            ItemStack dirtStack = new ItemStack(Material.DIRT, 2);
            ItemStack stoneStack = new ItemStack(Material.STONE, 3);
            ItemMeta stoneStackMeta = stoneStack.getItemMeta();
            stoneStackMeta.setDisplayName("Test");
            stoneStack.setItemMeta(stoneStackMeta);

            ItemStack[] matrixStack = new ItemStack[10];
            matrixStack[0] = stoneSword;
            matrixStack[1] = dirtStack;
            matrixStack[2] = stoneStack;
            for (int i = 3; i < 10; i++) {
                matrixStack[i] = air;
            }

            shiftInventory.setMatrix(matrixStack);
            shiftInventory.setResult(stoneSword);

            workbenchEvents = new WorkbenchEvents();


            PlayerInventory playerInventory = new TestPlayerInventory();

            Player player = mock(Player.class);
            when(player.hasPermission(Perms.FLAG_ALL)).thenReturn(true);
            when(player.getInventory()).thenReturn(playerInventory);

            InventoryView view = mock(InventoryView.class);
            when(view.getPlayer()).thenReturn(player);

            craftEvent = mock(CraftItemEvent.class);
            when(craftEvent.isShiftClick()).thenReturn(false);
            when(craftEvent.getInventory()).thenReturn(inventory);
            when(craftEvent.getView()).thenReturn(view);

            shiftCraftEvent = mock(CraftItemEvent.class);
            when(shiftCraftEvent.isShiftClick()).thenReturn(true);
            when(shiftCraftEvent.getInventory()).thenReturn(shiftInventory);
            when(shiftCraftEvent.getView()).thenReturn(view);
        }
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagNoResult/");
        reloadRecipeProcessor(false, file);


        Map<BaseRecipe, RMCRecipeInfo> indexedRecipes = Recipes.getInstance().getIndex();

        assertEquals(1, indexedRecipes.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : indexedRecipes.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getResults().get(0);
                Material resultType = result.getType();

                Recipe bukkitRecipe = recipe.getBukkitRecipe(false);
                when(craftEvent.getRecipe()).thenReturn(bukkitRecipe);
                when(shiftCraftEvent.getRecipe()).thenReturn(bukkitRecipe);

                //Args a = ArgBuilder.create().build();

                if (resultType == Material.STONE_SWORD) {
                    //a = ArgBuilder.create().recipe(recipe).player(testUUID).inventory(inventory).build();

                    workbenchEvents.craftFinish(craftEvent);
                    assertNull(craftEvent.getCurrentItem());
                    ItemStack[] contents = craftEvent.getView().getPlayer().getInventory().getContents();

                    int count = 0;
                    for (ItemStack item : contents) {
                        if (item != null && item.getType() != Material.AIR) {
                            count += item.getAmount();
                        }
                    }
                    assertEquals(0, count);

                    workbenchEvents.craftFinish(shiftCraftEvent);
                    assertNull(shiftCraftEvent.getCurrentItem());
                    ItemStack[] shiftContents = shiftCraftEvent.getView().getPlayer().getInventory().getContents();

                    count = 0;
                    for (ItemStack item : shiftContents) {
                        if (item != null && item.getType() != Material.AIR) {
                            count += item.getAmount();
                        }
                    }

                    assertEquals(0, count);
                }
            }
        }
    }
}
