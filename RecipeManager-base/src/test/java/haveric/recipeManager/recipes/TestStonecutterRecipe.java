package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.stonecutting.RMStonecuttingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class TestStonecutterRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/stonecutter");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int totalRecipes = 6;
        assertEquals(totalRecipes, queued.size());

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMStonecuttingRecipe recipe = (RMStonecuttingRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getResult();

                String name = recipe.getName();
                if (name.equals("default")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, primaryChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    assertEquals(Material.DIRT, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, primaryChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    assertEquals(Material.SHORT_GRASS, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("data")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.WOODEN_SWORD, primaryItem.getType());
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    Damageable primaryDamageable = (Damageable) primaryItem.getItemMeta();
                    assertEquals(2, primaryDamageable.getDamage());

                    assertEquals(Material.WOODEN_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-data")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.IRON_SWORD, primaryItem.getType());
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    Damageable primaryDamageable = (Damageable) primaryItem.getItemMeta();
                    assertEquals(2, primaryDamageable.getDamage());

                    assertEquals(Material.IRON_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("ingredient-flag-data")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.GOLDEN_SWORD, primaryItem.getType());
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(3, primaryDamageable.getDamage());

                    assertEquals("Gold Sword", primaryMeta.getDisplayName());

                    assertEquals(Material.GOLDEN_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-ingredient-flag")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.DIAMOND_SWORD, primaryItem.getType());
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(0, primaryDamageable.getDamage());

                    assertEquals("Test Sword", primaryMeta.getDisplayName());

                    assertEquals(Material.DIAMOND_SWORD, result.getType());

                    numRecipesChecked++;
                }
            }
        }

        assertEquals(totalRecipes, numRecipesChecked);
    }
}
