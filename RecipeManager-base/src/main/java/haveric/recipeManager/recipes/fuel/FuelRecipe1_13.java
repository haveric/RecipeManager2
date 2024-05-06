package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuelRecipe1_13 extends BaseRecipe {
    private float minTime;
    private float maxTime;
    private RecipeChoice ingredientChoice;

    public FuelRecipe1_13() {

    }

    public FuelRecipe1_13(Material type, float burnTime) {
        setIngredientChoice(Collections.singletonList(type));
        minTime = burnTime;
    }

    public FuelRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof FuelRecipe1_13) {
            FuelRecipe1_13 r = (FuelRecipe1_13) recipe;

            minTime = r.minTime;
            maxTime = r.maxTime;

            if (r.ingredientChoice != null) {
                ingredientChoice = r.ingredientChoice.clone();
            }
        }
    }

    public FuelRecipe1_13(Flags flags) {
        super(flags);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.FUEL;
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

    public RecipeChoice getIngredientChoice() {
        return ingredientChoice;
    }

    public void addIngredientChoice(List<Material> materials) {
        if (ingredientChoice == null) {
            setIngredientChoice(materials);
        } else {
            ingredientChoice = ToolsRecipeChoice.mergeRecipeChoiceWithMaterials(ingredientChoice, materials);
            updateHash();
        }
    }

    public void addIngredientChoiceItems(List<ItemStack> items) {
        if (ingredientChoice == null) {
            setIngredientChoiceItems(items);
        } else {
            ingredientChoice = ToolsRecipeChoice.mergeRecipeChoiceWithItems(ingredientChoice, items);
            updateHash();
        }
    }

    public void setIngredientChoice(List<Material> materials) {
        RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(materials);
        setIngredientChoice(materialChoice);
    }

    public void setIngredientChoiceItems(List<ItemStack> items) {
        RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(items);
        setIngredientChoice(exactChoice);
    }

    protected void setIngredientChoice(RecipeChoice choice) {
        ingredientChoice = choice.clone();

        updateHash();
    }

    private void updateHash() {
        String newHash = "fuel";

        if (hasIngredientChoice()) {
            newHash += ToolsRecipeChoice.getRecipeChoiceHash(ingredientChoice);
        }

        hash = newHash.hashCode();
    }

    public boolean hasIngredientChoice() {
        return ingredientChoice != null;
    }

    @Override
    public List<String> getIndexes() {
        List<String> indexString = new ArrayList<>();

        if (ingredientChoice instanceof RecipeChoice.MaterialChoice) {
            for (Material material : ((RecipeChoice.MaterialChoice) ingredientChoice).getChoices()) {
                indexString.add(material.toString());
            }
        } else if (ingredientChoice instanceof RecipeChoice.ExactChoice) {
            for (ItemStack item : ((RecipeChoice.ExactChoice) ingredientChoice).getChoices()) {
                indexString.add(item.getType().toString());
            }
        }

        return indexString;
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("fuel ");

        s.append(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice));

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoice();
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasCustomName()) {
            print.add(RMCChatColor.ITALIC + getName());
        } else {
            print.add(ToolsRecipeChoice.getRecipeChoiceName(ingredientChoice) + " Fuel");
        }

        return print;
    }


    @Override
    public String printBookResult(ItemResult result) {
        return getPrintForIngredient(ToolsRecipeChoice.printRecipeChoice(ingredientChoice, RMCChatColor.BLACK, RMCChatColor.BLACK));
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);

            boolean checkExact = true;
            if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
                checkExact = false;
            }
            return ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
        }

        return 0;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs an ingredient!";
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
