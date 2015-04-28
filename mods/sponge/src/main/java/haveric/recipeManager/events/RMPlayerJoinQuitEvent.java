package haveric.recipeManager.events;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerQuitEvent;

public class RMPlayerJoinQuitEvent {

    private RecipeManager plugin;

    public RMPlayerJoinQuitEvent(RecipeManager recipeManager) {
        plugin = recipeManager;
    }

    @Subscribe
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUIDFetcher.addPlayerToCache(plugin, player.getName(), player.getUniqueId());
    }

    @Subscribe
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUIDFetcher.removePlayerFromCache(player.getName());
    }
}
