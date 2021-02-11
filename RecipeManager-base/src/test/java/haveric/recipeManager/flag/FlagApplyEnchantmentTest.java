package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestEnchantmentSharpness;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagApplyEnchantment;
import haveric.recipeManager.flag.flags.result.FlagEnchantItem;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FlagApplyEnchantmentTest extends FlagBaseTest {
    private InventoryView inventoryView;

    @BeforeEach
    public void setup() {
        try {
            Enchantment.registerEnchantment(new TestEnchantmentSharpness(Enchantment.DAMAGE_ALL));
        } catch (IllegalArgumentException ignored) { }

        mockStatic(Inventory.class);
        CraftingInventory inventory = mock(CraftingInventory.class);

        mockStatic(InventoryView.class);
        inventoryView = mock(InventoryView.class);
        when(inventoryView.getTopInventory()).thenReturn(inventory);

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 1, false);
            book.setItemMeta(meta);

            ItemStack sword = new ItemStack(Material.STONE_SWORD);
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);

            ItemStack[] matrix = new ItemStack[2];
            matrix[0] = book;
            matrix[1] = sword;
            when(inventory.getMatrix()).thenReturn(matrix);
        }
    }

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagApplyEnchantment/");

        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(6, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).inventoryView(inventoryView).build();

                FlagEnchantItem flagEnchantItem = (FlagEnchantItem) result.getFlag(FlagType.ENCHANT_ITEM);
                flagEnchantItem.onPrepare(a);

                FlagApplyEnchantment flag = (FlagApplyEnchantment) result.getFlag(FlagType.APPLY_ENCHANTMENT);
                flag.onPrepare(a);

                String name = recipe.getName();
                switch (name) {
                    case "default":
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.LARGEST, flag.getIngredientAction());
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.LARGEST, flag.getResultAction());
                        assertEquals(2, result.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
                        break;
                    case "ingredientcombine":
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.COMBINE, flag.getIngredientAction());
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.LARGEST, flag.getResultAction());
                        assertEquals(3, result.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
                        break;
                    case "resultcombine":
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.LARGEST, flag.getIngredientAction());
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.COMBINE, flag.getResultAction());
                        assertEquals(3, result.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
                        break;
                    case "bothcombine":
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.COMBINE, flag.getIngredientAction());
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.COMBINE, flag.getResultAction());
                        assertEquals(4, result.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
                        break;
                    case "onlybooksdefault":
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.LARGEST, flag.getIngredientAction());
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.LARGEST, flag.getResultAction());
                        assertEquals(1, result.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
                        break;
                    case "onlybookscombine":
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.COMBINE, flag.getIngredientAction());
                        assertEquals(FlagApplyEnchantment.ApplyEnchantmentAction.COMBINE, flag.getResultAction());
                        assertEquals(2, result.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
                        break;

                }
            }
        }
    }
}
