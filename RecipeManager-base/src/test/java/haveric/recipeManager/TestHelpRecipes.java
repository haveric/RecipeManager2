package haveric.recipeManager;

import haveric.recipeManager.common.recipes.RMCRecipeInfo;
import haveric.recipeManager.flag.FlagBaseYamlTest;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.TestMessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.brew.BrewRecipe;
import haveric.recipeManager.recipes.combine.CombineRecipe;
import haveric.recipeManager.recipes.cooking.furnace.RMBaseFurnaceRecipe;
import haveric.recipeManager.recipes.craft.CraftRecipe;
import haveric.recipeManager.recipes.fuel.FuelRecipe;
import haveric.recipeManager.settings.SettingsYaml;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mockStatic;

public class TestHelpRecipes extends FlagBaseYamlTest {
    @BeforeEach
    public void before() {
        try (MockedStatic<MessageSender> mockedMessageSender = mockStatic(MessageSender.class)) {
            mockedMessageSender.when(MessageSender::getInstance).thenReturn(TestMessageSender.getInstance());

            ((SettingsYaml) settings).loadItemAliases(null, new File(originalResourcesPath), "item aliases.yml");
        }
    }

    //@Test TODO: Rewrite test
    public void basicRecipes() {
        File file = new File(baseRecipePath + "helpRecipes/basicRecipes.txt");
        reloadRecipeProcessor(true, file);

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(10, queued.size());

        int numCraftRecipes = 0;
        int numCombineRecipes = 0;
        int numSmeltRecipes = 0;
        int numFuelRecipes = 0;
        int numBrewingRecipes = 0;
        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            BaseRecipe baseRecipe = entry.getKey();

            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);

                if (baseRecipe instanceof CraftRecipe recipe) {
                    ItemResult result = recipe.getResults().get(0);
                    Material resultType = result.getType();

                    if (resultType == Material.WOODEN_HOE) {
                        numCraftRecipes++;
                    } else if (resultType == Material.BOWL) {
                        numCraftRecipes++;
                    } else if (resultType == Material.LIGHT_GRAY_TERRACOTTA) {
                        numCraftRecipes++;
                    }
                } else if (baseRecipe instanceof CombineRecipe recipe) {
                    ItemResult result = recipe.getResults().get(0);
                    Material resultType = result.getType();

                    if (resultType == Material.TNT) {
                        numCombineRecipes++;
                    } else if (resultType == Material.DIAMOND) {
                        numCombineRecipes++;
                    }
                } else if (baseRecipe instanceof RMBaseFurnaceRecipe recipe) {
                    ItemResult result = recipe.getResult();
                    Material resultType = result.getType();

                    if (resultType == Material.GOLD_ORE) {
                        numSmeltRecipes++;
                    } else if (resultType == Material.EXPERIENCE_BOTTLE) {
                        numSmeltRecipes++;
                    }
                } else if (baseRecipe instanceof FuelRecipe recipe) {
                    RecipeChoice choice = recipe.getIngredientChoice();
                    assertInstanceOf(RecipeChoice.MaterialChoice.class, choice);

                    List<Material> choices = ((RecipeChoice.MaterialChoice) choice).getChoices();

                    if (choices.contains(Material.GUNPOWDER)) {
                        numFuelRecipes++;
                    } else if (choices.contains(Material.JACK_O_LANTERN)) {
                        numFuelRecipes++;
                    }
                } else if (baseRecipe instanceof BrewRecipe recipe) {
                    ItemResult result = recipe.getResults().get(0);
                    Material resultType = result.getType();

                    if (resultType == Material.STONE) {
                        numBrewingRecipes++;
                    }
                }
            }
        }

        assertEquals(3, numCraftRecipes);
        assertEquals(2, numCombineRecipes);
        assertEquals(2, numSmeltRecipes);
        assertEquals(2, numFuelRecipes);
        assertEquals(1, numBrewingRecipes);
    }

// TODO: Fix or replace now that registerEnchantment has been removed
//
//    @Test
//    public void advancedRecipes() {
//        try {
//            Enchantment.registerEnchantment(new TestEnchantmentUnbreaking(Enchantment.DURABILITY));
//        } catch (IllegalArgumentException ignored) { }
//
//        settings.addEnchantName("durability", Enchantment.DURABILITY);
//
//        File file = new File(baseRecipePath + "helpRecipes/advancedRecipes.txt");
//        reloadRecipeProcessor(true, file);
//
//        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();
//
//        assertEquals(4, queued.size());
//
//        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
//            BaseRecipe baseRecipe = entry.getKey();
//
//            if (baseRecipe instanceof CombineRecipe1_13) {
//                assertTrue(baseRecipe.hasFlag(FlagType.MESSAGE));
//            } else if (baseRecipe instanceof CraftRecipe1_13) {
//                assertFalse(baseRecipe.hasFlag(FlagType.MESSAGE));
//            } else if (baseRecipe instanceof FuelRecipe1_13) {
//                assertTrue(baseRecipe.hasFlag(FlagType.MESSAGE));
//            }
//
//        }
//    }
}
