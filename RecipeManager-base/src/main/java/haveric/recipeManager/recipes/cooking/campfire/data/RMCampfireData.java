package haveric.recipeManager.recipes.cooking.campfire.data;

import haveric.recipeManager.messages.MessageSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("RM_CampfireData")
public class RMCampfireData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(RMCampfireData.class, "RM_CampfireData");
    }

    private UUID item0UUID = null;
    private UUID item1UUID = null;
    private UUID item2UUID = null;
    private UUID item3UUID = null;

    private static final String ID_ITEM0_UUID = "item0UUID";
    private static final String ID_ITEM1_UUID = "item1UUID";
    private static final String ID_ITEM2_UUID = "item2UUID";
    private static final String ID_ITEM3_UUID = "item3UUID";


    public static void init() { }

    public RMCampfireData() { }

    public RMCampfireData(Map<String, Object> map) {
        try {
            Object obj;

            obj = map.get(ID_ITEM0_UUID);
            if (obj instanceof String) {
                item0UUID = UUID.fromString((String) obj);
            }

            obj = map.get(ID_ITEM1_UUID);
            if (obj instanceof String) {
                item1UUID = UUID.fromString((String) obj);
            }

            obj = map.get(ID_ITEM2_UUID);
            if (obj instanceof String) {
                item2UUID = UUID.fromString((String) obj);
            }

            obj = map.get(ID_ITEM3_UUID);
            if (obj instanceof String) {
                item3UUID = UUID.fromString((String) obj);
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(4);


        if (item0UUID != null) {
            map.put(ID_ITEM0_UUID, item0UUID.toString());
        }

        if (item1UUID != null) {
            map.put(ID_ITEM1_UUID, item1UUID.toString());
        }

        if (item2UUID != null) {
            map.put(ID_ITEM2_UUID, item2UUID.toString());
        }

        if (item3UUID != null) {
            map.put(ID_ITEM3_UUID, item3UUID.toString());
        }

        return map;
    }

    public static RMCampfireData deserialize(Map<String, Object> map) {
        return new RMCampfireData(map);
    }

    public static RMCampfireData valueOf(Map<String, Object> map) {
        return new RMCampfireData(map);
    }

    public UUID getItemUUID(int slot) {
        UUID uuid = null;
        if (slot == 0) {
            uuid = item0UUID;
        } else if (slot == 1) {
            uuid = item1UUID;
        } else if (slot == 2) {
            uuid = item2UUID;
        } else if (slot == 3) {
            uuid = item3UUID;
        }

        return uuid;
    }

    public void setItemId(int slot, UUID itemUUID) {
        if (slot == 0) {
            item0UUID = itemUUID;
        } else if (slot == 1) {
            item1UUID = itemUUID;
        } else if (slot == 2) {
            item2UUID = itemUUID;
        } else if (slot == 3) {
            item3UUID = itemUUID;
        }
    }

    public boolean allSlotsEmpty() {
        return item0UUID == null && item1UUID == null && item2UUID == null && item3UUID == null;
    }


    public UUID getItem0UUID() {
        return item0UUID;
    }

    public void setItem0UUID(UUID item4UUID) {
        this.item0UUID = item4UUID;
    }

    public UUID getItem1UUID() {
        return item1UUID;
    }

    public void setItem1UUID(UUID item1UUID) {
        this.item1UUID = item1UUID;
    }

    public UUID getItem2UUID() {
        return item2UUID;
    }

    public void setItem2UUID(UUID item2UUID) {
        this.item2UUID = item2UUID;
    }

    public UUID getItem3UUID() {
        return item3UUID;
    }

    public void setItem3UUID(UUID item3UUID) {
        this.item3UUID = item3UUID;
    }

}
