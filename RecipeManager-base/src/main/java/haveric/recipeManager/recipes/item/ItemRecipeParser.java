package haveric.recipeManager.recipes.item;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;

import java.util.ArrayList;
import java.util.List;

public class ItemRecipeParser extends BaseRecipeParser {
    public ItemRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        reader.nextLine();

        if (recipeName == null || recipeName.isEmpty()) {
            return ErrorReporter.getInstance().error("Item recipes require a name so they can be referenced more easily.");
        }

        if (reader.getLine() != null && !reader.lineIsResult()) {
            return ErrorReporter.getInstance().error("Item recipes do not support recipe flags.");
        }

        ItemRecipe recipe = new ItemRecipe();
        List<ItemResult> results = new ArrayList<>();
        if (!parseResults(recipe, results)) { // results have errors
            return false;
        }

        if (results.isEmpty()) {
            return ErrorReporter.getInstance().error("Item recipes need a result.");
        } else if (results.size() > 1) {
            ErrorReporter.getInstance().warning("Item recipes need exactly one result. The rest are ignored.");
        }

        ItemResult result = results.get(0);
        recipe.setResult(result);

        recipe.addRecipe(recipeName);

        return true;
    }
}
