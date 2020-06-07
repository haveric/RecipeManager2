package haveric.recipeManager;

import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseTest;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.brew.BrewRecipe1_13;
import haveric.recipeManager.recipes.combine.CombineRecipe1_13;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManager.recipes.fuel.FuelRecipe1_13;
import haveric.recipeManager.recipes.cooking.furnace.RMBaseFurnaceRecipe1_13;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.RecipeChoice;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestHelpRecipes extends FlagBaseTest {
    @Before
    public void before() {
        settings.loadItemAliases(null, new File(originalResourcesPath), "item aliases.yml");
    }

    @Test
    public void basicRecipes() {
        File file = new File(baseRecipePath + "helpRecipes/basicRecipes.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(10, queued.size());

        int numCraftRecipes = 0;
        int numCombineRecipes = 0;
        int numSmeltRecipes = 0;
        int numFuelRecipes = 0;
        int numBrewingRecipes = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            BaseRecipe baseRecipe = entry.getKey();

            if (baseRecipe instanceof CraftRecipe1_13) {
                CraftRecipe1_13 recipe = (CraftRecipe1_13) baseRecipe;
                ItemResult result = recipe.getResults().get(0);
                Material resultType = result.getType();

                if (resultType == Material.WOODEN_HOE) {
                    numCraftRecipes ++;
                } else if (resultType == Material.BOWL) {
                    numCraftRecipes ++;
                } else if (resultType == Material.LIGHT_GRAY_TERRACOTTA) {
                    numCraftRecipes ++;
                }
            } else if (baseRecipe instanceof CombineRecipe1_13) {
                CombineRecipe1_13 recipe = (CombineRecipe1_13) baseRecipe;
                ItemResult result = recipe.getResults().get(0);
                Material resultType = result.getType();

                if (resultType == Material.TNT) {
                    numCombineRecipes ++;
                } else if (resultType == Material.DIAMOND) {
                    numCombineRecipes ++;
                }
            } else if (baseRecipe instanceof RMBaseFurnaceRecipe1_13) {
                RMBaseFurnaceRecipe1_13 recipe = (RMBaseFurnaceRecipe1_13) baseRecipe;
                ItemResult result = recipe.getResult();
                Material resultType = result.getType();

                if (resultType == Material.GOLD_ORE) {
                    numSmeltRecipes ++;
                } else if (resultType == Material.EXPERIENCE_BOTTLE) {
                    numSmeltRecipes ++;
                }
            } else if (baseRecipe instanceof FuelRecipe1_13) {
                FuelRecipe1_13 recipe = (FuelRecipe1_13) baseRecipe;
                RecipeChoice choice = recipe.getIngredientChoice();
                assertTrue(choice instanceof RecipeChoice.MaterialChoice);

                List<Material> choices = ((RecipeChoice.MaterialChoice) choice).getChoices();

                if (choices.contains(Material.GUNPOWDER)) {
                    numFuelRecipes ++;
                } else if (choices.contains(Material.JACK_O_LANTERN)) {
                    numFuelRecipes ++;
                }
            } else if (baseRecipe instanceof BrewRecipe1_13) {
                BrewRecipe1_13 recipe = (BrewRecipe1_13) baseRecipe;
                ItemResult result = recipe.getResults().get(0);
                Material resultType = result.getType();

                if (resultType == Material.STONE) {
                    numBrewingRecipes ++;
                }
            }
        }

        assertEquals(3, numCraftRecipes);
        assertEquals(2, numCombineRecipes);
        assertEquals(2, numSmeltRecipes);
        assertEquals(2, numFuelRecipes);
        assertEquals(1, numBrewingRecipes);
    }

    @Test
    public void advancedRecipes() {
        Enchantment.registerEnchantment(new TestEnchantmentUnbreaking(Enchantment.DURABILITY));
        settings.addEnchantName("durability", Enchantment.DURABILITY);

        File file = new File(baseRecipePath + "helpRecipes/advancedRecipes.txt");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(4, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            BaseRecipe baseRecipe = entry.getKey();

            if (baseRecipe instanceof CombineRecipe1_13) {
                assertTrue(baseRecipe.hasFlag(FlagType.MESSAGE));
            } else if (baseRecipe instanceof CraftRecipe1_13) {
                assertFalse(baseRecipe.hasFlag(FlagType.MESSAGE));
            } else if (baseRecipe instanceof FuelRecipe1_13) {
                assertTrue(baseRecipe.hasFlag(FlagType.MESSAGE));
            }

        }
    }
}
