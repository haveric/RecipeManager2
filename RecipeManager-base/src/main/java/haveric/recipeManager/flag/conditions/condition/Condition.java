package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.flag.args.Args;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Condition {
    protected CheckCallback checkCallback;
    protected String name;

    public Condition(String name, CheckCallback checkCallback) {
        this.name = name;
        this.checkCallback = checkCallback;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean hasValue();
    public abstract void copy(Condition condition);
    public abstract String getHashString();
    public abstract void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage);

    public boolean check(ItemStack item, ItemMeta meta) {
        return this.checkCallback.checkCondition(item, meta, this);
    }
}
