package haveric.recipeManager.recipes.brew.data;

import haveric.recipeManager.messages.MessageSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("RM_BrewingStandData")
public class BrewingStandData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(BrewingStandData.class, "RM_BrewingStandData");
    }

    private UUID fuelerUUID;

    private static final String ID_FUELER_UUID = "fuelerUUID";

    private float currentBrewTime = -1;

    private BukkitTask updateTask = null;

    private BukkitTask finishBrewingTask = null;

    public static void init() {

    }

    public BrewingStandData() {

    }

    public BrewingStandData(Map<String, Object> map) {
        try {
            Object obj;

            obj = map.get(ID_FUELER_UUID);
            if (obj instanceof String) {
                fuelerUUID = UUID.fromString((String) obj);
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(1);

        if (fuelerUUID != null) {
            map.put(ID_FUELER_UUID, fuelerUUID.toString());
        }

        return map;
    }

    public static BrewingStandData deserialize(Map<String, Object> map) {
        return new BrewingStandData(map);
    }

    public static BrewingStandData valueOf(Map<String, Object> map) {
        return deserialize(map);
    }

    public UUID getFuelerUUID() {
        return fuelerUUID;
    }

    public void setFuelerUUID(UUID newFueler) {
        fuelerUUID = newFueler;
    }

    public float getCurrentBrewTime() {
        return currentBrewTime;
    }

    public void setCurrentBrewTime(float newBrewTime) {
        currentBrewTime = newBrewTime;
    }

    public BukkitTask getUpdateTask() {
        return updateTask;
    }

    public void setUpdateTask(BukkitTask newTask) {
        updateTask = newTask;
    }

    public BukkitTask getFinishBrewingTask() {
        return finishBrewingTask;
    }

    public void setFinishBrewingTask(BukkitTask newTask) {
        finishBrewingTask = newTask;
    }

    public void cancelBrewing() {
        currentBrewTime = -1;
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }

        if (finishBrewingTask != null) {
            finishBrewingTask.cancel();
            finishBrewingTask = null;
        }
    }

    public void completeBrewing() {
        currentBrewTime = -1;
        updateTask = null;
        finishBrewingTask = null;
    }

    public boolean isBrewing() {
        return updateTask != null || finishBrewingTask != null;
    }
}
