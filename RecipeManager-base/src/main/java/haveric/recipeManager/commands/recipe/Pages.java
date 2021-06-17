package haveric.recipeManager.commands.recipe;

import haveric.recipeManager.RecipeManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class Pages {
    private UUID playerUUID;
    private ItemStack item;
    private int page = -1;
    private String[] pages;
    private BukkitTask task;

    public Pages(UUID uuid, ItemStack newItem, List<String> newPages) {
        playerUUID = uuid;
        item = newItem;
        pages = newPages.toArray(new String[0]);
    }

    private void doTask() {
        if (task != null) {
            task.cancel();
        }

        task = new BukkitRunnable() {
            public void run() {
                RecipePagination.remove(playerUUID);
            }
        }.runTaskLater(RecipeManager.getPlugin(), 20 * 60);
    }

    public boolean hasNext() {
        return pages.length > (page + 1);
    }

    public String next() {
        page++;

        if (page >= pages.length) {
            return null;
        }

        doTask();
        return pages[page];
    }

    public boolean hasPrev() {
        return page > 0;
    }

    public String prev() {
        if (page <= 0) {
            return null;
        }

        page--;

        doTask();
        return pages[page];
    }

    public int getPage() {
        return page;
    }

    public int getNumPages() {
        return pages.length;
    }

    public ItemStack getItem() {
        return item;
    }
}
