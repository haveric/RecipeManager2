package haveric.recipeManager.recipes.grindstone;

import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneRecipeParser extends BaseRecipeParser {
    public GrindstoneRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredients
        String[] ingredientsRaw = reader.getLine().split("\\+");

        List<List<Material>> choicesList = parseIngredients(ingredientsRaw, recipe.getType(), 2, true);
        if (choicesList == null) {
            return false;
        }

        recipe.setPrimaryIngredient(choicesList.get(0));
        if (choicesList.size() > 1) {
            recipe.setSecondaryIngredient(choicesList.get(1));
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

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true;
    }
}
