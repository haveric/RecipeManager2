package haveric.recipeManager.recipes.campfire.data;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.data.BlockID;
import haveric.recipeManager.messages.MessageSender;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Campfire;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class RMCampfires {
    private static final String SAVE_EXTENSION = ".rmcampfiredata";

    private static Map<BlockID, RMCampfireData> campfires = new LinkedHashMap<>(128);

    protected static void init() { }

    public static void clean() {
        campfires.clear();
    }

    public static Map<BlockID, RMCampfireData> getRMCampfires() {
        return campfires;
    }

    public static boolean exists(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        return campfires.containsKey(id);
    }

    public static void set(Campfire campfire) {
        set(null, campfire);
    }

    public static void set(BlockID id, Campfire campfire) {
        Validate.notNull(campfire, "campfire argument must not be null!");

        if (id == null) {
            id = BlockID.fromLocation(campfire.getLocation());
        }

        RMCampfireData data = campfires.get(id);

        if (data == null) {
            data = new RMCampfireData();
            campfires.put(id, data);
        }
    }

    public static void add(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        campfires.put(id, new RMCampfireData());
    }

    public static void add(Location location) {
        add(BlockID.fromLocation(location));
    }

    public static RMCampfireData get(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        RMCampfireData data = campfires.get(id);

        if (data == null) {
            data = new RMCampfireData();
            campfires.put(id, data);
        }

        return data;
    }

    public static RMCampfireData get(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        return get(BlockID.fromLocation(location));
    }

    public static void remove(BlockID id) {
        Validate.notNull(id, "id argument must not be null!");

        campfires.remove(id);
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

                for (Entry<String, Object> e : yml.getConfigurationSection("coords").getValues(false).entrySet()) {
                    campfires.put(BlockID.fromString(id, e.getKey()), (RMCampfireData) e.getValue());
                }
            }
        }

        MessageSender.getInstance().log("Loaded " + campfires.size() + " campfires in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }

    public static void save() {
        long start = System.currentTimeMillis();

        MessageSender.getInstance().log("Saving " + campfires.size() + " campfires...");

        Map<UUID, Map<String, RMCampfireData>> mapWorld = new HashMap<>();
        Map<String, RMCampfireData> mapCoords;
        BlockID id;

        for (Entry<BlockID, RMCampfireData> e : campfires.entrySet()) {
            id = e.getKey();
            mapCoords = mapWorld.computeIfAbsent(id.getWorldID(), k -> new HashMap<>());

            mapCoords.put(id.getCoordsString(), e.getValue());
        }

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists() && !dir.mkdirs()) {
            MessageSender.getInstance().info("<red>Couldn't create directories: " + dir.getPath());
            return;
        }

        for (Entry<UUID, Map<String, RMCampfireData>> w : mapWorld.entrySet()) {
            World world = Bukkit.getWorld(w.getKey());

            FileConfiguration yml = new YamlConfiguration();
            yml.set("id", w.getKey().toString());

            for (Entry<String, RMCampfireData> f : w.getValue().entrySet()) {
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
            } catch (Throwable e) {
                MessageSender.getInstance().error(null, e, "Failed to create '" + file.getPath() + "' file!");
            }
        }

        MessageSender.getInstance().log("Saved campfires in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }
}
