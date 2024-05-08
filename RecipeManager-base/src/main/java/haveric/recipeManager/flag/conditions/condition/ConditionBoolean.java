package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConditionBoolean extends Condition {
    private Boolean value;

    public ConditionBoolean(String name, Boolean value, CheckCallback checkCallback) {
        super(name, checkCallback);

        this.value = value;
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public void copy(Condition condition) {
        if (condition instanceof ConditionBoolean conditionBoolean) {
            value = conditionBoolean.value;
        }
    }

    @Override
    public void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage) {
        if (hasValue() && value) {
            a.addReason("flag.ingredientconditions.no" + name, failMessage, "{item}", ToolsItem.print(item));
        } else {
            a.addReason("flag.ingredientconditions.empty" + name, failMessage, "{item}", ToolsItem.print(item));
        }
    }

    @Override
    public String getHashString() {
        return "conditionBoolean:" + name + ": " + value;
    }
}
