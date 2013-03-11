package ro.thehunters.digi.recipeManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ro.thehunters.digi.recipeManager.data.BlockID;
import ro.thehunters.digi.recipeManager.data.FurnaceData;

public class Furnaces
{
    protected static final Map<BlockID, FurnaceData> furnaces       = new HashMap<BlockID, FurnaceData>();
    
    // constants
    private static final String                      SAVE_EXTENSION = ".furnacedata";
    
    static void init()
    {
    }
    
    static void clear()
    {
        furnaces.clear();
    }
    
    public static Map<BlockID, FurnaceData> getFurnaces()
    {
        return furnaces;
    }
    
    public static boolean exists(BlockID id)
    {
        Validate.notNull(id, "id argument must not be null!");
        
        return furnaces.containsKey(id);
    }
    
    public static void add(BlockID id)
    {
        Validate.notNull(id, "id argument must not be null!");
        
        furnaces.put(id, new FurnaceData());
    }
    
    public static void add(Location location)
    {
        Validate.notNull(location, "location argument must not be null!");
        
        add(BlockID.fromLocation(location));
    }
    
    /*
    protected static void updateBurnTime(BlockID id, int burnTime)
    {
        FurnaceData data = furnaces.get(id);
        boolean exists = data != null;
        
        if(!exists)
            data = new FurnaceData();
        
        data.setBurnTime(burnTime);
        
        if(!exists)
            furnaces.put(id, data);
    }
    */
    
    public static FurnaceData get(BlockID id)
    {
        Validate.notNull(id, "id argument must not be null!");
        
        FurnaceData data = furnaces.get(id);
        
        if(data == null)
        {
            data = new FurnaceData();
            furnaces.put(id, data);
        }
        
        return data;
    }
    
    public static FurnaceData get(Location location)
    {
        Validate.notNull(location, "location argument must not be null!");
        
        return get(BlockID.fromLocation(location));
    }
    
    public static void remove(BlockID id)
    {
        Validate.notNull(id, "id argument must not be null!");
        
        furnaces.remove(id);
    }
    
    public static void remove(Location location)
    {
        Validate.notNull(location, "location argument must not be null!");
        
        remove(BlockID.fromLocation(location));
    }
    
    public static void load()
    {
        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);
        
        if(!dir.exists())
            return;
        
        FileConfiguration yml;
        UUID id;
        
        for(File file : dir.listFiles())
        {
            if(!file.isFile() || !file.getName().endsWith(SAVE_EXTENSION))
                continue;
            
            yml = YamlConfiguration.loadConfiguration(file);
            
            id = UUID.fromString(yml.getString("id"));
            
            for(Entry<String, Object> e : yml.getConfigurationSection("coords").getValues(false).entrySet())
            {
                furnaces.put(BlockID.fromString(id, e.getKey()), (FurnaceData)e.getValue());
            }
        }
    }
    
    public static void save()
    {
        Map<UUID, Map<String, FurnaceData>> mapWorld = new HashMap<UUID, Map<String, FurnaceData>>();
        Map<String, FurnaceData> mapCoords;
        BlockID id;
        
        for(Entry<BlockID, FurnaceData> e : furnaces.entrySet())
        {
            id = e.getKey();
            mapCoords = mapWorld.get(id.getWorldID());
            
            if(mapCoords == null)
            {
                mapCoords = new HashMap<String, FurnaceData>();
                mapWorld.put(id.getWorldID(), mapCoords);
            }
            
            mapCoords.put(id.getCoordsString(), e.getValue());
        }
        
        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);
        
        if(!dir.mkdirs())
        {
            Messages.info("<red>Couldn't create directories: " + dir.getPath());
            return;
        }
        
        FileConfiguration yml;
        File file;
        World world;
        
        for(Entry<UUID, Map<String, FurnaceData>> w : mapWorld.entrySet())
        {
            world = Bukkit.getWorld(w.getKey());
            
            file = new File(dir.getPath() + File.separator + (world == null ? w.getKey().toString() : world.getName()) + SAVE_EXTENSION);
            
            if(!file.exists())
            {
                try
                {
                    file.createNewFile();
                }
                catch(IOException ioe)
                {
                    Messages.error(null, ioe, "Failed to create " + file.getPath() + " file!");
                    break;
                }
            }
            
            yml = YamlConfiguration.loadConfiguration(file);
            yml.set("id", w.getKey().toString());
            
            for(Entry<String, FurnaceData> f : w.getValue().entrySet())
            {
                yml.set("coords." + f.getKey(), f.getValue());
            }
            
            try
            {
                yml.save(file);
            }
            catch(IOException ioe)
            {
                Messages.error(null, ioe, "Failed to create " + file.getPath() + " file!");
                break;
            }
        }
    }
}