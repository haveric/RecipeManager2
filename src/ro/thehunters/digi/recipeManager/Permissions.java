package ro.thehunters.digi.recipeManager;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Permissions
{
    private Permission permissions = null;
    
    public Permissions()
    {
        if(Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault)
        {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            
            if(permissionProvider != null)
            {
                permissions = permissionProvider.getProvider();
                
                if(permissions.isEnabled())
                {
                    Messages.log("Vault has made group-permission available for this plugin.");
                }
                else
                {
                    permissions = null;
                    Messages.log("Vault doesn't have a group-permission plugin connected!");
                }
            }
        }
    }
    
    public boolean isEnabled()
    {
        return permissions != null;
    }
    
    public boolean playerInGroup(String playerName, String group)
    {
        return (permissions == null ? false : permissions.playerInGroup((String)null, playerName, group));
    }
    
    protected void clean()
    {
        permissions = null;
    }
}