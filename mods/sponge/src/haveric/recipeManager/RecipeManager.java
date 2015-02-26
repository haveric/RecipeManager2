package haveric.recipeManager;

import haveric.recipeManager.commands.Commands;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.event.Subscribe;

import com.google.inject.Inject;



@Plugin(id = "RecipeManager", name = "Recipe Manager", version = "3.0")
public class RecipeManager {

    @Inject
    private Logger log;

    private Game game;
    private Commands commands;

    @Subscribe
    public void preStartup(ServerAboutToStartEvent event) {
        game = event.getGame();
    }

    @Subscribe
    public void onStartup(ServerStartingEvent event) {
        commands = new Commands(this);
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
