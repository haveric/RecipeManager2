package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipeParser extends BaseRecipeParser {
    public BrewRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BrewRecipe recipe = new BrewRecipe();
        this.reader.parseFlags(recipe.getFlags());

        if (reader.getLine() == null || this.reader.lineIsResult()) {
            return ErrorReporter.getInstance().error("No ingredient defined!");
        }

        ItemStack ingredient = Tools.parseItem(reader.getLine(), RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
        if (ingredient == null) {
            return ErrorReporter.getInstance().error("Recipe has an invalid ingredient, needs fixing!");
        }

        recipe.setIngredient(ingredient);

        this.reader.nextLine();

        if (reader.getLine() == null || this.reader.lineIsResult()) {
            return ErrorReporter.getInstance().error("No potion defined!");
        }

        ItemStack potion = Tools.parseItem(reader.getLine(), RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
        if (potion == null) {
            return ErrorReporter.getInstance().error("Recipe has an invalid potion, needs fixing!");
        }

        recipe.setPotion(potion);

        List<ItemResult> results = new ArrayList<>();

        if (!parseResults(recipe, results)) { // results have errors
            return false;
        }

        recipe.setResults(results); // done with results, set 'em

        // check if the recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, this.reader.getFileName())) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, this.reader.getFileName());

        return true;
    }

}
