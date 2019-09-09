package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class AnvilRecipeParser extends BaseRecipeParser {
    public AnvilRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        AnvilRecipe recipe = new AnvilRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        // get the ingredient
        String[] split = reader.getLine().split("%");
        if (split.length == 0) {
            return ErrorReporter.getInstance().error("Recipe needs an ingredient!");
        }

        // get the ingredients
        String[] ingredientsRaw = split[0].split("\\+");

        int numIngredients = ingredientsRaw.length;
        if (numIngredients < 1) {
            return ErrorReporter.getInstance().error("Recipe does not have any ingredients.");
        } else if (numIngredients > 2) {
            return ErrorReporter.getInstance().error("Recipe has too many ingredients. Needs 1 or 2.");
        } else {
            List<Material> primary = Tools.parseChoice(ingredientsRaw[0], ParseBit.NONE);

            if (primary == null) {
                return ErrorReporter.getInstance().error("Recipe needs a primary ingredient!");
            }

            if (primary.contains(Material.AIR)) {
                return ErrorReporter.getInstance().error("Recipe does not accept AIR as primary ingredient!");
            }
            recipe.setPrimaryIngredient(primary);

            List<Material> secondary;
            if (numIngredients == 1) {
                secondary = new ArrayList<>();
                secondary.add(Material.AIR);
            } else {
                secondary = Tools.parseChoice(ingredientsRaw[1], ParseBit.NONE);
                if (secondary == null) {
                    return ErrorReporter.getInstance().error("Recipe needs a secondary ingredient!");
                }
            }

            recipe.setSecondaryIngredient(secondary);
        }

        if (split.length > 1) {
            String repairString = split[1].trim();

            // Skip if empty
            if (!repairString.isEmpty()) {
                try {
                    int repairCost = Integer.parseInt(repairString);

                    recipe.setRepairCost(repairCost);

                    if (!Version.has1_11Support()) {
                        ErrorReporter.getInstance().warning("Repair Cost is only supported in 1.11 or newer.");
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Recipe has invalid repair cost: " + split[1] + ". Defaulting to 0.");
                }
            }
        }

        if (split.length > 2) {
            String renameText = split[2].trim().toLowerCase();

            // Skip if empty
            if (!renameText.isEmpty()) {
                if (renameText.equals("allowrename") || renameText.equals("true")) {
                    recipe.setRenamingAllowed(true);
                } else {
                    ErrorReporter.getInstance().warning("Invalid rename attribute: " + split[2] + ". Defaulting to false. Accepted values: allowrename, true, false.");
                }
            }
        }

        if (split.length > 3) {
            try {
                double anvilDamageChance = Double.parseDouble(split[3].trim());

                if (anvilDamageChance < 0) {
                    ErrorReporter.getInstance().warning("Anvil damage chance cannot be below 0: " + split[3] + ". Allowed values from 0-100 (decimal values allowed). Defaulting to 0.");
                    anvilDamageChance = 0;
                } else if (anvilDamageChance > 100) {
                    ErrorReporter.getInstance().warning("Anvil damage chance cannot be above 100: " + split[3] + ". Allowed values from 0-100 (decimal values allowed). Defaulting to 100.");
                    anvilDamageChance = 100;
                }

                recipe.setAnvilDamageChance(anvilDamageChance);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("Invalid anvil damage chance: " + split[3] + ". Allowed values from 0-100 (decimal values allowed). Defaulting to 12.");
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
