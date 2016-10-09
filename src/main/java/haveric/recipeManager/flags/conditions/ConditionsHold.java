package haveric.recipeManager.flags.conditions;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flags.Args;
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
        boolean addReasons = true;

        return checkIngredient(item, a, addReasons);
    }

    public void parse(String value, String[] args) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

            if (arg.startsWith("slot")) {
                String val = arg.substring("slot".length()).trim();

                if (val.equals("mainhand")) {
                    setSlot(ConditionsSlot.MAINHAND);
                } else if (val.equals("offhand") || val.equals("shield")) {
                    setSlot(ConditionsSlot.OFFHAND);
                } else if (val.equals("helmet")) {
                    setSlot(ConditionsSlot.HELMET);
                } else if (val.equals("chest") || val.equals("chestplate")) {
                    setSlot(ConditionsSlot.CHEST);
                } else if (val.equals("legs") || val.equals("leggings")) {
                    setSlot(ConditionsSlot.LEGS);
                } else if (val.equals("boots")) {
                    setSlot(ConditionsSlot.BOOTS);
                } else if (val.equals("inventory")) {
                    setSlot(ConditionsSlot.INVENTORY);
                } else {
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
