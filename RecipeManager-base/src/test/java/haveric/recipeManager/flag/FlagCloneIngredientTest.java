package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagCloneIngredient;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlagCloneIngredientTest extends FlagBaseTest {

    @Mock
    private CraftingInventory inventory;
    @Mock
    private InventoryView inventoryView;

    @BeforeEach
    public void setup() {
// TODO: Fix or replace now that registerEnchantment has been removed
//
//        try {
//            Enchantment.registerEnchantment(new TestEnchantmentSharpness(Enchantment.DAMAGE_ALL));
//        } catch (IllegalArgumentException ignored) { }
//
//        settings.addEnchantName("sharpness", Enchantment.DAMAGE_ALL);

        when(inventoryView.getTopInventory()).thenReturn(inventory);
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            ItemStack dirt = new ItemStack(Material.DIRT);
            ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
            stoneSword.setDurability((short) 10);
//            stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
            stoneSword.setAmount(2);
            List<String> lores = new ArrayList<>();
            lores.add("Test Lore");
            ItemMeta stoneMeta = stoneSword.getItemMeta();
            stoneMeta.setLore(lores);
            stoneSword.setItemMeta(stoneMeta);

            ItemStack[] matrix = new ItemStack[2];
            matrix[0] = dirt;
            matrix[1] = stoneSword;
            when(inventory.getMatrix()).thenReturn(matrix);
        }
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagCloneIngredient/");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).inventoryView(inventoryView).build();

                FlagCloneIngredient flag = (FlagCloneIngredient) result.getFlag(FlagType.CLONE_INGREDIENT);
                flag.onPrepare(a);

                String name = recipe.getName();
                switch (name) {
                    case "one":
                        assertEquals(4, result.getAmount());
                        assertTrue(result.getItemMeta().getLore().isEmpty());
                        assertTrue(result.getEnchantments().isEmpty());
                        break;
                    case "two":
                        assertEquals(1, result.getAmount());
                        assertEquals(10, result.getDurability());
                        assertTrue(result.getItemMeta().getLore().isEmpty());
                        assertTrue(result.getEnchantments().isEmpty());
                        break;
                    case "three":
                        assertEquals(1, result.getAmount());
                        assertEquals(12, result.getDurability());
                        assertTrue(result.getItemMeta().getLore().isEmpty());
                        assertTrue(result.getEnchantments().isEmpty());
                        break;
                    case "four":
                        assertEquals(1, result.getAmount());
                        assertEquals(0, result.getDurability());
                        assertTrue(result.getItemMeta().getLore().isEmpty());
                        assertTrue(result.getEnchantments().isEmpty());
                        break;
                    case "five":
                        assertEquals(2, result.getAmount());
                        assertEquals(10, result.getDurability());
                        assertTrue(result.getItemMeta().getLore().contains("Test Lore"));
                        assertTrue(result.getEnchantments().isEmpty());
                        break;
                    case "six":
                        assertEquals(2, result.getAmount());
                        assertEquals(10, result.getDurability());
                        assertTrue(result.getItemMeta().getLore().contains("Test Lore"));
//                        assertFalse(result.getEnchantments().isEmpty());
//                        assertTrue(result.getEnchantments().containsKey(Enchantment.DAMAGE_ALL));
//                        assertEquals(2, result.getEnchantments().get(Enchantment.DAMAGE_ALL).intValue());
                        break;
                }
            }
        }

        // TODO: Needs more thorough testing
    }
}