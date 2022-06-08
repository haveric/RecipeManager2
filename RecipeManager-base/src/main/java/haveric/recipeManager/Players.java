package haveric.recipeManager;

import haveric.recipeManager.commands.recipe.RecipePagination;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Players {
    private static Map<String, Integer> joined = new HashMap<>();

    private Players() {
    }

    protected static void init() {
    }

    protected static void clean() {
        joined.clear();
        RecipePagination.clean();
    }

    public static void remove(Player player) {
        removeJoined(player);
        RecipePagination.clean(player.getUniqueId());
    }

    public static void addJoined(Player player) {
        Validate.notNull(player, "player can not be null");

        joined.put(player.getName(), (int) System.currentTimeMillis() / 1000);
    }

    public static void removeJoined(Player player) {
        Validate.notNull(player, "player can not be null");

        joined.remove(player.getName());
    }

    public static Integer getJoinedTime(Player player) {
        return joined.get(player.getName());
    }
}
