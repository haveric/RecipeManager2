package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.campfire.RMCampfireRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCampfireRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/campfire");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int totalRecipes = 9;
        assertEquals(totalRecipes, queued.size());

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            RMCampfireRecipe recipe = (RMCampfireRecipe) entry.getKey();

            ItemResult result = recipe.getResult();

            String name = recipe.getName();
            if (name.equals("default")) {
                RecipeChoice primaryChoice = recipe.getIngredientChoice();
                assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                assertEquals(1, primaryMaterials.size());
                assertTrue(primaryMaterials.contains(Material.DIRT));

                assertEquals(Material.DIRT, result.getType());

                numRecipesChecked ++;
            } else if (name.equals("shape")) {
                RecipeChoice primaryChoice = recipe.getIngredientChoice();
                assertTrue(primaryChoice instanceof RecipeChoice.MaterialChoice);
                List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                assertEquals(1, primaryMaterials.size());
                assertTrue(primaryMaterials.contains(Material.DIRT));

                assertEquals(Material.GRASS, result.getType());

                numRecipesChecked ++;
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

                assertEquals(Material.WOODEN_SWORD, result.getType());

                numRecipesChecked ++;
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

                assertEquals(Material.IRON_SWORD, result.getType());

                numRecipesChecked ++;
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

                assertEquals(Material.GOLDEN_SWORD, result.getType());

                numRecipesChecked ++;
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

                assertEquals(Material.DIAMOND_SWORD, result.getType());

                numRecipesChecked ++;
            } else if (name.equals("time")) {
                assertEquals(5, recipe.getMinTime(), 0);

                numRecipesChecked ++;
            } else if (name.equals("time2")) {
                assertEquals(4, recipe.getMinTime(), 0);

                numRecipesChecked ++;
            } else if (name.equals("time3")) {
                assertEquals(3, recipe.getMinTime(), 0);

                numRecipesChecked ++;
            }
        }

        assertEquals(totalRecipes, numRecipesChecked);
    }
}
