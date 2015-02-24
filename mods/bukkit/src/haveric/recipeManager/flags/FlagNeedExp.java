package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsExp;

public class FlagNeedExp extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.NEEDEXP;
    protected static final String[] A = new String[] {
        "{flag} <min or min-max> | [message]", };

    protected static final String[] D = new String[] {
        "Checks if crafter has at least 'min' experience and optionally at most 'max' experience.",
        "Using this flag more than once will overwrite the previous one.",
        "",
        "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
        "In the message the following variables can be used:",
        "  {exp}    = exp or exp range.",
        "  {minexp} = defined min exp range.",
        "  {maxexp} = defined max exp range.",
        "  {playerexp} = player's current experience.",
        "",
        "NOTE: This is for total experience points, for experience levels use " + FlagType.NEEDLEVEL.toString(), };

    protected static final String[] E = new String[] {
        "{flag} 100 // player needs to have at least 100 experience to craft",
        "{flag} 250-250 // player needs to have exactly 250 experience to craft",
        "{flag} 0-500 // player can only craft if he has between 0 and 500 experience",
        "{flag} 1000 | <red>Need {exp} exp!", };


    // Flag code

    private int minExp;
    private int maxExp;
    private String failMessage;
    private boolean setBoth = false;

    public FlagNeedExp() {
    }

    public FlagNeedExp(FlagNeedExp flag) {
        minExp = flag.minExp;
        maxExp = flag.maxExp;
        failMessage = flag.failMessage;
        setBoth = flag.setBoth;
    }

    @Override
    public FlagNeedExp clone() {
        super.clone();
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
        boolean isValid = false;

        isValid = exp >= minExp;

        if (isValid && setBoth) {
            isValid = exp <= maxExp;
        }

        return isValid;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    public String getResultLore() {
        String resultString = "Need ";

        if (setBoth) {
            resultString += "exact ";
        }

        resultString += "exp: " + getMinExp();

        if (getMaxExp() > getMinExp()) {
            resultString += "-" + getMaxExp();
        }

        return resultString;
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
            setMinExp(Integer.parseInt(value));
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
                setMaxExp(Integer.parseInt(value));
                setBoth = true;
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
        if (!a.hasPlayer() || !checkExp(ToolsExp.getTotalExperience(a.player()))) {
            a.addReason(Messages.FLAG_NEEDEXP, failMessage, "{exp}", getExpString(), "{minexp}", getMinExp(), "{maxexp}", getMaxExp(), "{playerexp}", ToolsExp.getTotalExperience(a.player()));
        }
    }
}
