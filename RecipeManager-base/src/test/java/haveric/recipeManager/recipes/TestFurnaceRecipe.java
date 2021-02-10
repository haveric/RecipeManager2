package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.cooking.furnace.RMFurnaceRecipe1_13;
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

public class TestFurnaceRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/furnace");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int totalRecipes = 11;
        assertEquals(totalRecipes, queued.size());

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMFurnaceRecipe1_13 recipe = (RMFurnaceRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getResult();

                String name = recipe.getName();
                if (name.equals("default")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    assertFalse(recipe.hasFuel());

                    assertEquals(10, recipe.getMinTime(), 0);

                    assertEquals(Material.DIRT, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    assertFalse(recipe.hasFuel());

                    assertEquals(Material.GRASS, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("data")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.WOODEN_SWORD, primaryItem.getType());
                    assertTrue(primaryItem.getItemMeta() instanceof Damageable);
                    Damageable primaryDamageable = (Damageable) primaryItem.getItemMeta();
                    assertEquals(2, primaryDamageable.getDamage());

                    assertFalse(recipe.hasFuel());

                    assertEquals(Material.WOODEN_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-data")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.IRON_SWORD, primaryItem.getType());
                    assertTrue(primaryItem.getItemMeta() instanceof Damageable);
                    Damageable primaryDamageable = (Damageable) primaryItem.getItemMeta();
                    assertEquals(2, primaryDamageable.getDamage());

                    assertFalse(recipe.hasFuel());

                    assertEquals(Material.IRON_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("ingredient-flag-data")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.GOLDEN_SWORD, primaryItem.getType());
                    assertTrue(primaryItem.getItemMeta() instanceof Damageable);
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(3, primaryDamageable.getDamage());

                    assertEquals("Gold Sword", primaryMeta.getDisplayName());

                    assertFalse(recipe.hasFuel());

                    assertEquals(Material.GOLDEN_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-ingredient-flag")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.DIAMOND_SWORD, primaryItem.getType());
                    assertTrue(primaryItem.getItemMeta() instanceof Damageable);
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(0, primaryDamageable.getDamage());

                    assertEquals("Test Sword", primaryMeta.getDisplayName());

                    assertFalse(recipe.hasFuel());

                    assertEquals(Material.DIAMOND_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("fuel")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.COBBLESTONE));

                    assertTrue(recipe.hasFuel());
                    assertEquals(Material.COAL, recipe.getFuel().getType());

                    assertEquals(Material.COAL, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-fuel")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.STONE));

                    assertTrue(recipe.hasFuel());
                    assertEquals(Material.CHARCOAL, recipe.getFuel().getType());

                    assertEquals(Material.CHARCOAL, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("time")) {
                    assertEquals(5, recipe.getMinTime(), 0);

                    numRecipesChecked++;
                } else if (name.equals("time2")) {
                    assertEquals(4, recipe.getMinTime(), 0);

                    numRecipesChecked++;
                } else if (name.equals("time3")) {
                    assertEquals(3, recipe.getMinTime(), 0);

                    numRecipesChecked++;
                }
            }
        }

        assertEquals(totalRecipes, numRecipesChecked);
    }
}
