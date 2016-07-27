package haveric.recipeManager.flags;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.CombineRecipe;
import haveric.recipeManager.recipes.CraftRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IngredientTest extends FlagBaseTest {
    @Test
    public void onCraftParse() {
        File file = new File("src/test/resources/recipes/ingredient/ingredientCraft.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(9, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            ItemStack[] ing = recipe.getIngredients();

            assertEquals(Material.DIRT, ing[0].getType());

            int numIngredients = 0;
            if (resultType == Material.DIRT) {
                numIngredients = 1;
            } else if (resultType == Material.COBBLESTONE) {
                numIngredients = 2;
            } else if (resultType == Material.LOG) {
                numIngredients = 3;
            } else if (resultType == Material.STONE) {
                numIngredients = 4;
            } else if (resultType == Material.BRICK) {
                numIngredients = 5;
            } else if (resultType == Material.STONE_SWORD) {
                numIngredients = 6;
            } else if (resultType == Material.GOLD_SWORD) {
                numIngredients = 7;
            } else if (resultType == Material.DIAMOND_SWORD) {
                numIngredients = 8;
            } else if (resultType == Material.DIAMOND) {
                numIngredients = 9;
            }

            if (numIngredients > 1) {
                assertEquals(Material.COBBLESTONE, ing[1].getType());
            }
            if (numIngredients > 2) {
                assertEquals(Material.LOG, ing[2].getType());
            }
            if (numIngredients > 3) {
                assertEquals(Material.STONE, ing[3].getType());
            }
            if (numIngredients > 4) {
                assertEquals(Material.BRICK, ing[4].getType());
            }
            if (numIngredients > 5) {
                assertEquals(Material.STONE_SWORD, ing[5].getType());
            }
            if (numIngredients > 6) {
                assertEquals(Material.GOLD_SWORD, ing[6].getType());
            }
            if (numIngredients > 7) {
                assertEquals(Material.DIAMOND_SWORD, ing[7].getType());
            }
            if (numIngredients > 8) {
                assertEquals(Material.DIAMOND, ing[8].getType());
            }

            for (int i = numIngredients; i <= 8; i++) {
                assertEquals(null, ing[i]);
            }
        }
    }

    @Test
    public void onCraftErrorsParse() {
        File file = new File("src/test/resources/recipes/ingredient/ingredientCraftErrors.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(1, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            ItemStack[] ing = recipe.getIngredients();
            if (resultType == Material.DIRT) {
                assertEquals(Material.DIRT, ing[0].getType());
                assertEquals(Material.COBBLESTONE, ing[1].getType());
                assertEquals(Material.LOG, ing[2].getType());

                for (int i = 3; i <= 8; i++) {
                    assertEquals(null, ing[i]);
                }
            } else if (resultType == Material.STONE) {
                assertEquals(Material.DIRT, ing[0].getType());
                assertEquals(null, ing[1]);
                assertEquals(null, ing[2]);
                assertEquals(Material.COBBLESTONE, ing[3].getType());
                assertEquals(null, ing[4]);
                assertEquals(null, ing[5]);
                assertEquals(Material.LOG, ing[6].getType());
                assertEquals(null, ing[7]);
                assertEquals(null, ing[8]);
            }
        }
    }

    @Test
    public void onCombineParse() {
        File file = new File("src/test/resources/recipes/ingredient/ingredientCombine.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(9, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            List<ItemStack> ing = recipe.getIngredients();

            assertTrue(containsItem(ing, Material.DIRT));

            int numIngredients = 0;
            if (resultType == Material.DIRT) {
                numIngredients = 1;
            } else if (resultType == Material.COBBLESTONE) {
                numIngredients = 2;
            } else if (resultType == Material.LOG) {
                numIngredients = 3;
            } else if (resultType == Material.STONE) {
                numIngredients = 4;
            } else if (resultType == Material.BRICK) {
                numIngredients = 5;
            } else if (resultType == Material.STONE_SWORD) {
                numIngredients = 6;
            } else if (resultType == Material.GOLD_SWORD) {
                numIngredients = 7;
            } else if (resultType == Material.DIAMOND_SWORD) {
                numIngredients = 8;
            } else if (resultType == Material.DIAMOND) {
                numIngredients = 9;
            }

            if (numIngredients > 1) {
                assertTrue(containsItem(ing, Material.COBBLESTONE));
            }
            if (numIngredients > 2) {
                assertTrue(containsItem(ing, Material.LOG));
            }
            if (numIngredients > 3) {
                assertTrue(containsItem(ing, Material.STONE));
            }
            if (numIngredients > 4) {
                assertTrue(containsItem(ing, Material.BRICK));
            }
            if (numIngredients > 5) {
                assertTrue(containsItem(ing, Material.STONE_SWORD));
            }
            if (numIngredients > 6) {
                assertTrue(containsItem(ing, Material.GOLD_SWORD));
            }
            if (numIngredients > 7) {
                assertTrue(containsItem(ing, Material.DIAMOND_SWORD));
            }
            if (numIngredients > 8) {
                assertTrue(containsItem(ing, Material.DIAMOND));
            }
            assertEquals(numIngredients, ing.size());
        }
    }

    @Test
    public void onCombineErrorsParse() {
        File file = new File("src/test/resources/recipes/ingredient/ingredientCombineErrors.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            List<ItemStack> ing = recipe.getIngredients();
            if (resultType == Material.DIRT) {
                assertFalse(containsItem(ing, Material.STONE));
            } else if (resultType == Material.STONE) {
                fail();
            }
        }
    }

    private boolean containsItem(List<ItemStack> list, Material mat) {
        boolean contains = false;

        for (ItemStack item : list) {
            if (mat.equals(item.getType())) {
                contains = true;
                break;
            }
        }

        return contains;
    }
}
