package haveric.recipeManager;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateInventory extends BukkitRunnable {
    private final Player player;

    public UpdateInventory(Player newPlayer, int ticks) {
        player = newPlayer;

        if (ticks <= 0) {
            run();
        } else {
            runTaskLater(RecipeManager.getPlugin(), ticks);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        player.updateInventory();
    }
}