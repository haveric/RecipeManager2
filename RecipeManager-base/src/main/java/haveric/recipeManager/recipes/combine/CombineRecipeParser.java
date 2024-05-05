package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
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
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.*;

public class CombineRecipeParser extends BaseRecipeParser {
    public CombineRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        CombineRecipe1_13 recipe = new CombineRecipe1_13(fileFlags); // create recipe and copy flags from file
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

        String shapeFormatLine = reader.getLine().toLowerCase();
        if (shapeFormatLine.startsWith("pattern") || shapeFormatLine.startsWith("a ")) {
            Map<Character, Integer> ingredientCharacters = new HashMap<>();
            Map<Character, RecipeChoice> ingredientRecipeChoiceMap = new HashMap<>();
            String patternLine;
            if (shapeFormatLine.startsWith("pattern")) {
                patternLine = shapeFormatLine.substring("pattern".length()).trim();

                if (patternLine.length() > 9) {
                    ErrorReporter.getInstance().warning("Pattern has more than 9 characters: " + patternLine + ". Using only the first nine: " + patternLine.substring(0, 9));
                    patternLine = patternLine.substring(0, 9);
                }

                for (char c : patternLine.toCharArray()) {
                    if (!ingredientCharacters.containsKey(c)) {
                        ingredientCharacters.put(c, 1);
                    } else {
                        ingredientCharacters.put(c, ingredientCharacters.get(c) + 1);
                    }
                }

                reader.nextLine();
            } else {
                // Default to a single item
                patternLine = "a";
                ingredientCharacters.put('a', 1);
            }

            int ingredientsNum = 0;
            while (!reader.lineIsResult()) {
                String line = reader.getLine();
                char ingredientChar = line.substring(0, 2).trim().charAt(0);

                if (ingredientCharacters.containsKey(ingredientChar)) {
                    RecipeChoice choice = Tools.parseRecipeChoice(line.substring(2), ParseBit.NONE);
                    if (choice == null || choice instanceof AirChoice) {
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

                                items.add(a.result().getItemStack());
                            }
                        } else if (choice instanceof RecipeChoice.ExactChoice) {
                            RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                            List<ItemStack> exactItems = exactChoice.getChoices();

                            for (ItemStack exactItem : exactItems) {
                                Args a = ArgBuilder.create().result(exactItem).build();
                                ingredientFlags.sendCrafted(a, true);

                                items.add(a.result().getItemStack());
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

            recipe.setChoicePattern(patternLine);

            for (Map.Entry<Character, Integer> entry : ingredientCharacters.entrySet()) {
                if (!ingredientRecipeChoiceMap.containsKey(entry.getKey())) {
                    return ErrorReporter.getInstance().error("Character from pattern not found in ingredients: " + entry.getKey());
                }
            }
            recipe.setIngredientsRecipeChoiceMap(ingredientRecipeChoiceMap);
        } else {
            // get the ingredients
            String[] ingredientsRaw = reader.getLine().split("\\+");
            Map<Character, RecipeChoice> ingredientRecipeChoiceMap = new HashMap<>();

            StringBuilder shape = new StringBuilder();
            char letter = 'a';
            int items = 0;
            for (String str : ingredientsRaw) {
                Map<RecipeChoice, Integer> choiceAmountMap = Tools.parseRecipeChoiceWithAmount(str, ParseBit.NONE);

                if (choiceAmountMap == null) {
                    return ErrorReporter.getInstance().error("Ingredient cannot be empty: " + str, "Check for incorrect spelling or missing tags or aliases.");
                }

                // We're always returning only one item, so this should always work
                Map.Entry<RecipeChoice, Integer> entry = choiceAmountMap.entrySet().iterator().next();
                RecipeChoice choice = entry.getKey();

                if (choice == null || choice instanceof AirChoice) {
                    return ErrorReporter.getInstance().error("Ingredient cannot be empty: " + str, "Check for incorrect spelling or missing tags or aliases.");
                }

                if (choice instanceof RecipeChoice.MaterialChoice) {
                    RecipeChoice.MaterialChoice materialChoice = (RecipeChoice.MaterialChoice) choice;
                    if (materialChoice.getChoices().contains(Material.AIR)) {
                        return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
                    }
                } else if (choice instanceof RecipeChoice.ExactChoice) {
                    RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) choice;
                    for (ItemStack item : exactChoice.getChoices()) {
                        if (item.getType() == Material.AIR) {
                            return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
                        }
                    }
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

                ingredientRecipeChoiceMap.put(letter, choice);
                for (int i = 0; i < newAmount; i++) {
                    shape.append(letter);
                }

                letter ++;
            }

            recipe.setChoicePattern(shape.toString());
            recipe.setIngredientsRecipeChoiceMap(ingredientRecipeChoiceMap);
        }

        // get the results
        List<ItemResult> results = new ArrayList<>();

        if (!parseResults(recipe, results)) {
            return false;
        }

        recipe.setResults(results);

        if (!recipe.hasValidResult()) {
            return ErrorReporter.getInstance().error("Recipe must have at least one non-air result!");
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
