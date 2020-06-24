package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.combine.CombineRecipe1_13;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestCombineRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/combine");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe1_13 recipe = (CombineRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            String name = recipe.getName();
            if (name.equals("default")) {
                String pattern = "ab";
                assertEquals(pattern, recipe.getChoicePattern());

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

                numRecipesChecked ++;
            } else if (name.equals("pattern-default")) {
                String pattern = "ab";
                assertEquals(pattern, recipe.getChoicePattern());

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
                numRecipesChecked ++;
            } else if (name.equals("pattern-ingredient-flag")) {
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

                numRecipesChecked ++;
            } else if (name.equals("data")) {
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

                numRecipesChecked ++;
            } else if (name.equals("pattern-data")) {
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

                numRecipesChecked ++;
            } else if (name.contains("multiple-data")) {
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

                numRecipesChecked ++;
            }
        }

        int totalRecipes = 9;
        assertEquals(totalRecipes, queued.size());
        assertEquals(totalRecipes, numRecipesChecked);
    }
}
