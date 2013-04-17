package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagPermission extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.PERMISSION;
        
        A = new String[]
        {
            "{flag} [!]<permission>, [...] | [fail message]",
        };
        
        D = new String[]
        {
            "Makes the recipe or item require the crafter to have a permission.",
            "Using this flag more than once will add more permissions, the player must have at least one to allow crafting.",
            "",
            "The '<permission>' argument must be an permission node, regardless if it exists or not.",
            "",
            "Adding ! character as prefix to individual permission nodes will do the opposite check, if crafter has permission it will not craft.",
            "",
            "You can also specify more permissions separated by , character.",
            "",
            "Optionally you can specify a failure message that will be used on the specific permission(s) defined.",
            "The messages can have the following variables:",
            "  {permission}  = permission that was not found or was found and it's unallowed.",
            "  {permissions}  = a comma separated list of the allowed or unallowed permission nodes.",
        };
        
        E = new String[]
        {
            "{flag} ranks.vip",
            "{flag} !jobs.builder | <red>Builders can't use this!",
            "{flag} jobs.famer, jobs.trader | <red>You must be a farmer or trader!",
            "{flag} ! ranks.newbs, ! ranks.newbies | <yellow>Noobs can't use this. // valid with spaces too",
        };
    }
    
    // Flag code
    
    private Map<String, Boolean> permissions = new HashMap<String, Boolean>();
    private Map<String, String> messages = new HashMap<String, String>();
    
    public FlagPermission()
    {
    }
    
    public FlagPermission(FlagPermission flag)
    {
        permissions.putAll(flag.permissions);
        messages.putAll(flag.messages);
    }
    
    @Override
    public FlagPermission clone()
    {
        return new FlagPermission(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    public Map<String, Boolean> getPermissions()
    {
        return permissions;
    }
    
    public void addPermission(String permission, String message, boolean allowed)
    {
        permissions.put(permission, allowed);
        messages.put(permission, message);
    }
    
    public Map<String, String> getMessages()
    {
        return messages;
    }
    
    public String getPermissionMessage(String permission)
    {
        return messages.get(permission);
    }
    
    public String getPermissionsString(boolean allowed)
    {
        StringBuilder s = new StringBuilder();
        
        for(Entry<String, Boolean> e : permissions.entrySet())
        {
            if(allowed == e.getValue().booleanValue())
            {
                if(s.length() > 0)
                {
                    s.append(", ");
                }
                
                s.append(e.getKey());
            }
        }
        
        return s.toString();
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        String message = (split.length > 1 ? split[1].trim() : null);
        split = split[0].toLowerCase().split(",");
        
        for(String arg : split)
        {
            arg = arg.trim();
            boolean not = arg.charAt(0) == '!';
            
            if(not)
            {
                arg = arg.substring(1).trim();
            }
            
            /*
            Permission permission = Bukkit.getPluginManager().getPermission(arg);
            
            if(permission == null)
            {
                permission = new Permission(arg, PermissionDefault.FALSE);
                Bukkit.getPluginManager().addPermission(permission);
                //RecipeErrorReporter.warning("Flag " + getType() + " has permission '" + arg + "' which is not registered!");
            }
            */
            
            addPermission(arg, message, !not);
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        for(Entry<String, Boolean> e : permissions.entrySet())
        {
            if(e.getValue().booleanValue())
            {
                if(!a.hasPlayer() || !a.player().hasPermission(e.getKey()))
                {
                    a.addReason(Messages.FLAG_PERMISSION_ALLOWED, getPermissionMessage(e.getKey()), "{permission}", e.getKey(), "{permissions}", getPermissionsString(true));
                }
            }
            else
            {
                if(a.hasPlayer() && a.player().hasPermission(e.getKey()))
                {
                    a.addReason(Messages.FLAG_PERMISSION_UNALLOWED, getPermissionMessage(e.getKey()), "{permission}", e.getKey(), "{permissions}", getPermissionsString(false));
                }
            }
        }
    }
    
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(2);
        
        String allowed = getPermissionsString(true);
        String unallowed = getPermissionsString(false);
        
        if(!allowed.isEmpty())
        {
            int i = allowed.indexOf(',');
            String permission = allowed.substring(0, (i > 0 ? i : allowed.length()));
            list.add(Messages.FLAG_PERMISSION_ALLOWED.get("{permission}", permission, "{permissions}", allowed));
        }
        
        if(!unallowed.isEmpty())
        {
            int i = unallowed.indexOf(',');
            String permission = unallowed.substring(0, (i > 0 ? i : unallowed.length()));
            list.add(Messages.FLAG_PERMISSION_UNALLOWED.get("{permission}", permission, "{permissions}", unallowed));
        }
        
        return list;
    }
}
