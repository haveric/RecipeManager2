package haveric.recipeManager.recipes;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flags.FlagIngredientCondition;
import haveric.recipeManager.flags.FlagIngredientCondition.Conditions;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;


public class SmeltRecipe extends SingleResultRecipe {
    private ItemStack ingredient;
    private ItemResult fuel;
    private float minTime = Vanilla.FURNACE_RECIPE_TIME;
    private float maxTime = -1;
    private int hash;

    public SmeltRecipe() {
    }

    public SmeltRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof SmeltRecipe) {
            SmeltRecipe r = (SmeltRecipe) recipe;

            if (r.ingredient == null) {
                ingredient = null;
            } else {
                ingredient = r.ingredient.clone();
            }

            if (r.fuel == null) {
                fuel = null;
            } else {
                fuel = r.fuel.clone();
            }

            minTime = r.minTime;
            maxTime = r.maxTime;
            hash = r.hash;
        }
    }

    public SmeltRecipe(Flags flags) {
        super(flags);
    }

    public SmeltRecipe(FurnaceRecipe recipe) {
        setIngredient(recipe.getInput());
        setResult(recipe.getResult());
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;
        hash = ("smelt" + newIngredient.getTypeId() + ":" + newIngredient.getDurability()).hashCode();
    }

    public ItemResult getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack newFuel) {
        Validate.notNull(newFuel);

        if (newFuel instanceof ItemResult) {
            fuel = ((ItemResult) newFuel).setRecipe(this);
        } else {
            fuel = new ItemResult(newFuel).setRecipe(this);
        }
    }

    public boolean hasCustomTime() {
        return minTime != Vanilla.FURNACE_RECIPE_TIME;
    }

    public float getMinTime() {
        return minTime;
    }

    /**
     * @param minTime
     *            min random time range (seconds)
     */
    public void setMinTime(float newMinTime) {
        minTime = newMinTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

    /**
     * @param maxTime
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

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("smelt ");

        s.append(ingredient.getType().toString().toLowerCase());

        if (ingredient.getDurability() != Vanilla.DATA_WILDCARD) {
            s.append(":").append(ingredient.getDurability());
        }

        s.append(" to ");

        if (!removed) {
            s.append(getResultString());
        } else {
            s.append("removed recipe");
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public int getIndex() {
        return ingredient.getTypeId();
    }

    public String getIndexString() {
        return ingredient.getTypeId() + ":" + ingredient.getDurability();
    }

    public String getFuelIndex() {
        String fuelIndex = "" + fuel.getTypeId();

        if (fuel.getDurability() != Vanilla.DATA_WILDCARD) {
            fuelIndex += ":" + fuel.getDurability();
        }

        return fuelIndex;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof SmeltRecipe)) {
            return false;
        }

        if (hash != ((SmeltRecipe) obj).hashCode()) {
            return false;
        }

        return true;
    }

    @Override
    public FurnaceRecipe toBukkitRecipe() {
        if (!hasIngredient() || !hasResult()) {
            return null;
        }

        return new FurnaceRecipe(getResult(), ingredient.getType(), ingredient.getDurability());
    }

    public boolean hasIngredient() {
        return ingredient != null;
    }

    public boolean hasFuel() {
        return fuel != null;
    }

    @Override
    public boolean isValid() {
        return hasIngredient() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResult());
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMELT;
    }

    @Override
    public String printBookIndex() {
        String print;

        if (hasCustomName()) {
            print = ChatColor.ITALIC + getName();
        } else {
            print = ToolsItem.getName(getResult());
        }

        return print;
    }

    @Override
    public String printBook() {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.RECIPEBOOK_HEADER_SMELT.get());

        if (hasCustomName()) {
            s.append('\n').append(ChatColor.DARK_BLUE).append(getName()).append(ChatColor.BLACK);
        }

        s.append('\n').append(ChatColor.GRAY).append('=').append(ChatColor.BLACK).append(ChatColor.BOLD).append(ToolsItem.print(getResult(), ChatColor.DARK_GREEN, null, true));

        /*
         * if(isMultiResult()) { s.append('\n').append(Messages.RECIPEBOOK_MORERESULTS.get("{amount}", (getResults().size() - 1))); }
         */

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_INGREDIENT.get()).append(ChatColor.BLACK);
        s.append('\n').append(ToolsItem.print(getIngredient(), ChatColor.RED, ChatColor.BLACK, false));

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_COOKTIME.get()).append(ChatColor.BLACK);
        s.append('\n');

        if (hasCustomTime()) {
            if (maxTime > minTime) {
                s.append(Messages.RECIPEBOOK_SMELT_TIME_RANDOM.get("{min}", Tools.printNumber(minTime), "{max}", Tools.printNumber(maxTime)));
            } else {
                if (minTime <= 0) {
                    s.append(Messages.RECIPEBOOK_SMELT_TIME_INSTANT.get());
                } else {
                    s.append(Messages.RECIPEBOOK_SMELT_TIME_FIXED.get("{time}", Tools.printNumber(minTime)));
                }
            }
        } else {
            s.append(Messages.RECIPEBOOK_SMELT_TIME_NORMAL.get("{time}", Tools.printNumber(minTime)));
        }

        if (hasFuel()) {
            s.append('\n');
            s.append('\n').append(Messages.RECIPEBOOK_HEADER_REQUIREFUEL.get()).append(ChatColor.BLACK);
            s.append('\n').append(ToolsItem.print(getFuel(), ChatColor.RED, ChatColor.BLACK, true));
        }

        return s.toString();
    }

    public void subtractIngredient(FurnaceInventory inv, boolean onlyExtra) {
        FlagIngredientCondition flag;
        if (hasFlag(FlagType.INGREDIENTCONDITION)) {
            flag = getFlag(FlagIngredientCondition.class);
        } else {
            flag = null;
        }

        ItemStack item = inv.getSmelting();

        if (item != null) {
            int amt = item.getAmount();
            int newAmt = amt;

            if (flag != null) {
                Conditions cond = flag.getIngredientConditions(item);

                if (cond != null && cond.getAmount() > 1) {
                    newAmt -= (cond.getAmount() - 1);
                }
            }

            if (!onlyExtra) {
                newAmt -= 1;
            }

            if (amt != newAmt) {
                if (newAmt > 0) {
                    item.setAmount(newAmt);
                } else {
                    inv.setSmelting(null);
                }
            }
        }
    }
}
