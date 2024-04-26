package haveric.recipeManager.flag.conditions.condition;

import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConditionBoolean extends Condition {
    private CheckCallback checkCallback;
    private Boolean value;

    public ConditionBoolean(String name, Boolean value, CheckCallback checkCallback) {
        super(name);

        this.checkCallback = checkCallback;
        this.value = value;
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    public Boolean getValue() {
        return this.value;
    }

    @Override
    public void copy(Condition condition) {
        if (condition instanceof ConditionBoolean) {
            this.value = ((ConditionBoolean) condition).value;
        }
    }

    @Override
    public boolean check(ItemStack item, ItemMeta meta) {
        return this.checkCallback.checkCondition(item, meta, this);
    }

    @Override
    public void addReasons(Args a, ItemStack item, ItemMeta meta, String failMessage) {
        if (hasValue() && value) {
            a.addReason("flag.ingredientconditions.no" + name, failMessage, "{item}", ToolsItem.print(item));
        } else {
            a.addReason("flag.ingredientconditions." + name, failMessage, "{item}", ToolsItem.print(item));
        }
    }

    @Override
    public String getHashString() {
        return name + ": " + value;
    }
}
