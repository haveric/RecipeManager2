package haveric.recipeManager.flags;

import haveric.recipeManager.Perms;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Map;

public class FlagFactory {

    private static FlagFactory instance = null;
    private Map<String, FlagDescriptor> flags = new HashMap<String, FlagDescriptor>();
    private Map<String, FlagDescriptor> nameMap = new HashMap<String, FlagDescriptor>();

    private boolean initialized = false;

    public static FlagFactory getInstance() {
        if (instance == null) {
            instance = new FlagFactory();
        }

        return instance;
    }

    private FlagFactory() {
        initialized = false;
    }

    protected void initializeFlag(String mainAlias, Flag newFlag, int bits, String... aliases) {
        if (!initialized) {
            if (mainAlias.startsWith("@")) {
                mainAlias = mainAlias.split("@")[1];
            }
            if (!flags.containsKey(mainAlias)) {
                FlagDescriptor desc = new FlagDescriptor(mainAlias, newFlag, bits, aliases);
                flags.put(mainAlias, desc);
                nameMap.put(mainAlias, desc);
            }
        }
    }

    public void init() {
        initialized = true;
        for (FlagDescriptor flag : flags.values()) {
            for (String alias : flag.getAliases()) {
                if (!nameMap.containsKey(alias)) {
                    nameMap.put(alias, flag);
                }
            }
        }
    }

    public void initPermissions() {
        Permission parent = Bukkit.getPluginManager().getPermission(Perms.FLAG_ALL);

        if (parent == null) {
            parent = new Permission(Perms.FLAG_ALL, PermissionDefault.TRUE);
            parent.setDescription("Allows use of flag.");

            Bukkit.getPluginManager().addPermission(parent);
        }

        Permission p;

        for (Map.Entry<String, FlagDescriptor> entry : nameMap.entrySet()) {
            String name = entry.getKey();
            FlagDescriptor flag = entry.getValue();

            if (flag.hasBit(FlagBit.NO_SKIP_PERMISSION)) {
                continue;
            }

            if (Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + name) != null) {
                continue;
            }

            p = new Permission(Perms.FLAG_PREFIX + name, PermissionDefault.TRUE);
            p.setDescription("Allows use of the " + flag.getNameDisplay() + " flag.");
            p.addParent(parent, true);
            Bukkit.getPluginManager().addPermission(p);
        }
    }

    public Map<String, FlagDescriptor> getFlags() {
        return flags;
    }

    public FlagDescriptor getFlagByName(String name) {
        Validate.notNull(name);

        if (name.startsWith("@")) {
            name = name.split("@")[1];
        }

        return nameMap.get(name);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
