package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipeParser1_13 extends BaseRecipeParser {
    public BrewRecipeParser1_13() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BrewRecipe1_13 recipe = new BrewRecipe1_13(fileFlags);

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE);

        int numIngredients = 0;
        while (!reader.lineIsResult()) {
            String[] splitIngredient = reader.getLine().split("%");

            String materialsValue = splitIngredient[0].trim();

            String lineChars = materialsValue.substring(0, 2).trim();
            char ingredientChar = lineChars.charAt(0);

            RecipeChoice choice;
            if (lineChars.length() == 1 && (ingredientChar == 'a' || ingredientChar == 'b')) {
                choice = Tools.parseRecipeChoice(materialsValue.substring(2), ParseBit.NONE);
            } else {
                if (numIngredients == 0) {
                    ingredientChar = 'a';
                } else {
                    ingredientChar = 'b';
                }

                choice = Tools.parseRecipeChoice(materialsValue, ParseBit.NONE);
            }

            if (choice == null) {
                return false;
            }

            numIngredients += 1;

            FlaggableRecipeChoice flaggable = new FlaggableRecipeChoice();
            flaggable.setChoice(choice);
            Flags ingredientFlags = flaggable.getFlags();

            reader.parseFlags(ingredientFlags, FlagBit.INGREDIENT);

            if (ingredientFlags.hasFlags()) {
                List<ItemStack> items = new ArrayList<>();
                if (choice instanceof RecipeChoice.MaterialChoice) {
                    RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                    List<Material> materials = materialChoice.getChoices();

                    for (Material material : materials) {
                        Args a = ArgBuilder.create().result(new ItemStack(material)).build();
                        ingredientFlags.sendCrafted(a, true);

                        items.add(a.result());
                    }
                } else if (choice instanceof RecipeChoice.ExactChoice) {
                    RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                    List<ItemStack> exactItems = exactChoice.getChoices();

                    for (ItemStack exactItem : exactItems) {
                        Args a = ArgBuilder.create().result(exactItem).build();
                        ingredientFlags.sendCrafted(a, true);

                        items.add(a.result());
                    }
                }

                if (!recipe.hasIngredient(ingredientChar)) {
                    recipe.setIngredient(ingredientChar, new RecipeChoice.ExactChoice(items));
                } else {
                    recipe.setIngredient(ingredientChar, ToolsRecipeChoice.mergeRecipeChoiceWithItems(recipe.getIngredient(ingredientChar), items));
                }
            } else {
                if (!recipe.hasIngredient(ingredientChar)) {
                    recipe.setIngredient(ingredientChar, choice);
                } else {
                    recipe.setIngredient(ingredientChar, ToolsRecipeChoice.mergeRecipeChoices(recipe.getIngredient(ingredientChar), choice));
                }
            }

            if (!parseArgs(recipe, splitIngredient)) {
                return false;
            }
        }

        return parseAndSetResults(recipe, directiveLine);
    }

    private boolean parseAndSetResults(BrewRecipe1_13 recipe, int directiveLine) {
        List<ItemResult> results = new ArrayList<>();
        boolean hasResults = parseResults(recipe, results);

        if (!hasResults) {
            return false;
        }

        recipe.setResults(results);

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

    // get min-max or fixed brewing time
    private boolean parseArgs(BrewRecipe1_13 recipe, String[] split) {
        if (!recipe.hasFlag(FlagType.REMOVE)) { // if it's got @remove we don't care about brew time
            int minTime;
            int maxTime = -1;

            if (split.length >= 2) {
                String[] timeSplit = split[1].trim().toLowerCase().split("-");

                if (timeSplit[0].equals("instant")) {
                    minTime = 0;
                } else {
                    try {
                        minTime = Integer.parseInt(timeSplit[0]);

                        if (timeSplit.length >= 2) {
                            maxTime = Integer.parseInt(timeSplit[1]);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Invalid brewing time integer number! Brewing time left as default.");

                        minTime = Vanilla.BREWING_RECIPE_DEFAULT_TICKS;

                        maxTime = -1;
                    }
                }

                if (maxTime > -1.0 && minTime >= maxTime) {
                    return ErrorReporter.getInstance().error("Brewing recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }

                recipe.setMinTime(minTime);
                recipe.setMaxTime(maxTime);
            }
        }

        return true;
    }
}
