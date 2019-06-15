package haveric.recipeManager.recipes.smelt.data;

import haveric.recipeManager.messages.MessageSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores data about a furnace
 */
@SerializableAs("RM_FurnaceData")
public class FurnaceData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(FurnaceData.class, "RM_FurnaceData");
    }

    private UUID fuelerUUID = null;
    private ItemStack smelting = null;
    private ItemStack fuel = null;

    private static final String ID_FUELER_UUID = "fuelerUUID";
    private static final String ID_SMELTING = "smelting";
    private static final String ID_FUEL = "fuel";

    public static void init() {
    }

    public FurnaceData() {
    }

    /**
     * Deserialization constructor
     *
     * @param map
     */
    @SuppressWarnings("unchecked")
    public FurnaceData(Map<String, Object> map) {
        try {
            Object obj;

            obj = map.get(ID_FUELER_UUID);
            if (obj instanceof String) {
                fuelerUUID = UUID.fromString((String) obj);
            }

            obj = map.get(ID_SMELTING);
            if (obj instanceof Map) {
                smelting = ItemStack.deserialize((Map<String, Object>) obj);
            }

            obj = map.get(ID_FUEL);
            if (obj instanceof Map) {
                fuel = ItemStack.deserialize((Map<String, Object>) obj);
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(4);

        if (fuelerUUID != null) {
            map.put(ID_FUELER_UUID, fuelerUUID.toString());
        }

        if (smelting != null) {
            map.put(ID_SMELTING, smelting.serialize());
        }

        if (fuel != null) {
            map.put(ID_FUEL, fuel.serialize());
        }

        return map;
    }

    public static FurnaceData deserialize(Map<String, Object> map) {
        return new FurnaceData(map);
    }

    public static FurnaceData valueOf(Map<String, Object> map) {
        return new FurnaceData(map);
    }

    public UUID getFuelerUUID() {
        return fuelerUUID;
    }

    public void setFuelerUUID(UUID newFuelerUUID) {
        fuelerUUID = newFuelerUUID;
    }

    public ItemStack getSmelting() {
        return smelting;
    }

    public void setSmelting(ItemStack newSmelting) {
        if (newSmelting == null) {
            smelting = null;
        } else {
            smelting = newSmelting.clone();
        }
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack newFuel) {
        if (newFuel == null) {
            fuel = null;
        } else {
            fuel = newFuel.clone();
        }
    }
}
