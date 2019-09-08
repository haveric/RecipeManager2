package haveric.recipeManager.recipes.anvil.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.anvil.AnvilRecipe;
import org.bukkit.inventory.ItemStack;

public class Anvil {
    private AnvilRecipe recipe;
    private ItemResult result;
    private ItemStack leftIngredient;
    private ItemStack rightIngredient;
    private ItemStack leftSingleStack;
    private ItemStack rightSingleStack;
    private String renameText;

    public Anvil(AnvilRecipe recipe, ItemStack leftIngredient, ItemStack rightIngredient, ItemResult result, String renameText) {
        this.recipe = recipe;
        this.leftIngredient = leftIngredient;
        if (leftIngredient == null) {
            this.leftSingleStack = null;
        } else {
            this.leftSingleStack = leftIngredient.clone();
            this.leftSingleStack.setAmount(1);
        }
        this.rightIngredient = rightIngredient;
        if (rightIngredient == null) {
            this.rightSingleStack = null;
        } else {
            this.rightSingleStack = rightIngredient.clone();
            this.rightSingleStack.setAmount(1);
        }
        this.result = result;
        this.renameText = renameText;
    }

    public AnvilRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(AnvilRecipe recipe) {
        this.recipe = recipe;
    }

    public ItemStack getLeftIngredient() {
        return leftIngredient;
    }

    public void setLeftIngredient(ItemStack leftIngredient) {
        this.leftIngredient = leftIngredient;
    }

    public ItemStack getRightIngredient() {
        return rightIngredient;
    }

    public void setRightIngredient(ItemStack rightIngredient) {
        this.rightIngredient = rightIngredient;
    }

    public ItemResult getResult() {
        return result;
    }

    public void setResult(ItemResult result) {
        this.result = result;
    }

    public ItemStack getLeftSingleStack() {
        return leftSingleStack;
    }

    public ItemStack getRightSingleStack() {
        return rightSingleStack;
    }

    public String getRenameText() {
        return renameText;
    }

    public void setRenameText(String renameText) {
        this.renameText = renameText;
    }
}
