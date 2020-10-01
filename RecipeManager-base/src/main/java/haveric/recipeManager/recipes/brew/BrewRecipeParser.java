package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.tools.Tools;
import org.bukkit.inventory.ItemStack;

public class BrewRecipeParser extends BaseBrewParser {
    public BrewRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BrewRecipe recipe = new BrewRecipe();

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE);

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

        return parseAndSetResults(recipe, directiveLine);
    }

}
