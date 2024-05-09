package haveric.recipeManager.recipes.brew;

import com.google.common.collect.ImmutableList;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiChoiceResultRecipe;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipe extends MultiChoiceResultRecipe {
    private int minTime = -1;
    private int maxTime = -1;

    public BrewRecipe() {
        init();
    }

    public BrewRecipe(BaseRecipe recipe) {
        super(recipe);
        init();

        if (recipe instanceof BrewRecipe r) {
            minTime = r.minTime;
            maxTime = r.maxTime;
        }
    }

    public BrewRecipe(Flags flags) {
        super(flags);
        init();
    }

    private void init() {
        minTime = Vanilla.BREWING_RECIPE_DEFAULT_TICKS;
        setMaxIngredients(2);
        addValidChars(ImmutableList.of('a', 'b'));
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.BREW;
    }


    public RecipeChoice getPrimaryIngredientChoice() {
        return getIngredient('a');
    }

    public RecipeChoice getPotionIngredientChoice() {
        return getIngredient('b');
    }

    public void setPrimaryIngredientChoice(RecipeChoice choice) {
        setIngredient('a', choice);
    }

    public void setPotionIngredientChoice(RecipeChoice choice) {
        setIngredient('b', choice);
    }

    public boolean hasPrimaryIngredientChoice() {
        return hasIngredient('a');
    }

    public boolean hasPotionIngredientChoice() {
        return hasIngredient('b');
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();

        s.append(ToolsRecipeChoice.getRecipeChoiceName(getPrimaryIngredientChoice()));
        s.append(" + ");
        s.append(ToolsRecipeChoice.getRecipeChoiceName(getPotionIngredientChoice()));

        s.append(" to ").append(getResultsString());

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasPrimaryIngredientChoice() && hasPotionIngredientChoice() && hasResults();
    }

    @Override
    public void onRegister() {
        if (hasPrimaryIngredientChoice()) {
            List<Material> ingredientMaterials = ToolsRecipeChoice.getMaterialsInRecipeChoice(getPrimaryIngredientChoice());
            BrewInventoryUtil.addIngredients(ingredientMaterials);
        }

        if (hasPotionIngredientChoice()) {
            List<Material> potionMaterials = ToolsRecipeChoice.getMaterialsInRecipeChoice(getPotionIngredientChoice());
            BrewInventoryUtil.addPotions(potionMaterials);
        }

        if (hasResults()) {
            List<ItemResult> results = getResults();
            for (ItemResult itemResult : results) {
                BrewInventoryUtil.addResult(itemResult.getType());
            }
        }
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

    @Override
    public void updateHash() {
        String newHash = "brew";

        if (hasPrimaryIngredientChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(getPrimaryIngredientChoice());
        }

        newHash += " + ";

        if (hasPotionIngredientChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(getPotionIngredientChoice());
        }

        hash = newHash.hashCode();
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() > 1) {
            ItemStack ingredient = ingredients.get(0);

            for (int i = 1; i < ingredients.size(); i++) {
                recipeIndexes.add(ingredient.getType() + " - " + ingredients.get(i).getType());
            }
        }

        return recipeIndexes;
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        boolean checkExact = true;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            checkExact = false;
        } else {
            for (ItemResult result : getResults()) {
                if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                    checkExact = false;
                    break;
                }
            }
        }

        int totalQuality = 0;
        int ingredientQuality = 0;
        int potionQuality = 0;

        ItemStack ingredient = ingredients.get(0);
        if (ingredient.getType() != Material.AIR) {
            int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(ingredient, getPrimaryIngredientChoice(), checkExact);
            if (newQuality > ingredientQuality) {
                ingredientQuality = newQuality;
            }
        }

        if (ingredientQuality > 0) {
            for (int i = 1; i < ingredients.size(); i++) {
                ItemStack potion = ingredients.get(i);

                if (potion.getType() != Material.AIR) {
                    int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(potion, getPotionIngredientChoice(), checkExact);
                    if (newQuality > potionQuality) {
                        potionQuality = newQuality;
                    }
                }
            }

            if (potionQuality > 0) {
                totalQuality = ingredientQuality + potionQuality;
            }
        }

        return totalQuality;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("brewing", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredient"));
        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(getPrimaryIngredientChoice(), RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.potion"));
        s.append('\n').append(ToolsRecipeChoice.printRecipeChoice(getPotionIngredientChoice(), RMCChatColor.BLACK, RMCChatColor.BLACK));

        return s.toString();
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }

    public int subtractIngredientCondition(Inventory inv, ItemResult result) {
        int amountToSubtract = 0;

        ItemStack item = inv.getItem(3);
        if (item != null) {
            if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
                FlagIngredientCondition flagIC = (FlagIngredientCondition) getFlag(FlagType.INGREDIENT_CONDITION);
                List<ConditionsIngredient> condList = flagIC.getIngredientConditions(item);

                for (ConditionsIngredient cond : condList) {
                    if (cond != null && cond.checkIngredient(item, ArgBuilder.create().build())) {
                        if (cond.getAmount() > 1) {
                            amountToSubtract += cond.getAmount();
                        }
                    }
                }
            }

            if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                FlagIngredientCondition flagIC = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
                List<ConditionsIngredient> condList = flagIC.getIngredientConditions(item);

                for (ConditionsIngredient cond : condList) {
                    if (cond != null && cond.checkIngredient(item, ArgBuilder.create().build())) {
                        if (cond.getAmount() > 1) {
                            amountToSubtract += cond.getAmount();
                        }
                    }
                }
            }
        }
        return amountToSubtract;
    }
}
