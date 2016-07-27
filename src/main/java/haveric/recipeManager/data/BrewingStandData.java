package haveric.recipeManager.data;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;
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

    @Deprecated
    private String fueler;

    private UUID fuelerUUID;
    @Deprecated
    private static final String ID_FUELER = "fueler";

    private static final String ID_FUELER_UUID = "fuelerUUID";

    public static void init() {

    }

    public BrewingStandData() {

    }

    public BrewingStandData(Map<String, Object> map) {
        try {
            Object obj;

            obj = map.get(ID_FUELER);
            if (obj instanceof String) {
                fuelerUUID = UUIDFetcher.getUUIDOf((String) obj);
                setFueler(null);
            }

            obj = map.get(ID_FUELER_UUID);
            if (obj instanceof UUID) {
                fuelerUUID = (UUID) obj;
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>(1);

        if (fuelerUUID != null) {
            map.put(ID_FUELER, fuelerUUID);
        }

        return map;
    }

    public static BrewingStandData deserialize(Map<String, Object> map) {
        return new BrewingStandData(map);
    }

    public static BrewingStandData valueOf(Map<String, Object> map) {
        return deserialize(map);
    }

    @Deprecated
    public String getFueler() { return fueler; }
    @Deprecated
    public void setFueler(String newFueler) { fueler = newFueler; }

    public UUID getFuelerUUID() {
        return fuelerUUID;
    }

    public void setFuelerUUID(UUID newFueler) {
        fuelerUUID = newFueler;
    }
}
