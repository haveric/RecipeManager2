package haveric.recipeManager.recipes.anvil.data;

import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.anvil.AnvilRecipe1_13;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
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

    public static void add(Player player, AnvilRecipe1_13 recipe, List<ItemStack> ingredients, ItemResult result, String renameText) {
        anvils.put(player.getUniqueId(), new Anvil(recipe, ingredients, result, renameText));
    }

    public static Anvil get(Player player) {
        return anvils.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        anvils.remove(player.getUniqueId());
    }
}
