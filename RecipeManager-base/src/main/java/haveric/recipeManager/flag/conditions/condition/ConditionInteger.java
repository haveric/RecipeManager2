package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConditionInteger extends Condition {
    private CheckCallback checkCallback;
    private Integer value;

    public ConditionInteger(String name, Integer value, CheckCallback checkCallback) {
        super(name);

        this.checkCallback = checkCallback;
        this.value = value;
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    public Integer getValue() {
        return this.value;
    }

    @Override
    public void copy(Condition condition) {
        if (condition instanceof ConditionInteger) {
            this.value = ((ConditionInteger) condition).value;
        }
    }

    @Override
    public boolean check(ItemStack item, ItemMeta meta) {
        return this.checkCallback.checkCondition(item, meta, this);
    }

    @Override
    public void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage) {
        if (hasValue()) {
            a.addReason("flag.ingredientconditions.no" + name, failMessage, "{item}", ToolsItem.print(item), "{data}", value);
        } else {
            a.addReason("flag.ingredientconditions.empty" + name, failMessage, "{item}", ToolsItem.print(item));
        }
    }

    @Override
    public String getHashString() {
        return name + ": " + value;
    }
}
