package haveric.recipeManager.flag.conditions.condition;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface CheckBooleanCallback {
    boolean checkCondition(ItemStack item, ItemMeta meta, boolean hasValue, Boolean value);
}
