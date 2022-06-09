package haveric.recipeManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLastChangedVersions {

    String originalResourcesPath;

    @BeforeEach
    public void beforeAll() {
        File baseSrcDir = new File("src");
        String baseSrcPath = baseSrcDir.getAbsolutePath().replace(".idea\\modules\\", "") + "/";

        originalResourcesPath = baseSrcPath + "main/resources/";
    }

    @Test
    public void testConfigVersion() {
        File pluginFile = new File(originalResourcesPath + "config.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(pluginFile);
        String version = yamlConfig.getString("lastchanged");

        assertEquals(version, Files.LASTCHANGED_CONFIG);
    }

    @Test
    public void testMessagesVersion() {
        File pluginFile = new File(originalResourcesPath + "messages.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(pluginFile);
        String version = yamlConfig.getString("lastchanged");

        assertEquals(version, Files.LASTCHANGED_MESSAGES);
    }

    @Test
    public void testChoiceAliasesVersion() {
        File pluginFile = new File(originalResourcesPath + "choice aliases.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(pluginFile);
        String version = yamlConfig.getString("lastchanged");

        assertEquals(version, Files.LASTCHANGED_CHOICE_ALIASES);
    }

    @Test
    public void testItemDatasVersion() {
        File pluginFile = new File(originalResourcesPath + "item datas.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(pluginFile);
        String version = yamlConfig.getString("lastchanged");

        assertEquals(version, Files.LASTCHANGED_ITEM_DATAS);
    }

    @Test
    public void testItemAliasesVersion() {
        File pluginFile = new File(originalResourcesPath + "item aliases.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(pluginFile);
        String version = yamlConfig.getString("lastchanged");

        assertEquals(version, Files.LASTCHANGED_ITEM_ALIASES);
    }
}
