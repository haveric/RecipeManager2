package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;

public class FlagForPermission extends Flag
{
    private Map<String, Flag> flagMap = new HashMap<String, Flag>();
    
    public FlagForPermission()
    {
        type = FlagType.FORPERMISSION;
    }
    
    public FlagForPermission(FlagForPermission flag)
    {
        this();
        
        for(Entry<String, ? extends Flag> e : flag.flagMap.entrySet())
        {
            flagMap.put(e.getKey(), e.getValue().clone(this.getFlagsContainer()));
        }
    }
    
    public Map<String, Flag> getFlagMap()
    {
        return flagMap;
    }
    
    public void setFlagMap(Map<String, Flag> map)
    {
        flagMap = map;
    }
    
    public void addFlag(String permission, Flag flag)
    {
        flagMap.put(permission, flag);
    }
    
    @Override
    public FlagForPermission clone()
    {
        return new FlagForPermission(this);
    }
    
    @Override
    public boolean onParse(String value)
    {
        String[] split = value.split("@");
        
        if(split.length <= 1)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " is missing the inner flag declaration !");
        }
        
        String permission = split[0].trim(); // store permission node for later use
        split = split[1].trim().split("[:\\s]+", 2); // split by space or : char
        String flagString = '@' + split[0].trim(); // format flag name
        FlagType type = FlagType.getByName(flagString); // Find the current flag
        
        if(type == null) // If no valid flag was found
        {
            return RecipeErrorReporter.error("Flag " + getType() + " has unknown flag: " + flagString, "Name might be diferent, check " + Files.FILE_INFO_FLAGS + " for flag list.");
        }
        
        if(type.hasBit(Bit.NO_STORE))
        {
            return RecipeErrorReporter.error("Flag " + getType() + "'s flag " + flagString + " is a unstorable flag, can't be used with permissions.");
        }
        
        Flag flag = flagMap.get(type); // get existing flag, if any
        
        if(flag == null)
        {
            flag = type.createFlagClass(); // create a new instance of the flag does not exist
            flag.flagsContainer = this.getFlagsContainer(); // set container before hand to allow checks
        }
        
        value = (split.length > 1 ? split[1].trim() : null);
        
        // make sure the flag can be added to this flag list
        if(!flag.validateParse(value))
            return false;
        
        // check if parsed flag had valid values and needs to be added to flag list
        if(!flag.onParse(value))
            return false;
        
        flagMap.put(permission, flag);
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasPlayer())
            return;
        
        for(Entry<String, Flag> e : flagMap.entrySet())
        {
            if(a.player().hasPermission(e.getKey()))
            {
                e.getValue().check(a);
            }
        }
    }
    
    @Override
    protected boolean onPrepare(Args a)
    {
        if(!a.hasPlayer())
            return true;
        
        boolean failed = false;
        
        for(Entry<String, Flag> e : flagMap.entrySet())
        {
            if(a.player().hasPermission(e.getKey()))
            {
                boolean returned = e.getValue().prepare(a);
                
                if(!failed && !returned)
                    failed = true;
            }
        }
        
        return !failed;
    }
    
    @Override
    protected void onFailed(Args a)
    {
        if(!a.hasPlayer())
            return;
        
        for(Entry<String, Flag> e : flagMap.entrySet())
        {
            if(a.player().hasPermission(e.getKey()))
            {
                e.getValue().failed(a);
            }
        }
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        if(!a.hasPlayer())
            return true;
        
        boolean failed = false;
        
        for(Entry<String, Flag> e : flagMap.entrySet())
        {
            if(a.player().hasPermission(e.getKey()))
            {
                boolean returned = e.getValue().crafted(a);
                
                if(!failed && !returned)
                    failed = true;
            }
        }
        
        return !failed;
    }
}