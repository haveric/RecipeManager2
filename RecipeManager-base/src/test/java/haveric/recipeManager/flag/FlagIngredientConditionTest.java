package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.conditions.condition.ConditionBoolean;
import haveric.recipeManager.flag.conditions.condition.ConditionString;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.TestMessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.WorkbenchEvents;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.settings.SettingsYaml;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlagIngredientConditionTest extends FlagBaseYamlTest {
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

    private TestCraftingInventory inventory;
    @Mock
    private CraftItemEvent craftEvent;
    private static WorkbenchEvents workbenchEvents;

    @BeforeEach
    public void before() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

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
            grassStack10 = new ItemStack(Material.SHORT_GRASS, 10);

            air = new ItemStack(Material.AIR);

            try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
                mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

                ((SettingsYaml) settings).loadItemDatas(null, new File(baseDataPath), "item datas.yml");

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
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagIngredientCondition/");
        reloadRecipeProcessor(false, file);

        Map<BaseRecipe, RMCRecipeInfo> indexedRecipes = Recipes.getInstance().getIndex();

        assertEquals(12, indexedRecipes.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : indexedRecipes.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
                ItemResult result = recipe.getResults().get(0);
                Material resultType = result.getType();

                Args a = ArgBuilder.create().build();

                Recipe bukkitRecipe = recipe.getBukkitRecipe(false);
                lenient().when(craftEvent.getRecipe()).thenReturn(bukkitRecipe);

                FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);

                try (MockedStatic<RecipeManager> mockedRecipeManager = mockStatic(RecipeManager.class)) {
                    mockedRecipeManager.when(RecipeManager::getSettings).thenReturn(settings);
                    mockedRecipeManager.when(RecipeManager::getRecipes).thenReturn(recipes);

                    try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
                        mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

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
                            ConditionString conditionString = (ConditionString) cond.getConditions().get("name");
                            assertEquals(RMCChatColor.COLOR_CHAR + "bHammer", conditionString.getValuesString());
                            assertEquals(RMCChatColor.COLOR_CHAR + "cFoo", cond.getFailMessage());

                            a.clear();
                            assertTrue(flag.checkIngredientConditions(hammerOfFoo, a));
                        } else if (resultType == Material.COBBLESTONE) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                            ConditionsIngredient cond = conditions.get(0);
                            ConditionString conditionString = (ConditionString) cond.getConditions().get("name");
                            assertEquals("One", conditionString.getValuesString());
                            assertEquals("Two", cond.getLores().get(0));
                            assertEquals("Three", cond.getFailMessage());

                            a.clear();
                            assertTrue(flag.checkIngredientConditions(oneTwoThree, a));

                            a.clear();
                            assertFalse(flag.checkIngredientConditions(oneTwoThreeQuotes, a));
                        } else if (resultType == Material.STONE) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                            ConditionsIngredient cond = conditions.get(0);
                            ConditionString conditionString = (ConditionString) cond.getConditions().get("name");
                            assertEquals("   One   ", conditionString.getValuesString());
                            assertEquals("   Two   ", cond.getLores().get(0));
                            assertEquals("   Three   ", cond.getFailMessage());

                            a.clear();
                            assertTrue(flag.checkIngredientConditions(oneTwoThreeQuotes, a));

                            a.clear();
                            assertFalse(flag.checkIngredientConditions(oneTwoThree, a));
                        } else if (resultType == Material.WOODEN_SWORD) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                            ConditionsIngredient cond = conditions.get(0);
                            assertFalse(cond.getConditions().containsKey("unbreakable"));
                        } else if (resultType == Material.IRON_SWORD) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                            ConditionsIngredient cond = conditions.get(0);
                            assertTrue(cond.getConditions().containsKey("unbreakable"));
                            ConditionBoolean condition = (ConditionBoolean) cond.getConditions().get("unbreakable");
                            assertTrue(condition.getValue());

                            a.clear();
                            assertTrue(flag.checkIngredientConditions(unbreakableSword, a));

                            a.clear();
                            assertFalse(flag.checkIngredientConditions(sword, a));

                        } else if (resultType == Material.GOLDEN_SWORD) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                            ConditionsIngredient cond = conditions.get(0);
                            assertTrue(cond.getConditions().containsKey("unbreakable"));
                            ConditionBoolean condition = (ConditionBoolean) cond.getConditions().get("unbreakable");
                            assertFalse(condition.getValue());

                            a.clear();
                            assertFalse(flag.checkIngredientConditions(unbreakableSword, a));

                            a.clear();
                            assertTrue(flag.checkIngredientConditions(sword, a));
                        } else if (resultType == Material.DIAMOND_SWORD) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SWORD));
                            ConditionsIngredient cond = conditions.get(0);
                            assertTrue(cond.getConditions().containsKey("unbreakable"));
                            ConditionBoolean condition = (ConditionBoolean) cond.getConditions().get("unbreakable");
                            assertNull(condition.getValue());
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
                            workbenchEvents.craftFinish(craftEvent);

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
                            workbenchEvents.craftFinish(craftEvent);

                            ItemStack[] shiftContents = craftEvent.getView().getPlayer().getInventory().getContents();
                            int count = 0;
                            for (ItemStack item : shiftContents) {
                                if (item != null && item.getType() != Material.AIR) {
                                    count += item.getAmount();
                                }
                            }

                            assertEquals(4, count);
                        } else if (resultType == Material.STONE_SHOVEL) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.STONE_SHOVEL));
                            ConditionsIngredient cond = conditions.get(0);
                            ConditionString conditionString = (ConditionString) cond.getConditions().get("name");
                            assertEquals("regex:foo", conditionString.getValuesString());
                        } else if (resultType == Material.IRON_SHOVEL) {
                            List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.IRON_SHOVEL));
                            ConditionsIngredient cond = conditions.get(0);
                            ConditionString conditionString = (ConditionString) cond.getConditions().get("name");
                            assertEquals("regex:foo|bar", conditionString.getValuesString());
                        }
                    }
                }
            }
        }

        // TODO: Add more tests
    }
}
