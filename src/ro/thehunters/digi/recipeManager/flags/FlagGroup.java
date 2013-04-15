package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagGroup extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} <permission or -permission or false>",
        };
        
        D = new String[]
        {
            "Makes the recipe or item require the crafter to have a permission.",
            "",
            "This flag can be used more than once to add more permissions, the player must have at least one to allow crafting.",
            "",
            "Specifying permission nodes with the - prefix would prevent crafting if player has at least one of those permissions.",
            "",
            "Using 'false' will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} ranks.vip",
            "{flag} jobs.crafter",
            "{flag} -ranks.newbs",
            "{flag} - jobs.warrior  // valid with a space too",
            "{flag} false",
        };
    }
    
    // Flag code
    
    private List<String> allowedGroups = new ArrayList<String>();
    private List<String> unallowedGroups = new ArrayList<String>();
    private String message;
    
    public FlagGroup()
    {
        type = FlagType.GROUP;
    }
    
    public FlagGroup(FlagGroup flag)
    {
        this();
        
        allowedGroups.addAll(flag.allowedGroups);
        unallowedGroups.addAll(flag.unallowedGroups);
        message = flag.message;
    }
    
    @Override
    public FlagGroup clone()
    {
        return new FlagGroup(this);
    }
    
    public List<String> getAllowedGroups()
    {
        return allowedGroups;
    }
    
    public void setAllowedGroups(List<String> groups)
    {
        this.allowedGroups = groups;
    }
    
    public void addAllowedGroup(String group)
    {
        this.allowedGroups.add(group);
    }
    
    public List<String> getUnallowedGroups()
    {
        return unallowedGroups;
    }
    
    public void setUnallowedGroups(List<String> groups)
    {
        this.unallowedGroups = groups;
    }
    
    public void addUnallowedGroup(String group)
    {
        this.unallowedGroups.add(group);
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setMessage(split[1].trim());
        }
        
        value = split[0].trim();
        
        if(value.charAt(0) == '-')
        {
            addUnallowedGroup(value.substring(1).trim());
        }
        else
        {
            addAllowedGroup(value);
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasPlayer())
        {
            if(!allowedGroups.isEmpty())
            {
                a.addReason(Messages.FLAG_GROUP_ALLOWED, message, "{group}", allowedGroups.get(0), "{groups}", Tools.collectionToString(allowedGroups));
            }
            
            return;
        }
        
        Player player = a.player();
        boolean ok = false;
        
        for(String perm : allowedGroups)
        {
            if(player.hasPermission(perm))
            {
                ok = true;
                break;
            }
        }
        
        if(!ok)
        {
            a.addReason(Messages.FLAG_PERMISSION_ALLOWED, message, "{permission}", allowedGroups.get(0), "{permissions}", Tools.collectionToString(allowedGroups));
        }
        
        for(String perm : unallowedGroups)
        {
            if(player.hasPermission(perm))
            {
                a.addReason(Messages.FLAG_PERMISSION_UNALLOWED, message, "{permission}", perm, "{permissions}", Tools.collectionToString(unallowedGroups));
                break;
            }
        }
    }
}
