package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagItemLore;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class FlagItemLoreTest extends FlagBaseTest {
    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagItemLore/flagItemLore.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).build();
            ItemResult result = recipe.getResult(a);

            FlagItemLore flag = (FlagItemLore) result.getFlag(FlagType.ITEM_LORE);
            flag.onPrepare(a);

            List<String> lores = result.getItemMeta().getLore();

            Material resultType = result.getType();
            if (resultType == Material.DIRT) {
                assertTrue(lores.contains("One"));
                assertTrue(lores.contains("Two"));
                assertEquals(lores.size(), 2);
            } else if (resultType == Material.COBBLESTONE) {
                assertTrue(lores.contains("One"));
                assertTrue(lores.contains("   Two   "));
                assertEquals(lores.size(), 2);
            }
        }

        // TODO: Finish
    }

    @Test
    public void testShiftClick() {
        Recipe bukkitRecipe;
        // Prep
        File booksDir = new File(workDir.getPath() + "/books/");
        booksDir.mkdirs();

        RecipeBooks.getInstance().init(booksDir);
        RecipeBooks.getInstance().reload(null);

        mockStatic(Inventory.class);



        ItemStack air = new ItemStack(Material.AIR);
        ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
        ItemStack dirt = new ItemStack(Material.DIRT);
        ItemStack stone = new ItemStack(Material.STONE);
        ItemStack dirtStack = new ItemStack(Material.DIRT, 3);
        ItemStack stoneStack = new ItemStack(Material.STONE, 4);

        ItemStack[] matrix = new ItemStack[10];
        matrix[0] = stoneSword;
        matrix[1] = dirt;
        matrix[2] = stone;
        for (int i = 3; i < 10; i++) {
            matrix[i] = air;
        }
        TestCraftingInventory inventory = new TestCraftingInventory();
        inventory.setMatrix(matrix);
        inventory.setResult(stoneSword);

        ItemStack[] matrixStack = new ItemStack[10];
        matrixStack[0] = stoneSword;
        matrixStack[1] = dirtStack;
        matrixStack[2] = stoneStack;
        for (int i = 3; i < 10; i++) {
            matrixStack[i] = air;
        }

        TestCraftingInventory shiftInventory = new TestCraftingInventory();
        shiftInventory.setMatrix(matrixStack);
        shiftInventory.setResult(stoneSword);

        Events events = new Events();


        PlayerInventory playerInventory = new TestPlayerInventory();

        Player player = mock(Player.class);
        when(player.hasPermission(Perms.FLAG_ALL)).thenReturn(true);
        when(player.getInventory()).thenReturn(playerInventory);
        when(player.getDisplayName()).thenReturn("TestPlayer");

        InventoryView view = mock(InventoryView.class);
        when(view.getPlayer()).thenReturn(player);

        CraftItemEvent craftEvent = mock(CraftItemEvent.class);
        when(craftEvent.isShiftClick()).thenReturn(false);
        when(craftEvent.getInventory()).thenReturn(inventory);
        when(craftEvent.getView()).thenReturn(view);

        PrepareItemCraftEvent prepareCraftEvent = mock(PrepareItemCraftEvent.class);
        when(prepareCraftEvent.getInventory()).thenReturn(inventory);
        when(prepareCraftEvent.getView()).thenReturn(view);

        CraftItemEvent shiftCraftEvent = mock(CraftItemEvent.class);
        when(shiftCraftEvent.isShiftClick()).thenReturn(true);
        when(shiftCraftEvent.getInventory()).thenReturn(shiftInventory);
        when(shiftCraftEvent.getView()).thenReturn(view);

        PrepareItemCraftEvent prepareShiftCraftEvent = mock(PrepareItemCraftEvent.class);
        when(prepareShiftCraftEvent.getInventory()).thenReturn(shiftInventory);
        when(prepareShiftCraftEvent.getView()).thenReturn(view);

        // Actual Event

        File file = new File(baseRecipePath + "flagItemLore/flagItemLoreShift.txt");
        RecipeProcessor.reload(null, false, file.getPath(), workDir.getPath());


        Map<BaseRecipe, RMCRecipeInfo> indexedRecipes = Recipes.getInstance().getIndex();

        assertEquals(1, indexedRecipes.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : indexedRecipes.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            bukkitRecipe = recipe.getBukkitRecipe(false);
            when(craftEvent.getRecipe()).thenReturn(bukkitRecipe);
            when(prepareCraftEvent.getRecipe()).thenReturn(bukkitRecipe);

            when(shiftCraftEvent.getRecipe()).thenReturn(bukkitRecipe);
            when(prepareShiftCraftEvent.getRecipe()).thenReturn(bukkitRecipe);

            if (resultType == Material.STONE_SWORD) {
                events.prepareCraft((prepareCraftEvent));

                events.craftFinish(craftEvent);

                ItemStack eventResult = craftEvent.getInventory().getResult();
                assertNotNull(eventResult);
                ItemMeta meta = eventResult.getItemMeta();
                assertNotNull(meta);
                assertNotNull(meta.getLore());
                assertEquals(1, meta.getLore().size());
                assertEquals("One", meta.getLore().get(0));


                events.prepareCraft(prepareShiftCraftEvent);

                events.craftFinish(shiftCraftEvent);
                assertNull(shiftCraftEvent.getCurrentItem());
                ItemStack[] shiftContents = shiftCraftEvent.getView().getPlayer().getInventory().getContents();

                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                        ItemMeta stackMeta = item.getItemMeta();
                        assertNotNull(stackMeta);
                        if (stackMeta.hasLore()) {
                            List<String> loreLines = stackMeta.getLore();
                            assertEquals(1, loreLines.size());
                            assertEquals("One", loreLines.get(0));
                        }
                    }
                }

                assertEquals(3, count);
            }
        }
    }
}
