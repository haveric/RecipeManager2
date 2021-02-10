package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.brew.BrewRecipe1_13;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class TestBrewRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/brew");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int totalRecipes = 5;
        assertEquals(totalRecipes, queued.size());

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            BrewRecipe1_13 recipe = (BrewRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                String name = recipe.getName();
                if (name.equals("default")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    RecipeChoice secondaryChoice = recipe.getPotionChoice();
                    assertTrue(secondaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> secondaryMaterials = ((RecipeChoice.MaterialChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryMaterials.size());
                    assertTrue(secondaryMaterials.contains(Material.GRASS));

                    assertEquals(Material.DIRT, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape")) {
                    RecipeChoice primaryChoice = recipe.getIngredientChoice();
                    assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    RecipeChoice secondaryChoice = recipe.getPotionChoice();
                    assertTrue(secondaryChoice instanceof RecipeChoice.MaterialChoice);
                    List<Material> secondaryMaterials = ((RecipeChoice.MaterialChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryMaterials.size());
                    assertTrue(secondaryMaterials.contains(Material.STONE));

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

                    RecipeChoice secondaryChoice = recipe.getPotionChoice();
                    assertTrue(secondaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> secondaryItems = ((RecipeChoice.ExactChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryItems.size());

                    ItemStack secondaryItem = secondaryItems.get(0);
                    assertEquals(Material.WOODEN_SWORD, secondaryItem.getType());
                    assertTrue(secondaryItem.getItemMeta() instanceof Damageable);
                    Damageable secondaryDamageable = (Damageable) secondaryItem.getItemMeta();
                    assertEquals(3, secondaryDamageable.getDamage());

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

                    RecipeChoice secondaryChoice = recipe.getPotionChoice();
                    assertTrue(secondaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> secondaryItems = ((RecipeChoice.ExactChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryItems.size());

                    ItemStack secondaryItem = secondaryItems.get(0);
                    assertEquals(Material.IRON_SWORD, secondaryItem.getType());
                    assertTrue(secondaryItem.getItemMeta() instanceof Damageable);
                    Damageable secondaryDamageable = (Damageable) secondaryItem.getItemMeta();
                    assertEquals(3, secondaryDamageable.getDamage());

                    assertEquals(Material.IRON_SWORD, result.getType());

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

                    RecipeChoice secondaryChoice = recipe.getPotionChoice();
                    assertTrue(secondaryChoice instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> secondaryItems = ((RecipeChoice.ExactChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryItems.size());

                    ItemStack secondaryItem = secondaryItems.get(0);
                    assertEquals(Material.DIAMOND_SWORD, secondaryItem.getType());
                    assertTrue(secondaryItem.getItemMeta() instanceof Damageable);
                    ItemMeta secondaryMeta = secondaryItem.getItemMeta();
                    Damageable secondaryDamageable = (Damageable) secondaryMeta;
                    assertEquals(0, secondaryDamageable.getDamage());

                    assertEquals("Test Sword 2", secondaryMeta.getDisplayName());

                    assertEquals(Material.DIAMOND_SWORD, result.getType());

                    numRecipesChecked++;
                }
            }
        }

        assertEquals(totalRecipes, numRecipesChecked);
    }
}
