package haveric.recipeManager.recipes.compost.data;

import com.google.common.base.Preconditions;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.data.BlockID;
import haveric.recipeManager.messages.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Composters {
    private static final String SAVE_EXTENSION = ".composterdata";

    private static BukkitTask updateTask;
    private static boolean needsUpdate = false;

    private static Map<BlockID, ComposterData> composters = new LinkedHashMap<>(128);

    protected static void init() {

    }

    public static void clean() {
        composters.clear();
    }

    public static void update() {
        needsUpdate = true;
    }

    public static Map<BlockID, ComposterData> getComposters() {
        return composters;
    }

    public static boolean exists(BlockID id) {
        Preconditions.checkNotNull(id, "id argument must not be null");

        return composters.containsKey(id);
    }

    public static void set(Block composter) {
        set(null, composter);
    }

    public static void set(BlockID id, Block composter) {
        Preconditions.checkNotNull(composter, "composter argument must not be null!");
        Preconditions.checkArgument(composter.getType() == Material.COMPOSTER, "composter argument must be a composter!");

        if (id == null) {
            id = BlockID.fromLocation(composter.getLocation());
        }

        composters.computeIfAbsent(id, k -> new ComposterData());
    }

    public static void add(BlockID id) {
        Preconditions.checkNotNull(id, "id argument must not be null!");

        composters.put(id, new ComposterData());
    }

    public static void add(Location location) {
        add(BlockID.fromLocation(location));
    }

    public static ComposterData get(BlockID id) {
        Preconditions.checkNotNull(id, "id argument must not be null!");

        return composters.computeIfAbsent(id, k -> new ComposterData());
    }

    public static ComposterData get(Location location) {
        Preconditions.checkNotNull(location, "location argument must not be null!");

        return get(BlockID.fromLocation(location));
    }

    public static void remove(BlockID id) {
        Preconditions.checkNotNull(id, "id argument must not be null!");

        composters.remove(id);
    }

    public static void remove(Location location) {
        Preconditions.checkNotNull(location, "location argument must not be null!");

        remove(BlockID.fromLocation(location));
    }

    public static void load() {
        long start = System.currentTimeMillis();

        clean();

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

                for (Map.Entry<String, Object> e : yml.getConfigurationSection("coords").getValues(false).entrySet()) {
                    composters.put(BlockID.fromString(id, e.getKey()), (ComposterData) e.getValue());
                }
            }
        }

        if (updateTask != null) {
            updateTask.cancel();
        }

        int saveFrequency = RecipeManager.getSettings().getSaveFrequencyForComposters();
        updateTask = new BukkitRunnable() {
            public void run() {
                if (needsUpdate) {
                    save();
                }
            }
        }.runTaskTimerAsynchronously(RecipeManager.getPlugin(), 0, saveFrequency);

        MessageSender.getInstance().log("Loaded " + composters.size() + " composters in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }

    public static void save() {
        long start = System.currentTimeMillis();

        MessageSender.getInstance().log("Saving " + composters.size() + " composters...");

        Map<UUID, Map<String, ComposterData>> mapWorld = new HashMap<>();
        Map<String, ComposterData> mapCoords;
        BlockID id;

        for (Map.Entry<BlockID, ComposterData> e : composters.entrySet()) {
            ComposterData data = e.getValue();
            // Ignore if no data is set
            if (data.getPlayerUUID() != null) {
                id = e.getKey();
                mapCoords = mapWorld.computeIfAbsent(id.getWorldID(), k -> new HashMap<>());

                mapCoords.put(id.getCoordsString(), data);
            }
        }

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists() && !dir.mkdirs()) {
            MessageSender.getInstance().info("<red>Couldn't create directories: " + dir.getPath());
            return;
        }

        for (Map.Entry<UUID, Map<String, ComposterData>> w : mapWorld.entrySet()) {
            World world = Bukkit.getWorld(w.getKey());

            FileConfiguration yml = new YamlConfiguration();
            yml.set("id", w.getKey().toString());

            for (Map.Entry<String, ComposterData> f : w.getValue().entrySet()) {
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

        // Reset needsUpdate
        needsUpdate = false;

        MessageSender.getInstance().log("Saved composters in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }
}
