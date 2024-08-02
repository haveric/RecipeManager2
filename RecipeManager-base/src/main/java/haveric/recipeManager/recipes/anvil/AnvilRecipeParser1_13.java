package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.AirChoice;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class AnvilRecipeParser1_13 extends BaseAnvilParser {
    public AnvilRecipeParser1_13() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        AnvilRecipe1_13 recipe = new AnvilRecipe1_13(fileFlags);

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // parse recipe's flags

        while (!reader.lineIsResult()) {
            String[] lineSplit = reader.getLine().split("%");
            String lineChars = lineSplit[0].substring(0, 2).trim();
            char ingredientChar = lineChars.charAt(0);

            if (lineChars.length() == 1 && (ingredientChar == 'a' || ingredientChar == 'b')) {
                RecipeChoice choice = Tools.parseRecipeChoice(lineSplit[0].substring(2), ParseBit.NONE);
                if (choice == null || choice instanceof AirChoice) {
                    return false;
                }

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
}
