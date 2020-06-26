package haveric.recipeManager.recipes.smithing.data;

import haveric.recipeManager.data.BaseRecipeData;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SmithingTableData extends BaseRecipeData {
    private Location location;

    public SmithingTableData(BaseRecipe recipe, List<ItemStack> ingredients, ItemResult result, Location location) {
        super(recipe, ingredients, result);

        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location newLocation) {
        location = newLocation;
    }
}
