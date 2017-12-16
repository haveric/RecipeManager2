package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.FlagIngredientCondition;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

public class FlagIngredientConditionTest extends FlagBaseTest {
    private ItemStack unbreakableSword;
    private ItemStack sword;

    @Before
    public void before() {
        unbreakableSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = unbreakableSword.getItemMeta();
        meta.setUnbreakable(true);
        unbreakableSword.setItemMeta(meta);

        sword = new ItemStack(Material.IRON_SWORD);
    }

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagIngredientCondition/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(8, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            Args a = ArgBuilder.create().build();

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
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIAMOND_SPADE));
                ConditionsIngredient cond = conditions.get(0);
                assertEquals(RMCChatColor.COLOR_CHAR + "bHammer", cond.getName());
                assertEquals(RMCChatColor.COLOR_CHAR + "cFoo", cond.getFailMessage());
            } else if (resultType == Material.COBBLESTONE) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                ConditionsIngredient cond = conditions.get(0);
                assertEquals("One", cond.getName());
                assertEquals("Two", cond.getLores().get(0));
                assertEquals("Three", cond.getFailMessage());
            } else if (resultType == Material.STONE) {
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(new ItemStack(Material.DIRT));
                ConditionsIngredient cond = conditions.get(0);
                assertEquals("   One   ", cond.getName());
                assertEquals("   Two   ", cond.getLores().get(0));
                assertEquals("   Three   ", cond.getFailMessage());
            } else if (resultType == Material.WOOD_SWORD) {
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

            } else if (resultType == Material.GOLD_SWORD) {
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
            }
        }

        // TODO: Add more tests
    }
}
