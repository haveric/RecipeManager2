package haveric.recipeManager;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Settings {

    private RecipeManager plugin;
    private File defaultConfig;
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private File defaultFolder;

    private CommentedConfigurationNode config;

    public Settings(RecipeManager recipeManager, File defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        plugin = recipeManager;
        this.defaultConfig = defaultConfig;
        this.configManager = configManager;
        defaultFolder = defaultConfig.getParentFile();

        init();
    }

    public void init() {
        if (!defaultFolder.exists()) {
            defaultFolder.mkdirs();
        }

        try {
            if (!defaultConfig.exists()) {
                defaultConfig.createNewFile();
                config = configManager.load();


                configManager.save(config);
            }
            config = configManager.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getDefaultFolder() {
        return defaultFolder;
    }

    public String getDefaultFolderPath() {
        return defaultFolder.getPath();
    }

}
