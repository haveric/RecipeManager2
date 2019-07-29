package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagItemLore;
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
        File booksDir;

        TestCraftingInventory shiftInventory;
        CraftItemEvent shiftCraftEvent;
        PrepareItemCraftEvent prepareCraftEvent;
        Events events;

        Recipe bukkitRecipe;
        // Prep
        booksDir = new File(workDir.getPath() + "/books/");
        booksDir.mkdirs();

        RecipeBooks.getInstance().init(booksDir);
        RecipeBooks.getInstance().reload(null);

        mockStatic(Inventory.class);

        shiftInventory = new TestCraftingInventory();
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
        ItemStack dirtStack = new ItemStack(Material.DIRT, 3);
        ItemStack stoneStack = new ItemStack(Material.STONE, 4);

        ItemStack[] matrixStack = new ItemStack[10];
        matrixStack[0] = stoneSword;
        matrixStack[1] = dirtStack;
        matrixStack[2] = stoneStack;
        for (int i = 3; i < 10; i++) {
            matrixStack[i] = air;
        }

        shiftInventory.setMatrix(matrixStack);
        shiftInventory.setResult(stoneSword);

        events = new Events();


        PlayerInventory playerInventory = new TestPlayerInventory();

        Player player = mock(Player.class);
        when(player.hasPermission(Perms.FLAG_ALL)).thenReturn(true);
        when(player.getInventory()).thenReturn(playerInventory);
        when(player.getDisplayName()).thenReturn("TestPlayer");

        InventoryView view = mock(InventoryView.class);
        when(view.getPlayer()).thenReturn(player);

        shiftCraftEvent = mock(CraftItemEvent.class);
        when(shiftCraftEvent.isShiftClick()).thenReturn(true);
        when(shiftCraftEvent.getInventory()).thenReturn(shiftInventory);
        when(shiftCraftEvent.getView()).thenReturn(view);

        prepareCraftEvent = mock(PrepareItemCraftEvent.class);
        when(prepareCraftEvent.getInventory()).thenReturn(shiftInventory);
        when(prepareCraftEvent.getView()).thenReturn(view);

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
            when(shiftCraftEvent.getRecipe()).thenReturn(bukkitRecipe);
            when(prepareCraftEvent.getRecipe()).thenReturn(bukkitRecipe);

            if (resultType == Material.STONE_SWORD) {
                events.prepareCraft(prepareCraftEvent);

                events.craftFinish(shiftCraftEvent);
                assertNull(shiftCraftEvent.getCurrentItem());
                ItemStack[] shiftContents = shiftCraftEvent.getView().getPlayer().getInventory().getContents();

                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                        ItemMeta meta = item.getItemMeta();
                        assertNotNull(meta);
                        if (meta.hasLore()) {
                            List<String> loreLines = meta.getLore();
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
