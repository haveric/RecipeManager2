package haveric.recipeManager.recipes.compost;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CompostRecipeParser extends BaseRecipeParser {
    public CompostRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CompostRecipe recipe = new CompostRecipe(fileFlags);

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredient
        String[] split = reader.getLine().split("%");

        List<Material> choices = parseIngredient(split, recipe.getType());
        if (choices == null) {
            return false;
        }

        recipe.setIngredients(choices);

        if (split.length > 1) {
            try {
                double chance = Double.parseDouble(split[1].trim());

                if (chance > 0 && chance <= 100) {
                    recipe.setLevelSuccessChance(chance);
                } else {
                    ErrorReporter.getInstance().warning("Invalid level success chance: " + split[1] + ". Defaulting to 100.","Allowed values > 0, <= 100 (Decimal values allowed).");
                }

            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Invalid level success chance: " + split[1] + ". Defaulting to 100.","Allowed values > 0, <= 100 (Decimal values allowed).");
            }
        }

        if (split.length > 2) {
            try {
                double levels = Double.parseDouble(split[2].trim());

                if (levels > 0 && levels <= 7) {
                    recipe.setLevels(levels);
                } else {
                    ErrorReporter.getInstance().warning("Invalid levels: " + split[1] + ". Defaulting to 1.","Allowed values > 0, <= 7 (Decimal values allowed).");
                }
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Invalid levels: " + split[1] + ". Defaulting to 1.","Allowed values > 0, <= 7 (Decimal values allowed).");
            }

        }

        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);

        // get result or move current line after them if we got @remove and results
        List<ItemResult> results = new ArrayList<>();

        if (isRemove) { // ignore result errors if we have @remove
            ErrorReporter.getInstance().setIgnoreErrors(true);
        }

        boolean hasResults = parseResults(recipe, results);

        if (!hasResults) {
            return false;
        }

        ItemResult result = results.get(0);

        recipe.setResult(result);

        if (isRemove) { // un-ignore result errors
            ErrorReporter.getInstance().setIgnoreErrors(false);
        }

        // check if the recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return recipe.hasFlag(FlagType.REMOVE);
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true;
    }
}
