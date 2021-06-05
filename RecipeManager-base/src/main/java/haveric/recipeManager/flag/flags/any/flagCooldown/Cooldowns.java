package haveric.recipeManager.flag.flags.any.flagCooldown;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.messages.MessageSender;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cooldowns {
    private static final String SAVE_EXTENSION = ".coolingdata";

    private static Map<String, CooldownData> cooldowns = new LinkedHashMap<>(128);

    protected static void init() { }

    public static void clean() {
        cooldowns.clear();
    }

    public static Map<String, CooldownData> getCooldowns() {
        return cooldowns;
    }

    public static CooldownData get(String cooldownFlagId) {
        Validate.notNull(cooldownFlagId, "cooldownFlagId argument must not be null!");

        CooldownData data = cooldowns.get(cooldownFlagId);

        if (data == null) {
            data = new CooldownData();
            cooldowns.put(cooldownFlagId, data);
        }

        return data;
    }

    public static void remove(String cooldownFlagId) {
        Validate.notNull(cooldownFlagId, "cooldownFlagId argument must not be null!");

        cooldowns.remove(cooldownFlagId);
    }

    public static void load() {
        long start = System.currentTimeMillis();

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists()) {
            return;
        }

        FileConfiguration yml;

        File[] listOfFiles = dir.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (!file.isFile() || !file.getName().endsWith(SAVE_EXTENSION)) {
                    continue;
                }

                yml = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection cooldownsSection = yml.getConfigurationSection("cooldowns");
                if (cooldownsSection != null) {
                    for (Map.Entry<String, Object> entry : cooldownsSection.getValues(false).entrySet()) {
                        cooldowns.put(entry.getKey(), (CooldownData) entry.getValue());
                    }
                }
            }
        }

        MessageSender.getInstance().log("Loaded " + cooldowns.size() + " cooldowns in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }

    public static void save() {
        long start = System.currentTimeMillis();

        MessageSender.getInstance().log("Saving " + cooldowns.size() + " cooldowns...");

        File dir = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "save" + File.separator);

        if (!dir.exists() && !dir.mkdirs()) {
            MessageSender.getInstance().info("<red>Couldn't create directories: " + dir.getPath());
            return;
        }

        FileConfiguration yml = new YamlConfiguration();

        for (Map.Entry<String, CooldownData> f : cooldowns.entrySet()) {
            CooldownData cooldownData = f.getValue();
            if (cooldownData.hasCooldowns()) {
                yml.set("cooldowns." + f.getKey(), f.getValue());
            }
        }

        File file = new File(dir.getPath() + File.separator + "cooldowns" + SAVE_EXTENSION);

        try {
            yml.save(file);
        } catch (IOException e) {
            MessageSender.getInstance().error(null, e, "Failed to create '" + file.getPath() + "' file!");
        }

        MessageSender.getInstance().log("Saved cooldowns in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
    }
}
