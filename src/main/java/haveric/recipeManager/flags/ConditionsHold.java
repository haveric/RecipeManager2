package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
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

    public static void parse(String value, String[] args, ConditionsHold cond) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

            if (arg.startsWith("slot")) {
                String val = arg.substring("slot".length()).trim();

                if (val.equals("mainhand")) {
                    cond.setSlot(ConditionsSlot.MAINHAND);
                } else if (val.equals("offhand") || val.equals("shield")) {
                    cond.setSlot(ConditionsSlot.OFFHAND);
                } else if (val.equals("helmet")) {
                    cond.setSlot(ConditionsSlot.HELMET);
                } else if (val.equals("chest") || val.equals("chestplate")) {
                    cond.setSlot(ConditionsSlot.CHEST);
                } else if (val.equals("legs") || val.equals("leggings")) {
                    cond.setSlot(ConditionsSlot.LEGS);
                } else if (val.equals("boots")) {
                    cond.setSlot(ConditionsSlot.BOOTS);
                } else if (val.equals("inventory")) {
                    cond.setSlot(ConditionsSlot.INVENTORY);
                } else {
                    ErrorReporter.getInstance().warning("Flag " + cond.getFlagType() + " has 'slot' argument with invalid value: " + val);
                }
            } else {
                parseArg(value, arg, cond);
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
