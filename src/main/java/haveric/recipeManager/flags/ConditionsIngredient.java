package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import org.bukkit.inventory.ItemStack;

public class ConditionsIngredient extends Conditions {
    private int needed;
    private int neededLeft;

    public ConditionsIngredient() {
        super();
    }

    public ConditionsIngredient(ConditionsIngredient original) {
        super(original);

        needed = original.needed;
        setNeededLeft(original.getNeededLeft());
    }

    @Override
    public ConditionsIngredient clone() {
        return new ConditionsIngredient(this);
    }

    public int getNeeded() {
        return needed;
    }

    public void setNeeded(int newNeeded) {
        needed = newNeeded;
        setNeededLeft(needed);
    }

    public boolean hasNeeded() {
        return needed > 0;
    }

    public int getNeededLeft() {
        return neededLeft;
    }

    public void setNeededLeft(int neededLeft) {
        this.neededLeft = neededLeft;
    }

    public boolean checkIngredient(ItemStack item, Args a) {
        boolean ok = true;
        boolean addReasons = true;

        if (hasNeeded()) {
            addReasons = false;

            if (getNeededLeft() == 0) {
                return ok;
            }
        }

        return checkIngredient(item, a, addReasons);
    }

    public static void parse(String value, String[] args, ConditionsIngredient cond) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

            if (arg.startsWith("needed")) {
                value = arg.substring("needed".length()).trim();

                try {
                    cond.setNeeded(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'needed' argument with invalid number: " + value);
                }
            } else {
                parseArg(value, arg, cond);
            }
        }
    }
}
