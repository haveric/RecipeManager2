package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
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

public class TestCraftRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/craft");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int totalRecipes = 11;
        assertEquals(totalRecipes, queued.size());

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                ItemResult result = recipe.getFirstResult();

                String name = recipe.getName();
                if (name.equals("default")) {
                    assertEquals(2, recipe.getWidth());
                    assertEquals(1, recipe.getHeight());

                    String[] pattern = {"ab"};
                    assertArrayEquals(pattern, recipe.getChoicePattern());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesA = ((RecipeChoice.MaterialChoice) choiceA).getChoices();
                    assertEquals(1, choicesA.size());
                    assertTrue(choicesA.contains(Material.DIRT));

                    RecipeChoice choiceB = recipe.getIngredientsChoiceMap().get('b');
                    assertTrue(choiceB instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesB = ((RecipeChoice.MaterialChoice) choiceB).getChoices();
                    assertEquals(1, choicesB.size());
                    assertTrue(choicesB.contains(Material.COBBLESTONE));

                    assertEquals(Material.STONE, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("pattern-default")) {
                    assertEquals(2, recipe.getWidth());
                    assertEquals(1, recipe.getHeight());

                    String[] pattern = {"ab"};
                    assertArrayEquals(pattern, recipe.getChoicePattern());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesA = ((RecipeChoice.MaterialChoice) choiceA).getChoices();
                    assertEquals(1, choicesA.size());
                    assertTrue(choicesA.contains(Material.DIRT));

                    RecipeChoice choiceB = recipe.getIngredientsChoiceMap().get('b');
                    assertTrue(choiceB instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesB = ((RecipeChoice.MaterialChoice) choiceB).getChoices();
                    assertEquals(1, choicesB.size());
                    assertTrue(choicesB.contains(Material.GRASS));

                    assertEquals(Material.COBBLESTONE, result.getType());
                    numRecipesChecked++;
                } else if (name.equals("choice")) {
                    assertEquals(1, recipe.getWidth());
                    assertEquals(2, recipe.getHeight());

                    String[] pattern = {"a", "b"};
                    assertArrayEquals(pattern, recipe.getChoicePattern());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesA = ((RecipeChoice.MaterialChoice) choiceA).getChoices();
                    assertEquals(2, choicesA.size());
                    assertTrue(choicesA.contains(Material.DIRT));
                    assertTrue(choicesA.contains(Material.GRASS));

                    RecipeChoice choiceB = recipe.getIngredientsChoiceMap().get('b');
                    assertTrue(choiceB instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesB = ((RecipeChoice.MaterialChoice) choiceB).getChoices();
                    assertEquals(2, choicesB.size());
                    assertTrue(choicesB.contains(Material.SPONGE));
                    assertTrue(choicesB.contains(Material.BRICK));

                    assertEquals(Material.BRICK, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("pattern-choice")) {
                    assertEquals(1, recipe.getWidth());
                    assertEquals(2, recipe.getHeight());

                    String[] pattern = {"a", "b"};
                    assertArrayEquals(pattern, recipe.getChoicePattern());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesA = ((RecipeChoice.MaterialChoice) choiceA).getChoices();
                    assertEquals(2, choicesA.size());
                    assertTrue(choicesA.contains(Material.SPONGE));
                    assertTrue(choicesA.contains(Material.BRICK));

                    RecipeChoice choiceB = recipe.getIngredientsChoiceMap().get('b');
                    assertTrue(choiceB instanceof RecipeChoice.MaterialChoice);
                    List<Material> choicesB = ((RecipeChoice.MaterialChoice) choiceB).getChoices();
                    assertEquals(2, choicesB.size());
                    assertTrue(choicesB.contains(Material.DIRT));
                    assertTrue(choicesB.contains(Material.GRASS));

                    assertEquals(Material.SPONGE, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("pattern-ingredient-flag")) {
                    assertEquals(1, recipe.getWidth());
                    assertEquals(1, recipe.getHeight());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> choicesA = ((RecipeChoice.ExactChoice) choiceA).getChoices();
                    assertEquals(1, choicesA.size());

                    ItemStack itemA = choicesA.get(0);
                    assertEquals(Material.DIAMOND_SWORD, itemA.getType());
                    ItemMeta meta = itemA.getItemMeta();
                    assertTrue(meta instanceof Damageable);
                    Damageable damageable = (Damageable) meta;
                    assertEquals(0, damageable.getDamage());

                    assertEquals("Test Sword", meta.getDisplayName());

                    assertEquals(Material.DIAMOND_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("data")) {
                    assertEquals(1, recipe.getWidth());
                    assertEquals(1, recipe.getHeight());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> choicesA = ((RecipeChoice.ExactChoice) choiceA).getChoices();
                    assertEquals(1, choicesA.size());

                    ItemStack itemA = choicesA.get(0);
                    assertEquals(Material.IRON_SWORD, itemA.getType());
                    assertTrue(itemA.getItemMeta() instanceof Damageable);
                    Damageable damageable = (Damageable) itemA.getItemMeta();
                    assertEquals(1, damageable.getDamage());

                    assertEquals(Material.IRON_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.equals("pattern-data")) {
                    assertEquals(1, recipe.getWidth());
                    assertEquals(1, recipe.getHeight());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> choicesA = ((RecipeChoice.ExactChoice) choiceA).getChoices();
                    assertEquals(1, choicesA.size());

                    ItemStack itemA = choicesA.get(0);
                    assertEquals(Material.GOLDEN_SWORD, itemA.getType());
                    assertTrue(itemA.getItemMeta() instanceof Damageable);
                    Damageable damageable = (Damageable) itemA.getItemMeta();
                    assertEquals(1, damageable.getDamage());

                    assertEquals(Material.GOLDEN_SWORD, result.getType());

                    numRecipesChecked++;
                } else if (name.contains("multiple-data")) {
                    assertEquals(1, recipe.getWidth());
                    assertEquals(1, recipe.getHeight());

                    RecipeChoice choiceA = recipe.getIngredientsChoiceMap().get('a');
                    assertTrue(choiceA instanceof RecipeChoice.ExactChoice);
                    List<ItemStack> choicesA = ((RecipeChoice.ExactChoice) choiceA).getChoices();
                    assertEquals(2, choicesA.size());

                    ItemStack itemA = choicesA.get(0);
                    assertEquals(Material.WOODEN_SWORD, itemA.getType());
                    assertTrue(itemA.getItemMeta() instanceof Damageable);
                    Damageable damageable = (Damageable) itemA.getItemMeta();
                    assertEquals(1, damageable.getDamage());

                    ItemStack itemB = choicesA.get(1);
                    assertEquals(Material.IRON_SWORD, itemB.getType());
                    assertTrue(itemB.getItemMeta() instanceof Damageable);
                    Damageable damageableB = (Damageable) itemB.getItemMeta();
                    assertEquals(2, damageableB.getDamage());

                    numRecipesChecked++;
                }
            }
        }

        assertEquals(totalRecipes, numRecipesChecked);
    }
}
