package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipeParser extends BaseRecipeParser {
    public BrewRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BrewRecipe recipe = new BrewRecipe();

        reader.parseFlags(recipe.getFlags());

        if (reader.getLine() == null || reader.lineIsResult()) {
            return ErrorReporter.getInstance().error("No ingredient defined!");
        }

        ItemStack ingredient = Tools.parseItem(reader.getLine(), RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
        if (ingredient == null) {
            return ErrorReporter.getInstance().error("Recipe has an invalid ingredient, needs fixing!");
        }

        recipe.setIngredient(ingredient);

        reader.nextLine();

        if (reader.getLine() == null || reader.lineIsResult()) {
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
