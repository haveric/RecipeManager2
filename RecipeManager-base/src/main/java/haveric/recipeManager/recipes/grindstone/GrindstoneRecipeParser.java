package haveric.recipeManager.recipes.grindstone;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.common.util.ParseBit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneRecipeParser extends BaseRecipeParser {
    public GrindstoneRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredients
        String[] ingredientsRaw = reader.getLine().split("\\+");

        int numIngredients = ingredientsRaw.length;
        if (numIngredients < 1) {
            return ErrorReporter.getInstance().error("Recipe does not have any ingredients.");
        } else if (numIngredients > 2) {
            return ErrorReporter.getInstance().error("Recipe has too many ingredients. Needs 1 or 2.");
        } else {
            List<Material> primary = Tools.parseChoice(ingredientsRaw[0], ParseBit.NONE);

            if (primary == null) {
                return ErrorReporter.getInstance().error("Recipe needs a primary ingredient!");
            }

            if (primary.contains(Material.AIR)) {
                return ErrorReporter.getInstance().error("Recipe does not accept AIR as primary ingredient!");
            }
            recipe.setPrimaryIngredient(primary);

            List<Material> secondary;
            if (numIngredients == 1) {
                secondary = new ArrayList<>();
                secondary.add(Material.AIR);
            } else {
                secondary = Tools.parseChoice(ingredientsRaw[1], ParseBit.NONE);
                if (secondary == null) {
                    return ErrorReporter.getInstance().error("Recipe needs a secondary ingredient!");
                }
            }

            recipe.setSecondaryIngredient(secondary);
        }

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

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true;
    }
}
