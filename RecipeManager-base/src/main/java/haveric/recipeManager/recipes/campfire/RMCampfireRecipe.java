package haveric.recipeManager.recipes.campfire;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.FlagIngredientCondition;
import haveric.recipeManager.flag.flags.FlagItemName;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RMCampfireRecipe extends SingleResultRecipe {
    private ItemStack ingredient;

    private float minTime = Vanilla.CAMPFIRE_RECIPE_TIME;
    private float maxTime = -1;
    private float experience = 2;
    private int hash;

    public RMCampfireRecipe() {

    }

    public RMCampfireRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof RMCampfireRecipe) {
            RMCampfireRecipe r = (RMCampfireRecipe) recipe;

            if (r.ingredient == null) {
                ingredient = null;
            } else {
                ingredient = r.ingredient.clone();
            }

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
        setIngredient(recipe.getInput());
        setResult(recipe.getResult());
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;
        hash = ("campfire" + newIngredient.getType().toString() + ":" + newIngredient.getDurability()).hashCode();
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
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("campfire ");

        s.append(ingredient.getType().toString().toLowerCase());

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            s.append(':').append(ingredient.getDurability());
        }

        s.append(" to ");

        if (removed) {
            s.append("removed recipe");
        } else {
            s.append(getResultString());
        }

        name = s.toString();
        customName = false;
    }

    public String getIndexString() {
        return ingredient.getType().toString() + ":" + ingredient.getDurability();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof CampfireRecipe && hash == obj.hashCode();
    }

    @Override
    public CampfireRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredient() || !hasResult()) {
            return null;
        }

        return new CampfireRecipe(getNamespacedKey(), getResult(), ingredient.getType(), experience, getCookTicks());
    }

    public boolean hasIngredient() {
        return ingredient != null;
    }

    @Override
    public boolean isValid() {
        return hasIngredient() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResult());
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.CAMPFIRE;
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasCustomName()) {
            print.add(RMCChatColor.ITALIC + getName());
        } else {
            print.add(getResultPrintName(getResult()));
        }

        return print;
    }

    private String getResultPrintName(ItemResult result) {
        String print;

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            print = RMCUtil.parseColors(flag.getItemName(), false);
        } else {
            print = ToolsItem.getName(getResult());
        }

        return print;
    }

    @Override
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        recipes.add(printBookResult(getResult()));

        return recipes;
    }

    public String printBookResult(ItemResult result) {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header.campfire"));

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.ITALIC).append(getName());
        }

        s.append('\n').append(RMCChatColor.GRAY).append('=');

        if (result.hasFlag(FlagType.ITEM_NAME)) {
            FlagItemName flag = (FlagItemName)result.getFlag(FlagType.ITEM_NAME);
            s.append(RMCChatColor.BLACK).append(RMCUtil.parseColors(flag.getItemName(), false));
        } else {
            s.append(ToolsItem.print(getResult(), RMCChatColor.DARK_GREEN, null));
        }

        /*
         * if(isMultiResult()) { s.append('\n').append(MessagesOld.RECIPEBOOK_MORERESULTS.get("{amount}", (getResults().size() - 1))); }
         */

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.ingredient")).append(RMCChatColor.BLACK);

        String print = "";
        if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
            FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
            List<ConditionsIngredient> conditions = flag.getIngredientConditions(result);

            if (conditions.size() > 0) {
                ConditionsIngredient condition = conditions.get(0);

                if (condition.hasName()) {
                    print = RMCChatColor.BLACK + condition.getName();
                } else if (condition.hasLore()) {
                    print = RMCChatColor.BLACK + "" + RMCChatColor.ITALIC + condition.getLores().get(0);
                }
            }
        }

        if (print.equals("")) {
            print = ToolsItem.print(getIngredient(), RMCChatColor.RESET, RMCChatColor.BLACK);
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

    /*
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
    */
}
