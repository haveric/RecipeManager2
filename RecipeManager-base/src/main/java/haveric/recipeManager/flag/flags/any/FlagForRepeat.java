package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.*;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class FlagForRepeat extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FOR_REPEAT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <times to repeat> <delay per repeat> @<flag declaration>",
            "{flag} <times to repeat> @<flag declaration> // Defaults to a <delay per repeat> of 0",
            "{flag} @<flag declaration> // Add more flags to the previous one", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Run other flags multiple times with an optional delay between them.",
            "You can specify this flag more than once.",
            "",
            "The <times to repeat> is the number of times the contained flags will be repeated.",
            "The <delay per repeat> is the number of ticks that each repeat after the first will be delayed by.",
            "The '<flag declaration>' must be a flag that will work without affecting the result.",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 5 10 " + FlagType.COMMAND + " /summon lightning_bolt ~{rand -5-5} ~ ~{rand -5-5} // Summon lightning 5 times, with a 10 tick delay between them",
        };
    }

    public static class RepeatFlag {
        private List<Flag> flags = new ArrayList<>();
        private int repeatTimes;
        private int delayPerRepeat;

        public RepeatFlag(List<Flag> newFlags, int newRepeatTimes, int newDelayPerRepeat) {
            flags.addAll(newFlags);
            repeatTimes = newRepeatTimes;
            delayPerRepeat = newDelayPerRepeat;
        }

        public RepeatFlag(Flag flag, int newRepeatTimes, int newDelayPerRepeat) {
            flags.add(flag);
            repeatTimes = newRepeatTimes;
            delayPerRepeat = newDelayPerRepeat;
        }

        public List<Flag> getFlags() {
            return flags;
        }

        public void addFlag(Flag flag) {
            flags.add(flag);
        }

        public int getRepeatTimes() {
            return repeatTimes;
        }

        public int getDelayPerRepeat() {
            return delayPerRepeat;
        }

        @Override
        public int hashCode() {
            String toHash = "";
            for (Flag flag : flags) {
                 toHash += "flag: " + flag.hashCode();
            }

            toHash += "repeatTimes: " + repeatTimes;
            toHash += "delayPerRepeat: " + delayPerRepeat;

            return toHash.hashCode();
        }
    }

    private List<RepeatFlag> repeatFlags = new ArrayList<>();

    public FlagForRepeat() {

    }

    public FlagForRepeat(FlagForRepeat flag) {
        super(flag);
        for (RepeatFlag repeatFlag : flag.repeatFlags) {
            List<Flag> flags = new ArrayList<>();
            for (Flag f : repeatFlag.flags) {
                flags.add(f.clone());
            }

            repeatFlags.add(new RepeatFlag(flags, repeatFlag.repeatTimes, repeatFlag.delayPerRepeat));
        }
    }

    @Override
    public FlagForRepeat clone() {
        return new FlagForRepeat((FlagForRepeat) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        int flagIndex = value.indexOf('@');

        if (flagIndex == -1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has no <flag declaration>.");
        }

        String beforeFlag = value.substring(0, flagIndex).trim();
        if (beforeFlag.isEmpty()) {
            if (repeatFlags.isEmpty()) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs <times to repeat> and <delay per repeat> declared on initial declaration.");
            } else {
                Flag flag = getFlag(value.substring(flagIndex), restrictedBit);
                if (flag == null) {
                    return false;
                }

                RepeatFlag repeatFlag = repeatFlags.get(repeatFlags.size() - 1); // Get last repeat flag
                repeatFlag.addFlag(flag);
            }
        } else {
            String[] args = beforeFlag.split(" ");

            int numRepeat;
            try {
                numRepeat = Integer.parseInt(args[0].trim());
            } catch (NumberFormatException e) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid <times to repeat> value: " + args[0]);
            }

            if (numRepeat <= 1) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid <times to repeat> value: " + numRepeat + ". Value must be > 1.");
            }

            int delay = 0;
            if (args.length > 1) {
                try {
                    delay = Integer.parseInt(args[1].trim());
                } catch (NumberFormatException e) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid <delay per repeat> value: " + args[1] + ". Defaulting to 0.");
                }
            }

            if (delay <= 0) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid <delay per repeat> value: " + delay + ". Defaulting to 0.");
            }

            Flag flag = getFlag(value.substring(flagIndex), restrictedBit);
            if (flag == null) {
                return false;
            }

            repeatFlags.add(new RepeatFlag(flag, numRepeat, delay));
        }

        return true;
    }

    private Flag getFlag(String flagDeclaration, int restrictedBit) {
        String[] split = flagDeclaration.split("[:\\s]+", 2); // split by space or : char
        String flagString = split[0].trim(); // format flag name

        FlagDescriptor type = FlagFactory.getInstance().getFlagByName(flagString); // Find the current flag

        if (type == null) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " has unknown flag type: " + flagString);
            return null;
        }

        if (type.hasBit(FlagBit.NO_FOR) || type.hasBit(FlagBit.NO_DELAY)) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + "'s flag " + flagString + " can not be used with this!");
            return null;
        }

        Flag flag = type.createFlagClass();
        flag.setFlagsContainer(getFlagsContainer()); // set container before hand to allow checks

        String value;
        if (split.length > 1) {
            value = split[1].trim();
        } else {
            value = null;
        }

        // make sure the flag can be added to this flag list
        if (!flag.validateParse(value, restrictedBit)) {
            return null;
        }

        // check if parsed flag had valid values and needs to be added to flag list
        if (!flag.onParse(value, sourceFileName, sourceLineNum, restrictedBit)) {
            return null;
        }

        return flag;
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
        for (RepeatFlag repeatFlag : repeatFlags) {
            int numRepeat = repeatFlag.getRepeatTimes();
            int delay = repeatFlag.getDelayPerRepeat();

            for (int i = 0; i < numRepeat; i++) {
                if (delay == 0) {
                    trigger(repeatFlag.getFlags(), a, method);
                } else {
                    Bukkit.getScheduler().runTaskLater(RecipeManager.getPlugin(), () -> trigger(repeatFlag.getFlags(), a, method), (long) delay * i);
                }
            }
        }
    }

    private void trigger(List<Flag> flags, Args a, char method) {
        if (flags == null) {
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

        toHash += "RepeatFlags: ";
        for (RepeatFlag repeatFlag : repeatFlags) {
            toHash += repeatFlag.hashCode();
        }

        return toHash.hashCode();
    }
}
