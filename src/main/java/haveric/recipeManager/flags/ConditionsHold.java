package haveric.recipeManager.flags;

import org.bukkit.inventory.ItemStack;

public class ConditionsHold extends Conditions {

    public ConditionsHold() {
        super();
    }

    public ConditionsHold(ConditionsHold original) {
        super(original);
    }

    @Override
    public ConditionsHold clone() {
        return new ConditionsHold(this);
    }

    public boolean checkIngredient(ItemStack item, Args a) {
        boolean addReasons = true;

        return checkIngredient(item, a, addReasons);
    }

    public static void parse(String value, String[] args, ConditionsHold cond) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

            if (arg.startsWith("offhand")) {

            } else {
                parseArg(value, arg, cond);
            }
        }
    }
}
