package haveric.recipeManager.data;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.messages.MessageSender;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BrewingStand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.BrewerInventory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class BrewingStands {
    private static final String SAVE_EXTENSION = ".brewingdata";

    private static Map<BlockID, BrewingStandData> brewingStands = new LinkedHashMap<BlockID, BrewingStandData>(128);

    protected static void init() { }

    public static void clean() {
        brewingStands.clear();
    }

    public static void cleanChunk(Chunk chunk, Set<BlockID> added) {
        Iterator<Entry<BlockID, BrewingStandData>> iter = brewingStands.entrySet().iterator();
        int x = chunk.getX();
        int z = chunk.getZ();

        while (iter.hasNext()) {
            Entry<BlockID, BrewingStandData> entry = iter.next();
            BlockID id = entry.getKey();

            if (Math.round(Math.floor(id.getX() / 16.0)) == x && Math.round(Math.floor(id.getZ() / 16.0)) == z && !added.contains(id)) {
                iter.remove();
            }
        }
    }

    public static Map<BlockID, BrewingStandData> getBrewingStands() {
        return brewingStands;
    }

    public static boolean exists(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        return brewingStands.containsKey(id);
    }

    public static void set(BrewingStand brewingStand) {
        set(null, brewingStand);
    }

    public static void set(BlockID id, BrewingStand brewingStand) {
        Validate.notNull(brewingStand, "brewing stand argument must not be null!");

        if (id == null) {
            id = BlockID.fromLocation(brewingStand.getLocation());
        }

        BrewerInventory inventory = brewingStand.getInventory();

        if (inventory == null) {
            return; // invalid brewing stand, no inventory
        }

        BrewingStandData data = brewingStands.get(id);

        if (data == null) {
            data = new BrewingStandData();
            brewingStands.put(id, data);
        }
    }

    public static void add(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        brewingStands.put(id, new BrewingStandData());
    }

    public static void add(Location location) {
        add(BlockID.fromLocation(location));
    }

    public static BrewingStandData get(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        BrewingStandData data = brewingStands.get(id);

        if (data == null) {
            data = new BrewingStandData();
            brewingStands.put(id, data);
        }

        return data;
    }

    public static BrewingStandData get(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        return get(BlockID.fromLocation(location));
    }

    public static void remove(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        brewingStands.remove(id);
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

        File[] listOfFiles = dir.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (!file.isFile() || !file.getName().endsWith(SAVE_EXTENSION)) {
                    continue;
                }

                yml = YamlConfiguration.loadConfiguration(file);

                id = UUID.fromString(yml.getString("id"));

                for (Entry<String, Object> entry : yml.getConfigurationSection("coords").getValues(false).entrySet()) {
                    brewingStands.put(BlockID.fromString(id, entry.getKey()), (BrewingStandData) entry.getValue());
                }
            }
        }

        MessageSender.getInstance().log("Loaded " + brewingStands.size() + " brewing stands in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }

    public static void save() {
        long start = System.currentTimeMillis();

        MessageSender.getInstance().log("Saving " + brewingStands.size() + " brewing stands...");

        Map<UUID, Map<String, BrewingStandData>> mapWorld = new HashMap<>();
        Map<String, BrewingStandData> mapCoords;
        BlockID id;

        for (Entry<BlockID, BrewingStandData> entry : brewingStands.entrySet()) {
            id = entry.getKey();
            mapCoords = mapWorld.get(id.getWorldID());

            if (mapCoords == null) {
                mapCoords = new HashMap<>();
                mapWorld.put(id.getWorldID(), mapCoords);
            }

            mapCoords.put(id.getCoordsString(), entry.getValue());
        }

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists() && !dir.mkdirs()) {
            MessageSender.getInstance().info("<red>Couldn't create directories: " + dir.getPath());
            return;
        }

        for (Entry<UUID, Map<String, BrewingStandData>> w : mapWorld.entrySet()) {
            World world = Bukkit.getWorld(w.getKey());

            FileConfiguration yml = new YamlConfiguration();
            yml.set("id", w.getKey().toString());

            for (Entry<String, BrewingStandData> f : w.getValue().entrySet()) {
                yml.set("coords." + f.getKey(), f.getValue());
            }

            String worldString;
            if (world == null) {
                worldString = w.getKey().toString();
            } else {
                worldString = world.getName();
            }

            File file = new File(dir.getPath() + File.separator + worldString + SAVE_EXTENSION);


            try {
                yml.save(file);
            } catch (IOException e) {
                MessageSender.getInstance().error(null, e, "Failed to create '" + file.getPath() + "' file!");
            }
        }

        MessageSender.getInstance().log("Saved brewing standings in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }
}
