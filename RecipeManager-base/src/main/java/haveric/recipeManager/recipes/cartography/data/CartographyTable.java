package haveric.recipeManager.recipes.cartography.data;

import haveric.recipeManager.data.BaseRecipeData;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CartographyTable extends BaseRecipeData {
    private Location location;

    public CartographyTable(CartographyRecipe recipe, List<ItemStack> ingredients, ItemResult result, Location location) {
        super(recipe, ingredients, result);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
