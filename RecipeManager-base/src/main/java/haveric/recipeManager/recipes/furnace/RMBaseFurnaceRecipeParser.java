package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RMBaseFurnaceRecipeParser extends BaseRecipeParser {
    private RMCRecipeType recipeType;

    public RMBaseFurnaceRecipeParser(RMCRecipeType recipeType) {
        super();

        this.recipeType = recipeType;
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        SingleResultRecipe recipe;
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

        if (recipe instanceof RMBaseFurnaceRecipe1_13) {
            while (!reader.lineIsResult()) {
                String[] splitIngredient = reader.getLine().split("%");

                List<Material> choices = parseIngredient(splitIngredient, recipe.getType());
                if (choices == null || choices.isEmpty()) {
                    return false;
                }

                Flags ingredientFlags = new Flags();
                reader.parseFlags(ingredientFlags);

                if (ingredientFlags.hasFlags()) {
                    List<ItemStack> items = new ArrayList<>();
                    for (Material choice : choices) {
                        Args a = ArgBuilder.create().result(new ItemStack(choice)).build();
                        ingredientFlags.sendCrafted(a, true);

                        items.add(a.result());
                    }
                    ((RMBaseFurnaceRecipe1_13) recipe).addIngredientChoiceItems(items);
                } else {
                    ((RMBaseFurnaceRecipe1_13) recipe).addIngredientChoice(choices);
                }
            }
        } else {
            if (split.length == 0) {
                return ErrorReporter.getInstance().error("Smelting recipe doesn't have an ingredient!");
            }

            ItemStack ingredient = Tools.parseItem(split[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

            if (ingredient == null) {
                return false;
            }

            if (ingredient.getType() == Material.AIR) {
                return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
            }

            ((RMFurnaceRecipe) recipe).setIngredient(ingredient);
            reader.nextLine();
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
                        minTime = Float.parseFloat(timeSplit[0]);

                        if (timeSplit.length >= 2) {
                            maxTime = Float.parseFloat(timeSplit[1]);
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
            if (recipe instanceof RMBaseFurnaceRecipe1_13) {
                ((RMBaseFurnaceRecipe1_13) recipe).setMinTime(minTime);
                ((RMBaseFurnaceRecipe1_13) recipe).setMaxTime(maxTime);
            } else {
                ((RMFurnaceRecipe) recipe).setMinTime(minTime);
                ((RMFurnaceRecipe) recipe).setMaxTime(maxTime);
            }

            if (reader.getLine().charAt(0) == '&') { // check if we have a fuel
                ItemStack fuelItem = Tools.parseItem(reader.getLine().substring(1), 0, ParseBit.NO_AMOUNT);

                if (fuelItem == null) {
                    return false;
                }

                if (fuelItem.getType() == Material.AIR) {
                    return ErrorReporter.getInstance().error("Fuel can not be air!");
                }

                if (recipe instanceof RMBaseFurnaceRecipe1_13) {
                    ((RMBaseFurnaceRecipe1_13) recipe).setFuel(fuelItem);
                    reader.parseFlags(((RMBaseFurnaceRecipe1_13) recipe).getFuel().getFlags());
                } else {
                    ((RMFurnaceRecipe) recipe).setFuel(fuelItem);
                    reader.parseFlags(((RMFurnaceRecipe) recipe).getFuel().getFlags());
                }
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
