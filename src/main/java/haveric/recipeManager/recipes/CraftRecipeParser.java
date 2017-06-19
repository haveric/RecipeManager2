package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftRecipeParser extends BaseRecipeParser {


    public CraftRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) throws Exception {
        CraftRecipe recipe = new CraftRecipe(fileFlags); // create recipe and copy flags from file
        this.reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        ItemStack[] ingredients = new ItemStack[9];
        String[] split;
        ItemStack item;
        int rows = 0;
        int ingredientsNum = 0;
        boolean ingredientErrors = false;

        while (rows < 3) { // loop until we find 3 rows of ingredients (or bump into the result along the way)
            if (rows > 0) {
                this.reader.nextLine();
            }

            if (this.reader.getLine() == null) {
                if (rows == 0) {
                    return ErrorReporter.getInstance().error("No ingredients defined!");
                }

                break;
            }

            if (this.reader.lineIsResult()) { // if we bump into the result prematurely (smaller recipes)
                break;
            }

            split = this.reader.getLine().split("\\+"); // split ingredients by the + sign
            int rowLen = split.length;

            if (rowLen > 3) { // if we find more than 3 ingredients warn the user and limit it to 3
                rowLen = 3;
                ErrorReporter.getInstance().warning("You can't have more than 3 ingredients on a row, ingredient(s) ignored.", "Remove the extra ingredient(s).");
            }

            for (int i = 0; i < rowLen; i++) { // go through each ingredient on the line
                item = Tools.parseItem(split[i], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
                if (item == null) { // invalid item
                    ingredientErrors = true;
                }

                // no point in adding more ingredients if there are errors
                if (!ingredientErrors) {
                    // Minecraft 1.11 required air ingredients to include a data value of 0
                    if ((Version.has1_11Support() && !Version.has1_12Support()) || item.getType() != Material.AIR) {
                        ingredients[(rows * 3) + i] = item;
                        ingredientsNum++;
                    }
                }
            }

            rows++;
        }

        if (ingredientErrors) { // invalid ingredients found
            ErrorReporter.getInstance().error("Recipe has some invalid ingredients, fix them!");
            return false;
        } else if (ingredientsNum == 0) { // no ingredients were processed
            return ErrorReporter.getInstance().error("Recipe doesn't have ingredients!", "Consult readme.txt for proper recipe syntax.");
        } else if (ingredientsNum == 2 && !this.conditionEvaluator.checkIngredients(ingredients)) {
            return false;
        }

        recipe.setIngredients(ingredients); // done with ingredients, set 'em

        if (recipe.hasFlag(FlagType.REMOVE)) {
            this.reader.nextLine(); // Skip the results line, if it exists
        } else {
            // get results
            List<ItemResult> results = new ArrayList<>();

            if (!parseResults(recipe, results)) { // results have errors
                return false;
            }

            recipe.setResults(results); // done with results, set 'em

            if (!recipe.hasValidResult()) {
                return ErrorReporter.getInstance().error("Recipe must have at least one non-air result!");
            }
        }

        // check if the recipe already exists...
        if (!this.conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());
        
        return true; // successfully added
    }


}
