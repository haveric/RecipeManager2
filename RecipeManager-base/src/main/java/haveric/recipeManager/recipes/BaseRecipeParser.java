package haveric.recipeManager.recipes;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeRegistrator;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.furnace.RMBaseFurnaceRecipe;
import haveric.recipeManager.tools.Tools;
import org.bukkit.Material;

import java.util.List;

public abstract class BaseRecipeParser {

    protected RecipeFileReader reader;
    protected String recipeName;
    protected Flags fileFlags;
    protected ConditionEvaluator conditionEvaluator;
    protected RecipeRegistrator recipeRegistrator;

    public BaseRecipeParser(RecipeFileReader reader, String recipeName, Flags fileFlags, RecipeRegistrator recipeRegistrator) {
        this.reader = reader;
        this.recipeName = recipeName;
        this.fileFlags = fileFlags;
        this.conditionEvaluator = new ConditionEvaluator(recipeRegistrator);
        this.recipeRegistrator = recipeRegistrator;
    }

    public abstract boolean parseRecipe(int directiveLine);

    protected boolean parseResults(BaseRecipe recipe, List<ItemResult> results) {
        boolean allowAir = true;
        boolean oneResult = false;

        if (recipe instanceof RMBaseFurnaceRecipe) {
            allowAir = false;
            oneResult = true;
        }

        if (this.reader.getLine() == null) {
            return false;
        }

        if (!this.reader.lineIsResult()) { // check if current line is a result, if not move on
            this.reader.nextLine();

            if (!this.reader.lineIsResult() && !this.reader.lineIsRecipe()) {
                return ErrorReporter.getInstance().error("Recipe has more rows of ingredients than allowed!");
            }
        }
        ItemResult result;
        float totalPercentage = 0;
        int splitChanceBy = 0;

        while (this.reader.getLine() != null && this.reader.lineIsResult()) {
            result = Tools.parseItemResult(this.reader.getLine(), 0); // convert result to ItemResult, grabbing chance and what other stuff

            if (result == null) {
                this.reader.nextLine();
                continue;
            }

            if (!allowAir && result.getType() == Material.AIR) {
                ErrorReporter.getInstance().error("Result can not be AIR in this recipe!");
                return false;
            }

            results.add(result);
            result.setRecipe(recipe);

            if (result.getChance() < 0) {
                splitChanceBy++;
            } else {
                totalPercentage += result.getChance();
            }

            this.reader.parseFlags(result.getFlags()); // check for result flags and keeps the line flow going too
        }

        if (results.isEmpty()) {
            return ErrorReporter.getInstance().error("Found the '=' character but with no result!");
        }

        if (!recipe.hasFlag(FlagType.INDIVIDUAL_RESULTS)) {
            if (totalPercentage > 100) {
                return ErrorReporter.getInstance().error("Total result items' chance exceeds 100%!", "If you want some results to be split evenly automatically you can avoid the chance number.");
            }

            // Spread remaining chance to results that have undefined chance
            if (splitChanceBy > 0) {
                float remainingChance = (100.0f - totalPercentage);
                float chance = remainingChance / splitChanceBy;

                for (ItemResult r : results) {
                    if (r.getChance() < 0) {
                        r.setChance(chance);
                        totalPercentage += chance;
                    }
                }
            }

            if (!oneResult && totalPercentage < 100) {
                boolean foundAir = false;

                for (ItemResult r : results) {
                    if (r.getType() == Material.AIR) {
                        r.setChance(100.0f - totalPercentage);
                        foundAir = true;
                        break;
                    }
                }

                if (foundAir) {
                    ErrorReporter.getInstance().warning("All results are set but they do not stack up to 100% chance, extended fail chance to " + (100.0f - totalPercentage) + "!", "You can remove the chance for AIR to auto-calculate it");
                } else {
                    ErrorReporter.getInstance().warning("Results do not stack up to 100% and no fail chance defined, recipe now has " + (100.0f - totalPercentage) + "% chance to fail.", "You should extend or remove the chance for other results if you do not want fail chance instead!");

                    results.add(new ItemResult(Material.AIR, 0, 0, (100.0f - totalPercentage)));
                }
            }
        }

        if (oneResult && results.size() > 1) {
            ErrorReporter.getInstance().warning("Can't have more than 1 result! The rest were ignored.");
        }

        return true; // valid results
    }
}
