package haveric.recipeManager.recipes.stonecutting;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class RMStonecuttingRecipeParser extends BaseRecipeParser {
    public RMStonecuttingRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        RMStonecuttingRecipe recipe = new RMStonecuttingRecipe(fileFlags); // create recipe and copy flags from file
        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // check for @flags

        String groupLine = reader.getLine();
        if (groupLine.toLowerCase().startsWith("group ")) {
            groupLine = groupLine.substring("group ".length()).trim();

            recipe.setGroup(groupLine);

            reader.nextLine();
        }

        while (!reader.lineIsResult()) {
            // get the ingredient
            String materialsValue = reader.getLine();

            // There's no needed logic for shapes here, so trim the shape declaration
            if (materialsValue.startsWith("a ")) {
                materialsValue = materialsValue.substring(2);
            }

            RecipeChoice choice = Tools.parseRecipeChoice(materialsValue, ParseBit.NONE);
            if (choice == null) {
                return false;
            }

            Flags ingredientFlags = createFlaggableFlags(choice);
            reader.parseFlags(ingredientFlags, FlagBit.INGREDIENT);

            if (ingredientFlags.hasFlags()) {
                List<ItemStack> items = parseChoiceToItems(choice, ingredientFlags);

                recipe.addIngredientChoiceItems(items);
            } else {
                recipe.setIngredientChoice(ToolsRecipeChoice.mergeRecipeChoices(recipe.getIngredientChoice(), choice));
            }
        }

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
            return isRemove;
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true;
    }
}
