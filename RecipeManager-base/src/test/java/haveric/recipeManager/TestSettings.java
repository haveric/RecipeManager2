package haveric.recipeManager;

import haveric.recipeManager.settings.BaseSettings;

public class TestSettings extends BaseSettings {

    public TestSettings(boolean loadDefaultConfig) {
        super(loadDefaultConfig);
    }

    public boolean getMultithreading() {
        return false;
    }
}
