package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.*;
import haveric.recipeManager.flag.args.Args;

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
            "Optionally you can prefix the flag declaration with the '^' character to add more flags to the same group (no group is still a group, but a special one).",
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
            "{flag}    ^" + FlagType.COMMAND + " say extra message! // this command will be added to the previous command flag's chance.",
            "{flag}    ^" + FlagType.SUMMON + " zombie // This will summon a zombie along with the previous two commands.",
            "// appending flags with multiple chances example",
            "{flag} attack 40% " + FlagType.MESSAGE + " Zombie Attack! // 40% of the time, it will send 'Zombie Attack!' and summon a zombie",
            "{flag} attack ^" + FlagType.SUMMON + " zombie",
            "{flag} attack 40% " + FlagType.MESSAGE + " Skeleton Attack! // 40% of the time, it will send 'Skeleton Attack!' and summon a skeleton.",
            "{flag} attack ^" + FlagType.SUMMON + " skeleton",
            "  // The remaining 20% of the time, nothing will happen.",
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


    public static class ChanceFlag {
        private List<Flag> flags = new ArrayList<>();
        private float chance;
        private boolean autoChance = false;

        public ChanceFlag(List<Flag> newFlags, FlagForChance holder, Float newChance) {
            for (Flag flag: newFlags) {
                addFlag(flag, holder);
            }

            if (newChance == null) {
                autoChance = true;
            } else {
                chance = newChance;
            }
        }

        public ChanceFlag(Flag newFlag, FlagForChance holder, Float newChance) {
            addFlag(newFlag, holder);

            if (newChance == null) {
                autoChance = true;
            } else {
                chance = newChance;
            }
        }

        public List<Flag> getFlags() {
            return flags;
        }

        public void addFlag(Flag flagToSet, FlagForChance holder) {
            if (flagToSet != null) {
                flagToSet.setFlagsContainer(holder.getFlagsContainer());
                flags.add(flagToSet);
            }
        }

        public float getChance() {
            return chance;
        }

        public boolean isAutoChance() {
            return autoChance;
        }

        @Override
        public int hashCode() {
            String toHash = "";

            for (Flag flag : flags) {
                toHash += "flag: " + flag.hashCode();
            }

            toHash += "chance: " + chance;
            toHash += "autochance: " + autoChance;

            return toHash.hashCode();
        }
    }

    private Map<String, List<ChanceFlag>> flagMap = new HashMap<>();

    public FlagForChance() {
    }

    public FlagForChance(FlagForChance flag) {
        super(flag);
        for (Entry<String, List<ChanceFlag>> e : flag.flagMap.entrySet()) {
            List<ChanceFlag> flags = new ArrayList<>();

            for (ChanceFlag c : e.getValue()) {
                if (c.getFlags().isEmpty()) {
                    flags.add(new ChanceFlag((Flag) null, this, c.getChance()));
                } else {
                    Float chance;
                    if (c.isAutoChance()) {
                        chance = null;
                    } else {
                        chance = c.getChance();
                    }
                    flags.add(new ChanceFlag(c.getFlags(), this, chance));
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
     * Gets the first flag matching FlagType or the last if reverse is set to true.<br>
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

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
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
                    if (c.getFlags().isEmpty()) {
                        ErrorReporter.getInstance().warning("Flag " + getFlagType() + " already has a blank flag for this group!");
                        return false;
                    }
                }
            }

            flagChance = new ChanceFlag((Flag) null, this, chance);
            flags.add(flagChance);
            recalculateChances(group, flags);
        } else {
            String[] split = flagDeclaration.split("[:\\s]+", 2); // split by space or : char
            String flagString = split[0].trim(); // format flag name

            FlagDescriptor type = FlagFactory.getInstance().getFlagByName(flagString); // Find the current flag

            if (type == null) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has unknown flag type: " + flagString);
                return false;
            }

            if (type.hasBit(FlagBit.NO_FOR)) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + "'s flag " + flagString + " can not be used with this!");
                return false;
            }

            if (flags == null) {
                flags = new ArrayList<>();
                flagMap.put(group, flags);
            } else {
                if (appendFlag) {
                    // Get the last added FlagChance
                    if (!flags.isEmpty()) {
                        flagChance = flags.get(flags.size() - 1);
                    }
                }
            }

            Flag flag = type.createFlagClass();
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
                            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " already has 100% chance for '" + group + "' group!", "You can't add more flags to it until you reduce the chance of one or more flag.");
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

                flagChance = new ChanceFlag(flag, this, chance);
            } else {
                flagChance.addFlag(flag, this);
            }

            if (split.length > 1) {
                value = split[1].trim();
            } else {
                value = null;
            }

            // make sure the flag can be added to this flag list
            if (!flag.validateParse(value, restrictedBit)) {
                return false;
            }

            // check if parsed flag had valid values and needs to be added to flag list
            if (!flag.onParse(value, fileName, lineNum, restrictedBit)) {
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
                        trigger(c.getFlags(), a, method);
                    }
                }
            } else {
                // grouped flags, get one flag
                float random = RecipeManager.random.nextFloat() * 100;
                float chance = 0;

                for (ChanceFlag c : flags) {
                    chance += c.getChance();
                    if (chance >= random) {
                        trigger(c.getFlags(), a, method);
                        break;
                    }
                }
            }
        }
    }

    private void trigger(List<Flag> flags, Args a, char method) {
        if (flags == null || flags.isEmpty()) {
            return;
        }

        for (Flag flag : flags) {
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

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Map.Entry<String, List<ChanceFlag>> entry : flagMap.entrySet()) {
            toHash += entry.getKey();

            for (ChanceFlag flag : entry.getValue()) {
                toHash += flag.hashCode();
            }
        }

        return toHash.hashCode();
    }
}
