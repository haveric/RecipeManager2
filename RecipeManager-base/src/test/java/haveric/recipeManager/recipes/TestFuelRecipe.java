package haveric.recipeManager.recipes;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestFuelRecipe extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "recipes/fuel");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        int totalRecipes = 6;
        assertEquals(totalRecipes, queued.size());

        int numRecipesChecked = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            FuelRecipe recipe = (FuelRecipe) entry.getKey();

            String name = recipe.getName();
            if (name.equals("default")) {
                RecipeChoice primaryChoice = recipe.getIngredientChoice();
                assertInstanceOf(RecipeChoice.MaterialChoice.class, primaryChoice);
                List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                assertEquals(1, primaryMaterials.size());
                assertTrue(primaryMaterials.contains(Material.DIRT));

                assertEquals(100, recipe.getBurnTicks());

                numRecipesChecked ++;
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

                assertEquals(100, recipe.getBurnTicks());

                numRecipesChecked ++;
            } else if (name.equals("multiple")) {
                RecipeChoice primaryChoice = recipe.getIngredientChoice();
                assertInstanceOf(RecipeChoice.ExactChoice.class, primaryChoice);
                List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                assertEquals(1, primaryItems.size());

                ItemStack primaryItem = primaryItems.get(0);
                if (primaryItem.getType() == Material.IRON_SWORD) {
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(2, primaryDamageable.getDamage());

                    assertFalse(primaryMeta.hasDisplayName());

                    assertEquals(80, recipe.getBurnTicks());

                    numRecipesChecked ++;
                } else if (primaryItem.getType() == Material.GOLDEN_SWORD) {
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(3, primaryDamageable.getDamage());

                    assertEquals("Gold Sword", primaryMeta.getDisplayName());

                    assertEquals(100, recipe.getBurnTicks());

                    numRecipesChecked ++;
                }
            } else if (name.equals("multiple2")) {
                RecipeChoice primaryChoice = recipe.getIngredientChoice();
                if (primaryChoice instanceof RecipeChoice.ExactChoice) {
                    List<ItemStack> primaryItems = ((RecipeChoice.ExactChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryItems.size());
                    ItemStack primaryItem = primaryItems.get(0);
                    assertInstanceOf(Damageable.class, primaryItem.getItemMeta());
                    ItemMeta primaryMeta = primaryItem.getItemMeta();
                    Damageable primaryDamageable = (Damageable) primaryMeta;
                    assertEquals(0, primaryDamageable.getDamage());

                    assertEquals("Test Sword", primaryMeta.getDisplayName());

                    assertEquals(100, recipe.getBurnTicks());

                    numRecipesChecked ++;
                } else if (primaryChoice instanceof RecipeChoice.MaterialChoice) {
                    List<Material> primaryMaterials = ((RecipeChoice.MaterialChoice) primaryChoice).getChoices();
                    assertEquals(1, primaryMaterials.size());
                    assertTrue(primaryMaterials.contains(Material.STONE_SWORD));

                    assertEquals(80, recipe.getBurnTicks());

                    numRecipesChecked ++;
                }
            }
        }

        assertEquals(totalRecipes, numRecipesChecked);
    }
}
