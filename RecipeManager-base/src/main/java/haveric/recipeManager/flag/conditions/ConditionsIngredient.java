package haveric.recipeManager.flag.conditions;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.args.Args;
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
        neededLeft = original.neededLeft;
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
        neededLeft = needed;
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
        boolean addReasons = true;

        if (hasNeeded()) {
            addReasons = false;

            if (neededLeft == 0) {
                return true;
            }
        }

        return checkIngredient(item, a, addReasons);
    }

    public void parse(String[] args) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim();
            String argLower = arg.toLowerCase();

            if (argLower.startsWith("needed")) {
                String value = arg.substring("needed".length()).trim();

                try {
                    setNeeded(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'needed' argument with invalid number: " + value);
                }
            } else {
                parseArg(arg);
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "needed: " + needed;

        return toHash.hashCode();
    }
}
