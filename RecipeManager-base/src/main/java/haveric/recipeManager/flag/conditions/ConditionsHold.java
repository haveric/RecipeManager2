package haveric.recipeManager.flag.conditions;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.inventory.ItemStack;

public class ConditionsHold extends Conditions {

    private ConditionsSlot slot = ConditionsSlot.MAINHAND;

    public ConditionsHold() {
        super();
    }

    public ConditionsHold(ConditionsHold original) {
        super(original);

        slot = original.slot;
    }

    @Override
    public ConditionsHold clone() {
        return new ConditionsHold(this);
    }

    public void setSlot(ConditionsSlot newSlot) {
        slot = newSlot;
    }

    public ConditionsSlot getSlot() {
        return slot;
    }


    public boolean checkIngredient(ItemStack item, Args a) {
        return checkIngredient(item, a, true);
    }

    public void parse(String[] args) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim();
            String argLower = arg.toLowerCase();

            if (argLower.startsWith("slot")) {
                String val = arg.substring("slot".length()).trim();

                switch(val) {
                    case "mainhand":
                        slot = ConditionsSlot.MAINHAND;
                        break;
                    case "offhand":
                    case "shield":
                        slot = ConditionsSlot.OFFHAND;
                        break;
                    case "helmet":
                        slot = ConditionsSlot.HELMET;
                        break;
                    case "chest":
                    case "chestplate":
                        slot = ConditionsSlot.CHEST;
                        break;
                    case "legs":
                    case "leggings":
                        slot = ConditionsSlot.LEGS;
                        break;
                    case "boots":
                        slot = ConditionsSlot.BOOTS;
                        break;
                    case "inventory":
                        slot = ConditionsSlot.INVENTORY;
                        break;
                    default:
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'slot' argument with invalid value: " + val);
                }
            } else {
                parseArg(arg);
            }
        }
    }

    public enum ConditionsSlot {
        MAINHAND,
        OFFHAND,
        HELMET,
        CHEST,
        LEGS,
        BOOTS,
        INVENTORY
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "slot: " + slot.toString();

        return toHash.hashCode();
    }
}
