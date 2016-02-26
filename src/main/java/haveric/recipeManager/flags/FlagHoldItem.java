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
            "{flag} <item or false> | [conditions]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the recipe require crafter to hold an item.",
            "",
            "This flag can be used more than once to add more items, the player will need to hold one to craft.",
            "",
            "The <item> argument can be in this format: material:data",
            "",
            "For [conditions] argument you may further specify what item must be used.",
            "Conditions must be separated by | and can be specified in any order.",
            "Condition list:",
            "",
            "  data <[!][&]num or min-max or all or vanilla or damaged or new>, [...]",
            "    Condition for data/damage/durability, as argument you can specify data values separated by , character.",
            "    One number is required, you can add another number separated by - character to make a number range.",
            "    Additionally instead of the number you can specify 'item:data' to use the named data value.",
            "    Special data values:",
            "      all: Flips the data check to allow all data values instead of none initially.",
            "      vanilla: Only allow data values within the vanilla ranges.",
            "      new: Equivalent to 0, or an undamaged item.",
            "      damaged: On weapons and armor, this is everything within vanilla limits that is considered a damaged item.",
            "    Prefixing with '&' would make a bitwise operation on the data value.",
            "    Prefixing with '!' would reverse the statement's meaning making it not work with the value specified.",
            "    Optionally you can add more data conditions separated by ',' that the held item must match against one to proceed.",
            "    Defaults to the equivalent of !all.",

            "",
            "  enchant <name> [[!]num or min-max], [...]",
            "    Condition for applied enchantments (not stored in books).",
            "    This argument can be used more than once to add more enchantments as conditions.",
            "    The name must be an enchantment name, see '" + Files.FILE_INFO_NAMES + "' at 'ENCHANTMENTS' section.",
            "    The 2nd argument is the levels, it's optional",
            "    A number can be used as level to set that level as requirement.",
            "    You can also use 'max' to use the max supported level for that enchantment.",
            "    Additionally a second number separated by - can be added to specify a level range, 'max' is also supported in ranged value.",
            "    Prefixing with '!' would ban the level or level range.",
            "",
            "  noenchant or !enchant",
            "    Held item must have no enchantment",
            "    Overrides enchant condition if set",
            "",
            "  bookenchant <name> [[!]num or min-max], [...]",
            "    Condition for book enchantments (not applied enchantments)",
            "    This argument can be used more than once to add more enchantments as conditions.",
            "    The name must be an enchantment name, see '" + Files.FILE_INFO_NAMES + "' at 'ENCHANTMENTS' section.",
            "    The 2nd argument is the levels, it's optional",
            "    A number can be used as level to set that level as requirement.",
            "    You can also use 'max' to use the max supported level for that enchantment.",
            "    Additionally a second number separated by - can be added to specify a level range, 'max' is also supported in ranged value.",
            "    Prefixing with '!' would ban the level or level range.",
            "",
            "  nobookenchant or !bookenchant",
            "    Held item must have no book enchantment",
            "    Overrides bookenchant condition if set",
            "",
            "  amount <num>                     = stack amount",
            "  name <text or regex:pattern>     = check the item name against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
            "  noname or !name",
            "    Held item must have no/default name",
            "    Overrides name condition if set",
            "",
            "  lore <text or regex:pattern>     = checks each lore line for a specific text or if prefixed with 'regex:' it will check for a regex pattern.",
            "  nolore or !lore",
            "    Held item must have no lore",
            "    Overrides lore condition if set",
            "",
            "  color <colorname or R,G,B>       = only works for leather armor, checks color, the values can be individual values or ranged separated by - char or you can use a color name constant, see '" + Files.FILE_INFO_NAMES + "' at 'DYE COLOR'.",
            "",
            "  nocolor or !color",
            "    Only works for leather armor",
            "    Held item must have default/vanilla color",
            "    Overrides color condition if set",
            "",
            "  nometa or !meta",
            "    Held item must have no metadata (enchants, bookenchants, name, lore, color)",
            "    Overrides enchant, name, lore, color conditions if set",
            "    Equivalent to noenchant | nobookenchant | noname | nolore | nocolor", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} iron_pickaxe // any data/damage value",
            "{flag} iron_axe:0 // only undamaged axe!",
            "{flag} chainmail_helmet | enchant protection_fire 1 // requires chain helmet with any level of damage and fire protection enchant level 1",
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

            if (cond.checkIngredient(item, a)) {
                anySuccess = true;
                break;
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

        // Ignore ingredient reasons
        a.clearReasons();

        if (!found) {
            a.addReason(Messages.FLAG_HOLDITEM, "", "{items}", s.toString());
        }
    }
}
