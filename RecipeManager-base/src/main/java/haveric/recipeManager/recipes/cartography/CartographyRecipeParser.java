package haveric.recipeManager.recipes.cartography;

import haveric.recipeManager.common.util.ParseBit;
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

public class CartographyRecipeParser extends BaseRecipeParser {
    public CartographyRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CartographyRecipe recipe = new CartographyRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        while (!reader.lineIsResult()) {
            String line = reader.getLine();
            char ingredientChar = line.substring(0, 2).trim().charAt(0);

            if (ingredientChar == 'a' || ingredientChar == 'b') {
                List<Material> choices = Tools.parseChoice(line.substring(2), ParseBit.NONE);
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

                    if (!recipe.hasIngredient(ingredientChar)) {
                        recipe.setIngredient(ingredientChar, new RecipeChoice.ExactChoice(items));
                    } else {
                        recipe.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoiceWithItems(recipe.getIngredient(ingredientChar), items));
                    }
                } else {
                    if (!recipe.hasIngredient(ingredientChar)) {
                        recipe.setIngredient(ingredientChar, new RecipeChoice.MaterialChoice(choices));
                    } else {
                        recipe.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoiceWithMaterials(recipe.getIngredient(ingredientChar), choices));
                    }
                }
            } else {
                // get the ingredients
                String[] ingredientsRaw = reader.getLine().split("\\+");

                List<List<Material>> choicesList = parseIngredients(ingredientsRaw, recipe.getType(), 2, true);
                if (choicesList == null || choicesList.isEmpty()) {
                    return false;
                }

                recipe.setPrimaryIngredient(new RecipeChoice.MaterialChoice(choicesList.get(0)));
                if (choicesList.size() > 1) {
                    recipe.setSecondaryIngredient(new RecipeChoice.MaterialChoice(choicesList.get(1)));
                }
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
