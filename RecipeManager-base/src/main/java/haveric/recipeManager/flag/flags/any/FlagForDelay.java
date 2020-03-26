package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.*;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class FlagForDelay extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FOR_DELAY;
    }


    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <delay> @<flag declaration>",
            "{flag} @<flag declaration> // Add more flags to the previous one", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Run other flags after a delay.",
            "You can specify this flag more than once.",
            "",
            "The <delay> is the number of ticks that the <flag declarations>'s will be delayed by.",
            "The '<flag declaration>' must be a flag that will work without affecting the result.",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 5 10 " + FlagType.COMMAND + " /summon lightning_bolt ~{rand -5-5} ~ ~{rand -5-5} // Summon lightning 5 times, with a 10 tick delay between them",
        };
    }

    public class DelayFlag {
        private List<Flag> flags = new ArrayList<>();
        private int delay;

        public DelayFlag(List<Flag> newFlags, int newDelay) {
            flags.addAll(newFlags);
            delay = newDelay;
        }

        public DelayFlag(Flag flag, int newDelay) {
            flags.add(flag);
            delay = newDelay;
        }

        public List<Flag> getFlags() {
            return flags;
        }

        public void addFlag(Flag flag) {
            flags.add(flag);
        }

        public int getDelay() {
            return delay;
        }

        @Override
        public int hashCode() {
            String toHash = "";
            for (Flag flag : flags) {
                toHash += "flag: " + flag.hashCode();
            }

            toHash += "delay: " + delay;

            return toHash.hashCode();
        }
    }

    private List<DelayFlag> delayFlags = new ArrayList<>();

    public FlagForDelay() {

    }

    public FlagForDelay(FlagForDelay flag) {
        for (DelayFlag delayFlag : flag.delayFlags) {
            List<Flag> flags = new ArrayList<>();
            for (Flag f : delayFlag.flags) {
                flags.add(f.clone());
            }

            delayFlags.add(new DelayFlag(flags, delayFlag.delay));
        }
    }

    @Override
    public FlagForDelay clone() {
        return new FlagForDelay((FlagForDelay) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        int flagIndex = value.indexOf('@');

        if (flagIndex == -1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has no <flag declaration>.");
        }

        String beforeFlag = value.substring(0, flagIndex).trim();
        if (beforeFlag.isEmpty()) {
            if (delayFlags.isEmpty()) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs <delay> declared on initial declaration.");
            } else {
                Flag flag = getFlag(value.substring(flagIndex));
                if (flag == null) {
                    return false;
                }

                DelayFlag delayFlag = delayFlags.get(delayFlags.size() - 1); // Get last delay flag
                delayFlag.addFlag(flag);
            }
        } else {
            int delay;
            try {
                delay = Integer.parseInt(beforeFlag);
            } catch (NumberFormatException e) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid <delay> value: " + beforeFlag);
            }

            if (delay <= 0) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid <delay> value: " + delay + ". Value must be > 0");
            }

            Flag flag = getFlag(value.substring(flagIndex));
            if (flag == null) {
                return false;
            }

            delayFlags.add(new DelayFlag(flag, delay));
        }

        return true;
    }

    private Flag getFlag(String flagDeclaration) {
        String[] split = flagDeclaration.split("[:\\s]+", 2); // split by space or : char
        String flagString = split[0].trim(); // format flag name

        FlagDescriptor type = FlagFactory.getInstance().getFlagByName(flagString); // Find the current flag

        if (type == null) {
            ErrorReporter.getInstance().error("Flag " + type.getNameDisplay() + " has unknown flag type: " + flagString);
            return null;
        }

        if (type.hasBit(FlagBit.NO_FOR) || type.hasBit(FlagBit.NO_DELAY) || type.hasBit(FlagBit.RECIPE) || type.hasBit(FlagBit.RESULT)) {
            ErrorReporter.getInstance().error("Flag " + type.getNameDisplay() + "'s flag " + flagString + " can not be used with this!");
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
        if (!flag.validateParse(value)) {
            return null;
        }

        // check if parsed flag had valid values and needs to be added to flag list
        if (!flag.onParse(value, sourceFileName, sourceLineNum)) {
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
        for (DelayFlag delayFlag : delayFlags) {
            Bukkit.getScheduler().runTaskLater(RecipeManager.getPlugin(), () -> trigger(delayFlag.getFlags(), a, method), delayFlag.getDelay());
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

        toHash += "DelayFlags: ";
        for (DelayFlag delayFlag : delayFlags) {
            toHash += delayFlag.hashCode();
        }

        return toHash.hashCode();
    }
}
