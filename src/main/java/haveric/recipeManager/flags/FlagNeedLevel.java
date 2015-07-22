package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;

public class FlagNeedLevel extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.NEEDLEVEL;
    protected static final String[] A = new String[] {
        "{flag} <min or min-max> | [fail message]", };

    protected static final String[] D = new String[] {
        "Checks if crafter has at least 'min' levels and optionally at most 'max' levels.",
        "Using this flag more than once will overwrite the previous one.",
        "",
        "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
        "In the message the following variables can be used:",
        "  {level}  = level or level range",
        "",
        "NOTE: This is for experience levels, for experience points use " + FlagType.NEEDEXP.toString() + " or for world height use " + FlagType.HEIGHT + ".", };

    protected static final String[] E = new String[] {
        "{flag} 1 // Requires a minimum level of 1",
        "{flag} 5-5 // Requires exactly level 5",
        "{flag} 25-100 | <red>Need level 25 to 100!", };


    // Flag code

    private int minLevel;
    private int maxLevel;
    private boolean setBoth = false;

    private String failMessage;

    public FlagNeedLevel() {
    }

    public FlagNeedLevel(FlagNeedLevel flag) {
        minLevel = flag.minLevel;
        maxLevel = flag.maxLevel;
        failMessage = flag.failMessage;
        setBoth = flag.setBoth;
    }

    @Override
    public FlagNeedLevel clone() {
        super.clone();
        return new FlagNeedLevel(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int newMinLevel) {
        minLevel = newMinLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int newMaxLevel) {
        maxLevel = newMaxLevel;
    }

    public String getLevelString() {
        String levelString = "" + getMinLevel();

        if (getMaxLevel() > getMinLevel()) {
            levelString += " - " + getMaxLevel();
        }
        return levelString;
    }

    public boolean checkLevel(int level) {
        boolean isValid = false;

        isValid = level >= minLevel;

        if (isValid && setBoth) {
            isValid = level <= maxLevel;
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

        resultString += "level: " + getMinLevel();

        if (getMaxLevel() > getMinLevel()) {
            resultString += "-" + getMaxLevel();
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

        try {
            setMinLevel(Integer.parseInt(value));
            setMaxLevel(getMinLevel());
        } catch (NumberFormatException e) {
            ErrorReporter.error("The " + getType() + " flag has invalid min required level number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                setMaxLevel(Integer.parseInt(value));
                setBoth = true;
            } catch (NumberFormatException e) {
                ErrorReporter.error("The " + getType() + " flag has invalid max required level number: " + value);
                return false;
            }
        }

        if ((getMinLevel() <= 0 && getMaxLevel() <= 0) || getMaxLevel() < getMinLevel()) {
            ErrorReporter.error("The " + getType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!a.hasPlayer() || !checkLevel(a.player().getLevel())) {
            a.addReason(Messages.FLAG_NEEDLEVEL, failMessage, "{level}", getLevelString());
        }
    }
}
