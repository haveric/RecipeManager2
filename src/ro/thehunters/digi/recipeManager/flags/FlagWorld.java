package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagWorld extends Flag
{
    // Flag documentation
    
    public static final String[] A;
    public static final String[] D;
    public static final String[] E;
    
    static
    {
        A = new String[]
        {
            "{flag} ",
            "{flag} [-]<world>[, ...] [| <message>]",
            "{flag} false",
        };
        
        D = new String[]
        {
            "Restricts recipe to only some worlds.",
            "The '<world>' arguemnt can be a world name, the recipe/result will only work in that world.",
            "Using the flag more than once will add more worlds to the list.",
            "",
            "The '-<world>' argument can be a world name prefixed with - character to define a disallowed world.",
            "",
            "You should only allow or disallow words, using both systems would be pointless.",
            "",
            "Optionally you can specify a message for allowed permission nodes and one for unallowed worlds, but not for individual worlds.",
            "The messages can have the following variables:",
            "  {permission}  = first allowed or unallowed permission node in the list",
            "  {permissions}  = a comma separated list of the allowed or unallowed permission nodes",
            "",
            "Using 'false' will disable the flag.",
        };
        
        E = new String[]
        {
            "{flag} world // only allows 'world'",
            "{flag} -world_nether // disallows 'world_nether'",
            "{flag} false",
        };
    }
    
    // Flag code
    
    // TODO redesign
    // @perm perm.perm, perm.perm, perm.perm | message applies to all 3
    // @perm newperm | message that applies only to this perm
    
    private Map<String, String> allowedWorlds = new HashMap<String, String>();
    private Map<String, String> unallowedWorlds = new HashMap<String, String>();
    
    /*
    private Set<String> allowedWorlds = new HashSet<String>();
    private String allowedMessage;
    
    private Set<String> unallowedWorlds = new HashSet<String>();
    private String unallowedMessage;
    */
    
    public FlagWorld()
    {
        type = FlagType.WORLD;
    }
    
    public FlagWorld(FlagWorld flag)
    {
        this();
        
        /*
        allowedWorlds.addAll(flag.allowedWorlds);
        allowedMessage = flag.allowedMessage;
        unallowedWorlds.addAll(flag.unallowedWorlds);
        unallowedMessage = flag.unallowedMessage;
        */
    }
    
    @Override
    public FlagWorld clone()
    {
        return new FlagWorld(this);
    }
    
    /*
    public Set<String> getAllowedWorlds()
    {
        return allowedWorlds;
    }
    
    public void setAllowedWorlds(Set<String> worlds)
    {
        this.allowedWorlds = worlds;
    }
    
    public void addAllowedWorld(String world)
    {
        this.allowedWorlds.add(world);
    }
    
    public Set<String> getUnallowedWorlds()
    {
        return unallowedWorlds;
    }
    
    public void setUnallowedWorlds(Set<String> worlds)
    {
        this.unallowedWorlds = worlds;
    }
    
    public void addUnallowedWorld(String world)
    {
        this.unallowedWorlds.add(world);
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
    */
    
    @Override
    protected boolean onParse(String value)
    {
        /*
        String[] split = value.split("\\|");
        value = split[0].trim().toLowerCase();
        
        if(value.charAt(0) == '-')
        {
            addUnallowedWorld(value.substring(1).trim());
            
            if(split.length > 1)
            {
                setUnallowedMessage(split[1].trim());
            }
        }
        else
        {
            addAllowedWorld(value);
            
            if(split.length > 1)
            {
                setAllowedMessage(split[1].trim());
            }
        }
        */
        
        String[] split = value.split("\\|");
        String message = (split.length > 1 ? split[1].trim() : null);
        split = split[0].toLowerCase().split(",");
        
        for(String s : split)
        {
            if(s.charAt(0) == '-')
            {
                unallowedWorlds.put(s.substring(1).trim(), message);
            }
            else
            {
                allowedWorlds.put(s.trim(), message);
            }
        }
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        String world = null;
        
        if(a.hasLocation())
        {
            world = a.location().getWorld().toString().toLowerCase();
        }
        
        if(!allowedWorlds.isEmpty() && (world == null || !allowedWorlds.containsKey(world)))
        {
            a.addReason(Messages.FLAG_WORLD_ALLOWED, allowedWorlds.get(world), "{worlds}", Tools.collectionToString(allowedWorlds.keySet()));
        }
        
        if(!unallowedWorlds.isEmpty() && (world == null || unallowedWorlds.containsKey(world)))
        {
            a.addReason(Messages.FLAG_WORLD_UNALLOWED, unallowedWorlds.get(world), "{worlds}", Tools.collectionToString(unallowedWorlds.keySet()));
        }
        
        /*
        if(!allowedWorlds.isEmpty() && (world == null || !allowedWorlds.contains(world)))
        {
            a.addReason(Messages.FLAG_WORLD_ALLOWED, allowedMessage, "{worlds}", Tools.collectionToString(allowedWorlds));
        }
        
        if(!unallowedWorlds.isEmpty() && (world == null || unallowedWorlds.contains(world)))
        {
            a.addReason(Messages.FLAG_WORLD_UNALLOWED, unallowedMessage, "{worlds}", Tools.collectionToString(unallowedWorlds));
        }
        */
    }
    
    @Override
    public List<String> information()
    {
        List<String> list = new ArrayList<String>(2);
        
        if(!allowedWorlds.isEmpty())
        {
            list.add(Messages.FLAG_WORLD_ALLOWED.get("{worlds}", Tools.collectionToString(allowedWorlds.keySet())));
        }
        
        if(!unallowedWorlds.isEmpty())
        {
            list.add(Messages.FLAG_WORLD_UNALLOWED.get("{worlds}", Tools.collectionToString(unallowedWorlds.keySet())));
        }
        
        return list;
    }
}
