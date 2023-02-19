package haveric.recipeManager.recipes.cooking.campfire;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Vanilla;
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
import org.bukkit.inventory.recipe.CookingBookCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RMCampfireRecipeParser extends BaseRecipeParser {
    public RMCampfireRecipeParser() {
        super();
    }

    @Override
    public boolean parseRecipe(int directiveLine) {
        RMCampfireRecipe recipe = new RMCampfireRecipe(fileFlags); // create recipe and copy flags from file
        reader.parseFlags(recipe.getFlags(), FlagBit.RECIPE); // check for @flags

        checkForArgs(recipe);

        boolean isRemove = recipe.hasFlag(FlagType.REMOVE);

        while (!reader.lineIsResult()) {
            String[] splitIngredient = reader.getLine().split("%");

            String materialsValue = splitIngredient[0].trim();

            // There's no needed logic for shapes here, so trim the shape declaration
            if (materialsValue.startsWith("a ")) {
                materialsValue = materialsValue.substring(2);
            }

            RecipeChoice choice = Tools.parseRecipeChoice(materialsValue, ParseBit.NONE);
            if (choice == null) {
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

                recipe.addIngredientChoiceItems(items);
            } else {
                recipe.setIngredientChoice(ToolsRecipeChoice.mergeRecipeChoices(recipe.getIngredientChoice(), choice));
            }

            if (!parseArgs(recipe, splitIngredient, isRemove)) {
                return false;
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

    private void checkForArgs(RMCampfireRecipe recipe) {
        String argLine = reader.getLine();

        String argLower = argLine.toLowerCase();
        if (argLower.startsWith("group ") || argLower.startsWith("category ") || argLower.startsWith("xp ")) {
            if (argLower.startsWith("group ")) {
                argLine = argLine.substring("group ".length()).trim();

                recipe.setGroup(argLine);
            } else if (argLower.startsWith("category ")) {
                argLine = argLine.substring("category ".length()).trim();

                if (Supports.categories()) {
                    try {
                        CookingBookCategory category = CookingBookCategory.valueOf(argLine);
                        recipe.setCategory(category.name());
                    } catch (IllegalArgumentException e) {
                        ErrorReporter.getInstance().warning("Category is invalid. Category: " + argLine + " ignored. Valid values: " + Arrays.toString(CookingBookCategory.values()));
                    }
                } else {
                    ErrorReporter.getInstance().warning("Category is supported on 1.19.3 or newer only. Category: " + argLine + " ignored.");
                }
            } else if (argLower.startsWith("xp ")) {
                argLine = argLine.substring("xp ".length()).trim();

                try {
                    float experience = Float.parseFloat(argLine);
                    recipe.setExperience(experience);
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Xp is not a valid float. Xp: " + argLine + " ignored and defaulted to 0.");
                }
            }

            reader.nextLine();

            // Continue checking for other args
            checkForArgs(recipe);
        }
    }

    private boolean parseArgs(RMCampfireRecipe recipe, String[] split, boolean isRemove) {
        if (!isRemove) { // if it's got @remove we don't care about cook time
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
                        ErrorReporter.getInstance().warning("Invalid burn time float number! Campfire time left as default.");

                        minTime = Vanilla.CAMPFIRE_RECIPE_TIME;
                        maxTime = -1;
                    }
                }

                if (maxTime > -1.0 && minTime >= maxTime) {
                    return ErrorReporter.getInstance().error("Campfire recipe has the min-time less or equal to max-time!", "Use a single number if you want a fixed value.");
                }

                recipe.setMinTime(minTime);
                recipe.setMaxTime(maxTime);
            }

        }

        return true;
    }
}
