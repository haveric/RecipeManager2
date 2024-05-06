package haveric.recipeManager.flag.flags.any;

import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagDescriptor;
import haveric.recipeManager.flag.FlagFactory;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsHold;
import haveric.recipeManager.flag.conditions.ConditionsHold.ConditionsSlot;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.Map.Entry;

public class FlagHoldItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.HOLD_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <item or false> | [conditions]", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[] {
            "Makes the recipe require crafter to hold an item.",
            "",
            "This flag can be used more than once to add more items, the player will need to hold one to craft.",
            "Using the flag more than once with slot conditions will require an item in each slot added.",
            "",
            "The <item> argument can be in this format: material:data",
            "",
            "For [conditions] argument you may further specify what item must be used.",
            "Conditions must be separated by | and can be specified in any order.",
            "Condition list:",
            "",
            "  slot <slotname>",
            "    Changes the slot that is checked",
            "    Slot name values:",
            "      mainhand: selected hotbar slot. Defaults to this.",
            "      offhand or shield: offhand slot.",
            "      helmet: Helmet slot.",
            "      chest or chestplate: Chestplate slot.",
            "      legs or leggings: Leggings slot.",
            "      boots: Boots slot.",
            "      inventory: Any inventory slot.",
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
            "    The name must be an enchantment name, see " + Files.getNameIndexHashLink("enchantment"),
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
            "    The name must be an enchantment name, see " + Files.getNameIndexHashLink("enchantment"),
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
            "",
            "  name <text or regex:pattern>     = check the item name against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "",
            "  noname or !name",
            "    Held item must have no/default name",
            "    Overrides name condition if set",
            "",
            "  itemname <text or regex:pattern>     = check the item name against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  noitemname or !itemname",
            "    Ingredient must have no/default item name",
            "    Overrides itemname condition if set",
        };

        if (!Version.has1_20_5Support()) {
            description = ObjectArrays.concat(description, new String[]{
                "",
                "  localizedname <text or regex:pattern>     = check the item's localizedname against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
                "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
                "  nolocalizedname or !localizedname",
                "    Ingredient must have no localizedname",
                "    Overrides localizedname condition if set",
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[] {
            "",
            "  lore <text or regex:pattern>     = checks each lore line for a specific text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  nolore or !lore",
            "    Ingredient must have no lore",
            "    Overrides lore condition if set",
            "",
            "  color <colorname or R,G,B>       = only works for leather armor, checks color",
            "                                     values can be individual values or ranged separated by - char or you can use a color name constant, see " + Files.getNameIndexHashLink("dyecolor"),
            "  nocolor or !color",
            "    Only works for leather armor",
            "    Held item must have default/vanilla color",
            "    Overrides color condition if set",
        }, String.class);

        Map<String, String[]> conditionDescriptionsMap = new TreeMap<>();
        List<String> conditionNoMetas = new ArrayList<>();
        for (FlagDescriptor flagDescriptor : FlagFactory.getInstance().getFlags().values()) {
            Flag flag = flagDescriptor.getFlag();
            String conditionString = flag.getConditionName();
            String[] conditionDescription = flag.getConditionDescription();

            if (conditionString != null) {
                if (conditionDescription != null) {
                    conditionDescriptionsMap.put(conditionString, conditionDescription);
                }

                conditionNoMetas.add(conditionString);
            }
        }

        for (String[] conditionDescriptions : conditionDescriptionsMap.values()) {
            description = ObjectArrays.concat(description, new String[] { "", }, String.class);
            description = ObjectArrays.concat(description, conditionDescriptions, String.class);
        }

        StringBuilder metaList = new StringBuilder();
        StringBuilder noMetaList = new StringBuilder();

        if (!Version.has1_20_5Support()) {
            conditionNoMetas.add("localizedname");
        }

        Collections.sort(conditionNoMetas);
        for (String noMeta : conditionNoMetas) {
            metaList.append(", ").append(noMeta);
            noMetaList.append(" | no").append(noMeta);
        }

        description = ObjectArrays.concat(description, new String[] {
            "",
            "  nometa or !meta",
            "    Ingredient must have no metadata (enchants, bookenchants, name, itemname, lore, color" + metaList + ")",
            "    Overrides enchant, name, itemname, lore, color" + metaList + " conditions if set",
            "    Equivalent to noenchant | nobookenchant | noname | noitemname | nolore | nocolor" + noMetaList,
            "",
            "  potion <condition>, [...]",
            "    type &lt;potiontype&gt;      = Type of potion, see " + Files.getNameIndexHashLink("potiontype"),
        }, String.class);

        if (!Supports.basePotionType()) {
            description = ObjectArrays.concat(description, new String[]{
                "    level                  = Potion's level/tier, usually 1(default) or 2, you can enter 'max' to set it at highest supported level",
                "    extended or !extended  = Potion's extended duration",
            }, String.class);
        }

        description = ObjectArrays.concat(description, new String[]{
            "",
            "  potioneffect <condition>, [...]",
            "    type &lt;effecttype&gt;         = Type of potion effect, see " + Files.getNameIndexHashLink("potioneffect"),
            "    duration <num or min-max> = Duration of the potion effect in seconds, default 1 (does not work on HEAL and HARM)",
            "    amplify <num or min-max>  = Amplify the effects of the potion, default 0 (e.g. 2 = <PotionName> III, numbers after potion's max level will display potion.potency.number instead)",
            "    ambient or !ambient       = Check effect's extra visual particles setting",
            "    particles or !particles   = Check effect's particles setting",
            "    icon or !icon             = Check effect's icon setting",
            "",
            "  suspiciousstew <condition>, [...]",
            "    type &lt;effecttype&gt;         = Type of potion effect, see " + Files.getNameIndexHashLink("potioneffect"),
            "    duration <num or min-max> = Duration of the potion effect in seconds, default 1 (does not work on HEAL and HARM)",
            "    amplify <num or min-max>  = Amplify the effects of the potion, default 0 (e.g. 2 = <PotionName> III, numbers after potion's max level will display potion.potency.number instead)",
            "    ambient or !ambient       = Check effect's extra visual particles setting",
            "    particles or !particles   = Check effect's particles setting",
            "    icon or !icon             = Check effect's icon setting",
            "",
            "  banner <condition>, [...]",
            "    pattern <pattern> [dyecolor]",
            "",
            "    Patterns: " + Files.getNameIndexHashLink("bannerpattern"),
            "    Dye Colors: " + RMCUtil.collectionToString(Arrays.asList(DyeColor.values())).toLowerCase(),
        }, String.class);

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} iron_pickaxe // any data/damage value",
            "{flag} iron_axe:0 // only undamaged axe!",
            "{flag} chainmail_helmet | enchant protection_fire 1 // requires chain helmet with any level of damage and fire protection enchant level 1",
            "{flag} false // makes all previous statements useless", };
    }

    private Map<String, ConditionsHold> conditions = new HashMap<>();

    public FlagHoldItem() {
    }

    public FlagHoldItem(FlagHoldItem flag) {
        super(flag);
        for (Entry<String, ConditionsHold> e : flag.conditions.entrySet()) {
            conditions.put(e.getKey(), e.getValue().clone());
        }
    }

    @Override
    public FlagHoldItem clone() {
        return new FlagHoldItem((FlagHoldItem) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        // Match on single pipes '|', but not double '||'
        // Double pipes will be replaced by single pipes for each arg
        String[] args = value.split("(?<!\\|)\\|(?!\\|)");

        if (args.length < 1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an item!", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        ItemStack item = Tools.parseItem(args[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null) {
            return false;
        }

        ConditionsHold cond = new ConditionsHold();
        cond.setFlagType(getFlagType());
        setConditions(item, cond);

        cond.setIngredient(item);

        cond.parse(args);

        return true;
    }

    public void setConditions(ItemStack item, ConditionsHold cond) {
        Preconditions.checkNotNull(item, "item argument must not be null!");
        Preconditions.checkNotNull(cond, "cond argument must not be null!");

        String conditionIdentifier = Tools.convertItemToStringId(item) + "-" + cond.hashCode();
        conditions.put(conditionIdentifier, cond);
    }

    public int getNumConditionsOfSlot(ConditionsSlot slot) {
        int num = 0;

        for (Entry<String, ConditionsHold> stringConditionsHoldEntry : conditions.entrySet()) {
            if (slot.equals(stringConditionsHoldEntry.getValue().getSlot())) {
                num++;
            }
        }

        return num;
    }

    public List<ConditionsHold> getConditions(ItemStack item) {
        if (item == null) {
            return null;
        }

        List<ConditionsHold> conditionsList = new ArrayList<>();

        for (Entry<String, ConditionsHold> entry : conditions.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(item.getType() + ":" + item.getDurability() + "-")) {
                conditionsList.add(entry.getValue());
            } else if (key.startsWith(item.getType() + "-")) {
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
    public boolean checkConditions(ItemStack item, Args a, ConditionsSlot slot) {
        if (item == null) {
            return false;
        }

        boolean anySuccess = false;
        List<ConditionsHold> condList = getConditions(item);

        for (ConditionsHold cond : condList) {
            if (cond == null) {
                return true;
            }

            if (slot.equals(cond.getSlot()) && cond.checkIngredient(item, a)) {
                anySuccess = true;
                break;
            }
        }

        return anySuccess;
    }

    @Override
    public void onCheck(Args a) {
        StringBuilder s = new StringBuilder(); // TODO: Add items that need to be held
        boolean found = false;

        if (a.hasPlayer()) {
            boolean mainFound = true;
            boolean offFound = true;
            boolean helmetFound = true;
            boolean chestFound = true;
            boolean legsFound = true;
            boolean bootsFound = true;
            boolean inventoryFound = true;

            PlayerInventory inventory = a.player().getInventory();

            if (getNumConditionsOfSlot(ConditionsSlot.MAINHAND) > 0) {
                mainFound = checkConditions(inventory.getItemInMainHand(), a, ConditionsSlot.MAINHAND);
            }

            if (getNumConditionsOfSlot(ConditionsSlot.OFFHAND) > 0) {
                offFound = checkConditions(inventory.getItemInOffHand(), a, ConditionsSlot.OFFHAND);
            }

            if (getNumConditionsOfSlot(ConditionsSlot.HELMET) > 0) {
                helmetFound = checkConditions(inventory.getHelmet(), a, ConditionsSlot.HELMET);
            }

            if (getNumConditionsOfSlot(ConditionsSlot.CHEST) > 0) {
                chestFound = checkConditions(inventory.getChestplate(), a, ConditionsSlot.CHEST);
            }

            if (getNumConditionsOfSlot(ConditionsSlot.LEGS) > 0) {
                legsFound = checkConditions(inventory.getLeggings(), a, ConditionsSlot.LEGS);
            }

            if (getNumConditionsOfSlot(ConditionsSlot.BOOTS) > 0) {
                bootsFound = checkConditions(inventory.getBoots(), a, ConditionsSlot.BOOTS);
            }

            if (getNumConditionsOfSlot(ConditionsSlot.INVENTORY) > 0) {
                boolean anySuccess = false;
                ItemStack[] storage = inventory.getContents();

                for (ItemStack item : storage) {
                    if (checkConditions(item, a, ConditionsSlot.INVENTORY)) {
                        anySuccess = true;
                        break;
                    }
                }

                inventoryFound = anySuccess;
            }

            found = mainFound && offFound && helmetFound && chestFound && legsFound && bootsFound && inventoryFound;
        }

        // Ignore ingredient reasons
        a.clearReasons();

        if (!found) {
            a.addReason("flag.holditem", "", "{items}", s.toString());
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Map.Entry<String, ConditionsHold> entry : conditions.entrySet()) {
            toHash += entry.getKey() + entry.getValue().hashCode();
        }

        return toHash.hashCode();
    }
}
