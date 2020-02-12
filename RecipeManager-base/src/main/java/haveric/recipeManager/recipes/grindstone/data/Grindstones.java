package haveric.recipeManager.recipes.grindstone.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.grindstone.GrindstoneRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores in-use grindstones to help keep custom recipes consistent and provide easier lookups
 */
public class Grindstones {
    private static Map<UUID, Grindstone> grindstones = new HashMap<>();

    private Grindstones() {

    }

    public static void init() { }

    public static void clean() {
        grindstones.clear();
    }

    public static void add(Player player, GrindstoneRecipe recipe, ItemStack top, ItemStack bottom, ItemResult result) {
        ItemStack topAdd = null;
        ItemStack bottomAdd = null;
        if (top != null) {
            topAdd = top.clone();
        }

        if (bottom != null) {
            bottomAdd = bottom.clone();
        }

        grindstones.put(player.getUniqueId(), new Grindstone(recipe, topAdd, bottomAdd, result));
    }

    public static Grindstone get(Player player) {
        return grindstones.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        grindstones.remove(player.getUniqueId());
    }
}
