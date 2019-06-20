package haveric.recipeManager.recipes.stonecutting;

import haveric.recipeManager.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.StonecutterInventory;

public class RMStonecuttingEvents implements Listener {
    public RMStonecuttingEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getRMStonecuttingEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getRMStonecuttingEvents(), RecipeManager.getPlugin());
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        HumanEntity ent = event.getWhoClicked();

        if (ent instanceof Player) {
            Inventory inv = event.getInventory();

            if (inv instanceof StonecutterInventory) {
                int rawSlot = event.getRawSlot();
                if (rawSlot == 1) { // Result

                }
            }
        }
    }

}
