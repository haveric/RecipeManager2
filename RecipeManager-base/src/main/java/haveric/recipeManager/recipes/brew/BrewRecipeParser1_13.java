package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
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
        BrewRecipe1_13 recipe = new BrewRecipe1_13();

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE);

        while (!reader.lineIsResult()) {
            String line = reader.getLine();
            String lineChars = line.substring(0, 2).trim();
            char ingredientChar = lineChars.charAt(0);

            if (lineChars.length() == 1 && (ingredientChar == 'a' || ingredientChar == 'b')) {
                RecipeChoice choice = Tools.parseRecipeChoice(line.substring(2), ParseBit.NONE);
                if (choice == null) {
                    return false;
                }

                Flags ingredientFlags = new Flags();
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
                        recipe.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoiceWithItems(recipe.getIngredient(ingredientChar), items));
                    }
                } else {
                    if (!recipe.hasIngredient(ingredientChar)) {
                        recipe.setIngredient(ingredientChar, choice);
                    } else {
                        recipe.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoices(recipe.getIngredient(ingredientChar), choice));
                    }
                }
            } else {
                RecipeChoice ingredientChoice = Tools.parseRecipeChoice(line, ParseBit.NO_WARNINGS);
                if (ingredientChoice == null) {
                    return ErrorReporter.getInstance().error("Recipe has an invalid ingredient, needs fixing!");
                }

                recipe.setIngredientChoice(ingredientChoice);
                reader.nextLine();

                String potionLine = reader.getLine();
                RecipeChoice potionChoice = Tools.parseRecipeChoice(potionLine, ParseBit.NO_WARNINGS);
                if (potionChoice == null) {
                    return ErrorReporter.getInstance().error("Recipe has an invalid potion, needs fixing!");
                }

                recipe.setPotionChoice(potionChoice);
                reader.nextLine();
            }
        }

        List<ItemResult> results = new ArrayList<>();

        if (!parseResults(recipe, results)) { // results have errors
            return false;
        }

        recipe.setResults(results); // done with results, set 'em

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
