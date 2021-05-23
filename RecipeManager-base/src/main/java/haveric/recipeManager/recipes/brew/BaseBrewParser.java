package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Vanilla;
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

    // get min-max or fixed brewing time
    protected boolean parseArgs(BaseBrewRecipe recipe, String[] split) {
        if (!recipe.hasFlag(FlagType.REMOVE)) { // if it's got @remove we don't care about brew time
            int minTime;
            int maxTime = -1;

            if (split.length >= 2) {
                String[] timeSplit = split[1].trim().toLowerCase().split("-");

                if (timeSplit[0].equals("instant")) {
                    minTime = 0;
                } else {
                    try {
                        minTime = Integer.parseInt(timeSplit[0]);

                        if (timeSplit.length >= 2) {
                            maxTime = Integer.parseInt(timeSplit[1]);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Invalid brewing time integer number! Brewing time left as default.");

                        minTime = Vanilla.BREWING_RECIPE_DEFAULT_TICKS;

                        maxTime = -1;
                    }
                }

                if (maxTime > -1.0 && minTime >= maxTime) {
                    return ErrorReporter.getInstance().error("Brewing recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }

                recipe.setMinTime(minTime);
                recipe.setMaxTime(maxTime);
            }
        }

        return true;
    }
}
