package haveric.recipeManager.recipes.grindstone.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.grindstone.GrindstoneRecipe;
import org.bukkit.inventory.ItemStack;

public class Grindstone {
    private GrindstoneRecipe recipe;
    private ItemResult result;
    private ItemStack topIngredient;
    private ItemStack bottomIngredient;

    public Grindstone(GrindstoneRecipe recipe, ItemStack topIngredient, ItemStack bottomIngredient, ItemResult result) {
        this.recipe = recipe;
        this.topIngredient = topIngredient;

        this.bottomIngredient = bottomIngredient;

        this.result = result;
    }

    public GrindstoneRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(GrindstoneRecipe recipe) {
        this.recipe = recipe;
    }

    public ItemResult getResult() {
        return result;
    }

    public void setResult(ItemResult result) {
        this.result = result;
    }

    public ItemStack getTopIngredient() {
        return topIngredient;
    }

    public void setTopIngredient(ItemStack topIngredient) {
        this.topIngredient = topIngredient;
    }

    public ItemStack getBottomIngredient() {
        return bottomIngredient;
    }

    public void setBottomIngredient(ItemStack bottomIngredient) {
        this.bottomIngredient = bottomIngredient;
    }
}
