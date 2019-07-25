package haveric.recipeManager.recipes.craft;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.RecipeFileReader;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.util.ParseBit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftRecipeParser extends BaseRecipeParser {


    public CraftRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        super(reader, recipeName, fileFlags, recipeRegistrator);
    }

    @Override
    public boolean parseRecipe(int directiveLine) throws Exception {
        CraftRecipe recipe;
        if (Version.has1_13Support()) {
            recipe = new CraftRecipe1_13(fileFlags);
        } else {
            recipe = new CraftRecipe(fileFlags); // create recipe and copy flags from file
        }
        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        Map<Character, List<Material>> ingredientsChoiceMap = new HashMap<>();
        char characterKey = 'a';
        List<String> choiceShapeString = new ArrayList<>();

        ItemStack[] ingredients = new ItemStack[9];
        String[] split;

        int rows = 0;
        int ingredientsNum = 0;
        boolean ingredientErrors = false;

        while (rows < 3) { // loop until we find 3 rows of ingredients (or bump into the result along the way)
            if (rows > 0) {
                reader.nextLine();
            }

            if (reader.getLine() == null) {
                if (rows == 0) {
                    return ErrorReporter.getInstance().error("No ingredients defined!");
                }

                break;
            }

            if (reader.lineIsResult()) { // if we bump into the result prematurely (smaller recipes)
                break;
            }

            split = reader.getLine().split("\\+"); // split ingredients by the + sign
            int rowLen = split.length;

            if (rowLen > 3) { // if we find more than 3 ingredients warn the user and limit it to 3
                rowLen = 3;
                ErrorReporter.getInstance().warning("You can't have more than 3 ingredients on a row, ingredient(s) ignored.", "Remove the extra ingredient(s).");
            }

            for (int i = 0; i < rowLen; i++) { // go through each ingredient on the line
                if (Version.has1_13Support()) {
                    List<Material> choices = Tools.parseChoice(split[i], ParseBit.NONE);

                    if (choices == null || choices.isEmpty()) { // No items found
                        ingredientErrors = true;
                    }

                    if (!ingredientErrors) {
                        if (choiceShapeString.size() == rows) {
                            choiceShapeString.add("" + characterKey);
                        } else {
                            choiceShapeString.set(rows, choiceShapeString.get(rows) + characterKey);
                        }

                        ingredientsChoiceMap.put(characterKey, choices);

                        characterKey++;
                        ingredientsNum++;
                    }
                } else {
                    ItemStack item = Tools.parseItem(split[i], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
                    if (item == null) { // invalid item
                        ingredientErrors = true;
                    }

                    // no point in adding more ingredients if there are errors
                    if (!ingredientErrors) {
                        // Minecraft 1.11 required air ingredients to include a data value of 0
                        if ((Version.has1_11Support() && !Version.has1_12Support()) || item.getType() != Material.AIR) {
                            ingredients[(rows * 3) + i] = item;
                            ingredientsNum++;
                        }
                    }
                }
            }

            rows++;
        }

        if (ingredientErrors) { // invalid ingredients found
            ErrorReporter.getInstance().error("Recipe has some invalid ingredients, fix them!");
            return false;
        } else if (ingredientsNum == 0) { // no ingredients were processed
            return ErrorReporter.getInstance().error("Recipe doesn't have ingredients!", "Consult '" + Files.FILE_INFO_BASICS + "' for proper recipe syntax.");
        } else if (ingredientsNum == 2) {
            if (Version.has1_13Support()) {
                if (!conditionEvaluator.checkMaterialChoices(ingredientsChoiceMap)) {
                    return false;
                }
            } else {
                if (!conditionEvaluator.checkIngredients(ingredients)) {
                    return false;
                }
            }
        }

        // done with ingredients, set 'em
        if (Version.has1_13Support()) {

            // Add extra air to fill rectangle
            if (choiceShapeString.size() > 1) {
                int min = choiceShapeString.get(0).length();
                int max = min;

                for (int i = 1; i < choiceShapeString.size(); i++) {
                    String characters = choiceShapeString.get(i);
                    max = Math.max(characters.length(), max);
                    min = Math.min(characters.length(), min);
                }

                if (min < max) {
                    for (int i = 0; i < choiceShapeString.size(); i++) {
                        String shape = choiceShapeString.get(i);
                        for (int j = shape.length(); j < max; j++) {
                            shape += characterKey;
                        }
                        choiceShapeString.set(i, shape);
                    }

                    List<Material> airList = new ArrayList<>();
                    airList.add(Material.AIR);

                    ingredientsChoiceMap.put(characterKey, airList);
                }
            }


            recipe.setChoiceShape(choiceShapeString.toArray(new String[0]));
            recipe.setIngredientsChoiceMap(ingredientsChoiceMap);
        } else {
            recipe.setIngredients(ingredients);
        }

        if (recipe.hasFlag(FlagType.REMOVE) && !Version.has1_12Support()) { // for mc1.12, matching requires outcome too...
            reader.nextLine(); // Skip the results line, if it exists
        } else {
            // get results
            List<ItemResult> results = new ArrayList<>();

            if (!parseResults(recipe, results)) { // results have errors
                return false;
            }

            recipe.setResults(results); // done with results, set 'em

            if (!recipe.hasValidResult()) {
                return ErrorReporter.getInstance().error("Recipe must have at least one non-air result!");
            }
        }

        // check if the recipe already exists...
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return false;
        }

        if (recipeName != null && !recipeName.equals("")) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());
        
        return true; // successfully added
    }


}
