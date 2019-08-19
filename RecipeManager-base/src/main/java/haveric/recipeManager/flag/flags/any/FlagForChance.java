package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.*;
import haveric.recipeManager.flag.args.Args;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlagForChance extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FOR_CHANCE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <group> [chance]% [^]@[flag declaration]",
            "{flag} <chance>% [^]@<flag declaration>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Triggers other flags or groups of flags by specified chance.",
            "Using this flag more than once will add more flags.",
            "",
            "The 'group' argument defines a group for the flags (not permission related), can be any combination of letters only, no spaces either.",
            "Grouping flags makes the system pick only one flag from the bunch which means it's also limited to 100% total chance.",
            "If a group is not defined then the flags will be added to the default group which is a special group that will trigger flags randomly according to their chance, it can trigger all at once or even none at all.",
            "",
            "The 'chance' argument suggests a chance value that can be between 0.01 and 100 and the '%' suffix is required.",
            "The chance argument is only optional if there's a group defined, then the remaining chance will be evenly split between all flags with undefined chance.",
            "",
            "The 'flag declaration' is a flag like you'd add a flag to a recipe or result, you can even add this flag into itself to make multi-chance structures.",
            "The flag declaration argument is only optional if there's a group defined and will act as literally nothing.",
            "Optionally you can prefix the flag declaration with the '^' character to append the data from the flag to the previous flag of the same type from the same group (no group is still a group, but a special one).",
            "",
            "NOTE: If using '^' prefix, always use '^' and '@' together like '^@', no space in between.",
            "NOTE: In a group there must be at least a chance value or a flag declaration.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "// some simple example",
            "{flag} 25% " + FlagType.EXPLODE + " // 25% chance to explode",
            "// appending to flags example",
            "{flag} 80% " + FlagType.COMMAND + " say high chance message!",
            "{flag} 50% " + FlagType.COMMAND + " say 50-50 message... // this is a totally new flag, individual from the previous one.",
            "{flag}    ^" + FlagType.COMMAND + " say extra message! // this command will be appended to the previous command flag.",
            "{flag}    ^" + FlagType.COMMAND + " say extra-large message!!! // this will also add append to the same previous command flag, now it has 3 commands.",
            "// all flags in a group must have a total of 100% chance since only one triggers, in this case the chance is calculated and it would be 33.33% for each.",
            "{flag} mystuff " + FlagType.SOUND + " level_up",
            "{flag} mystuff " + FlagType.SOUND + " note_bass",
            "{flag} mystuff " + FlagType.SOUND + " hurt",
            "// example of empty flag definition as nothing chance",
            "{flag} dostuff " + FlagType.BROADCAST + " yay!",
            "{flag} dostuff 75% // this sets the 'dostuff' group to do nothing 75% of the time",
            "// forchanception", "{flag} 50% {flag} 25% {flag} test " + FlagType.BROADCAST + " chanception occurred!",
            "// NOTE all of the examples above can be used in a single recipe if you want, there's no limit to the combinations!", };
    }


    public class ChanceFlag {
        private Flag flag;
        private float chance;
        private boolean autoChance = false;

        public ChanceFlag(Flag newFlag, Float newChance) {
            flag = newFlag;

            if (newChance == null) {
                autoChance = true;
            } else {
                chance = newChance;
            }
        }

        public Flag getFlag() {
            return flag;
        }

        public void setFlag(Flag flagToSet, FlagForChance holder) {
            flagToSet.setFlagsContainer(holder.getFlagsContainer());
            flag = flagToSet;
        }

        public float getChance() {
            return chance;
        }

        public boolean isAutoChance() {
            return autoChance;
        }
    }

    private Map<String, List<ChanceFlag>> flagMap = new HashMap<>();

    public FlagForChance() {
    }

    public FlagForChance(FlagForChance flag) {
        for (Entry<String, List<ChanceFlag>> e : flag.flagMap.entrySet()) {
            List<ChanceFlag> flags = new ArrayList<>();

            for (ChanceFlag c : e.getValue()) {
                if (c.getFlag() == null) {
                    flags.add(new ChanceFlag(null, c.getChance()));
                } else {
                    Float chance;
                    if (c.isAutoChance()) {
                        chance = null;
                    } else {
                        chance = c.getChance();
                    }
                    flags.add(new ChanceFlag(c.getFlag().clone(getFlagsContainer()), chance));
                }
            }

            recalculateChances(e.getKey(), flags);
            flagMap.put(e.getKey(), flags);
        }
    }

    @Override
    public FlagForChance clone() {
        return new FlagForChance((FlagForChance) super.clone());
    }

    /**
     * The flag map, map keys are groups and values are lists of ChanceFlag classes which contain a Flag object and a chance float value.
     *
     * @return
     */
    public Map<String, List<ChanceFlag>> getFlagMap() {
        return flagMap;
    }

    /**
     * Gets the first flag matching FlagTypeOld or the last if reverse is set to true.<br>
     * There can be more instances of the same type of flag.
     *
     * @param group
     *            the chance group, can be null for no group/individual
     * @return flag list or null if group doesn't exist
     */
    public List<ChanceFlag> getFlagsFromGroup(String group) {
        return flagMap.get(group);
    }

    /**
     * @param group
     *            the chance group, can be null for no group/individual
     * @param flags
     *            the list of flags
     */
    public void setFlagsForGroup(String group, List<ChanceFlag> flags) {
        flagMap.put(group, flags);
    }

    /**
     * Checks if the flag can be added to this flag list.
     *
     * @param flag
     * @return false if flag can only be added on specific flaggables
     */
    public boolean canAdd(Flag flag) {

        return flag != null && flag.validate() && !FlagFactory.getInstance().getFlagByName(getFlagType()).hasBit(FlagBit.NO_FOR);
    }

    /**
     * Attempts to add a flag to this flag list for the chance group.<br>
     * Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     *
     * @param group
     *            the chance group, can be null for no group/individual
     * @param flag
     *            the flag
     */
    public void addFlag(String group, Flag flag) {
        addFlag(group, flag, 100.0f);
    }

    /**
     * Attempts to add a flag to this flag list for the chance group.<br>
     * Adds an error to the {@link ErrorReporter} class if flag is not compatible with recipe/result.
     *
     * @param group
     *            the chance group, can be null for no group/individual
     * @param flag
     *            the flag
     * @param chance
     *            trigger chance, valid values between 0.01f to 100.0f or null to auto-calculate
     */
    public void addFlag(String group, Flag flag, Float chance) {
        Validate.notNull(flag, "Argument flag must not be null!");

        if (canAdd(flag)) {
            List<ChanceFlag> flags = flagMap.computeIfAbsent(group, k -> new ArrayList<>());

            flag.setFlagsContainer(getFlagsContainer());

            if (chance != null) {
                chance = Math.min(Math.max(chance, 0.01f), 100.0f);
            }

            flags.add(new ChanceFlag(flag, chance));
            recalculateChances(group, flags);
        }
    }

    @Override
    public boolean onParse(String value) {
        int i = value.indexOf(' '); // get position of first space
        String flagDeclaration = null;
        String group = null;
        Float chance = null;
        boolean appendFlag = false;

        if (i < 0) {
            group = value;
        } else {
            String arg = value.substring(0, i);

            if (arg.charAt(0) == '@') {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid group name that resembles a flag: " + arg);
                return false;
            }

            if (arg.startsWith("^@")) {
                arg = value.substring(1);
                flagDeclaration = arg;
                appendFlag = true;
            } else {
                if (arg.charAt(i - 1) == '%') { // check if character before space is a '%'
                    arg = arg.substring(0, i - 1).trim(); // get the string between beginning of string and the space - 1 character to skip the '%' char

                    try {
                        chance = Float.valueOf(arg);
                    } catch (NumberFormatException e) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid chance number: " + arg);
                        return false;
                    }

                    if (chance < 0.01f || chance > 100) {
                        chance = Math.min(Math.max(chance, 0.01f), 100.0f);

                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " is lower than 0.01 or higher than 100%, trimmed.");
                    }

                    arg = value.substring(i + 1).trim(); // get the string after the first space

                    if (arg.charAt(0) != '@' && !arg.startsWith("^@")) { // we need a flag declaration at this point
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has chance as first argument but not a flag as second argument: " + arg);
                        return false;
                    }

                    if (arg.charAt(0) == '^') {
                        arg = arg.substring(1);
                        appendFlag = true;
                    }

                    flagDeclaration = arg;
                } else {
                    group = arg; // otherwise it must be a group!

                    arg = value.substring(i + 1).trim(); // get the string after the space

                    if (arg.charAt(0) == '@' || arg.startsWith("^@")) {
                        if (arg.charAt(0) == '^') {
                            arg = arg.substring(1);
                            appendFlag = true;
                        }

                        flagDeclaration = arg;
                    } else {
                        i = arg.indexOf('%'); // get location of first '%' char...

                        if (i == -1) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has neither a flag nor a chance argument: " + value);
                            return false;
                        }

                        String chanceString = arg.substring(0, i); // get string between group and '%' char...

                        try {
                            chance = Float.valueOf(chanceString);
                        } catch (NumberFormatException e) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid chance number: " + chanceString);
                            return false;
                        }

                        if (chance < 0.01f || chance > 100) {
                            chance = Math.min(Math.max(chance, 0.01f), 100.0f);

                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " is lower than 0.01 or higher than 100%, trimmed.");
                        }

                        if (arg.length() > (i + 1)) {
                            arg = arg.substring(i + 1).trim(); // get string after '%' char

                            if (arg.charAt(0) == '@' || arg.startsWith("^@")) {
                                if (arg.charAt(0) == '^') {
                                    arg = arg.substring(1);
                                    appendFlag = true;
                                }

                                flagDeclaration = arg;
                            } else {
                                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown last argument, expected flag: " + arg);
                            }
                        }
                    }
                }
            }
        }

        List<ChanceFlag> flags = flagMap.get(group); // get flags list for group even if group is null
        ChanceFlag flagChance = null;

        if (flagDeclaration == null) {
            if (flags == null) {
                flags = new ArrayList<>();
                flagMap.put(group, flags);
            } else {
                for (ChanceFlag c : flags) {
                    if (c.getFlag() == null) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " already has a blank flag for this group!");
                        return false;
                    }
                }
            }

            flagChance = new ChanceFlag(null, chance);
            flags.add(flagChance);
            recalculateChances(group, flags);
        } else {
            String[] split = flagDeclaration.split("[:\\s]+", 2); // split by space or : char
            String flagString = split[0].trim(); // format flag name

            FlagDescriptor type = FlagFactory.getInstance().getFlagByName(flagString); // Find the current flag

            if (type == null) {
                ErrorReporter.getInstance().warning("Flag " + type.getNameDisplay() + " has unknown flag type: " + flagString);
                return false;
            }

            if (type.hasBit(FlagBit.NO_FOR)) {
                ErrorReporter.getInstance().warning("Flag " + type.getNameDisplay() + "'s flag " + flagString + " can not be used with this!");
                return false;
            }

            if (flags == null) {
                flags = new ArrayList<>();
                flagMap.put(group, flags);
            } else {
                if (appendFlag) {
                    // Loop through flags backwards to get the last added flag
                    for (i = flags.size() - 1; i >= 0; i--) {
                        ChanceFlag c = flags.get(i);

                        if (c.getFlag() != null && c.getFlag().getFlagType().equals(type.getName())) {
                            flagChance = c;

                            if (chance != null) {
                                ErrorReporter.getInstance().warning("Flag " + type.getNameDisplay() + " has flag @" + flagChance.getFlag().getFlagType() + " with chance defined, chance will be ignored because flag will append the previous one!", "Or remove the '^' prefix to create a new fresh flag, see '" + Files.FILE_INFO_FLAGS + "' for details about the prefix.");
                            }

                            break;
                        }
                    }
                }
            }

            Flag flag;

            if (appendFlag && flagChance == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " can't append to " + type.getNameDisplay() + " flag because it hasn't been defined for this group!");
            }

            if (!appendFlag || flagChance == null) {
                if (group != null) {
                    float totalChance = 0;

                    for (ChanceFlag c : flags) {
                        if (!c.isAutoChance()) {
                            totalChance += c.getChance();
                        }
                    }

                    if (chance == null) {
                        if (totalChance >= 100) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " already has 100% chance for '" + group + "' group!", "You can't add more flag to it until you reduce the chance of one or more flag.");
                            return false;
                        }
                    } else {
                        totalChance += chance;

                        if (totalChance > 100) {
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " exceeds 100% chance for '" + group + "' group!", "Reduce the chance or remove it to be auto-calculated.");
                            return false;
                        }
                    }
                }

                flag = type.createFlagClass();
                flag.setFlagsContainer(getFlagsContainer()); // set container before hand to allow checks
                flagChance = new ChanceFlag(flag, chance);
            } else {
                flag = flagChance.getFlag();
            }

            if (split.length > 1) {
                value = split[1].trim();
            } else {
                value = null;
            }

            // make sure the flag can be added to this flag list
            if (!flag.validateParse(value)) {
                return false;
            }

            // check if parsed flag had valid values and needs to be added to flag list
            if (!flag.onParse(value)) {
                return false;
            }

            if (!appendFlag) {
                flags.add(flagChance);
                recalculateChances(group, flags);
            }
        }

        return true;
    }

    private void recalculateChances(String group, List<ChanceFlag> flags) {
        if (group == null) {
            return;
        }

        float totalChance = 100;
        int num = 0;

        for (ChanceFlag c : flags) {
            if (c.isAutoChance()) {
                num++;
            } else {
                totalChance -= c.getChance();
            }
        }

        if (num > 0) {
            float chance = totalChance / num;
            for (ChanceFlag c : flags) {
                if (c.isAutoChance()) {
                    c.chance = chance;
                }
            }
        }
    }

    @Override
    public void onCheck(Args a) {
        event(a, 'c');
    }

    @Override
    public void onFailed(Args a) {
        event(a, 'f');
    }

    @Override
    public void onPrepare(Args a) {
        event(a, 'p');
    }

    @Override
    public void onCrafted(Args a) {
        event(a, 'r');
    }

    @Override
    public void onFuelRandom(Args a) {
        event(a, 'd');
    }

    @Override
    public void onFuelEnd(Args a) {
        event(a, 'e');
    }

    private void event(Args a, char method) {
        for (Entry<String, List<ChanceFlag>> e : flagMap.entrySet()) {
            List<ChanceFlag> flags = e.getValue();

            if (e.getKey() == null) {
                // flags without group, get all flags that match the chance
                for (ChanceFlag c : flags) {
                    if (c.getChance() >= (RecipeManager.random.nextFloat() * 100)) {
                        trigger(c.getFlag(), a, method);
                    }
                }
            } else {
                // grouped flags, get one flag
                float random = RecipeManager.random.nextFloat() * 100;
                float chance = 0;

                for (ChanceFlag c : flags) {
                    chance += c.getChance();
                    if (chance >= random) {
                        trigger(c.getFlag(), a, method);
                        break;
                    }
                }
            }
        }
    }

    private void trigger(Flag flag, Args a, char method) {
        if (flag == null) {
            return;
        }

        switch (method) {
            case 'c':
                flag.check(a);
                break;
            case 'p':
                flag.prepare(a);
                break;
            case 'r':
                flag.crafted(a);
                break;
            case 'f':
                flag.failed(a);
                break;
            case 'd':
                flag.fuelRandom(a);
                break;
            case 'e':
                flag.fuelEnd(a);
                break;
            default:
                break;
        }
    }
}
