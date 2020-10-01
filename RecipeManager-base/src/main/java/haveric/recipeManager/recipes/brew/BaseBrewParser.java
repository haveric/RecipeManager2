package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;

import java.util.ArrayList;
import java.util.List;

public class BaseBrewParser extends BaseRecipeParser {
    @Override
    public boolean parseRecipe(int directiveLine) {
        return false;
    }

    protected boolean parseAndSetResults(BaseBrewRecipe recipe, int directiveLine) {
        List<ItemResult> results = new ArrayList<>();
        boolean hasResults = parseResults(recipe, results);

        if (!hasResults) {
            return false;
        }

        recipe.setResults(results);

        // check if the recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return recipe.hasFlag(FlagType.REMOVE);
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true;
    }
}
