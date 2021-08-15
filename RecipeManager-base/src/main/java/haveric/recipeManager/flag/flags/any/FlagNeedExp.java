package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.tools.ToolsExp;
import haveric.recipeManager.common.util.RMCUtil;

public class FlagNeedExp extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.NEED_EXP;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <min or min-max> | [message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
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
            "NOTE: This is for total experience points, for experience levels use " + FlagType.NEED_LEVEL, };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 100 // player needs to have at least 100 experience to craft",
            "{flag} 250-250 // player needs to have exactly 250 experience to craft",
            "{flag} 0-500 // player can only craft if he has between 0 and 500 experience",
            "{flag} 1000 | <red>Need {exp} exp!", };
    }


    private int minExp;
    private int maxExp;
    private String failMessage;
    private boolean setBoth = false;

    public FlagNeedExp() {
    }

    public FlagNeedExp(FlagNeedExp flag) {
        super(flag);
        minExp = flag.minExp;
        maxExp = flag.maxExp;
        failMessage = flag.failMessage;
        setBoth = flag.setBoth;
    }

    @Override
    public FlagNeedExp clone() {
        return new FlagNeedExp((FlagNeedExp) super.clone());
    }

    public boolean getSetBoth() {
        return setBoth;
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
        String expString = "" + minExp;

        if (maxExp > minExp) {
            expString += " - " + maxExp;
        }
        return expString;
    }

    public boolean checkExp(int exp) {
        boolean isValid = exp >= minExp;

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
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|");

        setBoth = false;
        if (split.length > 1) {
            failMessage = RMCUtil.trimExactQuotes(split[1]);
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        if (value.length() > String.valueOf(Integer.MAX_VALUE).length()) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has min exp value that is too long: " + value, "Value for integers can be between " + RMCUtil.printNumber(Integer.MIN_VALUE) + " and " + RMCUtil.printNumber(Integer.MAX_VALUE) + ".");
        }

        try {
            minExp = Integer.parseInt(value);
            maxExp = minExp;
        } catch (NumberFormatException e) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid min req exp number: " + value);
        }

        if (split.length > 1) {
            value = split[1].trim();

            if (value.length() > String.valueOf(Integer.MAX_VALUE).length()) {
                return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has max exp value that is too long: " + value, "Value for integers can be between " + RMCUtil.printNumber(Integer.MIN_VALUE) + " and " + RMCUtil.printNumber(Integer.MAX_VALUE) + ".");
            }

            try {
                maxExp = Integer.parseInt(value);
                setBoth = true;
            } catch (NumberFormatException e) {
                return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid max req exp number: " + value);
            }
        }

        if ((minExp <= 0 && maxExp <= 0) || maxExp < minExp) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag needs min or max higher than 0 and max higher than min.");
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!a.hasPlayer() || !checkExp(ToolsExp.getTotalExperience(a.player()))) {
            a.addReason("flag.needexp.checkmessage", failMessage, "{exp}", getExpString(), "{minexp}", minExp, "{maxexp}", maxExp, "{playerexp}", ToolsExp.getTotalExperience(a.player()));
        }
    }

    @Override
    public void onPrepare(Args a) {
        if (a.hasPlayer() && canAddMeta(a)) {
            String message = "flag.needexp.preparelore.";
            if (setBoth) {
                message += "exact";
            } else {
                message += "default";
            }

            if (maxExp > minExp) {
                message += "both";
            }

            addResultLore(a, Messages.getInstance().parse(message, "{exp}", getExpString(), "{minexp}", minExp, "{maxexp}", maxExp, "{playerexp}", ToolsExp.getTotalExperience(a.player())));
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "minExp: " + minExp;
        toHash += "maxExp: " + maxExp;
        toHash += "failMessage: " + failMessage;
        toHash += "setBoth: " + setBoth;

        return toHash.hashCode();
    }
}
