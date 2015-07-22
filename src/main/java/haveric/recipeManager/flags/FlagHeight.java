package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;

public class FlagHeight extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.HEIGHT;
    protected static final String[] A = new String[] {
        "{flag} <min or min-max> | [fail message]", };

    protected static final String[] D = new String[] {
        "Checks if crafter or furnace is at least at 'min' height and optionally at most 'max' height.",
        "Using this flag more than once will overwrite the previous one.",
        "",
        "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
        "In the message the following variables can be used:",
        "  {height}  = height or height range", };

    protected static final String[] E = new String[] {
        "{flag} 200 // must be high in the sky",
        "{flag} 0-30 | <red>You need to be deep underground!", };


    // Flag code

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
        super.clone();
        return new FlagHeight(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
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
    protected boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(split[1].trim());
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        try {
            setMinHeight(Integer.parseInt(value));
            setMaxHeight(getMinHeight());
        } catch (NumberFormatException e) {
            ErrorReporter.error("The " + getType() + " flag has invalid min required height number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                setMaxHeight(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                ErrorReporter.error("The " + getType() + " flag has invalid max required height number: " + value);
                return false;
            }
        }

        if ((getMinHeight() <= 0 && getMaxHeight() <= 0) || getMaxHeight() < getMinHeight()) {
            ErrorReporter.error("The " + getType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!a.hasLocation() || !checkHeight(a.location().getBlockY())) {
            a.addReason(Messages.FLAG_HEIGHT, failMessage, "{height}", getHeightString());
        }
    }
}
