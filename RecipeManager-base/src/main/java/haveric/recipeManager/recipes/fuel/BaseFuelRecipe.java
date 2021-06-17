package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFuelRecipe extends BaseRecipe {
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


    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs an ingredient!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.FUEL;
    }

    @Override
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        recipes.add(printBookResult(null));

        return recipes;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);
            recipeIndexes.add(ingredient.getType().toString());
            recipeIndexes.add(ingredient.getType() + ":" + ingredient.getDurability());
        }

        return recipeIndexes;
    }

    protected String getPrintForIngredient(String ingredient) {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header.fuel"));

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.ITALIC).append(getName());
        }

        s.append("\n\n");

        s.append(Messages.getInstance().parse("recipebook.header.ingredient")).append(RMCChatColor.BLACK);
        s.append('\n').append(ingredient);

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.burntime")).append(RMCChatColor.BLACK);
        s.append('\n');

        if (maxTime > minTime) {
            s.append(Messages.getInstance().parse("recipebook.fuel.time.random", "{min}", RMCUtil.printNumber(minTime), "{max}", RMCUtil.printNumber(maxTime)));
        } else {
            s.append(Messages.getInstance().parse("recipebook.fuel.time.fixed", "{time}", RMCUtil.printNumber(minTime)));
        }

        return s.toString();
    }

}
