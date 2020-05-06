package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombineRecipeParser extends BaseRecipeParser {
    public CombineRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BaseCombineRecipe recipe;
        if (Version.has1_13Support()) {
            recipe = new CombineRecipe1_13(fileFlags);
        } else {
            recipe = new CombineRecipe(fileFlags); // create recipe and copy flags from file
        }

        reader.parseFlags(recipe.getFlags()); // parse recipe's flags

        String shapeFormatLine = reader.getLine().toLowerCase();
        if (shapeFormatLine.startsWith("shape")) {
            if (recipe instanceof CombineRecipe1_13) {
                Map<Character, Integer> ingredientCharacters = new HashMap<>();
                Map<Character, RecipeChoice> ingredientRecipeChoiceMap = new HashMap<>();
                String shapeLine = shapeFormatLine.substring("shape".length()).trim();

                if (shapeLine.length() > 9) {
                    ErrorReporter.getInstance().warning("Shape has more than 9 characters: " + shapeLine + ". Using only the nine three: " + shapeLine.substring(0, 9));
                    shapeLine = shapeLine.substring(0, 9);
                }

                ((CombineRecipe1_13) recipe).setShape(shapeLine);

                for (char c : shapeLine.toCharArray()) {
                    if (!ingredientCharacters.containsKey(c)) {
                        ingredientCharacters.put(c, 1);
                    } else {
                        ingredientCharacters.put(c, ingredientCharacters.get(c) + 1);
                    }
                }

                reader.nextLine();

                int ingredientsNum = 0;
                while (!reader.lineIsResult()) {
                    String line = reader.getLine();
                    char ingredientChar = line.substring(0, 2).trim().charAt(0);

                    if (ingredientCharacters.containsKey(ingredientChar)) {
                        List<Material> choices = Tools.parseChoice(line.substring(2), ParseBit.NONE);
                        if (choices == null || choices.isEmpty()) {
                            return false;
                        }

                        ingredientsNum += ingredientCharacters.get(ingredientChar);

                        Flags ingredientFlags = new Flags();
                        reader.parseFlags(ingredientFlags);

                        if (ingredientFlags.hasFlags()) {
                            List<ItemStack> items = new ArrayList<>();
                            for (Material choice : choices) {
                                Args a = ArgBuilder.create().result(new ItemStack(choice)).build();
                                ingredientFlags.sendCrafted(a, true);

                                items.add(a.result());
                            }

                            if (!ingredientRecipeChoiceMap.containsKey(ingredientChar)) {
                                ingredientRecipeChoiceMap.put(ingredientChar, new RecipeChoice.ExactChoice(items));
                            } else {
                                ingredientRecipeChoiceMap.put(ingredientChar, ToolsItem.mergeRecipeChoiceWithItems(ingredientRecipeChoiceMap.get(ingredientChar), items));
                            }
                        } else {
                            if (!ingredientRecipeChoiceMap.containsKey(ingredientChar)) {
                                ingredientRecipeChoiceMap.put(ingredientChar, new RecipeChoice.MaterialChoice(choices));
                            } else {
                                ingredientRecipeChoiceMap.put(ingredientChar, ToolsItem.mergeRecipeChoiceWithMaterials(ingredientRecipeChoiceMap.get(ingredientChar), choices));
                            }
                        }
                    } else {
                        ErrorReporter.getInstance().warning("Character " + ingredientChar + " not found in shape.");
                    }
                }

                if (ingredientsNum == 0) { // no ingredients were processed
                    return ErrorReporter.getInstance().error("Recipe doesn't have ingredients!", "Consult '" + Files.FILE_INFO_BASICS + "' for proper recipe syntax.");
                } else if (ingredientsNum == 2) {
                    if (!conditionEvaluator.checkRecipeChoices(ingredientRecipeChoiceMap)) {
                        return false;
                    }
                }

                List<RecipeChoice> ingredientChoiceList = new ArrayList<>();
                for (Map.Entry<Character, RecipeChoice> entry : ingredientRecipeChoiceMap.entrySet()) {
                    RecipeChoice choice = entry.getValue();

                    int num = ingredientCharacters.get(entry.getKey());

                    for (int i = 0; i < num; i++) {
                        ingredientChoiceList.add(choice.clone());
                    }
                }

                ((CombineRecipe1_13) recipe).setIngredientChoiceList(ingredientChoiceList);
            } else {
                return ErrorReporter.getInstance().error("Shape is only supported on 1.13 or newer servers.");
            }
        } else {
            // get the ingredients
            String[] ingredientsRaw = reader.getLine().split("\\+");

            if (recipe instanceof CombineRecipe1_13) {
                List<RecipeChoice> ingredientChoiceList = new ArrayList<>();

                StringBuilder shape = new StringBuilder();
                char letter = 'a';
                int items = 0;
                for (String str : ingredientsRaw) {
                    Map<List<Material>, Integer> choiceAmountMap = Tools.parseChoiceWithAmount(str, ParseBit.NONE);

                    // We're always returning only one item, so this should always work
                    Map.Entry<List<Material>, Integer> entry = choiceAmountMap.entrySet().iterator().next();
                    List<Material> choices = entry.getKey();

                    if (choices == null || choices.isEmpty()) {
                        return ErrorReporter.getInstance().error("Ingredient cannot be empty: " + str, "Check for incorrect spelling or missing tags or aliases.");
                    }

                    if (choices.contains(Material.AIR)) {
                        return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
                    }

                    int newAmount;
                    int originalAmount = entry.getValue();

                    if (originalAmount <= 0) {
                        ErrorReporter.getInstance().warning("Recipe must have a positive amount. Defaulting to 1");
                        originalAmount = 1;
                    }

                    if (items + originalAmount > 9) {
                        newAmount = 9 - items;

                        int ignoredAmount = originalAmount - newAmount;
                        ErrorReporter.getInstance().warning("Combine recipes can't have more than 9 ingredients! Extra ingredient(s) ignored: " + str + " x" + ignoredAmount, "If you're using stacks make sure they don't exceed 9 items in total.");
                    } else {
                        newAmount = originalAmount;
                    }

                    items += newAmount;

                    for (int i = 0; i < newAmount; i++) {
                        ingredientChoiceList.add(new RecipeChoice.MaterialChoice(choices));
                        shape.append(letter);
                    }

                    letter ++;
                }
                ((CombineRecipe1_13) recipe).setShape(shape.toString());
                ((CombineRecipe1_13) recipe).setIngredientChoiceList(ingredientChoiceList);
            } else {
                List<ItemStack> ingredients = new ArrayList<>();
                ItemStack item;
                int items = 0;

                for (String str : ingredientsRaw) {
                    item = Tools.parseItem(str, RMCVanilla.DATA_WILDCARD, ParseBit.NO_META);

                    if (item == null || item.getType() == Material.AIR) {
                        continue;
                    }

                    if (items < 9) {
                        int originalAmount = item.getAmount();
                        if (items + originalAmount > 9) {
                            int newAmount = 9 - items;
                            items += newAmount;

                            item.setAmount(newAmount);
                            ingredients.add(item);

                            int ignoredAmount = originalAmount - newAmount;
                            ErrorReporter.getInstance().warning("Combine recipes can't have more than 9 ingredients! Extra ingredient(s) ignored: " + item.getType() + " x" + ignoredAmount, "If you're using stacks make sure they don't exceed 9 items in total.");
                        } else {
                            items += item.getAmount();
                            ingredients.add(item);
                        }
                    }
                }

                if (ingredients.size() == 2 && !conditionEvaluator.checkIngredients(ingredients.get(0), ingredients.get(1))) {
                    return false;
                }

                ((CombineRecipe) recipe).setIngredients(ingredients);
            }
        }

        if (recipe.hasFlag(FlagType.REMOVE) && !Version.has1_12Support()) { // for mc1.12, matching requires outcome too...
            reader.nextLine(); // Skip the results line, if it exists
        } else {
            // get the results
            List<ItemResult> results = new ArrayList<>();

            if (!parseResults(recipe, results)) {
                return false;
            }

            recipe.setResults(results);

            if (!recipe.hasValidResult()) {
                return ErrorReporter.getInstance().error("Recipe must have at least one non-air result!");
            }
        }

        // check if recipe already exists
        if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
            return recipe.hasFlag(FlagType.REMOVE);
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            recipe.setName(recipeName); // set recipe's name if defined
        }

        // add the recipe to the Recipes class and to the list for later adding to the server
        recipeRegistrator.queueRecipe(recipe, reader.getFileName());

        return true; // no errors encountered
    }
}
