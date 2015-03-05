package haveric.recipeManager.events;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.entity.living.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.living.player.PlayerQuitEvent;
import org.spongepowered.api.util.event.Subscribe;

public class RMPlayerJoinQuitEvent {

    private RecipeManager plugin;

    public RMPlayerJoinQuitEvent(RecipeManager recipeManager) {
        plugin = recipeManager;
    }

    @Subscribe
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUIDFetcher.addPlayerToCache(player.getName(), player.getUniqueId());
    }

    @Subscribe
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUIDFetcher.removePlayerFromCache(player.getName());
    }
}
