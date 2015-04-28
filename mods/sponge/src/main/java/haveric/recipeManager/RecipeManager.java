package haveric.recipeManager;

import haveric.recipeManager.commands.Commands;
import haveric.recipeManager.events.RMPlayerJoinQuitEvent;

import java.io.File;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.service.event.EventManager;

import com.google.common.base.Optional;
import com.google.inject.Inject;



@Plugin(id = "RecipeManager", name = "Recipe Manager", version = "3.0")
public class RecipeManager {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;


    @Inject
    private Logger log;

    @Inject
    private Game game;

    private Settings settings;
    private Files files;

    private PluginContainer pluginManager;

    private Commands commands;

    @Subscribe
    public void preStartup(ServerAboutToStartEvent event) {
        Optional<PluginContainer> optionalPluginContainer = game.getPluginManager().fromInstance(this);
        if (optionalPluginContainer.isPresent()) {
            pluginManager = optionalPluginContainer.get();
        }
    }

    @Subscribe
    public void onStartup(ServerStartingEvent event) {
        EventManager em = game.getEventManager();
        commands = new Commands(this);

        settings = new Settings(this, defaultConfig, configManager);
        files = new Files(this);

        em.register(this, new RMPlayerJoinQuitEvent(this));
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

    public Settings getSettings() {
        return settings;
    }

    public String getVersion() {
        return pluginManager.getVersion();
    }
}
