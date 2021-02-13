package haveric.recipeManager.flag;

import haveric.recipeManager.settings.SettingsYaml;

import java.io.File;

public class FlagBaseYamlTest extends FlagBaseTest {

    @Override
    protected void init() {
        settings = new SettingsYaml(false);
    }

    @Override
    protected void loadSettings() {
        ((SettingsYaml) settings).loadFileConfig(new File(baseDataPath), "config-no-multithreading.yml");
    }
}
