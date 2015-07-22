package haveric.recipeManager.recipes;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import haveric.recipeManagerCommon.util.RMCUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FuelRecipe extends BaseRecipe {
    private ItemStack ingredient;
    private float minTime;
    private float maxTime;

    public FuelRecipe(Material type, float burnTime) {
        setIngredient(new ItemStack(type, 1, Vanilla.DATA_WILDCARD));
        setMinTime(burnTime);
    }

    public FuelRecipe(Material type, float newMinTime, float newMaxTime) {
        setIngredient(new ItemStack(type, 1, Vanilla.DATA_WILDCARD));
        setMinTime(newMinTime);
        setMaxTime(newMaxTime);
    }

    public FuelRecipe(Material type, short data, float burnTime) {
        setIngredient(new ItemStack(type, 1, data));
        setMinTime(burnTime);
    }

    public FuelRecipe(Material type, short data, float newMinTime, float newMaxTime) {
        setIngredient(new ItemStack(type, 1, data));
        setMinTime(newMinTime);
        setMaxTime(newMaxTime);
    }

    public FuelRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof FuelRecipe) {
            FuelRecipe r = (FuelRecipe) recipe;

            if (r.ingredient == null) {
                ingredient = null;
            } else {
                ingredient = r.ingredient.clone();
            }

            minTime = r.minTime;
            maxTime = r.maxTime;
        }
    }

    public FuelRecipe(Flags flags) {
        super(flags);
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;

        hash = ("fuel" + newIngredient.getTypeId() + ":" + newIngredient.getDurability()).hashCode();
    }

    public float getMinTime() {
        return minTime;
    }

    /**
     * Set minimum time it can burn (or fixed if max not defined).
     *
     * @param minTime
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
     * @param maxTime
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

    public String getIndexString() {
        String indexString = "" + ingredient.getTypeId();

        if (ingredient.getDurability() != Vanilla.DATA_WILDCARD) {
            indexString += ":" + ingredient.getDurability();
        }

        return indexString;
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("fuel ");

        s.append(ingredient.getType().toString().toLowerCase());

        if (ingredient.getDurability() != Vanilla.DATA_WILDCARD) {
            s.append(':').append(ingredient.getDurability());
        }

        if (removed) {
            s.append(" / removed recipe");
        }

        name = s.toString();
        customName = false;
    }

    public boolean hasIngredient() {
        return ingredient != null;
    }

    @Override
    public boolean isValid() {
        return hasIngredient();
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.FUEL;
    }

    @Override
    public String printBookIndex() {
        String print;

        if (hasCustomName()) {
            print = RMCChatColor.ITALIC + getName();
        } else {
            print = ToolsItem.getName(getIngredient()) + " Fuel";
        }

        return print;
    }

    @Override
    public String printBook() {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.RECIPEBOOK_HEADER_FUEL.get());

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.DARK_BLUE).append(getName()).append(RMCChatColor.BLACK);
        }

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_INGREDIENT.get()).append(RMCChatColor.BLACK);
        s.append('\n').append(ToolsItem.print(getIngredient(), RMCChatColor.RED, RMCChatColor.BLACK, false));

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_BURNTIME.get()).append(RMCChatColor.BLACK);
        s.append('\n');

        if (maxTime > minTime) {
            s.append(Messages.RECIPEBOOK_FUEL_TIME_RANDOM.get("{min}", RMCUtil.printNumber(minTime), "{max}", RMCUtil.printNumber(maxTime)));
        } else {
            s.append(Messages.RECIPEBOOK_FUEL_TIME_FIXED.get("{time}", RMCUtil.printNumber(minTime)));
        }

        return s.toString();
    }
}
