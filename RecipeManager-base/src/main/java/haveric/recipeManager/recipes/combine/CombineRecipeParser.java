package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombineRecipeParser extends BaseRecipeParser {
    public CombineRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CombineRecipe recipe;
        if (Version.has1_13Support()) {
            recipe = new CombineRecipe1_13(fileFlags);
        } else {
            recipe = new CombineRecipe(fileFlags); // create recipe and copy flags from file
        }

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredients
        String[] ingredientsRaw = reader.getLine().split("\\+");

        if (Version.has1_13Support()) {
            List<List<Material>> ingredientChoiceList = new ArrayList<>();

            int items = 0;
            for (String str : ingredientsRaw) {
                Map<List<Material>, Integer> choiceAmountMap =  Tools.parseChoiceWithAmount(str, ParseBit.NONE);

                // We're always returning only one item, so this should always work
                Map.Entry<List<Material>, Integer> entry = choiceAmountMap.entrySet().iterator().next();
                List<Material> choices = entry.getKey();


                if (choices == null) {
                    return ErrorReporter.getInstance().error("Ingredient cannot be empty");
                }

                if (choices.contains(Material.AIR)) {
                    return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
                }

                int newAmount;
                int originalAmount = entry.getValue();

                if (originalAmount <= 0) {
                    ErrorReporter.getInstance().warning("Recipe must have a positive amount. Defaulting to 1");
                    originalAmount = 1;
                }

                if (items + originalAmount > 9) {
                    newAmount = 9 - items;

                    int ignoredAmount = originalAmount - newAmount;
                    ErrorReporter.getInstance().warning("Combine recipes can't have more than 9 ingredients! Extra ingredient(s) ignored: " + str + " x" + ignoredAmount, "If you're using stacks make sure they don't exceed 9 items in total.");
                } else {
                    newAmount = originalAmount;
                }

                items += newAmount;

                for (int i = 0; i < newAmount; i++) {
                    ingredientChoiceList.add(choices);
                }
            }

            recipe.setIngredientChoiceList(ingredientChoiceList);
        } else {
            List<ItemStack> ingredients = new ArrayList<>();
            ItemStack item;
            int items = 0;

            for (String str : ingredientsRaw) {
                item = Tools.parseItem(str, RMCVanilla.DATA_WILDCARD, ParseBit.NO_META);

                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                if (items < 9) {
                    int originalAmount = item.getAmount();
                    if (items + originalAmount > 9) {
                        int newAmount = 9 - items;
                        items += newAmount;

                        item.setAmount(newAmount);
                        ingredients.add(item);

                        int ignoredAmount = originalAmount - newAmount;
                        ErrorReporter.getInstance().warning("Combine recipes can't have more than 9 ingredients! Extra ingredient(s) ignored: " + item.getType() + " x" + ignoredAmount, "If you're using stacks make sure they don't exceed 9 items in total.");
                    } else {
                        items += item.getAmount();
                        ingredients.add(item);
                    }
                }
            }

            if (ingredients.size() == 2 && !conditionEvaluator.checkIngredients(ingredients.get(0), ingredients.get(1))) {
                return false;
            }

            recipe.setIngredients(ingredients);
        }

        if (recipe.hasFlag(FlagType.REMOVE) && !Version.has1_12Support()) { // for mc1.12, matching requires outcome too...
            reader.nextLine(); // Skip the results line, if it exists
        } else {
            // get the results
            List<ItemResult> results = new ArrayList<>();

            if (!parseResults(recipe, results)) {
                return false;
            }

            recipe.setResults(results);

            if (!recipe.hasValidResult()) {
                return ErrorReporter.getInstance().error("Recipe must have at least one non-air result!");
            }
        }

        // check if recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true; // no errors encountered
    }
}
