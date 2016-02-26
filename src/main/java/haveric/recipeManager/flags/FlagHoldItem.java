package haveric.recipeManager.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.Messages;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.util.ParseBit;

public class FlagHoldItem extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <item or false>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the recipe require crafter to hold an item.",
            "",
            "This flag can be used more than once to add more items, the player will need to hold one to craft.",
            "",
            "The <item> argument can be in this format: material:data:amount",
            "Just like recipe results, not all values from the item are required.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} iron_pickaxe // any data/damage value",
            "{flag} iron_axe:0 // only undamaged axe!",
            //"{flag} chainmail_helmet | protection_fire:1 // requires chain helmet with any level of damage and fire protection enchant level 1",
            "{flag} false // makes all previous statements useless", };
    }


    private Map<String, Conditions> conditions = new HashMap<String, Conditions>();

    public FlagHoldItem() {
    }

    public FlagHoldItem(FlagHoldItem flag) {
        for (Entry<String, Conditions> e : flag.conditions.entrySet()) {
            conditions.put(e.getKey(), e.getValue().clone());
        }
    }

    @Override
    public FlagHoldItem clone() {
        return new FlagHoldItem((FlagHoldItem) super.clone());
    }

    @Override
    protected boolean onParse(String value) {
        String[] args = value.split("\\|");

        if (args.length < 1) {
            return ErrorReporter.error("Flag " + getType() + " needs an item!", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        ItemStack item = Tools.parseItem(args[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null) {
            return false;
        }

        Conditions cond = new Conditions();
        cond.setFlagType(getType());
        setConditions(item, cond);

        cond.setIngredient(item);

        Conditions.parse(value, args, cond);

        return true;
    }

    public void setConditions(ItemStack item, Conditions cond) {
        Validate.notNull(item, "item argument must not be null!");
        Validate.notNull(cond, "cond argument must not be null!");

        String conditionIdentifier = Tools.convertItemToStringId(item) + "-" + cond.hashCode();
        conditions.put(conditionIdentifier, cond);
    }

    public List<Conditions> getConditions(ItemStack item) {
        if (item == null) {
            return null;
        }

        List<Conditions> conditionsList = new ArrayList<Conditions>();
        Iterator<Entry<String, Conditions>> iter = conditions.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Conditions> entry = iter.next();
            String key = entry.getKey();
            if (key.startsWith(String.valueOf(item.getTypeId() + ":" + item.getDurability() + "-"))) {
                conditionsList.add(entry.getValue());
            } else if (key.startsWith(String.valueOf(item.getTypeId() + "-"))) {
                conditionsList.add(entry.getValue());
            }
        }

        return conditionsList;
    }

    /**
     * @param item
     *            returns false if null.
     * @param a
     *            arguments to store reasons or null to just use return value.
     * @return true if passed, false otherwise
     */
    public boolean checkConditions(ItemStack item, Args a) {
        if (item == null) {
            return false;
        }

        boolean anySuccess = false;
        List<Conditions> condList = getConditions(item);

        for (Conditions cond : condList) {
            if (cond == null) {
                return true;
            }

            boolean success = cond.checkIngredient(item, a);
            if (success) {
                anySuccess = true;

                if (cond.hasNeeded() && cond.getNeededLeft() > 0) {
                    cond.setNeededLeft(cond.getNeededLeft() - 1);
                }
            }
        }

        return anySuccess;
    }

    @Override
    protected void onCheck(Args a) {
        StringBuilder s = new StringBuilder();
        boolean found = false;

        if (a.hasPlayer()) {
            ItemStack held = a.player().getItemInHand();

            if (held != null) {
                found = checkConditions(held, a);
            }
        }

        if (!found) {
            a.addReason(Messages.FLAG_HOLDITEM, "", "{items}", s.toString());
        }
    }
}
