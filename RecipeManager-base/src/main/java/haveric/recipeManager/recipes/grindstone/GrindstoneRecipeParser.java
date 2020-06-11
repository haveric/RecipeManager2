package haveric.recipeManager.recipes.grindstone;

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

public class GrindstoneRecipeParser extends BaseRecipeParser {
    public GrindstoneRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

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
