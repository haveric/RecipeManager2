package haveric.recipeManager.tools;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * In API versions 1.20.6 and earlier, InventoryView is a class.
 * In versions 1.21 and later, it is an interface.
 * This method uses reflection to get the top Inventory object from the
 * InventoryView associated with an InventoryEvent, to avoid runtime errors.
 */
public class InventoryCompatibilityUtil {

    public static HumanEntity getPlayer(InventoryEvent event) {
        try {
            Object view = ((InventoryEvent) event).getView();
            Method getPlayer = view.getClass().getMethod("getPlayer");
            getPlayer.setAccessible(true);
            return (HumanEntity) getPlayer.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static InventoryType getType(InventoryEvent event) {
        try {
            Object view = event.getView();
            Method getType = view.getClass().getMethod("getType");
            getType.setAccessible(true);
            return (InventoryType) getType.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Inventory getBottomInventory(InventoryEvent event) {
        try {
            Object view = event.getView();
            Method getBottomInventory = view.getClass().getMethod("getBottomInventory");
            getBottomInventory.setAccessible(true);
            return (Inventory) getBottomInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
