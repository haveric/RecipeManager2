package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CombineRecipeParser extends BaseRecipeParser {
    public CombineRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) throws Exception {

        CombineRecipe recipe = new CombineRecipe(fileFlags); // create recipe and copy flags from file
        this.reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredients
        String[] ingredientsRaw = reader.getLine().split("\\+");

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
                    ErrorReporter.getInstance().warning("Combine recipes can't have more than 9 ingredients! Extra ingredient(s) ignored: " + item.getType() + "x" + ignoredAmount, "If you're using stacks make sure they don't exceed 9 items in total.");
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

        if (recipe.hasFlag(FlagType.REMOVE)) {
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
