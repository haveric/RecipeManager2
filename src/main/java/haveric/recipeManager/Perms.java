package haveric.recipeManager;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Perms {
    public static final String FLAG_PREFIX = "recipemanager.flag.";
    public static final String FLAG_ALL = FLAG_PREFIX + "*";

    private Permission permission = null;
    private static Perms instance = null;

    protected Perms() {
       // Exists only to defeat instantiation.
    }

    public static Perms getInstance() {
        if (instance == null) {
            instance = new Perms();
        }

        return instance;
    }

    public void init(Permission newPermission) {
        if (newPermission != null) {
            if (newPermission.isEnabled()) {
                permission = newPermission;
                MessageSender.getInstance().log("Vault has made permission-group available for this plugin.");
            } else {
                permission = null;
                MessageSender.getInstance().info("<yellow>NOTE: <dark_aqua>Vault<reset> doesn't have a permission-group plugin connected!");
            }
        }
    }

    public boolean isEnabled() {
        return permission != null;
    }

    public boolean playerInGroup(String playerName, String group) {
        boolean isPlayerInGroup = false;

        if (permission != null) {
            try {
                UUID uuid = UUIDFetcher.getUUIDOf(playerName);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                isPlayerInGroup = permission.playerInGroup(null, player, group);
            } catch (Exception e) { }
        }

        return isPlayerInGroup;
    }

    protected void clean() {
        permission = null;
    }

    public static boolean hasFlagAll(Player player) {
        return player.hasPermission(FLAG_ALL);
    }

    public static boolean hasFlagPrefix(Player player, String name) {
        return player.hasPermission(FLAG_PREFIX + name);
    }
}
