package haveric.recipeManager.recipes.anvil.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.anvil.AnvilRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores in-use anvils to help keep custom recipes consistent and provide easier lookups
 */
public class Anvils {
    private static Map<UUID, Anvil> anvils = new HashMap<>();

    private Anvils() {

    }

    public static void init() { }

    public static void clean() {
        anvils.clear();
    }

    public static void add(Player player, AnvilRecipe recipe, ItemStack left, ItemStack right, ItemResult result, String renameText) {
        ItemStack leftAdd = null;
        ItemStack rightAdd = null;
        if (left != null) {
            leftAdd = left.clone();
        }

        if (right != null) {
            rightAdd = right.clone();
        }
        anvils.put(player.getUniqueId(), new Anvil(recipe, leftAdd, rightAdd, result, renameText));
    }

    public static Anvil get(Player player) {
        return anvils.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        anvils.remove(player.getUniqueId());
    }
}
