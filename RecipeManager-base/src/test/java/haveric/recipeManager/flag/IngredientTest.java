package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest extends FlagBaseTest {
    //@Test TODO: Rewrite test
    public void onCraftParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCraft.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(9, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            Map<Character, RecipeChoice> choiceMap = recipe.getIngredientsChoiceMap();

            RecipeChoice choiceA = choiceMap.get('a');
            RecipeChoice.MaterialChoice matChoiceA = (RecipeChoice.MaterialChoice) choiceA;

            assertTrue(matChoiceA.getChoices().contains(Material.DIRT));

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
                RecipeChoice choiceB = choiceMap.get('b');
                RecipeChoice.MaterialChoice matChoiceB = (RecipeChoice.MaterialChoice) choiceB;
                assertTrue(matChoiceB.getChoices().contains(Material.COBBLESTONE));
            }
            if (numIngredients > 2) {
                RecipeChoice choiceC = choiceMap.get('c');
                RecipeChoice.MaterialChoice matChoiceC = (RecipeChoice.MaterialChoice) choiceC;
                assertTrue(matChoiceC.getChoices().contains(Material.OAK_LOG));
            }
            if (numIngredients > 3) {
                RecipeChoice choiceD = choiceMap.get('d');
                RecipeChoice.MaterialChoice matChoiceD = (RecipeChoice.MaterialChoice) choiceD;
                assertTrue(matChoiceD.getChoices().contains(Material.STONE));
            }
            if (numIngredients > 4) {
                RecipeChoice choiceE = choiceMap.get('e');
                RecipeChoice.MaterialChoice matChoiceE = (RecipeChoice.MaterialChoice) choiceE;
                assertTrue(matChoiceE.getChoices().contains(Material.BRICK));
            }
            if (numIngredients > 5) {
                RecipeChoice choiceF = choiceMap.get('f');
                RecipeChoice.MaterialChoice matChoiceF = (RecipeChoice.MaterialChoice) choiceF;
                assertTrue(matChoiceF.getChoices().contains(Material.STONE_SWORD));
            }
            if (numIngredients > 6) {
                RecipeChoice choiceG = choiceMap.get('g');
                RecipeChoice.MaterialChoice matChoiceG = (RecipeChoice.MaterialChoice) choiceG;
                assertTrue(matChoiceG.getChoices().contains(Material.GOLDEN_SWORD));
            }
            if (numIngredients > 7) {
                RecipeChoice choiceH = choiceMap.get('h');
                RecipeChoice.MaterialChoice matChoiceH = (RecipeChoice.MaterialChoice) choiceH;
                assertTrue(matChoiceH.getChoices().contains(Material.DIAMOND_SWORD));
            }
            if (numIngredients > 8) {
                RecipeChoice choiceI = choiceMap.get('i');
                RecipeChoice.MaterialChoice matChoiceI = (RecipeChoice.MaterialChoice) choiceI;
                assertTrue(matChoiceI.getChoices().contains(Material.DIAMOND));
            }
        }
    }

    //@Test TODO: Rewrite test
    public void onCraftErrorsParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCraftErrors.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(1, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe recipe = (CraftRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            Map<Character, RecipeChoice> choiceMap = recipe.getIngredientsChoiceMap();

            if (resultType == Material.DIRT) {
                RecipeChoice choiceA = choiceMap.get('a');
                RecipeChoice.MaterialChoice matChoiceA = (RecipeChoice.MaterialChoice) choiceA;
                assertTrue(matChoiceA.getChoices().contains(Material.DIRT));

                RecipeChoice choiceB = choiceMap.get('b');
                RecipeChoice.MaterialChoice matChoiceB = (RecipeChoice.MaterialChoice) choiceB;
                assertTrue(matChoiceB.getChoices().contains(Material.COBBLESTONE));

                RecipeChoice choiceC = choiceMap.get('c');
                RecipeChoice.MaterialChoice matChoiceC = (RecipeChoice.MaterialChoice) choiceC;
                assertTrue(matChoiceC.getChoices().contains(Material.OAK_LOG));

                assertNull(choiceMap.get('d'));

                assertEquals(1, recipe.getChoicePattern().length);
                assertEquals("abc", recipe.getChoicePattern()[0]);
            } else if (resultType == Material.STONE) {
                RecipeChoice choiceA = choiceMap.get('a');
                RecipeChoice.MaterialChoice matChoiceA = (RecipeChoice.MaterialChoice) choiceA;
                assertTrue(matChoiceA.getChoices().contains(Material.DIRT));

                RecipeChoice choiceB = choiceMap.get('b');
                RecipeChoice.MaterialChoice matChoiceB = (RecipeChoice.MaterialChoice) choiceB;
                assertTrue(matChoiceB.getChoices().contains(Material.COBBLESTONE));

                RecipeChoice choiceC = choiceMap.get('c');
                RecipeChoice.MaterialChoice matChoiceC = (RecipeChoice.MaterialChoice) choiceC;
                assertTrue(matChoiceC.getChoices().contains(Material.OAK_LOG));

                assertNull(choiceMap.get('d'));

                assertEquals(3, recipe.getChoicePattern().length);
                assertEquals("a", recipe.getChoicePattern()[0]);
                assertEquals("b", recipe.getChoicePattern()[1]);
                assertEquals("c", recipe.getChoicePattern()[2]);
            }
        }
    }

    //@Test TODO: Rewrite test
    public void onCombineParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCombine.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(9, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            List<RecipeChoice> ingredientChoiceMap = recipe.getIngredientChoiceList();

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

    //@Test TODO: Rewrite test
    public void onCombineErrorsParse() {
        File file = new File(baseRecipePath + "ingredient/ingredientCombineErrors.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(2, queued.size());
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CombineRecipe recipe = (CombineRecipe) entry.getKey();
            ItemResult result = recipe.getResults().get(0);
            Material resultType = result.getType();

            List<RecipeChoice> ingredientChoiceMap = recipe.getIngredientChoiceList();
            if (resultType == Material.DIRT) {
                assertFalse(containsItem(ingredientChoiceMap, Material.STONE));
            } else if (resultType == Material.STONE) {
                fail();
            }
        }
    }

    private boolean containsItem(List<RecipeChoice> list, Material mat) {
        boolean contains = false;

        for (RecipeChoice choice : list) {
            if (choice instanceof RecipeChoice.MaterialChoice) {
                RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                List<Material> materials = materialChoice.getChoices();

                if (materials.contains(mat)) {
                    contains = true;
                    break;
                }
            } else if (choice instanceof RecipeChoice.ExactChoice) {
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                List<ItemStack> items = exactChoice.getChoices();

                for (ItemStack item : items) {
                    Material material = item.getType();
                    if (material == mat) {
                        contains = true;
                        break;
                    }
                }
            }
        }

        return contains;
    }
}
