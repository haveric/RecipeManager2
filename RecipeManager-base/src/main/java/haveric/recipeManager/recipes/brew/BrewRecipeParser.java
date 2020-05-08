package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipeParser extends BaseRecipeParser {
    public BrewRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BaseBrewRecipe recipe;

        if (Version.has1_13Support()) {
            recipe = new BrewRecipe1_13();
        } else {
            recipe = new BrewRecipe();
        }

        this.reader.parseFlags(recipe.getFlags());

        if (recipe instanceof BrewRecipe1_13) {
            BrewRecipe1_13 brewRecipe1_13 = (BrewRecipe1_13) recipe;

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

                        if (!brewRecipe1_13.hasIngredient(ingredientChar)) {
                            brewRecipe1_13.setIngredient(ingredientChar, new RecipeChoice.ExactChoice(items));
                        } else {
                            brewRecipe1_13.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoiceWithItems(brewRecipe1_13.getIngredient(ingredientChar), items));
                        }
                    } else {
                        if (!brewRecipe1_13.hasIngredient(ingredientChar)) {
                            brewRecipe1_13.setIngredient(ingredientChar, new RecipeChoice.MaterialChoice(choices));
                        } else {
                            brewRecipe1_13.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoiceWithMaterials(brewRecipe1_13.getIngredient(ingredientChar), choices));
                        }
                    }
                } else {
                    String[] ingredientLine = { line };

                    List<Material> choices = parseIngredient(ingredientLine, recipe.getType());
                    if (choices == null || choices.isEmpty()) {
                        return ErrorReporter.getInstance().error("Recipe has an invalid ingredient, needs fixing!");
                    }

                    brewRecipe1_13.setIngredientChoice(choices);

                    reader.nextLine();

                    String[] potionLine = { reader.getLine() };
                    List<Material> choicesPotion = parseIngredient(potionLine, recipe.getType());
                    if (choicesPotion == null || choicesPotion.isEmpty()) {
                        return ErrorReporter.getInstance().error("Recipe has an invalid potion, needs fixing!");
                    }

                    brewRecipe1_13.setPotionChoice(choices);
                }
            }
        } else {
            BrewRecipe brewRecipe = (BrewRecipe) recipe;
            if (reader.getLine() == null || reader.lineIsResult()) {
                return ErrorReporter.getInstance().error("No ingredient defined!");
            }

            ItemStack ingredient = Tools.parseItem(reader.getLine(), RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
            if (ingredient == null) {
                return ErrorReporter.getInstance().error("Recipe has an invalid ingredient, needs fixing!");
            }

            brewRecipe.setIngredient(ingredient);

            this.reader.nextLine();

            if (reader.getLine() == null || reader.lineIsResult()) {
                return ErrorReporter.getInstance().error("No potion defined!");
            }

            ItemStack potion = Tools.parseItem(reader.getLine(), RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
            if (potion == null) {
                return ErrorReporter.getInstance().error("Recipe has an invalid potion, needs fixing!");
            }

            brewRecipe.setPotion(potion);
        }

        List<ItemResult> results = new ArrayList<>();

        if (!parseResults(recipe, results)) { // results have errors
            return false;
        }

        recipe.setResults(results); // done with results, set 'em

        // check if the recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, this.reader.getFileName())) {
            return recipe.hasFlag(FlagType.REMOVE);
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, this.reader.getFileName());

        return true;
    }

}
