package haveric.recipeManager.recipes.craft;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.*;

public class CraftRecipeParser extends BaseRecipeParser {
    public CraftRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CraftRecipe recipe = new CraftRecipe(fileFlags); // create recipe and copy flags from file

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

        String groupLine = reader.getLine();
        if (groupLine.toLowerCase().startsWith("group ")) {
            groupLine = groupLine.substring("group ".length()).trim();
            recipe.setGroup(groupLine);

            reader.nextLine();
        }

        String categoryLine = reader.getLine();
        if (categoryLine.toLowerCase().startsWith("category ")) {
            categoryLine = categoryLine.substring("category ".length()).trim();

            if (Supports.categories()) {
                try {
                    CraftingBookCategory category = CraftingBookCategory.valueOf(categoryLine);
                    recipe.setCategory(category.name());
                } catch (IllegalArgumentException e) {
                    ErrorReporter.getInstance().warning("Category is invalid. Category: " + categoryLine + " ignored. Valid values: " + Arrays.toString(CraftingBookCategory.values()));
                }
            } else {
                ErrorReporter.getInstance().warning("Category is supported on 1.19.3 or newer only. Category: " + categoryLine + " ignored.");
            }

            reader.nextLine();
        }

        List<String> choicePatternString = new ArrayList<>();

        String patternFormatLine = reader.getLine().toLowerCase();
        if (patternFormatLine.startsWith("pattern") || patternFormatLine.startsWith("a ")) {
            Map<Character, Integer> ingredientCharacters = new HashMap<>();
            Map<Character, RecipeChoice> ingredientRecipeChoiceMap = new HashMap<>();
            if (patternFormatLine.startsWith("pattern")) {
                String[] patternLines = patternFormatLine.substring("pattern".length()).split("\\|", 3);

                for (String patternLine : patternLines) {
                    patternLine = patternLine.trim();

                    if (patternLine.length() > 3) {
                        ErrorReporter.getInstance().warning("Pattern line has more than three characters: " + patternLine + ". Using only the first three: " + patternLine.substring(0, 3));
                        patternLine = patternLine.substring(0, 3);
                    }
                    choicePatternString.add(patternLine);

                    for (char c : patternLine.toCharArray()) {
                        if (!ingredientCharacters.containsKey(c)) {
                            ingredientCharacters.put(c, 1);
                        } else {
                            ingredientCharacters.put(c, ingredientCharacters.get(c) + 1);
                        }
                    }
                }

                reader.nextLine();
            } else {
                // Default to a single item
                choicePatternString.add("a");
                ingredientCharacters.put('a', 1);
            }

            int ingredientsNum = 0;
            while (!reader.lineIsResult()) {
                String line = reader.getLine();
                char ingredientChar = line.substring(0, 2).trim().charAt(0);

                if (ingredientCharacters.containsKey(ingredientChar)) {
                    RecipeChoice choice = Tools.parseRecipeChoice(line.substring(2), ParseBit.NO_WARNINGS);
                    if (choice == null) {
                        return false;
                    }

                    ingredientsNum += ingredientCharacters.get(ingredientChar);

                    FlaggableRecipeChoice flaggable = new FlaggableRecipeChoice();
                    flaggable.setChoice(choice);
                    Flags ingredientFlags = flaggable.getFlags();

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

                        if (!ingredientRecipeChoiceMap.containsKey(ingredientChar)) {
                            ingredientRecipeChoiceMap.put(ingredientChar, new RecipeChoice.ExactChoice(items));
                        } else {
                            ingredientRecipeChoiceMap.put(ingredientChar, ToolsRecipeChoice.mergeRecipeChoiceWithItems(ingredientRecipeChoiceMap.get(ingredientChar), items));
                        }
                    } else {
                        if (!ingredientRecipeChoiceMap.containsKey(ingredientChar)) {
                            ingredientRecipeChoiceMap.put(ingredientChar, choice);
                        } else {
                            ingredientRecipeChoiceMap.put(ingredientChar, ToolsRecipeChoice.mergeRecipeChoices(ingredientRecipeChoiceMap.get(ingredientChar), choice));
                        }
                    }
                } else {
                    ErrorReporter.getInstance().warning("Character " + ingredientChar + " not found in shape.");
                    reader.nextLine();
                }
            }

            if (ingredientsNum == 0) { // no ingredients were processed
                return ErrorReporter.getInstance().error("Recipe doesn't have ingredients!", "Consult '" + Files.FILE_INFO_BASICS + "' for proper recipe syntax.");
            } else if (ingredientsNum == 2) {
                if (!conditionEvaluator.checkRecipeChoices(ingredientRecipeChoiceMap)) {
                    return false;
                }
            }

            // Add extra air to fill rectangle
            if (choicePatternString.size() > 1) {
                int min = choicePatternString.get(0).length();
                int max = min;

                for (int i = 1; i < choicePatternString.size(); i++) {
                    String characters = choicePatternString.get(i);
                    max = Math.max(characters.length(), max);
                    min = Math.min(characters.length(), min);
                }

                char availableChar = '0';
                for (char letter = 'a'; letter < 'z'; letter ++) {
                    if (!ingredientRecipeChoiceMap.containsKey(letter)) {
                        availableChar = letter;
                        break;
                    }
                }

                if (min < max) {
                    for (int i = 0; i < choicePatternString.size(); i++) {
                        String shape = choicePatternString.get(i);
                        for (int j = shape.length(); j < max; j++) {
                            shape += availableChar;
                        }
                        choicePatternString.set(i, shape);
                    }

                    ingredientRecipeChoiceMap.put(availableChar, null);
                }
            }

            recipe.setChoicePattern(choicePatternString.toArray(new String[0]));
            recipe.setIngredientsRecipeChoiceMap(ingredientRecipeChoiceMap);
        } else {
            Map<Character, RecipeChoice> ingredientsChoiceMap = new HashMap<>();
            char characterKey = 'a';

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
                    RecipeChoice choice = Tools.parseRecipeChoice(split[i], ParseBit.NO_WARNINGS);

                    if (choice == null) { // No items found
                        ingredientErrors = true;
                    }

                    choice = ToolsRecipeChoice.convertAirMaterialChoiceToNull(choice);

                    if (!ingredientErrors) {
                        if (choicePatternString.size() == rows) {
                            choicePatternString.add("" + characterKey);
                        } else {
                            choicePatternString.set(rows, choicePatternString.get(rows) + characterKey);
                        }

                        ingredientsChoiceMap.put(characterKey, choice);

                        characterKey++;
                        ingredientsNum++;
                    }
                }

                rows++;
            }

            if (ingredientErrors) { // invalid ingredients found
                return ErrorReporter.getInstance().error("Recipe has some invalid ingredients, fix them!");
            } else if (ingredientsNum == 0) { // no ingredients were processed
                return ErrorReporter.getInstance().error("Recipe doesn't have ingredients!", "Consult '" + Files.FILE_INFO_BASICS + "' for proper recipe syntax.");
            } else if (ingredientsNum == 2) {
                if (!conditionEvaluator.checkRecipeChoices(ingredientsChoiceMap)) {
                    return false;
                }
            }

            // Add extra air to fill rectangle
            if (choicePatternString.size() > 1) {
                int min = choicePatternString.get(0).length();
                int max = min;

                for (int i = 1; i < choicePatternString.size(); i++) {
                    String characters = choicePatternString.get(i);
                    max = Math.max(characters.length(), max);
                    min = Math.min(characters.length(), min);
                }

                if (min < max) {
                    for (int i = 0; i < choicePatternString.size(); i++) {
                        String shape = choicePatternString.get(i);
                        for (int j = shape.length(); j < max; j++) {
                            shape += characterKey;
                        }
                        choicePatternString.set(i, shape);
                    }

                    ingredientsChoiceMap.put(characterKey, null); // Null = Air
                }
            }

            recipe.setChoicePattern(choicePatternString.toArray(new String[0]));
            recipe.setIngredientsRecipeChoiceMap(ingredientsChoiceMap);
        }

        // get results
        List<ItemResult> results = new ArrayList<>();

        if (!parseResults(recipe, results)) { // results have errors
            return false;
        }

        recipe.setResults(results); // done with results, set 'em

        if (!recipe.hasValidResult()) {
            return ErrorReporter.getInstance().error("Recipe must have at least one non-air result!");
        }

        // check if the recipe already exists...
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return recipe.hasFlag(FlagType.REMOVE);
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());
        
        return true; // successfully added
    }
}
