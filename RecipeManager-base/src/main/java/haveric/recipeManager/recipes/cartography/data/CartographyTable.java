package haveric.recipeManager.recipes.cartography.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class CartographyTable {
    private CartographyRecipe recipe;
    private ItemResult result;
    private ItemStack topIngredient;
    private ItemStack bottomIngredient;
    private Location location;

    public CartographyTable(CartographyRecipe recipe, ItemStack topIngredient, ItemStack bottomIngredient, ItemResult result, Location location) {
        this.recipe = recipe;
        this.topIngredient = topIngredient;
        this.bottomIngredient = bottomIngredient;
        this.result = result;
        this.location = location;
    }

    public CartographyRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(CartographyRecipe recipe) {
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
