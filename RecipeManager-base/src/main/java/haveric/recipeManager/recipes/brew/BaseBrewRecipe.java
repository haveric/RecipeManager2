package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.MultiResultRecipe;

public class BaseBrewRecipe extends MultiResultRecipe {
    private int minTime = -1;
    private int maxTime = -1;

    public BaseBrewRecipe() {
        minTime = Vanilla.BREWING_RECIPE_DEFAULT_TICKS;
    }

    public BaseBrewRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof BaseBrewRecipe) {
            BaseBrewRecipe r = (BaseBrewRecipe) recipe;

            minTime = r.minTime;
            maxTime = r.maxTime;
        }
    }

    public BaseBrewRecipe(Flags flags) {
        super(flags);

        minTime = Vanilla.BREWING_RECIPE_DEFAULT_TICKS;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.BREW;
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }


    public int getMinTime() {
        return minTime;
    }

    /**
     * @param newMinTime
     *            min random time range (seconds)
     */
    public void setMinTime(int newMinTime) {
        minTime = newMinTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    /**
     * @param newMaxTime
     *            max random time range (seconds) or set to -1 to disable
     */
    public void setMaxTime(int newMaxTime) {
        maxTime = newMaxTime;
    }

    /**
     * @return if recipe has random time range
     */
    private boolean hasRandomTime() {
        return maxTime > minTime;
    }

    /**
     * @return min time or if hasRandomTime() gets a random between min and max time.
     */
    public int getBrewingTimeInTicks() {
        int time;

        if (hasRandomTime()) {
            time = minTime + ((maxTime - minTime) * RecipeManager.random.nextInt());
        } else {
            time = minTime;
        }

        return Math.max(time, 0);
    }
}
