package haveric.recipeManager;

import haveric.recipeManager.data.BlockID;
import haveric.recipeManager.data.FurnaceData;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.FurnaceInventory;


public class Furnaces {
    private static final String SAVE_EXTENSION = ".furnacedata";

    private static Map<BlockID, FurnaceData> furnaces = new LinkedHashMap<BlockID, FurnaceData>(128);

    protected static void init() {
    }

    protected static void clean() {
        furnaces.clear();
    }

    public static void cleanChunk(Chunk chunk, Set<BlockID> added) {
        Iterator<Entry<BlockID, FurnaceData>> it = furnaces.entrySet().iterator();
        int x = chunk.getX();
        int z = chunk.getZ();

        while (it.hasNext()) {
            Entry<BlockID, FurnaceData> e = it.next();
            BlockID id = e.getKey();

            if (Math.floor(id.getX() / 16.0) == x && Math.floor(id.getZ() / 16.0) == z && !added.contains(id)) {
                it.remove();
            }
        }
    }

    public static Map<BlockID, FurnaceData> getFurnaces() {
        return furnaces;
    }

    public static boolean exists(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        return furnaces.containsKey(id);
    }

    /**
     * Add/update existing furnace.
     * 
     * @param furnace
     */
    public static void set(Furnace furnace) {
        set(null, furnace);
    }

    protected static void set(BlockID id, Furnace furnace) {
        Validate.notNull(furnace, "furnace argument must not be null!");

        if (id == null) {
            id = BlockID.fromLocation(furnace.getLocation());
        }

        FurnaceInventory inv = furnace.getInventory();

        if (inv == null) {
            return; // invalid furnace, no inventory
        }

        FurnaceData data = furnaces.get(id);

        if (data == null) {
            data = new FurnaceData();
            furnaces.put(id, data);
        }

        if (data.getFuel() == null) {
            data.setFuel(inv.getFuel());
        }

        if (data.getSmelting() == null) {
            data.setSmelting(inv.getSmelting());
        }

        data.setBurnTicks(furnace.getBurnTime());
        data.setCookProgress(furnace.getCookTime());
    }

    /**
     * Add new furnace
     * 
     * @param id
     */
    public static void add(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        furnaces.put(id, new FurnaceData());
    }

    /**
     * Add new furnace
     * 
     * @param id
     */
    public static void add(Location location) {
        add(BlockID.fromLocation(location));
    }

    public static FurnaceData get(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        FurnaceData data = furnaces.get(id);

        if (data == null) {
            data = new FurnaceData();
            furnaces.put(id, data);
        }

        return data;
    }

    public static FurnaceData get(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        return get(BlockID.fromLocation(location));
    }

    public static void remove(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        furnaces.remove(id);
    }

    public static void remove(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        remove(BlockID.fromLocation(location));
    }

    public static void load() {
        long start = System.currentTimeMillis();

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists()) {
            return;
        }

        FileConfiguration yml;
        UUID id;

        for (File file : dir.listFiles()) {
            if (!file.isFile() || !file.getName().endsWith(SAVE_EXTENSION)) {
                continue;
            }

            yml = YamlConfiguration.loadConfiguration(file);

            id = UUID.fromString(yml.getString("id"));

            for (Entry<String, Object> e : yml.getConfigurationSection("coords").getValues(false).entrySet()) {
                furnaces.put(BlockID.fromString(id, e.getKey()), (FurnaceData) e.getValue());
            }
        }

        Messages.log("Loaded " + furnaces.size() + " furnaces in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }

    public static void save() {
        long start = System.currentTimeMillis();

        Messages.log("Saving " + furnaces.size() + " furnaces...");

        Map<UUID, Map<String, FurnaceData>> mapWorld = new HashMap<UUID, Map<String, FurnaceData>>();
        Map<String, FurnaceData> mapCoords;
        BlockID id;

        for (Entry<BlockID, FurnaceData> e : furnaces.entrySet()) {
            id = e.getKey();
            mapCoords = mapWorld.get(id.getWorldID());

            if (mapCoords == null) {
                mapCoords = new HashMap<String, FurnaceData>();
                mapWorld.put(id.getWorldID(), mapCoords);
            }

            mapCoords.put(id.getCoordsString(), e.getValue());
        }

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists() && !dir.mkdirs()) {
            Messages.info("<red>Couldn't create directories: " + dir.getPath());
            return;
        }

        for (Entry<UUID, Map<String, FurnaceData>> w : mapWorld.entrySet()) {
            World world = Bukkit.getWorld(w.getKey());

            FileConfiguration yml = new YamlConfiguration();
            yml.set("id", w.getKey().toString());

            for (Entry<String, FurnaceData> f : w.getValue().entrySet()) {
                yml.set("coords." + f.getKey(), f.getValue());
            }

            File file = new File(dir.getPath() + File.separator + (world == null ? w.getKey().toString() : world.getName()) + SAVE_EXTENSION);

            try {
                yml.save(file);
            } catch (Throwable e) {
                Messages.error(null, e, "Failed to create '" + file.getPath() + "' file!");
                continue;
            }
        }

        Messages.log("Saved furnaces in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }
}
