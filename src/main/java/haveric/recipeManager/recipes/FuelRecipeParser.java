package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FuelRecipeParser extends BaseRecipeParser {
    public FuelRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) throws Exception {
        FuelRecipe recipe = new FuelRecipe(fileFlags); // create recipe and copy flags from file
        reader.parseFlags(recipe.getFlags()); // check for @flags
        int added = 0;

        do {
            if (reader.lineIsRecipe() || reader.lineIsFlag()) {
                break;
            }

            recipe = new FuelRecipe(recipe);

            String[] split = reader.getLine().split("%");

            if (!recipe.hasFlag(FlagType.REMOVE)) { // if it's got @remove we don't care about burn time
                if (split.length < 2 || split[1] == null) {
                    ErrorReporter.getInstance().error("Burn time not set!", "It must be set after the ingredient like: ingredient % burntime");
                    continue;
                }

                // set the burn time
                String[] timeSplit = split[1].trim().split("-");
                float minTime = -1;
                float maxTime = -1;

                try {
                    minTime = Math.max(Float.valueOf(timeSplit[0]), 1);

                    if (timeSplit.length >= 2) {
                        maxTime = (float) Math.max(Float.valueOf(timeSplit[1]), 0.0);
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

            // set ingredient
            ItemStack ingredient = Tools.parseItem(split[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

            if (ingredient == null) {
                continue;
            }

            if (ingredient.getType() == Material.AIR) {
                ErrorReporter.getInstance().error("Can not use AIR as ingredient!");
                continue;
            }

            recipe.setIngredient(ingredient);

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

            recipeRegistrator.queueRecipe(recipe, reader.getFileName());

            added++;
        } while (reader.nextLine());

        return added > 0;
    }

}
