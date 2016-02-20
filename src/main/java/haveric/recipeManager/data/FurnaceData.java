package haveric.recipeManager.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import haveric.recipeManager.Messages;

/**
 * Stores data about a furnace
 */
@SerializableAs("RM_FurnaceData")
public class FurnaceData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(FurnaceData.class, "RM_FurnaceData");
    }

    private String smelter = null;
    private String fueler = null;
    private ItemStack smelting = null;
    private ItemStack fuel = null;

    // Constants
    private static final String ID_SMELTER = "smelter";
    private static final String ID_FUELER = "fueler";
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

            obj = map.get(ID_SMELTER);

            if (obj instanceof String) {
                smelter = (String) obj;
            }

            obj = map.get(ID_FUELER);

            if (obj instanceof String) {
                fueler = (String) obj;
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
            Messages.error(null, e, null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>(4);

        if (smelter != null) {
            map.put(ID_SMELTER, smelter);
        }

        if (fueler != null) {
            map.put(ID_FUELER, fueler);
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

    public String getFueler() {
        return fueler;
    }

    public void setFueler(String newFueler) {
        fueler = newFueler;
    }

    public String getSmelter() {
        return smelter;
    }

    public void setSmelter(String newSmelter) {
        smelter = newSmelter;
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
/*
    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean newFrozen) {
        frozen = newFrozen;
    }
*/
}
