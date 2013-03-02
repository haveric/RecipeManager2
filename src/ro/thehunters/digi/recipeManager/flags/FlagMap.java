package ro.thehunters.digi.recipeManager.flags;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;

public class FlagMap extends Flag
{
    public FlagMap()
    {
        type = FlagType.MAP;
    }
    
    @Override
    public boolean onValidate()
    {
        ItemResult result = getResult();
        
        if(result == null || result.getType() != Material.MAP || result.getDurability() < 0)
        {
            RecipeErrorReporter.error("Flag @" + type + " needs a MAP with a specific data value to work!");
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean onParse(String value)
    {
        String[] split = value.toLowerCase().split("\\|");
        
        if(split.length == 0)
        {
            RecipeErrorReporter.error("Flag @" + type + " doesn't have any arguments!");
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
                    RecipeErrorReporter.error("Flag @" + type + " has 'world' argument with no world!");
                    return false;
                }
                
                value = split[1].trim();
                world = Bukkit.getWorld(value);
                
                if(world == null)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'world' that does not exist: " + value);
                    return false;
                }
            }
            else if(s.startsWith("scale"))
            {
                split = s.split(" ", 2);
                
                if(split.length <= 1)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'scale' argument with no scale!");
                    return false;
                }
                
                value = split[1].trim();
                
                try
                {
                    scale = Scale.valueOf(value.toUpperCase());
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'scale' with invalid argument: " + value, "See scale options in " + Files.FILE_INFO_FLAGS);
                    return false;
                }
            }
            else if(s.startsWith("center"))
            {
                split = s.split(" ", 3);
                
                if(split.length < 3)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'center' argument without X and Z coords!");
                    return false;
                }
                
                try
                {
                    center = new int[] { Integer.valueOf(split[1].trim()), Integer.valueOf(split[1].trim()) };
                }
                catch(Exception e)
                {
                    RecipeErrorReporter.error("Flag @" + type + " has 'center' with invalid X/Z numbers, must be whole numbers!");
                    return false;
                }
            }
        }
        
        MapView map = Bukkit.getMap(getResult().getDurability());
        
        if(newMap)
        {
            if(world == null && map == null)
            {
                RecipeErrorReporter.error("Flag @" + type + " can't create a new map without either an existing map data value on item OR the world argument.");
                return false;
            }
            
            map = Bukkit.createMap(world == null ? map.getWorld() : world);
        }
        else if(map == null)
        {
            if(world == null)
            {
                RecipeErrorReporter.error("Flag @" + type + " can't find the map for item's data value and world is not set so it can't create one either.");
                return false;
            }
            
            map = Bukkit.createMap(world);
        }
        
        if(map == null)
        {
            RecipeErrorReporter.error("Flag @" + type + " couldn't create a new map!");
            return false;
        }
        
        if(center != null)
        {
            map.setCenterX(center[0]);
            map.setCenterZ(center[1]);
        }
        
        if(world != null)
            map.setWorld(world);
        
        if(scale != null)
            map.setScale(scale);
        
        return true;
    }
}