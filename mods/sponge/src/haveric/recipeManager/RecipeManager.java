package haveric.recipeManager;

import haveric.recipeManager.commands.Commands;

import java.io.File;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.event.Subscribe;

import com.google.inject.Inject;



@Plugin(id = "RecipeManager", name = "Recipe Manager", version = "3.0")
public class RecipeManager {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private File defaultFolder;
    @Inject
    private Logger log;

    private Game game;
    private Settings settings;

    private Commands commands;

    @Subscribe
    public void preStartup(ServerAboutToStartEvent event) {
        game = event.getGame();
    }

    @Subscribe
    public void onStartup(ServerStartingEvent event) {
        commands = new Commands(this);

        settings = new Settings(this, defaultConfig, configManager);
    }

    @Subscribe
    public void onShutdown(ServerStoppingEvent event) {

    }

    public Logger getLog() {
        return log;
    }

    public Game getGame() {
        return game;
    }
}
