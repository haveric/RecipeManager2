package haveric.recipeManager;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.fail;

public class TestItemAliases {

    private YamlConfiguration itemAliasesConfig;

    @BeforeEach
    public void beforeAll() {
        File baseSrcDir = new File("src");
        String baseSrcPath = baseSrcDir.getAbsolutePath().replace(".idea\\modules\\", "") + "/";

        String originalResourcesPath = baseSrcPath + "main/resources/";

        File pluginFile = new File(originalResourcesPath + "item aliases.yml");
        itemAliasesConfig = YamlConfiguration.loadConfiguration(pluginFile);
    }
    @Test
    public void loadItemAliases() {
        for (String arg : itemAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = Material.matchMaterial(arg);
            if (material == null) {
                System.out.println("Missing material in 'item aliases.yml`: " + arg);
                fail();
            }
        }
    }
}
