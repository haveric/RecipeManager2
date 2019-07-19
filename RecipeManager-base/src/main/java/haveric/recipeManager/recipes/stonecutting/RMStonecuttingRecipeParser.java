package haveric.recipeManager.recipes.stonecutting;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class RMStonecuttingRecipeParser extends BaseRecipeParser {
    public RMStonecuttingRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        RMStonecuttingRecipe recipe = new RMStonecuttingRecipe(fileFlags); // create recipe and copy flags from file
        reader.parseFlags(recipe.getFlags()); // check for @flags

        // get the ingredient
        String line = reader.getLine();

        List<Material> choices = Tools.parseChoice(line, ParseBit.NONE);

        if (choices == null) {
            return ErrorReporter.getInstance().error("Recipe needs an ingredient!");
        }

        if (choices.contains(Material.AIR)) {
            return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
        }

        recipe.setIngredientChoice(choices);

        if (recipe.hasFlag(FlagType.OVERRIDE)) {
            return ErrorReporter.getInstance().error("Recipe does not allow Overriding. Try removing the original and adding a new one.");
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
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());


        return true;
    }
}
