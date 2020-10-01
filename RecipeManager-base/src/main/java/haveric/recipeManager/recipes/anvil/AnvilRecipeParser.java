package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.FlagBit;
import org.bukkit.Material;

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

        return parseAndSetResults(recipe, directiveLine);
    }
}
