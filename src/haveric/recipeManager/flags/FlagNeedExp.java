package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsExp;

public class FlagNeedExp extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.NEEDEXP;

        A = new String[] { "{flag} <min or min-max> | [message]", };

        D = new String[] { "Checks if crafter has at least 'min' experience and optionally at most 'max' experience.",
                           "Using this flag more than once will overwrite the previous one.",
                           "",
                           "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
                           "In the message the following variables can be used:",
                           "  {exp}    = exp or exp range.",
                           "  {minexp} = defined min exp range.",
                           "  {maxexp} = defined max exp range.",
                           "",
                           "NOTE: This is for total experience points, for experience levels use " + FlagType.NEEDLEVEL.toString(), };

        E = new String[] { "{flag} 100 // player needs to have at least 100 experience to craft",
                           "{flag} 0-500 // player can only craft if he has between 0 and 500 experience",
                           "{flag} 1000 | <red>Need {exp} exp!", };
    }

    // Flag code

    private int minExp;
    private int maxExp;
    private String failMessage;

    public FlagNeedExp() {
    }

    public FlagNeedExp(FlagNeedExp flag) {
        minExp = flag.minExp;
        maxExp = flag.maxExp;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagNeedExp clone() {
        return new FlagNeedExp(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public int getMinExp() {
        return minExp;
    }

    public void setMinExp(int newMinExp) {
        minExp = newMinExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int newMaxExp) {
        maxExp = newMaxExp;
    }

    public String getExpString() {
        String expString = "" + getMinExp();

        if (maxExp > minExp) {
            expString += " - " + getMaxExp();
        }
        return expString;
    }

    public boolean checkExp(int exp) {
        return exp >= minExp && (maxExp > minExp ? exp <= maxExp : true);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(split[1].trim());
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        if (value.length() > String.valueOf(Integer.MAX_VALUE).length()) {
            ErrorReporter.error("The " + getType() + " flag has min exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
            return false;
        }

        try {
            setMinExp(Integer.valueOf(value));
            setMaxExp(getMinExp());
        } catch (NumberFormatException e) {
            ErrorReporter.error("The " + getType() + " flag has invalid min req exp number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            if (value.length() > String.valueOf(Integer.MAX_VALUE).length()) {
                ErrorReporter.error("The " + getType() + " flag has max exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
                return false;
            }

            try {
                setMaxExp(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                ErrorReporter.error("The " + getType() + " flag has invalid max req exp number: " + value);
                return false;
            }
        }

        if ((getMinExp() <= 0 && getMaxExp() <= 0) || getMaxExp() < getMinExp()) {
            ErrorReporter.error("The " + getType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!a.hasPlayer() || !checkExp(ToolsExp.getTotalExperience(a.player()))) { // p.getTotalExperience()
            a.addReason(Messages.FLAG_NEEDEXP, failMessage, "{exp}", getExpString(), "{minexp}", getMinExp(), "{maxexp}", getMaxExp());
        }
    }
}
