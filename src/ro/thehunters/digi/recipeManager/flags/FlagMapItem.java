package ro.thehunters.digi.recipeManager.flags;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
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
            "",
            "",
            "",
            "",
            "",
            "",
            "  scale <value>    = map scale, values: " + Tools.collectionToString(Arrays.asList(Scale.values())).toLowerCase(),
            "",
            "",
        };
        
        E = new String[]
        {
            "{flag} ...",
        };
    }
    
    // Flag code
    
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
    
    private boolean newMap = false;
    private String world = null;
    private Scale scale = Scale.CLOSEST;
    private int[] center = new int[2];
    
    @Override
    protected boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        if(split.length == 0)
        {
            RecipeErrorReporter.error("Flag " + getType() + " doesn't have any arguments!");
            return false;
        }
        
        for(String arg : split)
        {
            arg = arg.trim();
            
            if(arg.equals("newmap"))
            {
                newMap = true;
            }
            else if(arg.startsWith("world"))
            {
                value = arg.substring("world".length()).trim();
                World world = Bukkit.getWorld(value);
                
                if(world == null)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'world' that is not loaded or doesn't exist: " + value);
                    return false;
                }
                
                this.world = world.getName();
            }
            else if(arg.startsWith("scale"))
            {
                value = arg.substring("scale".length()).trim();
                
                try
                {
                    scale = Scale.valueOf(value.toUpperCase());
                }
                catch(IllegalArgumentException e)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'scale' with invalid argument: " + value, "See scale options in '" + Files.FILE_INFO_FLAGS + "' file at this flag.");
                    return false;
                }
            }
            else if(arg.startsWith("center"))
            {
                split = arg.split(" ", 2);
                
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
                        Integer.valueOf(split[2].trim())
                    };
                }
                catch(Throwable e)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'center' with invalid X/Z numbers, must be whole numbers!");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected void onPrepare(Args a)
    {
        // TODO
        
        MapView map = Bukkit.getMap(getResult().getDurability());
        
        if(newMap)
        {
            if(world == null && map == null)
            {
                RecipeErrorReporter.error("Flag " + getType() + " can't create a new map without either an existing map data value on item OR the world argument.");
                return;
            }
            
            map = Bukkit.createMap(world == null ? map.getWorld() : Bukkit.getWorld(world));
        }
        else if(map == null)
        {
            if(world == null)
            {
                RecipeErrorReporter.error("Flag " + getType() + " can't find the map for item's data value and world is not set so it can't create one either.");
                return;
            }
            
            map = Bukkit.createMap(Bukkit.getWorld(world));
        }
        
        if(map == null)
        {
            RecipeErrorReporter.error("Flag " + getType() + " couldn't create a new map!");
            return;
        }
        
        if(center != null)
        {
            map.setCenterX(center[0]);
            map.setCenterZ(center[1]);
        }
        
        if(world != null)
        {
            World world = Bukkit.getWorld(this.world);
            
            if(world == null)
            {
                a.addCustomReason("Unknown world: " + this.world);
                return;
            }
            
            map.setWorld(world);
        }
        
        if(scale != null)
        {
            map.setScale(scale);
        }
    }
}
