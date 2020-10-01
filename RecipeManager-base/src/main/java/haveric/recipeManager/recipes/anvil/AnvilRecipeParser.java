package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.anvil.data.BaseAnvilParser;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class AnvilRecipeParser extends BaseAnvilParser {
    public AnvilRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        AnvilRecipe recipe = new AnvilRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

        // get the ingredient
        String[] split = reader.getLine().split("%");
        if (split.length == 0) {
            return ErrorReporter.getInstance().error("Recipe needs an ingredient!");
        }

        // get the ingredients
        String[] ingredientsRaw = split[0].split("\\+");

        List<List<Material>> choicesList = parseIngredients(ingredientsRaw, recipe.getType(), 2, true);
        if (choicesList == null || choicesList.isEmpty()) {
            return false;
        }

        recipe.setPrimaryIngredient(choicesList.get(0));
        if (choicesList.size() > 1) {
            recipe.setSecondaryIngredient(choicesList.get(1));
        }

        parseArgs(recipe, split);

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
