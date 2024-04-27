package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConditionInteger extends Condition {
    private Integer value;

    public ConditionInteger(String name, Integer value, CheckCallback checkCallback) {
        super(name, checkCallback);
        this.value = value;
    }

    @Override
    public boolean hasValue() {
        return value != null && value != Integer.MIN_VALUE;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public void copy(Condition condition) {
        if (condition instanceof ConditionInteger) {
            value = ((ConditionInteger) condition).value;
        }
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
