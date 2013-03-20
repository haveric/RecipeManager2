package ro.thehunters.digi.recipeManager.flags;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.flags.FlagType.Bit;

public class FlagForPermission extends Flag
{
    private Map<String, Map<FlagType, Flag>> flagMap = new LinkedHashMap<String, Map<FlagType, Flag>>();
    
    public FlagForPermission()
    {
        type = FlagType.FORPERMISSION;
    }
    
    public FlagForPermission(FlagForPermission flag)
    {
        this();
        
        for(Entry<String, Map<FlagType, Flag>> e : flag.flagMap.entrySet())
        {
            Map<FlagType, Flag> flags = new LinkedHashMap<FlagType, Flag>();
            
            for(Flag f : e.getValue().values())
            {
                flags.put(f.getType(), f.clone(this.getFlagsContainer()));
            }
            
            flagMap.put(e.getKey(), flags);
        }
    }
    
    @Override
    public FlagForPermission clone()
    {
        return new FlagForPermission(this);
    }
    
    public Map<String, Map<FlagType, Flag>> getFlagMap()
    {
        return flagMap;
    }
    
    public Flag getFlagForPermission(String permission, FlagType type)
    {
        Map<FlagType, Flag> flags = flagMap.get(permission);
        
        if(flags != null)
        {
            return flags.get(type);
        }
        
        return null;
    }
    
    public void setFlagMap(Map<String, Map<FlagType, Flag>> map)
    {
        flagMap = map;
    }
    
    public void setFlagsForPermission(String permission, Map<FlagType, Flag> flags)
    {
        flagMap.put(permission, flags);
    }
    
    /**
     * Checks if the flag can be added to this flag list.<br>
     * 
     * @param flag
     * @return false if flag can only be added on specific flaggables
     */
    public boolean canAdd(Flag flag)
    {
        return flag != null && flag.validate() && !flag.getType().hasBit(Bit.NO_STORE);
    }
    
    /**
     * Attempts to add a flag to this flag list for the permission.<br>
     * Adds an error to the {@link RecipeErrorReporter} class if flag is not compatible with recipe/result.
     * 
     * @param permission
     * @param flag
     */
    public void addFlag(String permission, Flag flag)
    {
        Validate.notNull(flag);
        
        if(canAdd(flag))
        {
            Map<FlagType, Flag> flags = flagMap.get(permission);
            
            if(flags == null)
            {
                flags = new LinkedHashMap<FlagType, Flag>();
                flagMap.put(permission, flags);
            }
            
            flag.flagsContainer = this.getFlagsContainer();
            flags.put(flag.getType(), flag);
        }
    }
    
    @Override
    protected boolean onParse(String value)
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
        
        Flag flag = getFlagForPermission(permission, type); // get existing flag, if any
        
        if(flag == null)
        {
            flag = type.createFlagClass(); // create a new instance of the flag does not exist
            flag.flagsContainer = this.getFlagsContainer(); // set container before hand to allow checks
        }
        
        value = (split.length > 1 ? split[1].trim() : null);
        
        // make sure the flag can be added to this flag list
        if(!flag.validateParse(value))
        {
            return false;
        }
        
        // check if parsed flag had valid values and needs to be added to flag list
        if(!flag.onParse(value))
        {
            return false;
        }
        
        addFlag(permission, flag);
        
        return true;
    }
    
    @Override
    protected void onCheck(Args a)
    {
        event(a, 'c');
    }
    
    @Override
    protected void onFailed(Args a)
    {
        event(a, 'f');
    }
    
    @Override
    protected boolean onPrepare(Args a)
    {
        return event(a, 'p');
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        return event(a, 'r');
    }
    
    private boolean event(Args a, char method)
    {
        if(!a.hasPlayer())
        {
            return true; // no fail, optional flag
        }
        
        boolean failed = false;
        
        for(Entry<String, Map<FlagType, Flag>> e : flagMap.entrySet())
        {
            if(a.player().hasPermission(e.getKey()))
            {
                for(Flag f : e.getValue().values())
                {
                    Boolean returned = null;
                    
                    switch(method)
                    {
                        case 'c':
                            f.check(a);
                            break;
                        
                        case 'p':
                            returned = f.prepare(a);
                            break;
                        
                        case 'r':
                            returned = f.crafted(a);
                            break;
                        
                        case 'f':
                            f.failed(a);
                            break;
                    }
                    
                    if(returned != null && !returned && !failed)
                    {
                        failed = true;
                    }
                }
            }
        }
        
        return !failed;
    }
}