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

        slot = original.getSlot();
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

    public void parse(String value, String[] args) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim();
            String argLower = arg.toLowerCase();

            if (argLower.startsWith("slot")) {
                String val = arg.substring("slot".length()).trim();

                switch(val) {
                    case "mainhand":
                        setSlot(ConditionsSlot.MAINHAND);
                        break;
                    case "offhand":
                    case "shield":
                        setSlot(ConditionsSlot.OFFHAND);
                        break;
                    case "helmet":
                        setSlot(ConditionsSlot.HELMET);
                        break;
                    case "chest":
                    case "chestplate":
                        setSlot(ConditionsSlot.CHEST);
                        break;
                    case "legs":
                    case "leggings":
                        setSlot(ConditionsSlot.LEGS);
                        break;
                    case "boots":
                        setSlot(ConditionsSlot.BOOTS);
                        break;
                    case "inventory":
                        setSlot(ConditionsSlot.INVENTORY);
                        break;
                    default:
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'slot' argument with invalid value: " + val);
                }
            } else {
                parseArg(value, arg);
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
}
