package haveric.recipeManager.recipes.grindstone.data;

import haveric.recipeManager.data.BaseRecipeData;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.grindstone.GrindstoneRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stores in-use grindstones to help keep custom recipes consistent and provide easier lookups
 */
public class Grindstones {
    private static Map<UUID, BaseRecipeData> grindstones = new HashMap<>();

    private Grindstones() {

    }

    public static void init() { }

    public static void clean() {
        grindstones.clear();
    }

    public static void add(Player player, GrindstoneRecipe recipe, List<ItemStack> ingredients, ItemResult result) {
        grindstones.put(player.getUniqueId(), new BaseRecipeData(recipe, ingredients, result));
    }

    public static BaseRecipeData get(Player player) {
        return grindstones.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        grindstones.remove(player.getUniqueId());
    }
}
