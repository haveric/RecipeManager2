package haveric.recipeManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestReleaseVersion {

    private YamlConfiguration yamlConfig;

    @BeforeEach
    public void beforeEach() {
        File baseSrcDir = new File("src");
        String baseSrcPath = baseSrcDir.getAbsolutePath().replace(".idea\\modules\\", "") + "/";

        String originalResourcesPath = baseSrcPath + "main/resources/";

        File pluginFile = new File(originalResourcesPath + "plugin.yml");
        yamlConfig = YamlConfiguration.loadConfiguration(pluginFile);
    }

    @Test
    public void testDevVersion() {
        String version = yamlConfig.getString("version");

        assertNotNull(version);
        assertFalse(version.contains("dev"));
        assertFalse(version.contains("alpha"));
        assertFalse(version.contains("beta"));
        assertFalse(version.contains("-"));
    }
}
