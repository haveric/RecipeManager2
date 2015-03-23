package haveric.recipeManager.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("RM_BrewingStandData")
public class BrewingStandData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(BrewingStandData.class, "RM_BrewingStandData");
    }

    private String fueler;

    private static final String ID_FUELER = "fueler";

    public static void init() {

    }

    public BrewingStandData() {

    }

    public BrewingStandData(Map<String, Object> map) {
        Object obj;

        obj = map.get(ID_FUELER);

        if (obj instanceof String) {
            fueler = (String) obj;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>(1);

        if (fueler != null) {
            map.put(ID_FUELER, fueler);
        }

        return map;
    }

    public static BrewingStandData deserialize(Map<String, Object> map) {
        return new BrewingStandData(map);
    }

    public static BrewingStandData valueOf(Map<String, Object> map) {
        return deserialize(map);
    }

    public String getFueler() {
        return fueler;
    }

    public void setFueler(String newFueler) {
        fueler = newFueler;
    }
}
