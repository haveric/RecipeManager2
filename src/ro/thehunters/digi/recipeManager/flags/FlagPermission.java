package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagPermission extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} <permission>",
            "{flag} <permission> | [message]",
            "{flag} -<permission>",
            "{flag} -<permission> | [message]",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Makes the recipe or item require the crafter to have a permission.",
            "",
            "This flag can be used more than once to add more permissions, the player must have at least one to allow crafting.",
            "",
            "Specifying permission nodes with the - prefix would prevent crafting if player has at least one of those permissions.",
            "",
            "Optionally you can specify a message for allowed permission nodes and one for unallowed permission nodes, but not for individual permissions.",
            "The messages can have the following variables:",
            "  {permission}  = first allowed or unallowed permission node in the list",
            "  {permissions}  = a comma separated list of the allowed or unallowed permission nodes",
            "",
            "Using 'false' will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} ranks.vip",
            "{flag} jobs.crafter | <red>Need perms: {permissions} // this message will be printed for 'ranks.vip' too !",
            "{flag} -ranks.newbs | <red>Can't have perm: {permissions} // a diferent message for unallowed nodes",
            "{flag} - jobs.warrior // valid with a space too",
            "{flag} false",
        };
    }
    
    // Flag code
    
    private List<String> allowedPermissions = new ArrayList<String>();
    private String allowedMessage;
    
    private List<String> unallowedPermissions = new ArrayList<String>();
    private String unallowedMessage;
    
    public FlagPermission()
    {
        type = FlagType.PERMISSION;
    }
    
    public FlagPermission(FlagPermission flag)
    {
        this();
        
        allowedPermissions.addAll(flag.allowedPermissions);
        allowedMessage = flag.allowedMessage;
        unallowedPermissions.addAll(flag.unallowedPermissions);
        unallowedMessage = flag.unallowedMessage;
    }
    
    @Override
    public FlagPermission clone()
    {
        return new FlagPermission(this);
    }
    
    public List<String> getAllowedPermissions()
    {
        return allowedPermissions;
    }
    
    public void setAllowedPermissions(List<String> permissions)
    {
        this.allowedPermissions = permissions;
    }
    
    public void addAllowedPermission(String permission)
    {
        this.allowedPermissions.add(permission);
    }
    
    public List<String> getUnallowedPermissions()
    {
        return unallowedPermissions;
    }
    
    public void setUnallowedPermissions(List<String> permissions)
    {
        this.unallowedPermissions = permissions;
    }
    
    public void addUnallowedPermission(String permission)
    {
        this.unallowedPermissions.add(permission);
    }
    
    public String getAllowedMessage()
    {
        return allowedMessage;
    }
    
    public void setAllowedMessage(String allowedMessage)
    {
        this.allowedMessage = allowedMessage;
    }
    
    public String getUnallowedMessage()
    {
        return unallowedMessage;
    }
    
    public void setUnallowedMessage(String unallowedMessage)
    {
        this.unallowedMessage = unallowedMessage;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        value = split[0].trim();
        
        if(value.charAt(0) == '-')
        {
            addUnallowedPermission(value.substring(1).trim());
            
            if(split.length > 1)
            {
                setUnallowedMessage(split[1].trim());
            }
        }
        else
        {
            addAllowedPermission(value);
            
            if(split.length > 1)
            {
                setAllowedMessage(split[1].trim());
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasPlayer())
        {
            if(!allowedPermissions.isEmpty())
            {
                a.addReason(Messages.FLAG_PERMISSION_ALLOWED, allowedMessage, "{permission}", allowedPermissions.get(0), "{permissions}", Tools.collectionToString(allowedPermissions));
            }
            
            return;
        }
        
        Player player = a.player();
        boolean ok = false;
        
        for(String perm : allowedPermissions)
        {
            if(player.hasPermission(perm))
            {
                ok = true;
                break;
            }
        }
        
        if(!ok)
        {
            a.addReason(Messages.FLAG_PERMISSION_ALLOWED, allowedMessage, "{permission}", allowedPermissions.get(0), "{permissions}", Tools.collectionToString(allowedPermissions));
        }
        
        for(String perm : unallowedPermissions)
        {
            if(player.hasPermission(perm))
            {
                a.addReason(Messages.FLAG_PERMISSION_UNALLOWED, unallowedMessage, "{permission}", perm, "{permissions}", Tools.collectionToString(unallowedPermissions));
                break;
            }
        }
    }
    
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(2);
        
        if(!allowedPermissions.isEmpty())
        {
            list.add(Messages.FLAG_PERMISSION_ALLOWED.get("{permissions}", Tools.collectionToString(allowedPermissions)));
        }
        
        if(!unallowedPermissions.isEmpty())
        {
            list.add(Messages.FLAG_PERMISSION_UNALLOWED.get("{worpermissionslds}", Tools.collectionToString(unallowedPermissions)));
        }
        
        return list;
    }
}
