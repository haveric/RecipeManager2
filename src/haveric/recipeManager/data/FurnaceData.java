package haveric.recipeManager.data;

import haveric.recipeManager.Messages;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


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
    private float burnTicks = 0;
    private Float cookTime = null;
    private float cookProgress = 0;

    // Non-saveable fields
    private boolean frozen = false;

    // Constants
    private static final String ID_SMELTER = "smelter";
    private static final String ID_FUELER = "fueler";
    private static final String ID_SMELTING = "smelting";
    private static final String ID_FUEL = "fuel";
    private static final String ID_BURNTICKS = "burnTicks";
    private static final String ID_COOKTIME = "cookTime";
    private static final String ID_COOKPROGRESS = "cookProgress";

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

            obj = map.get(ID_BURNTICKS);

            if (obj instanceof Double) {
                burnTicks = ((Double) obj).floatValue();
            }

            obj = map.get(ID_COOKTIME);

            if (obj instanceof Double) {
                cookTime = ((Double) obj).floatValue();
            }

            obj = map.get(ID_COOKPROGRESS);

            if (obj instanceof Double) {
                cookProgress = ((Double) obj).floatValue();
            }
        } catch (Throwable e) {
            Messages.error(null, e, null);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>(7);

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

        if (burnTicks > 0) {
            map.put(ID_BURNTICKS, burnTicks);
        }

        if (cookTime != null) {
            map.put(ID_COOKTIME, cookTime);
        }

        if (cookProgress > 0) {
            map.put(ID_COOKPROGRESS, cookProgress);
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

    public void setFueler(Player newFueler) {
        if (newFueler == null) {
            fueler = null;
        } else {
            fueler = newFueler.getName();
        }
    }

    public String getSmelter() {
        return smelter;
    }

    public void setSmelter(String newSmelter) {
        smelter = newSmelter;
    }

    public void setSmelter(Player newSmelter) {
        if (newSmelter == null) {
            smelter = null;
        } else {
            smelter = newSmelter.getName();
        }
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

    public boolean isBurning() {
        return burnTicks > 0;
    }

    public float getBurnTicks() {
        return burnTicks;
    }

    public void setBurnTicks(float ticks) {
        burnTicks = ticks;
    }

    /**
     * @return furnace's total cooking time or null if not assigned.
     */
    public Float getCookTime() {
        return cookTime;
    }

    /**
     * Set furnace's total cooking time or null to unset.
     *
     * @param cookTime
     */
    public void setCookTime(Float newCookTime) {
        cookTime = newCookTime;
    }

    public float getCookProgress() {
        return cookProgress;
    }

    public void setCookProgress(float ticks) {
        cookProgress = ticks;
    }

    public short getCookProgressForFurnace() {
        return (short) Math.min(Math.max(Math.round(cookProgress), 1), 200);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean newFrozen) {
        frozen = newFrozen;
    }
}
