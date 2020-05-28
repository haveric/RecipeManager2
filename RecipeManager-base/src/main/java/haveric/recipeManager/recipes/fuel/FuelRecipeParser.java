package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipeParser;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class FuelRecipeParser extends BaseRecipeParser {
    public FuelRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        BaseFuelRecipe recipe;
        if (Version.has1_13Support()) {
            recipe = new FuelRecipe1_13(fileFlags); // create recipe and copy flags from file
        } else {
            recipe = new FuelRecipe(fileFlags); // create recipe and copy flags from file
        }
        reader.parseFlags(recipe.getFlags()); // check for @flags
        int added = 0;

        while (!reader.lineIsRecipe() && !reader.lineIsFlag()) {
            if (reader.lineIsRecipe() || reader.lineIsFlag()) {
                break;
            }

            if (Version.has1_13Support()) {
                recipe = new FuelRecipe1_13(fileFlags);
            } else {
                recipe = new FuelRecipe(fileFlags);
            }

            String[] split = reader.getLine().split("%");

            if (!recipe.hasFlag(FlagType.REMOVE)) { // if it's got @remove we don't care about burn time
                if (split.length < 2 || split[1] == null) {
                    ErrorReporter.getInstance().error("Burn time not set!", "It must be set after the ingredient like: ingredient % burntime");
                    continue;
                }

                // set the burn time
                String[] timeSplit = split[1].trim().split("-");
                float minTime;
                float maxTime = -1;

                try {
                    minTime = Math.max(Float.parseFloat(timeSplit[0]), 1);

                    if (timeSplit.length >= 2) {
                        maxTime = (float) Math.max(Float.parseFloat(timeSplit[1]), 0.0);
                    }
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().error("Invalid burn time float number!");
                    continue;
                }

                if (minTime <= 0) {
                    ErrorReporter.getInstance().error("Fuels can't burn for negative or 0 seconds!");
                    continue;
                }

                if (maxTime > -1 && minTime >= maxTime) {
                    maxTime = -1;
                    ErrorReporter.getInstance().warning("Fuel has minimum time less or equal to maximum time!", "Use a single number if you want a fixed value");
                }

                recipe.setMinTime(minTime);
                recipe.setMaxTime(maxTime);
            }

            if (recipe instanceof FuelRecipe1_13) {
                FuelRecipe1_13 fuelRecipe1_13 = (FuelRecipe1_13) recipe;
                RecipeChoice choice = Tools.parseRecipeChoice(split[0], ParseBit.NONE);
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

                    fuelRecipe1_13.addIngredientChoiceItems(items);
                } else {
                    fuelRecipe1_13.setIngredientChoice(ToolsItem.mergeRecipeChoices(fuelRecipe1_13.getIngredientChoice(), choice));
                }
            } else {
                // set ingredient
                ItemStack ingredient = Tools.parseItem(split[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

                if (ingredient == null) {
                    continue;
                }

                if (ingredient.getType() == Material.AIR) {
                    ErrorReporter.getInstance().error("Can not use AIR as ingredient!");
                    continue;
                }

                ((FuelRecipe) recipe).setIngredient(ingredient);

                reader.nextLine();
            }

            // check if the recipe already exists
            if (!conditionEvaluator.recipeExists(recipe, directiveLine, reader.getFileName())) {
                continue;
            }

            if (recipeName != null && !recipeName.isEmpty()) {
                String name = recipeName;
                if (added > 1) {
                    name += " (" + added + ")";
                }
                recipe.setName(name); // set recipe's name if defined
            }

            recipeRegistrator.queueRecipe(recipe, reader.getFileName());

            added++;

            boolean lineExists = true;
            while (reader.lineIsEmpty()) {
                lineExists = reader.nextLine();

                if (!lineExists) {
                    break;
                }
            }

            if (!lineExists) {
                break;
            }
        }

        return added > 0;
    }

}
