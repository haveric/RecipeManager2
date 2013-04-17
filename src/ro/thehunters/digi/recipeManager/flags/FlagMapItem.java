package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagMapItem extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.MAPITEM;
        
        A = new String[]
        {
            "{flag} ...",
        };
        
        D = new String[]
        {
            "FLAG NOT IMPLEMENTED",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
    private MapView map; // TODO
    
    public FlagMapItem()
    {
    }
    
    public FlagMapItem(FlagMapItem flag)
    {
        // TODO clone
    }
    
    @Override
    public FlagMapItem clone()
    {
        return new FlagMapItem(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getType() != Material.MAP || result.getDurability() < 0)
        {
            RecipeErrorReporter.error("Flag " + getType() + " needs a MAP with a specific data value to work!");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        if(split.length == 0)
        {
            RecipeErrorReporter.error("Flag " + getType() + " doesn't have any arguments!");
            return false;
        }
        
        boolean newMap = false;
        int[] center = null;
        World world = null;
        Scale scale = null;
        
        for(String s : split)
        {
            s = s.trim();
            
            if(s.equals("newmap"))
            {
                newMap = true;
            }
            else if(s.startsWith("world"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'world' argument with no world!");
                    return false;
                }
                
                value = split[1].trim();
                world = Bukkit.getWorld(value);
                
                if(world == null)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'world' that does not exist: " + value);
                    return false;
                }
            }
            else if(s.startsWith("scale"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'scale' argument with no scale!");
                    return false;
                }
                
                value = split[1].trim();
                
                try
                {
                    scale = Scale.valueOf(value.toUpperCase());
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'scale' with invalid argument: " + value, "See scale options in " + Files.FILE_INFO_FLAGS);
                    return false;
                }
            }
            else if(s.startsWith("center"))
            {
                split = s.split(" ", 3);
                
                if(split.length < 3)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'center' argument without X and Z coords!");
                    return false;
                }
                
                try
                {
                    center = new int[]
                    {
                        Integer.valueOf(split[1].trim()),
                        Integer.valueOf(split[1].trim())
                    };
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'center' with invalid X/Z numbers, must be whole numbers!");
                    return false;
                }
            }
        }
        
        MapView map = Bukkit.getMap(getResult().getDurability());
        
        if(newMap)
        {
            if(world == null && map == null)
            {
                RecipeErrorReporter.error("Flag " + getType() + " can't create a new map without either an existing map data value on item OR the world argument.");
                return false;
            }
            
            map = Bukkit.createMap(world == null ? map.getWorld() : world);
        }
        else if(map == null)
        {
            if(world == null)
            {
                RecipeErrorReporter.error("Flag " + getType() + " can't find the map for item's data value and world is not set so it can't create one either.");
                return false;
            }
            
            map = Bukkit.createMap(world);
        }
        
        if(map == null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " couldn't create a new map!");
            return false;
        }
        
        if(center != null)
        {
            map.setCenterX(center[0]);
            map.setCenterZ(center[1]);
        }
        
        if(world != null)
        {
            map.setWorld(world);
        }
        
        if(scale != null)
        {
            map.setScale(scale);
        }
        
        return true;
    }
}
