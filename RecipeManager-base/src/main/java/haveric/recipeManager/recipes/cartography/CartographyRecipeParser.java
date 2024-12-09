package haveric.recipeManager.recipes.cartography;

import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.AirChoice;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class CartographyRecipeParser extends BaseRecipeParser {
    public CartographyRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CartographyRecipe recipe = new CartographyRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

        while (!reader.lineIsResult()) {
            String line = reader.getLine();
            String lineChars = line.substring(0, 2).trim();
            char ingredientChar = lineChars.charAt(0);

            if (lineChars.length() == 1 && (ingredientChar == 'a' || ingredientChar == 'b')) {
                RecipeChoice choice = Tools.parseRecipeChoice(line.substring(2), ParseBit.NONE);
                if (choice == null || choice instanceof AirChoice) {
                    return false;
                }

                Flags ingredientFlags = createFlaggableFlags(choice);
                reader.parseFlags(ingredientFlags, FlagBit.INGREDIENT);

                if (ingredientFlags.hasFlags()) {
                    List<ItemStack> items = parseChoiceToItems(choice, ingredientFlags);

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
            } else {
                // get the ingredients
                String[] ingredientsRaw = reader.getLine().split("\\+");

                RecipeChoice primaryChoice = Tools.parseRecipeChoice(ingredientsRaw[0], ParseBit.NO_WARNINGS);
                if (primaryChoice == null) {
                    return false;
                }

                recipe.setPrimaryIngredient(primaryChoice);

                if (ingredientsRaw.length > 1) {
                    RecipeChoice secondaryChoice = Tools.parseRecipeChoice(ingredientsRaw[1], ParseBit.NO_WARNINGS);
                    if (secondaryChoice == null) {
                        return false;
                    }

                    recipe.setSecondaryIngredient(secondaryChoice);
                }

                reader.nextLine();
            }
        }

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
}
