package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.tools.Tools;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RemoveResultsParser extends BaseRecipeParser {
    public RemoveResultsParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        RemoveResultRecipe recipe;
        int added = 0;

        do {
            if (this.reader.lineIsRecipe()) {
                break;
            }

            ItemStack result = Tools.parseItem(reader.getLine(), 0);

            if (result == null) {
                continue;
            }

            if (result.getType() == Material.AIR) {
                ErrorReporter.getInstance().error("Recipe has invalid item to remove!");
                continue;
            }

            recipe = new RemoveResultRecipe(result);

            // check if the recipe already exists
            if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
                continue;
            }

            if (recipeName != null && !recipeName.equals("")) {
                String name = recipeName;
                if (added > 1) {
                    name += " (" + added + ")";
                }
                recipe.setName(name); // set recipe's name if defined
            }

            // registrator.queueRemoveResultRecipe(recipe, currentFile);

            added++;
        } while (reader.nextLine());

        return added > 0;
    }
}
