package haveric.recipeManager;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Permissions {
    public static final String FLAG_PREFIX = "recipemanager.flag.";
    public static final String FLAG_ALL = FLAG_PREFIX + "*";

    private Permission permissions = null;

    public Permissions() {
        if (Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

            if (permissionProvider != null) {
                permissions = permissionProvider.getProvider();

                if (permissions.isEnabled()) {
                    Messages.log("Vault has made permission-group available for this plugin.");
                } else {
                    permissions = null;
                    Messages.info("<yellow>NOTE: <dark_aqua>Vault<reset> doesn't have a permission-group plugin connected!");
                }
            }
        } else {
            Messages.log("Vault was not found.");
        }
    }

    public boolean isEnabled() {
        return permissions != null;
    }

    public boolean playerInGroup(String playerName, String group) {
        return (permissions == null ? false : permissions.playerInGroup((String) null, playerName, group));
    }

    protected void clean() {
        permissions = null;
    }
}
