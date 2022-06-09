package haveric.recipeManager.flag.flags.any.flagCooldown;

import haveric.recipeManager.messages.MessageSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("RM_CooldownData")
public class CooldownData implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(CooldownData.class, "RM_CooldownData");
    }

    Map<UUID, Long> cooldowns = new HashMap<>();

    public static void init() { }

    public CooldownData() {

    }

    public CooldownData(Map<String, Object> map) {
        long currentTimestampInSeconds = (System.currentTimeMillis() / 1000);
        cooldowns.clear();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String uuidString = entry.getKey();

            if (!uuidString.equals("==")) { // Skip data class identifier line
                UUID uuid = UUID.fromString(uuidString);
                Object obj = entry.getValue();

                Long cooldownTimestampInSeconds = null;
                if (obj instanceof Integer) {
                    int objAsInt = (Integer) obj;
                    cooldownTimestampInSeconds = (long) objAsInt;
                } else if (obj instanceof Long) {
                    cooldownTimestampInSeconds = (Long) obj;
                }

                if (cooldownTimestampInSeconds != null) {
                    try {
                        if (currentTimestampInSeconds < cooldownTimestampInSeconds) {
                            cooldowns.put(uuid, cooldownTimestampInSeconds);
                        }
                    } catch (NumberFormatException e) {
                        MessageSender.getInstance().error(null, e, null);
                    }
                }
            }
        }
    }

    public Map<String, Object> serialize() {
        long currentTimestampInSeconds = (System.currentTimeMillis() / 1000);
        Map<String, Object> map = new HashMap<>(1);

        if (!cooldowns.isEmpty()) {
            for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
                long cooldownTimestampInSeconds = entry.getValue();

                if (currentTimestampInSeconds < cooldownTimestampInSeconds) {
                    map.put(entry.getKey().toString(), cooldownTimestampInSeconds);
                }
            }
        }

        return map;
    }

    public static CooldownData deserialize(Map<String, Object> map) {
        return new CooldownData(map);
    }

    public static CooldownData valueOf(Map<String, Object> map) {
        return deserialize(map);
    }

    public boolean hasCooldowns() {
        return cooldowns.size() > 0;
    }

    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
    }

    public void setCooldowns(Map<UUID, Long> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public void setCooldown(UUID uuid, long cooldownTimestamp) {
        cooldowns.put(uuid, cooldownTimestamp);
        Cooldowns.update();
    }
}
