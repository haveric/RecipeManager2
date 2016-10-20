package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestEnchantmentSharpness;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.FlagCloneIngredient;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CraftingInventory.class})
public class FlagCloneIngredientTest extends FlagBaseTest {
    private CraftingInventory inventory;

    @Before
    public void setup() {
        Enchantment.registerEnchantment(new TestEnchantmentSharpness(16));
        when(settings.getEnchantment("sharpness")).thenReturn(Enchantment.DAMAGE_ALL);

        mockStatic(Inventory.class);
        inventory = mock(CraftingInventory.class);

        ItemStack dirt = new ItemStack(Material.DIRT);
        ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
        stoneSword.setDurability((short) 10);
        stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        stoneSword.setAmount(2);
        List<String> lores = new ArrayList<>();
        lores.add("Test Lore");
        stoneSword.getItemMeta().setLore(lores);

        ItemStack[] matrix = new ItemStack[2];
        matrix[0] = dirt;
        matrix[1] = stoneSword;
        when(inventory.getMatrix()).thenReturn(matrix);
    }

    @Test
    public void onRecipeParse() {
        File file = new File("src/test/resources/recipes/flagCloneIngredient/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            Args a = ArgBuilder.create().recipe(recipe).player(testUUID).inventory(inventory).build();

            ItemResult result = recipe.getResult(a);

            FlagCloneIngredient flag = (FlagCloneIngredient) result.getFlag(FlagType.CLONE_INGREDIENT);
            flag.onPrepare(a);

            String name = recipe.getName();
            if (name.equals("one")) {
                assertEquals(4, result.getAmount());
                assertTrue(result.getItemMeta().getLore().isEmpty());
                assertTrue(result.getEnchantments().isEmpty());
            } else if (name.equals("two")) {
                assertEquals(1, result.getAmount());
                assertEquals(10, result.getDurability());
                assertTrue(result.getItemMeta().getLore().isEmpty());
                assertTrue(result.getEnchantments().isEmpty());
            } else if (name.equals("three")) {
                assertEquals(1, result.getAmount());
                assertEquals(12, result.getDurability());
                assertTrue(result.getItemMeta().getLore().isEmpty());
                assertTrue(result.getEnchantments().isEmpty());
            } else if (name.equals("four")) {
                assertEquals(1, result.getAmount());
                assertEquals(0, result.getDurability());
                assertTrue(result.getItemMeta().getLore().isEmpty());
                assertTrue(result.getEnchantments().isEmpty());
            } else if (name.equals("five")) {
                assertEquals(2, result.getAmount());
                assertEquals(10, result.getDurability());
                assertTrue(result.getItemMeta().getLore().contains("Test Lore"));
                assertTrue(result.getEnchantments().isEmpty());
            } else if (name.equals("six")) {
                assertEquals(2, result.getAmount());
                assertEquals(10, result.getDurability());
                assertTrue(result.getItemMeta().getLore().contains("Test Lore"));
                assertFalse(result.getEnchantments().isEmpty());
                assertTrue(result.getEnchantments().containsKey(Enchantment.DAMAGE_ALL));
                assertEquals(2, result.getEnchantments().get(Enchantment.DAMAGE_ALL).intValue());
            }
        }

        // TODO: Needs more thorough testing
    }
}