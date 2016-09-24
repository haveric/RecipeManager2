package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;

public class FlagNeedLevel extends Flag {

    @Override
    protected String getFlagType() {
        return FlagType.NEED_LEVEL;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <min or min-max> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks if crafter has at least 'min' levels and optionally at most 'max' levels.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
            "In the message the following variables can be used:",
            "  {level}  = level or level range",
            "",
            "NOTE: This is for experience levels, for experience points use " + FlagType.NEED_EXP + " or for world height use " + FlagType.HEIGHT + ".", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 1 // Requires a minimum level of 1",
            "{flag} 5-5 // Requires exactly level 5",
            "{flag} 25-100 | <red>Need level 25 to 100!", };
    }


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
        return new FlagNeedLevel((FlagNeedLevel) super.clone());
    }

    public boolean getSetBoth() {
        return setBoth;
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
        boolean isValid;

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

        setBoth = false;

        if (split.length > 1) {
            setFailMessage(split[1].trim());
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        try {
            setMinLevel(Integer.parseInt(value));
            setMaxLevel(getMinLevel());
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid min required level number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                setMaxLevel(Integer.parseInt(value));
                setBoth = true;
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid max required level number: " + value);
                return false;
            }
        }

        if ((getMinLevel() <= 0 && getMaxLevel() <= 0) || getMaxLevel() < getMinLevel()) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!a.hasPlayer() || !checkLevel(a.player().getLevel())) {
            a.addReason("flag.needlevel", failMessage, "{level}", getLevelString());
        }
    }
}
