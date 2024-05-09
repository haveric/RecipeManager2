package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.brew.BrewRecipe;
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
            BrewRecipe recipe = (BrewRecipe) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                String name = recipe.getName();
                if (name.equals("default")) {
                    RecipeChoice primaryChoice = recipe.getPrimaryIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, primaryChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    RecipeChoice secondaryChoice = recipe.getPotionIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, secondaryChoice);
                    List<Material> secondaryMaterials = ((RecipeChoice.MaterialChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryMaterials.size());
                    assertTrue(secondaryMaterials.contains(Material.SHORT_GRASS));

                    assertEquals(Material.DIRT, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape")) {
                    RecipeChoice primaryChoice = recipe.getPrimaryIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, primaryChoice);
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.DIRT));

                    RecipeChoice secondaryChoice = recipe.getPotionIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, secondaryChoice);
                    List<Material> secondaryMaterials = ((RecipeChoice.MaterialChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryMaterials.size());
                    assertTrue(secondaryMaterials.contains(Material.STONE));

                    assertEquals(Material.SHORT_GRASS, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("data")) {
                    RecipeChoice primaryChoice = recipe.getPrimaryIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.WOODEN_SWORD, primaryItem.getType());
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    Damageable primaryDamageable = (Damageable) primaryItem.getItemMeta();
                    assertEquals(2, primaryDamageable.getDamage());

                    RecipeChoice secondaryChoice = recipe.getPotionIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, secondaryChoice);
                    List<ItemStack> secondaryItems = ((RecipeChoice.ExactChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryItems.size());

                    ItemStack secondaryItem = secondaryItems.get(0);
                    assertEquals(Material.WOODEN_SWORD, secondaryItem.getType());
                    assertInstanceOf(Damageable.class, secondaryItem.getItemMeta());
                    Damageable secondaryDamageable = (Damageable) secondaryItem.getItemMeta();
                    assertEquals(3, secondaryDamageable.getDamage());

                    assertEquals(Material.WOODEN_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-data")) {
                    RecipeChoice primaryChoice = recipe.getPrimaryIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());

                    ItemStack primaryItem = primaryItems.get(0);
                    assertEquals(Material.IRON_SWORD, primaryItem.getType());
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    Damageable primaryDamageable = (Damageable) primaryItem.getItemMeta();
                    assertEquals(2, primaryDamageable.getDamage());

                    RecipeChoice secondaryChoice = recipe.getPotionIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, secondaryChoice);
                    List<ItemStack> secondaryItems = ((RecipeChoice.ExactChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryItems.size());

                    ItemStack secondaryItem = secondaryItems.get(0);
                    assertEquals(Material.IRON_SWORD, secondaryItem.getType());
                    assertInstanceOf(Damageable.class, secondaryItem.getItemMeta());
                    Damageable secondaryDamageable = (Damageable) secondaryItem.getItemMeta();
                    assertEquals(3, secondaryDamageable.getDamage());

                    assertEquals(Material.IRON_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("shape-ingredient-flag")) {
                    RecipeChoice primaryChoice = recipe.getPrimaryIngredientChoice();
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

                    RecipeChoice secondaryChoice = recipe.getPotionIngredientChoice();
                    assertInstanceOf(RecipeChoice.ExactChoice.class, secondaryChoice);
                    List<ItemStack> secondaryItems = ((RecipeChoice.ExactChoice) secondaryChoice).getChoices();
                    assertEquals(1, secondaryItems.size());

                    ItemStack secondaryItem = secondaryItems.get(0);
                    assertEquals(Material.DIAMOND_SWORD, secondaryItem.getType());
                    assertInstanceOf(Damageable.class, secondaryItem.getItemMeta());
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
