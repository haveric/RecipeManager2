package haveric.recipeManager.recipes.cartography.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.cartography.CartographyRecipe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
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

    public static void add(Player player, CartographyRecipe recipe, ItemStack top, ItemStack bottom, ItemResult result, Location location) {
        ItemStack topAdd = null;
        ItemStack bottomAdd = null;
        if (top != null) {
            topAdd = top.clone();
        }

        if (bottom != null) {
            bottomAdd = bottom.clone();
        }

        cartographyTables.put(player.getUniqueId(), new CartographyTable(recipe, topAdd, bottomAdd, result, location));
    }

    public static CartographyTable get(Player player) {
        return cartographyTables.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        cartographyTables.remove(player.getUniqueId());
    }
}
