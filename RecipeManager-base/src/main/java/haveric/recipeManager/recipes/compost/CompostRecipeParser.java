package haveric.recipeManager.recipes.compost;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.AirChoice;
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

public class CompostRecipeParser extends BaseRecipeParser {
    public CompostRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CompostRecipe recipe = new CompostRecipe(fileFlags);

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

        while (!reader.lineIsResult()) {
            // get the ingredient
            String[] splitIngredient = reader.getLine().split("%");

            String materialsValue = splitIngredient[0].trim();

            // There's no needed logic for shapes here, so trim the shape declaration
            if (materialsValue.startsWith("a ")) {
                materialsValue = materialsValue.substring(2);
            }

            RecipeChoice choice = Tools.parseRecipeChoice(materialsValue, ParseBit.NONE);
            if (choice == null || choice instanceof AirChoice) {
                return false;
            }

            FlaggableRecipeChoice flaggable = new FlaggableRecipeChoice();
            flaggable.setChoice(choice);
            Flags ingredientFlags = flaggable.getFlags();

            reader.parseFlags(ingredientFlags, FlagBit.INGREDIENT);

            if (ingredientFlags.hasFlags()) {
                List<ItemStack> items = new ArrayList<>();
                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    List<Material> materials = materialChoice.getChoices();

                    for (Material material : materials) {
                        Args a = ArgBuilder.create().result(new ItemStack(material)).build();
                        ingredientFlags.sendCrafted(a, true);

                        items.add(a.result().getItemStack());
                    }
                } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                    List<ItemStack> exactItems = exactChoice.getChoices();

                    for (ItemStack exactItem : exactItems) {
                        Args a = ArgBuilder.create().result(exactItem).build();
                        ingredientFlags.sendCrafted(a, true);

                        items.add(a.result().getItemStack());
                    }
                }

                recipe.addIngredientChoiceItems(items);
            } else {
                recipe.setIngredientChoice(ToolsRecipeChoice.mergeRecipeChoices(recipe.getIngredientChoice(), choice));
            }

            parseArgs(recipe, splitIngredient);
        }

        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);

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

        recipe.setResult(result.getItemStack());

        if (isRemove) { // un-ignore result errors
            ErrorReporter.getInstance().setIgnoreErrors(false);
        }

        // check if the recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return isRemove;
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true;
    }

    private void parseArgs(CompostRecipe recipe, String[] splitIngredient) {
        if (splitIngredient.length > 1) {
            try {
                double chance = Double.parseDouble(splitIngredient[1].trim());

                if (chance > 0 && chance <= 100) {
                    recipe.setLevelSuccessChance(chance);
                } else {
                    ErrorReporter.getInstance().warning("Invalid level success chance: " + splitIngredient[1] + ". Defaulting to 100.", "Allowed values > 0, <= 100 (Decimal values allowed).");
                }

            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Invalid level success chance: " + splitIngredient[1] + ". Defaulting to 100.", "Allowed values > 0, <= 100 (Decimal values allowed).");
            }
        }

        if (splitIngredient.length > 2) {
            try {
                double levels = Double.parseDouble(splitIngredient[2].trim());

                if (levels > 0 && levels <= 7) {
                    recipe.setLevels(levels);
                } else {
                    ErrorReporter.getInstance().warning("Invalid levels: " + splitIngredient[1] + ". Defaulting to 1.", "Allowed values > 0, <= 7 (Decimal values allowed).");
                }
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().warning("Invalid levels: " + splitIngredient[1] + ". Defaulting to 1.", "Allowed values > 0, <= 7 (Decimal values allowed).");
            }
        }
    }
}
