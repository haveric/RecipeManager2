package haveric.recipeManager.flag.conditions.condition;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface CheckCallback {
    boolean checkCondition(ItemStack item, ItemMeta meta, Condition condition);
}
