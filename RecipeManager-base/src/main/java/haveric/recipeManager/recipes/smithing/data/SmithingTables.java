package haveric.recipeManager.recipes.smithing.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.smithing.RMSmithingRecipe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stores in-use grindstones to help keep custom recipes consistent and provide easier lookups
 */
public class SmithingTables {
    private static Map<UUID, SmithingTableData> smithingTables = new HashMap<>();

    private SmithingTables() {

    }

    public static void init() { }

    public static void clean() {
        smithingTables.clear();
    }

    public static void add(Player player, RMSmithingRecipe recipe, List<ItemStack> ingredients, ItemResult result, Location location) {
        smithingTables.put(player.getUniqueId(), new SmithingTableData(recipe, ingredients, result, location));
    }

    public static SmithingTableData get(Player player) {
        return smithingTables.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        smithingTables.remove(player.getUniqueId());
    }
}
