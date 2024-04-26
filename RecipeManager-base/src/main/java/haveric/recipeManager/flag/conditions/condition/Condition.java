package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.flag.args.Args;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Condition {
    protected String name;

    public Condition(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean hasValue();
    public abstract void copy(Condition condition);
    public abstract boolean check(ItemStack item, ItemMeta meta);
    public abstract String getHashString();
    public abstract void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage);

}
