package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Material;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IngredientTest extends FlagBaseTest {
    @Test
    public void onCraftParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCraft.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(9, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            Map<Character, List<Material>> choiceMap = recipe.getIngredientsChoiceMap();

            assertTrue(choiceMap.get('a').contains(Material.DIRT));

            int numIngredients = 0;
            if (resultType == Material.DIRT) {
                numIngredients = 1;
            } else if (resultType == Material.COBBLESTONE) {
                numIngredients = 2;
            } else if (resultType == Material.OAK_LOG) {
                numIngredients = 3;
            } else if (resultType == Material.STONE) {
                numIngredients = 4;
            } else if (resultType == Material.BRICK) {
                numIngredients = 5;
            } else if (resultType == Material.STONE_SWORD) {
                numIngredients = 6;
            } else if (resultType == Material.GOLDEN_SWORD) {
                numIngredients = 7;
            } else if (resultType == Material.DIAMOND_SWORD) {
                numIngredients = 8;
            } else if (resultType == Material.DIAMOND) {
                numIngredients = 9;
            }

            if (numIngredients > 1) {
                assertTrue(choiceMap.get('b').contains(Material.COBBLESTONE));
            }
            if (numIngredients > 2) {
                assertTrue(choiceMap.get('c').contains(Material.OAK_LOG));
            }
            if (numIngredients > 3) {
                assertTrue(choiceMap.get('d').contains(Material.STONE));
            }
            if (numIngredients > 4) {
                assertTrue(choiceMap.get('e').contains(Material.BRICK));
            }
            if (numIngredients > 5) {
                assertTrue(choiceMap.get('f').contains(Material.STONE_SWORD));
            }
            if (numIngredients > 6) {
                assertTrue(choiceMap.get('g').contains(Material.GOLDEN_SWORD));
            }
            if (numIngredients > 7) {
                assertTrue(choiceMap.get('h').contains(Material.DIAMOND_SWORD));
            }
            if (numIngredients > 8) {
                assertTrue(choiceMap.get('i').contains(Material.DIAMOND));
            }
        }
    }

    @Test
    public void onCraftErrorsParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCraftErrors.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(1, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            Map<Character, List<Material>> choiceMap = recipe.getIngredientsChoiceMap();

            if (resultType == Material.DIRT) {
                assertTrue(choiceMap.get('a').contains(Material.DIRT));
                assertTrue(choiceMap.get('b').contains(Material.COBBLESTONE));
                assertTrue(choiceMap.get('c').contains(Material.OAK_LOG));

                assertNull(choiceMap.get('d'));

                assertEquals(1, recipe.getChoiceShape().length);
                assertEquals("abc", recipe.getChoiceShape()[0]);
            } else if (resultType == Material.STONE) {
                assertTrue(choiceMap.get('a').contains(Material.DIRT));
                assertTrue(choiceMap.get('b').contains(Material.COBBLESTONE));
                assertTrue(choiceMap.get('c').contains(Material.OAK_LOG));

                assertNull(choiceMap.get('d'));

                assertEquals(3, recipe.getChoiceShape().length);
                assertEquals("a", recipe.getChoiceShape()[0]);
                assertEquals("b", recipe.getChoiceShape()[1]);
                assertEquals("c", recipe.getChoiceShape()[2]);
            }
        }
    }

    @Test
    public void onCombineParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCombine.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(9, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            List<List<Material>> ingredientChoiceMap = recipe.getIngredientChoiceList();

            assertTrue(containsItem(ingredientChoiceMap, Material.DIRT));

            int numIngredients = 0;
            if (resultType == Material.DIRT) {
                numIngredients = 1;
            } else if (resultType == Material.COBBLESTONE) {
                numIngredients = 2;
            } else if (resultType == Material.OAK_LOG) {
                numIngredients = 3;
            } else if (resultType == Material.STONE) {
                numIngredients = 4;
            } else if (resultType == Material.BRICK) {
                numIngredients = 5;
            } else if (resultType == Material.STONE_SWORD) {
                numIngredients = 6;
            } else if (resultType == Material.GOLDEN_SWORD) {
                numIngredients = 7;
            } else if (resultType == Material.DIAMOND_SWORD) {
                numIngredients = 8;
            } else if (resultType == Material.DIAMOND) {
                numIngredients = 9;
            }

            if (numIngredients > 1) {
                assertTrue(containsItem(ingredientChoiceMap, Material.COBBLESTONE));
            }
            if (numIngredients > 2) {
                assertTrue(containsItem(ingredientChoiceMap, Material.OAK_LOG));
            }
            if (numIngredients > 3) {
                assertTrue(containsItem(ingredientChoiceMap, Material.STONE));
            }
            if (numIngredients > 4) {
                assertTrue(containsItem(ingredientChoiceMap, Material.BRICK));
            }
            if (numIngredients > 5) {
                assertTrue(containsItem(ingredientChoiceMap, Material.STONE_SWORD));
            }
            if (numIngredients > 6) {
                assertTrue(containsItem(ingredientChoiceMap, Material.GOLDEN_SWORD));
            }
            if (numIngredients > 7) {
                assertTrue(containsItem(ingredientChoiceMap, Material.DIAMOND_SWORD));
            }
            if (numIngredients > 8) {
                assertTrue(containsItem(ingredientChoiceMap, Material.DIAMOND));
            }
            assertEquals(numIngredients, ingredientChoiceMap.size());
        }
    }

    @Test
    public void onCombineErrorsParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCombineErrors.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            List<List<Material>> ingredientChoiceMap = recipe.getIngredientChoiceList();
            if (resultType == Material.DIRT) {
                assertFalse(containsItem(ingredientChoiceMap, Material.STONE));
            } else if (resultType == Material.STONE) {
                fail();
            }
        }
    }

    private boolean containsItem(List<List<Material>> list, Material mat) {
        boolean contains = false;

        for (List<Material> materials : list) {
            if (materials.contains(mat)) {
                contains = true;
                break;
            }
        }

        return contains;
    }
}
