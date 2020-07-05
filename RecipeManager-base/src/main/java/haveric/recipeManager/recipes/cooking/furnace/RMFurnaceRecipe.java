package haveric.recipeManager.recipes.cooking.furnace;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RMFurnaceRecipe extends SingleResultRecipe {
    private ItemStack ingredient;
    private ItemResult fuel;
    private float minTime = Vanilla.FURNACE_RECIPE_TIME;
    private float maxTime = -1;

    public RMFurnaceRecipe() {
    }

    public RMFurnaceRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMFurnaceRecipe) {
            RMFurnaceRecipe r = (RMFurnaceRecipe) recipe;

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

    public RMFurnaceRecipe(Flags flags) {
        super(flags);
    }

    public RMFurnaceRecipe(FurnaceRecipe recipe) {
        setIngredient(recipe.getInput());
        setResult(recipe.getResult());
    }

    public String getRecipeBaseHash() {
        return "smelt";
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;
        hash = (getRecipeBaseHash() + newIngredient.getType().toString() + ":" + newIngredient.getDurability()).hashCode();
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.SMELT;
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

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append(getRecipeBaseHash()).append(" ");

        s.append(ingredient.getType().toString().toLowerCase());

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            s.append(':').append(ingredient.getDurability());
        }

        if (removed) {
            s.append(" [removed recipe]");
        } else {
            s.append(" to ").append(getResultString());
        }

        name = s.toString();
        customName = false;
    }

    public List<String> getIndexes() {
        List<String> indexString = new ArrayList<>();

        indexString.add(ingredient.getType().toString() + ":" + ingredient.getDurability());

        return indexString;
    }

    public String getFuelIndex() {
        String fuelIndex = "" + fuel.getType().toString();

        if (fuel.getDurability() != RMCVanilla.DATA_WILDCARD) {
            fuelIndex += ":" + fuel.getDurability();
        }

        return fuelIndex;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public boolean hasIngredient() {
        return ingredient != null;
    }

    public boolean hasFuel() {
        return fuel != null;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof RMFurnaceRecipe && hashCode() == obj.hashCode();
    }

    @Override
    public FurnaceRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredient() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        return new FurnaceRecipe(a.result(), ingredient.getType(), ingredient.getDurability());
    }

    @Override
    public boolean isValid() {
        return hasIngredient() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResult());
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult(getRecipeBaseHash());

        String print = getConditionResultName(result);

        if (print.isEmpty()) {
            print = ToolsItem.print(ingredient, RMCChatColor.RESET, RMCChatColor.BLACK);
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

        if (hasFuel()) {
            s.append("\n\n");
            s.append(Messages.getInstance().parse("recipebook.header.requirefuel"));
            s.append('\n').append(ToolsItem.print(fuel, RMCChatColor.RESET, RMCChatColor.BLACK));
        }

        return s.toString();
    }

    public void subtractIngredient(FurnaceInventory inv, ItemResult result, boolean onlyExtra) {
        FlagIngredientCondition flagIC;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            flagIC = (FlagIngredientCondition) getFlag(FlagType.INGREDIENT_CONDITION);
        } else {
            flagIC = null;
        }

        if (flagIC == null && result != null && result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            flagIC = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
        }

        ItemStack item = inv.getSmelting();
        if (item != null) {
            int amt = item.getAmount();
            int newAmt = amt;

            if (flagIC != null) {
                List<ConditionsIngredient> condList = flagIC.getIngredientConditions(item);

                for (ConditionsIngredient cond : condList) {
                    if (cond != null && cond.checkIngredient(item, ArgBuilder.create().build())) {
                        if (cond.getAmount() > 1) {
                            newAmt -= (cond.getAmount() - 1);
                        }
                    }
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

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        ItemStack i = ingredient;

        if (i.getType() == type && (data == null || data == RMCVanilla.DATA_WILDCARD || i.getDurability() == data)) {
            found++;
        }

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);
            recipeIndexes.add(ingredient.getType().toString() + ":" + ingredient.getDurability());
            recipeIndexes.add(ingredient.getType().toString() + ":" + RMCVanilla.DATA_WILDCARD);
        }

        return recipeIndexes;
    }
}
