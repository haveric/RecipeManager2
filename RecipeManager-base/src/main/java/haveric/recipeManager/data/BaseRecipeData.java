package haveric.recipeManager.data;

import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BaseRecipeData {
    private BaseRecipe recipe;
    private ItemResult result;
    private List<ItemStack> ingredients = new ArrayList<>();
    private List<ItemStack> ingredientsSingleStack = new ArrayList<>();

    public BaseRecipeData(BaseRecipe recipe, List<ItemStack> ingredients, ItemResult result) {
        this.recipe = recipe;

        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) {
                this.ingredients.add(null);
            } else {
                this.ingredients.add(ingredient.clone());
            }
        }

        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) {
                this.ingredientsSingleStack.add(null);
            } else {
                ItemStack clone = ingredient.clone();
                clone.setAmount(1);
                this.ingredientsSingleStack.add(clone);
            }
        }

        this.result = result;
    }

    public BaseRecipe getRecipe() {
        return recipe;
    }

    public ItemResult getResult() {
        return result;
    }

    public ItemStack getIngredient(int index) {
        return ingredients.get(index);
    }

    public ItemStack getIngredientSingleStack(int index) {
        return ingredientsSingleStack.get(index);
    }
}
