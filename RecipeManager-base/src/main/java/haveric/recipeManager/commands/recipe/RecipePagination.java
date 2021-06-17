package haveric.recipeManager.commands.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecipePagination {
    private static Map<UUID, Pages> pagination = new HashMap<>();

    public static void clean(UUID uuid) {
        pagination.remove(uuid);
    }

    public static void clean() {
        pagination.clear();
    }

    public static Pages get(UUID playerUUID) {
        return pagination.get(playerUUID);
    }

    public static void put(UUID playerUUID, Pages pages) {
        pagination.put(playerUUID, pages);
    }

    public static void remove(UUID playerUUID) {
        pagination.remove(playerUUID);
    }
}
