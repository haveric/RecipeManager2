package haveric.recipeManager.flag.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagHeight extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.HEIGHT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <min or min-max> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks if crafter or furnace is at least at 'min' height and optionally at most 'max' height.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
            "In the message the following variables can be used:",
            "  {height}  = height or height range", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 200 // must be high in the sky",
            "{flag} 0-30 | <red>You need to be deep underground!", };
    }


    private int minHeight;
    private int maxHeight;
    private String failMessage;

    public FlagHeight() {
    }

    public FlagHeight(FlagHeight flag) {
        minHeight = flag.minHeight;
        maxHeight = flag.maxHeight;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagHeight clone() {
        return new FlagHeight((FlagHeight) super.clone());
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int newMinHeight) {
        minHeight = newMinHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int newMaxHeight) {
        maxHeight = newMaxHeight;
    }

    public String getHeightString() {
        String heightString = "" + getMinHeight();

        if (getMaxHeight() > getMinHeight()) {
            heightString += " - " + getMaxHeight();
        }

        return heightString;
    }

    public boolean checkHeight(int height) {
        return (height >= minHeight && height <= maxHeight);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    public boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(RMCUtil.trimExactQuotes(split[1]));
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        try {
            setMinHeight(Integer.parseInt(value));
            setMaxHeight(getMinHeight());
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid min required height number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                setMaxHeight(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid max required height number: " + value);
                return false;
            }
        }

        if ((getMinHeight() <= 0 && getMaxHeight() <= 0) || getMaxHeight() < getMinHeight()) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!a.hasLocation() || !checkHeight(a.location().getBlockY())) {
            a.addReason("flag.height", failMessage, "{height}", getHeightString());
        }
    }
}
