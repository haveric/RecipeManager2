package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.AirChoice;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class FuelRecipeParser extends BaseRecipeParser {
    private static final double singleTickInSeconds = 0.05;

    public FuelRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        FuelRecipe recipe = new FuelRecipe(fileFlags); // create recipe and copy flags from file
        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // check for @flags
        int added = 0;

        while (!reader.lineIsRecipe() && !reader.lineIsFlag()) {
            if (reader.lineIsRecipe() || reader.lineIsFlag()) {
                break;
            }

            recipe = new FuelRecipe(fileFlags);

            String[] split = reader.getLine().split("%");

            if (!recipe.hasFlag(FlagType.REMOVE)) { // if it's got @remove we don't care about burn time
                if (split.length < 2 || split[1] == null) {
                    ErrorReporter.getInstance().error("Burn time not set!", "It must be set after the ingredient like: ingredient % burntime");
                    continue;
                }

                // set the burn time
                String[] timeSplit = split[1].trim().split("-");
                float minTime;
                float maxTime = -1;

                try {
                    minTime = (float) Math.max(Float.parseFloat(timeSplit[0]), singleTickInSeconds);

                    if (timeSplit.length >= 2) {
                        maxTime = (float) Math.max(Float.parseFloat(timeSplit[1]), 0.0);
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Invalid burn time float number!");
                    continue;
                }

                if (minTime <= 0) {
                    ErrorReporter.getInstance().error("Fuels can't burn for negative or 0 seconds!");
                    continue;
                }

                if (maxTime > -1 && minTime >= maxTime) {
                    maxTime = -1;
                    ErrorReporter.getInstance().warning("Fuel has minimum time less or equal to maximum time!", "Use a single number if you want a fixed value");
                }

                recipe.setMinTime(minTime);
                recipe.setMaxTime(maxTime);
            }

            RecipeChoice choice = Tools.parseRecipeChoice(split[0], ParseBit.NONE);
            if (choice == null || choice instanceof AirChoice) {
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

            // check if the recipe already exists
            if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
                continue;
            }

            if (recipeName != null && !recipeName.isEmpty()) {
                String name = recipeName;
                if (added > 1) {
                    name += " (" + added + ")";
                }
                recipe.setName(name); // set recipe's name if defined
            }

            recipeRegistrator.queueRecipe(recipe, reader.getFileName());

            added++;

            boolean lineExists = true;
            while (reader.lineIsEmpty()) {
                lineExists = reader.nextLine();

                if (!lineExists) {
                    break;
                }
            }

            if (!lineExists) {
                break;
            }
        }

        return added > 0;
    }
}
