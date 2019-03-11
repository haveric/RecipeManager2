package haveric.recipeManager.data;

import haveric.recipeManager.messages.MessageSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

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
}
