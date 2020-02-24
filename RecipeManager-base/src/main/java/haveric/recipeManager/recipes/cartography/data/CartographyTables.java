package haveric.recipeManager.recipes.cartography.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartographyTables {
    private static Map<UUID, CartographyTable> cartographyTables = new HashMap<>();

    private CartographyTables() {

    }

    public static void init() { }

    public static void clean() {
        cartographyTables.clear();
    }

    public static void add(Player player, CartographyRecipe recipe, List<ItemStack> ingredients, ItemResult result, Location location) {
        cartographyTables.put(player.getUniqueId(), new CartographyTable(recipe, ingredients, result, location));
    }

    public static CartographyTable get(Player player) {
        return cartographyTables.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        cartographyTables.remove(player.getUniqueId());
    }
}
