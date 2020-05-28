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

        reader.parseFlags(recipe.getFlags());

        if (recipe instanceof BrewRecipe1_13) {
            BrewRecipe1_13 brewRecipe1_13 = (BrewRecipe1_13) recipe;

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
                    reader.parseFlags(ingredientFlags);

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

                        if (!brewRecipe1_13.hasIngredient(ingredientChar)) {
                            brewRecipe1_13.setIngredient(ingredientChar, new RecipeChoice.ExactChoice(items));
                        } else {
                            brewRecipe1_13.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoiceWithItems(brewRecipe1_13.getIngredient(ingredientChar), items));
                        }
                    } else {
                        if (!brewRecipe1_13.hasIngredient(ingredientChar)) {
                            brewRecipe1_13.setIngredient(ingredientChar, choice);
                        } else {
                            brewRecipe1_13.setIngredient(ingredientChar, ToolsItem.mergeRecipeChoices(brewRecipe1_13.getIngredient(ingredientChar), choice));
                        }
                    }
                } else {
                    RecipeChoice ingredientChoice = Tools.parseRecipeChoice(line, ParseBit.NO_WARNINGS);
                    if (ingredientChoice == null) {
                        return ErrorReporter.getInstance().error("Recipe has an invalid ingredient, needs fixing!");
                    }

                    brewRecipe1_13.setIngredientChoice(ingredientChoice);
                    reader.nextLine();

                    String potionLine = reader.getLine();
                    RecipeChoice potionChoice = Tools.parseRecipeChoice(potionLine, ParseBit.NO_WARNINGS);
                    if (potionChoice == null) {
                        return ErrorReporter.getInstance().error("Recipe has an invalid potion, needs fixing!");
                    }

                    brewRecipe1_13.setPotionChoice(potionChoice);
                    reader.nextLine();
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

            reader.nextLine();

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
