package haveric.recipeManager.recipes.cooking.furnace;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.*;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RMBaseFurnaceRecipeParser extends BaseRecipeParser {
    private RMCRecipeType recipeType;

    public RMBaseFurnaceRecipeParser(RMCRecipeType recipeType) {
        super();

        this.recipeType = recipeType;
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        SingleResultRecipe recipe;
        if (recipeType == RMCRecipeType.BLASTING) {
            recipe = new RMBlastingRecipe(fileFlags); // create recipe and copy flags from file
        } else if (recipeType == RMCRecipeType.SMOKING) {
            recipe = new RMSmokingRecipe(fileFlags); // create recipe and copy flags from file
        } else {
            if (Version.has1_13Support()) {
                recipe = new RMFurnaceRecipe1_13(fileFlags); // create recipe and copy flags from file
            } else {
                recipe = new RMFurnaceRecipe(fileFlags); // create recipe and copy flags from file
            }
        }

        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // check for @flags

        checkForArgs(recipe);

        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);

        if (recipe instanceof RMBaseFurnaceRecipe1_13) {
            RMBaseFurnaceRecipe1_13 furnaceRecipe1_13 = (RMBaseFurnaceRecipe1_13) recipe;
            while (!reader.lineIsResult() && !reader.lineIsFuel()) {
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

                    furnaceRecipe1_13.addIngredientChoiceItems(items);
                } else {
                    furnaceRecipe1_13.setIngredientChoice(ToolsRecipeChoice.mergeRecipeChoices(furnaceRecipe1_13.getIngredientChoice(), choice));
                }

                if (!parseArgs(recipe, splitIngredient, isRemove)) {
                    return false;
                }
            }
        } else {
            // get the ingredient and smelting time
            String[] split = reader.getLine().split("%");
            if (split.length == 0) {
                return ErrorReporter.getInstance().error("Smelting recipe doesn't have an ingredient!");
            }

            ItemStack ingredient = Tools.parseItem(split[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

            if (ingredient == null) {
                return false;
            }

            if (ingredient.getType() == Material.AIR) {
                return ErrorReporter.getInstance().error("Recipe does not accept AIR as ingredients!");
            }

            ((RMFurnaceRecipe) recipe).setIngredient(ingredient);
            reader.nextLine();

            if (!parseArgs(recipe, split, isRemove)) {
                return false;
            }
        }

        if (!isRemove) { // if it's got @remove we don't care about fuel
            if (reader.lineIsFuel()) {
                ItemStack fuelItem = Tools.parseItem(reader.getLine().substring(1), 0, ParseBit.NO_AMOUNT);

                if (fuelItem == null) {
                    return false;
                }

                if (fuelItem.getType() == Material.AIR) {
                    return ErrorReporter.getInstance().error("Fuel can not be air!");
                }

                if (recipe instanceof RMBaseFurnaceRecipe1_13) {
                    ((RMBaseFurnaceRecipe1_13) recipe).setFuel(fuelItem);
                    reader.parseFlags(((RMBaseFurnaceRecipe1_13) recipe).getFuel().getFlags(), FlagBit.INGREDIENT);
                } else {
                    ((RMFurnaceRecipe) recipe).setFuel(fuelItem);
                    reader.parseFlags(((RMFurnaceRecipe) recipe).getFuel().getFlags(), FlagBit.INGREDIENT);
                }
            }
        }

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

        recipe.setResult(result);

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

    private void checkForArgs(SingleResultRecipe recipe) {
        String argLine = reader.getLine();

        String argLower = argLine.toLowerCase();
        if (argLower.startsWith("group ") || argLower.startsWith("category ") || argLower.startsWith("xp ")) {
            if (argLower.startsWith("group ")) {
                argLine = argLine.substring("group ".length()).trim();

                if (recipe instanceof RMBaseFurnaceRecipe1_13) {
                    ((RMBaseFurnaceRecipe1_13) recipe).setGroup(argLine);
                } else {
                    ErrorReporter.getInstance().warning("Group is supported on 1.13 or newer only. Group: " + argLine + " ignored.");
                }
            } else if (argLower.startsWith("category ")) {
                argLine = argLine.substring("category ".length()).trim();

                if (recipe instanceof RMBaseFurnaceRecipe1_13 && Supports.categories()) {
                    try {
                        CookingBookCategory category = CookingBookCategory.valueOf(argLine);
                        ((RMBaseFurnaceRecipe1_13) recipe).setCategory(category.name());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Category is invalid. Category: " + argLine + " ignored. Valid values: " + Arrays.toString(CookingBookCategory.values()));
                    }
                } else {
                    ErrorReporter.getInstance().warning("Category is supported on 1.19.3 or newer only. Category: " + argLine + " ignored.");
                }
            } else if (argLower.startsWith("xp ")) {
                argLine = argLine.substring("xp ".length()).trim();

                if (recipe instanceof RMBaseFurnaceRecipe1_13) {
                    try {
                        float experience = Float.parseFloat(argLine);
                        ((RMBaseFurnaceRecipe1_13) recipe).setExperience(experience);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Xp is not a valid float. Xp: " + argLine + " ignored and defaulted to 0.");
                    }
                } else {
                    ErrorReporter.getInstance().warning("Xp is supported on 1.13 or newer only. Xp: " + argLine + " ignored.");
                }
            }

            reader.nextLine();

            // Continue checking for other args
            checkForArgs(recipe);
        }
    }

    // get min-max or fixed smelting time
    private boolean parseArgs(SingleResultRecipe recipe, String[] split, boolean isRemove) {
        if (!isRemove) { // if it's got @remove we don't care about burn time or fuel
            float minTime;
            float maxTime = -1;

            if (split.length >= 2) {
                String[] timeSplit = split[1].trim().toLowerCase().split("-");

                if (timeSplit[0].equals("instant")) {
                    minTime = 0;
                } else {
                    try {
                        minTime = Float.parseFloat(timeSplit[0]);

                        if (timeSplit.length >= 2) {
                            maxTime = Float.parseFloat(timeSplit[1]);
                        }
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Invalid burn time float number! Smelt time left as default.");

                        if (recipeType == RMCRecipeType.BLASTING) {
                            minTime = Vanilla.BLASTING_RECIPE_TIME;
                        } else if (recipeType == RMCRecipeType.SMOKING) {
                            minTime = Vanilla.SMOKER_RECIPE_TIME;
                        } else {
                            minTime = Vanilla.FURNACE_RECIPE_TIME;
                        }

                        maxTime = -1;
                    }
                }

                if (maxTime > -1.0 && minTime >= maxTime) {
                    return ErrorReporter.getInstance().error("Smelting recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }

                if (recipe instanceof RMBaseFurnaceRecipe1_13) {
                    ((RMBaseFurnaceRecipe1_13) recipe).setMinTime(minTime);
                    ((RMBaseFurnaceRecipe1_13) recipe).setMaxTime(maxTime);
                } else {
                    ((RMFurnaceRecipe) recipe).setMinTime(minTime);
                    ((RMFurnaceRecipe) recipe).setMaxTime(maxTime);
                }
            }
        }

        return true;
    }
}
