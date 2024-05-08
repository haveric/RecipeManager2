package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.ErrorReporter;
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

public class AnvilRecipeParser extends BaseRecipeParser {
    public AnvilRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        AnvilRecipe recipe = new AnvilRecipe(fileFlags);

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

        while (!reader.lineIsResult()) {
            String[] lineSplit = reader.getLine().split("%");
            String lineChars = lineSplit[0].substring(0, 2).trim();
            char ingredientChar = lineChars.charAt(0);

            if (lineChars.length() == 1 && (ingredientChar == 'a' || ingredientChar == 'b')) {
                RecipeChoice choice = Tools.parseRecipeChoice(lineSplit[0].substring(2), ParseBit.NONE);
                if (choice == null) {
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

                            items.add(a.result());
                        }
                    } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
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
            } else {
                // get the ingredients
                String[] ingredientsRaw = lineSplit[0].split("\\+", 2);

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

            parseArgs(recipe, lineSplit);
        }

        return parseAndSetResults(recipe, directiveLine);
    }

    private void parseArgs(AnvilRecipe recipe, String[] split) {
        if (split.length > 1) {
            String repairString = split[1].trim();

            // Skip if empty
            if (!repairString.isEmpty()) {
                try {
                    int repairCost = Integer.parseInt(repairString);

                    recipe.setRepairCost(repairCost);
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
                } else if (renameText.equals("false")) {
                    recipe.setRenamingAllowed(false);
                } else {
                    ErrorReporter.getInstance().warning("Invalid rename attribute: " + renameText + ". Defaulting to false. Accepted values: allowrename, true, false.");
                }
            }
        }

        if (split.length > 3) {
            try {
                double anvilDamageChance = Double.parseDouble(split[3].trim());

                if (anvilDamageChance < 0) {
                    ErrorReporter.getInstance().warning("Anvil damage chance cannot be below 0: " + split[3] + ". Allowed values from 0-300 (decimal values allowed). Defaulting to 0.");
                    anvilDamageChance = 0;
                } else if (anvilDamageChance > 300) {
                    ErrorReporter.getInstance().warning("Anvil damage chance cannot be above 300: " + split[3] + ". Allowed values from 0-300 (decimal values allowed). Defaulting to 300.");
                    anvilDamageChance = 300;
                }

                recipe.setAnvilDamageChance(anvilDamageChance);
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("Invalid anvil damage chance: " + split[3] + ". Allowed values from 0-300 (decimal values allowed). Defaulting to 12.");
            }
        }
    }

    private boolean parseAndSetResults(AnvilRecipe recipe, int directiveLine) {
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
