package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.FlagIngredientCondition;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class FlagIngredientConditionTest extends FlagBaseTest {
    private ItemStack hammerOfFoo;
    private ItemStack oneTwoThree;
    private ItemStack oneTwoThreeQuotes;
    private ItemStack unbreakableSword;
    private ItemStack sword;

    private ItemStack brick;
    private ItemStack sponge;
    private ItemStack spongeStack10;
    private ItemStack brickStack45;
    private ItemStack grassStack10;
    private ItemStack air;

    private File booksDir;
    private TestCraftingInventory inventory;
    private CraftItemEvent craftEvent;
    private static Events events;
    private Recipe bukkitRecipe;

    @Before
    public void before() {
        hammerOfFoo = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta hammerOfFooMeta = hammerOfFoo.getItemMeta();
        hammerOfFooMeta.setDisplayName(RMCChatColor.COLOR_CHAR + "bHammer");
        hammerOfFoo.setItemMeta(hammerOfFooMeta);

        oneTwoThree = new ItemStack(Material.DIRT);
        ItemMeta oneTwoThreeMeta = oneTwoThree.getItemMeta();
        oneTwoThreeMeta.setDisplayName("One");
        oneTwoThreeMeta.setLore(Collections.singletonList("Two"));
        oneTwoThree.setItemMeta(oneTwoThreeMeta);

        oneTwoThreeQuotes = new ItemStack(Material.DIRT);
        ItemMeta oneTwoThreeQuotesMeta = oneTwoThreeQuotes.getItemMeta();
        oneTwoThreeQuotesMeta.setDisplayName("   One   ");
        oneTwoThreeQuotesMeta.setLore(Collections.singletonList("   Two   "));
        oneTwoThreeQuotes.setItemMeta(oneTwoThreeQuotesMeta);

        unbreakableSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = unbreakableSword.getItemMeta();
        meta.setUnbreakable(true);
        unbreakableSword.setItemMeta(meta);

        sword = new ItemStack(Material.IRON_SWORD);


        // Needed flag

        brick = new ItemStack(Material.BRICK);
        sponge = new ItemStack(Material.SPONGE);
        spongeStack10 = new ItemStack(Material.SPONGE, 10);
        brickStack45 = new ItemStack(Material.BRICK, 45);
        grassStack10 = new ItemStack(Material.GRASS, 10);

        air = new ItemStack(Material.AIR);

        when(Settings.getCustomData(Material.STONE_SWORD)).thenReturn((short)132);

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
        File file = new File(baseRecipePath + "flagIngredientCondition/");
        RecipeProcessor.reload(null, false, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> indexedRecipes = Recipes.getInstance().getIndex();

        assertEquals(10, indexedRecipes.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : indexedRecipes.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            Args a = ArgBuilder.create().build();

            bukkitRecipe = recipe.getBukkitRecipe(false);
            when(craftEvent.getRecipe()).thenReturn(bukkitRecipe);

            FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
            if (resultType == Material.DIRT) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                Map<Short, Boolean> values = conditions.get(0).getDataValues();
                assertTrue(values.containsKey((short) 0));
                assertTrue(values.containsKey((short) 1));
                assertTrue(values.containsKey((short) 2));
                assertTrue(values.containsKey((short) 3));
                assertTrue(values.containsKey((short) 4));
                assertTrue(values.containsKey((short) 5));
                assertFalse(values.containsKey((short) 6));
            } else if (resultType == Material.GRAVEL) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIAMOND_SHOVEL));
                ConditionsIngredient cond = conditions.get(0);
                assertEquals(RMCChatColor.COLOR_CHAR + "bHammer", cond.getName());
                assertEquals(RMCChatColor.COLOR_CHAR + "cFoo", cond.getFailMessage());

                a.clear();
                assertTrue(flag.checkIngredientConditions(hammerOfFoo, a));
            } else if (resultType == Material.COBBLESTONE) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                ConditionsIngredient cond = conditions.get(0);
                assertEquals("One", cond.getName());
                assertEquals("Two", cond.getLores().get(0));
                assertEquals("Three", cond.getFailMessage());

                a.clear();
                assertTrue(flag.checkIngredientConditions(oneTwoThree, a));

                a.clear();
                assertFalse(flag.checkIngredientConditions(oneTwoThreeQuotes, a));
            } else if (resultType == Material.STONE) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                ConditionsIngredient cond = conditions.get(0);
                assertEquals("   One   ", cond.getName());
                assertEquals("   Two   ", cond.getLores().get(0));
                assertEquals("   Three   ", cond.getFailMessage());

                a.clear();
                assertTrue(flag.checkIngredientConditions(oneTwoThreeQuotes, a));

                a.clear();
                assertFalse(flag.checkIngredientConditions(oneTwoThree, a));
            } else if (resultType == Material.WOODEN_SWORD) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                ConditionsIngredient cond = conditions.get(0);
                assertNull(cond.getUnbreakable());
            } else if (resultType == Material.IRON_SWORD) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                ConditionsIngredient cond = conditions.get(0);
                assertTrue(cond.getUnbreakable());

                a.clear();
                assertTrue(flag.checkIngredientConditions(unbreakableSword, a));

                a.clear();
                assertFalse(flag.checkIngredientConditions(sword, a));

            } else if (resultType == Material.GOLDEN_SWORD) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                ConditionsIngredient cond = conditions.get(0);
                assertFalse(cond.getUnbreakable());

                a.clear();
                assertFalse(flag.checkIngredientConditions(unbreakableSword, a));

                a.clear();
                assertTrue(flag.checkIngredientConditions(sword, a));
            } else if (resultType == Material.DIAMOND_SWORD) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                ConditionsIngredient cond = conditions.get(0);
                assertNull(cond.getUnbreakable());
                assertTrue(cond.isNoMeta());

                a.clear();
                assertFalse(flag.checkIngredientConditions(unbreakableSword, a));

                a.clear();
                assertTrue(flag.checkIngredientConditions(sword, a));
            } else if (resultType == Material.BRICK) {
                ItemStack[] matrix = new ItemStack[10];
                matrix[0] = brick.clone();
                matrix[1] = brickStack45.clone();
                matrix[2] = grassStack10.clone();
                for (int i = 3; i < 10; i++) {
                    matrix[i] = air;
                }

                inventory.setMatrix(matrix);
                inventory.setResult(brick.clone());

                // Switch to shift click
                craftEvent.getView().getPlayer().getInventory().clear();
                when(craftEvent.isShiftClick()).thenReturn(true);
                events.craftFinish(craftEvent);

                ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                    }
                }

                assertEquals(10, count);
            } else if (resultType == Material.SPONGE) {
                ItemStack[] matrix = new ItemStack[10];
                matrix[0] = sponge.clone();
                matrix[1] = brickStack45.clone();
                matrix[2] = spongeStack10.clone();
                for (int i = 3; i < 10; i++) {
                    matrix[i] = air;
                }

                inventory.setMatrix(matrix);
                inventory.setResult(sponge.clone());

                // Switch to shift click
                craftEvent.getView().getPlayer().getInventory().clear();
                when(craftEvent.isShiftClick()).thenReturn(true);
                events.craftFinish(craftEvent);

                ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                int count = 0;
                for (ItemStack item : shiftContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        count += item.getAmount();
                    }
                }

                assertEquals(4, count);
            }
        }

        // TODO: Add more tests
    }
}
