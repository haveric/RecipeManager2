package haveric.recipeManager.recipes.campfire;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleRecipeChoiceSingleResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.CampfireRecipe;

public class RMCampfireRecipe extends SingleRecipeChoiceSingleResultRecipe {
    private float minTime = Vanilla.CAMPFIRE_RECIPE_TIME;
    private float maxTime = -1;
    private float experience = 2;

    public RMCampfireRecipe() {

    }

    public RMCampfireRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMCampfireRecipe) {
            RMCampfireRecipe r = (RMCampfireRecipe) recipe;

            minTime = r.minTime;
            maxTime = r.maxTime;
            experience = r.experience;
            hash = r.hash;
        }
    }

    public RMCampfireRecipe(Flags flags) {
        super(flags);
    }

    public RMCampfireRecipe(CampfireRecipe recipe) {
        setIngredientChoice(recipe.getInputChoice());

        setResult(recipe.getResult());
    }

    public boolean hasCustomTime() {
        return minTime != Vanilla.CAMPFIRE_RECIPE_TIME;
    }

    public float getMinTime() {
        return minTime;
    }

    /**
     * @param newMinTime
     *            min random time range (seconds)
     */
    public void setMinTime(float newMinTime) {
        minTime = newMinTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

    /**
     * @param newMaxTime
     *            max random time range (seconds) or set to -1 to disable
     */
    public void setMaxTime(float newMaxTime) {
        maxTime = newMaxTime;
    }

    /**
     * @return if recipe has random time range
     */
    public boolean hasRandomTime() {
        return maxTime > minTime;
    }

    /**
     * @return min time or if hasRandomTime() gets a random between min and max time.
     */
    public float getCookTime() {
        float time;

        if (hasRandomTime()) {
            time = minTime + ((maxTime - minTime) * RecipeManager.random.nextFloat());
        } else {
            time = minTime;
        }

        return time;
    }

    /**
     * @return getCookTime() multiplied by 20.0 and rounded
     */
    public int getCookTicks() {
        return Math.round(getCookTime() * 20.0f);
    }

    public float getExperience() { return experience; }

    public void setExperience(float newExperience) { experience = newExperience; }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof CampfireRecipe && hash == obj.hashCode();
    }

    @Override
    public CampfireRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        return new CampfireRecipe(getNamespacedKey(), a.result(), ingredientChoice, experience, getCookTicks());
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.CAMPFIRE;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult(getType().getDirective());

        String print = getConditionResultName(result);

        if (print.isEmpty()) {
            print = ToolsItem.printRecipeChoice(ingredientChoice, RMCChatColor.RESET, RMCChatColor.BLACK);
        }

        s.append('\n').append(print);

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.cooktime")).append(RMCChatColor.BLACK);
        s.append('\n');

        if (hasCustomTime()) {
            if (maxTime > minTime) {
                s.append(Messages.getInstance().parse("recipebook.smelt.time.random", "{min}", RMCUtil.printNumber(minTime), "{max}", RMCUtil.printNumber(maxTime)));
            } else {
                if (minTime <= 0) {
                    s.append(Messages.getInstance().parse("recipebook.smelt.time.instant"));
                } else {
                    s.append(Messages.getInstance().parse("recipebook.smelt.time.fixed", "{time}", RMCUtil.printNumber(minTime)));
                }
            }
        } else {
            s.append(Messages.getInstance().parse("recipebook.smelt.time.normal", "{time}", RMCUtil.printNumber(minTime)));
        }

        return s.toString();
    }
}
