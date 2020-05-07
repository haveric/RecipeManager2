package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;

public class BaseFuelRecipe extends BaseRecipe {
    protected float minTime;
    protected float maxTime;

    public BaseFuelRecipe() {

    }

    public BaseFuelRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof BaseFuelRecipe) {
            BaseFuelRecipe r = (BaseFuelRecipe) recipe;

            minTime = r.minTime;
            maxTime = r.maxTime;
        }
    }

    public BaseFuelRecipe(Flags flags) {
        super(flags);
    }


    public float getMinTime() {
        return minTime;
    }

    /**
     * Set minimum time it can burn (or fixed if max not defined).
     *
     * @param newMinTime
     *            float value in seconds
     */
    public void setMinTime(float newMinTime) {
        minTime = newMinTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

    /**
     * Set maximum time it can burn.<br>
     * NOTE: minimum time must be smaller than this and higher than -1
     *
     * @param newMaxTime
     *            float value in seconds
     */
    public void setMaxTime(float newMaxTime) {
        maxTime = newMaxTime;
    }

    /**
     * Get the burn time value, randomized if supported, in ticks (multiplied by 20).
     *
     * @return burn time in ticks
     */
    public int getBurnTicks() {
        float time;

        if (maxTime > minTime) {
            time = minTime + (maxTime - minTime) * RecipeManager.random.nextFloat();
        } else {
            time = minTime;
        }

        return (int) Math.round(20.0 * time);
    }
}
