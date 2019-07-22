package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RMBaseFurnaceRecipeParser extends BaseRecipeParser {
    private RMCRecipeType recipeType;

    public RMBaseFurnaceRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator, RMCRecipeType recipeType) {
        super(reader, recipeName, fileFlags, recipeRegistrator);

        this.recipeType = recipeType;
    }

    @Override
    public boolean parseRecipe(int directiveLine) throws Exception {
        RMBaseFurnaceRecipe recipe;
        if (recipeType == RMCRecipeType.BLASTING) {
            recipe = new RMBlastingRecipe(fileFlags); // create recipe and copy flags from file
        } else if (recipeType == RMCRecipeType.SMOKING) {
            recipe = new RMSmokingRecipe(fileFlags); // create recipe and copy flags from file
        } else {
            if (Version.has1_13Support()) {
                recipe = new RMFurnaceRecipe1_13(fileFlags); // create recipe and copy flags from file
            } else {
                recipe = new RMFurnaceRecipe(fileFlags); // create recipe and copy flags from file
            }
        }

        reader.parseFlags(recipe.getFlags()); // check for @flags

        // get the ingredient and smelting time
        String[] split = reader.getLine().split("%");

        if (split.length == 0) {
            return ErrorReporter.getInstance().error("Smelting recipe doesn't have an ingredient!");
        }

        if (Version.has1_13Support()) {
            List<Material> choices = Tools.parseChoice(split[0], ParseBit.NONE);

            if (choices == null) {
                return ErrorReporter.getInstance().error("Recipe needs an ingredient!");
            }

            if (choices.contains(Material.AIR)) {
                return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
            }

            recipe.setIngredientChoice(choices);
        } else {
            ItemStack ingredient = Tools.parseItem(split[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

            if (ingredient == null) {
                return false;
            }

            if (ingredient.getType() == Material.AIR) {
                return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
            }

            recipe.setIngredient(ingredient);
        }

        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);

        // get min-max or fixed smelting time
        if (!isRemove) { // if it's got @remove we don't care about burn time or fuel
            float minTime;
            if (recipeType == RMCRecipeType.BLASTING) {
                minTime = Vanilla.BLASTING_RECIPE_TIME;
            } else if (recipeType == RMCRecipeType.SMOKING) {
                minTime = Vanilla.SMOKER_RECIPE_TIME;
            } else {
                minTime = Vanilla.FURNACE_RECIPE_TIME;
            }

            float maxTime = -1;

            if (split.length >= 2) {
                String[] timeSplit = split[1].trim().toLowerCase().split("-");

                if (timeSplit[0].equals("instant")) {
                    minTime = 0;
                } else {
                    try {
                        minTime = Float.valueOf(timeSplit[0]);

                        if (timeSplit.length >= 2) {
                            maxTime = Float.valueOf(timeSplit[1]);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Invalid burn time float number! Smelt time left as default.");

                        if (recipeType == RMCRecipeType.BLASTING) {
                            minTime = Vanilla.BLASTING_RECIPE_TIME;
                        } else if (recipeType == RMCRecipeType.SMOKING) {
                            minTime = Vanilla.SMOKER_RECIPE_TIME;
                        } else {
                            minTime = Vanilla.FURNACE_RECIPE_TIME;
                        }

                        maxTime = -1;
                    }
                }

                if (maxTime > -1.0 && minTime >= maxTime) {
                    return ErrorReporter.getInstance().error("Smelting recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }
            }

            recipe.setMinTime(minTime);
            recipe.setMaxTime(maxTime);

            reader.nextLine();

            if (reader.getLine().charAt(0) == '&') { // check if we have a fuel
                ItemStack fuelItem = Tools.parseItem(reader.getLine().substring(1), 0, ParseBit.NO_AMOUNT);

                if (fuelItem == null) {
                    return false;
                }

                if (fuelItem.getType() == Material.AIR) {
                    return ErrorReporter.getInstance().error("Fuel can not be air!");
                }

                recipe.setFuel(fuelItem);
                reader.parseFlags(recipe.getFuel().getFlags());
            }
        }

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
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());


        return true;
    }
}
