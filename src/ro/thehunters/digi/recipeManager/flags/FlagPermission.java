package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagPermission extends Flag
{
    private List<String> permissions = new ArrayList<String>();
    private String       message;
    
    public FlagPermission()
    {
        type = FlagType.PERMISSION;
    }
    
    @Override
    public FlagPermission clone()
    {
        FlagPermission clone = new FlagPermission();
        
        clone.permissions.addAll(permissions);
        clone.message = message;
        
        return clone;
    }
    
    public List<String> getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions)
    {
        this.permissions = permissions;
    }
    
    public void addPermissions(String permission)
    {
        permissions.add(permission);
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
    public boolean onParse(String value)
    {
        String[] split = value.split("\\|");
        
        if(split.length > 1)
        {
            setMessage(split[1].trim());
        }
        
        addPermissions(split[0].trim());
        
        return true;
    }
    
    @Override
    public void onCheck(Args a)
    {
        Player player = a.player();
        boolean ok = false;
        
        if(player != null)
        {
            for(String s : permissions)
            {
                if(player.hasPermission(s))
                {
                    ok = true;
                    break;
                }
            }
        }
        
        if(!ok)
        {
            a.addReason(Messages.CRAFT_FLAG_PERMISSIONS, message, "{permissions}", Tools.convertListToString(permissions));
        }
    }
}
