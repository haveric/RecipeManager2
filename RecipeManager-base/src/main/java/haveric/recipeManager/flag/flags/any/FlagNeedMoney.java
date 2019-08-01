package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.Econ;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagNeedMoney extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.NEED_MONEY;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <min or min-max> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Checks if crafter has at least 'min' money and optionally at most 'max' money.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
            "In the message the following variables can be used:",
            "  {money}      = money or money range; formatted with currency.",
            "",
            "NOTE: Vault with a supported economy plugin is required for this flag to work.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 0.25",
            "{flag} 0.1 - 1000 | <red>Need {money}!", };
    }


    private double minMoney;
    private double maxMoney;
    private boolean setBoth = false;

    private String failMessage;

    public FlagNeedMoney() {
    }

    public FlagNeedMoney(FlagNeedMoney flag) {
        minMoney = flag.minMoney;
        maxMoney = flag.maxMoney;
        failMessage = flag.failMessage;
        setBoth = flag.setBoth;
    }

    @Override
    public FlagNeedMoney clone() {
        return new FlagNeedMoney((FlagNeedMoney) super.clone());
    }

    public boolean getSetBoth() {
        return setBoth;
    }

    public double getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(double newMinMoney) {
        minMoney = newMinMoney;
    }

    public double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(double newMaxMoney) {
        maxMoney = newMaxMoney;
    }

    public String getMoneyString() {
        String moneyString;
        if (Econ.getInstance().isEnabled()) {
            moneyString = Econ.getInstance().getFormat(getMinMoney());

            if (getMaxMoney() > getMinMoney()) {
                moneyString += " - " + Econ.getInstance().getFormat(getMaxMoney());
            }
        } else {
            moneyString = null;
        }

        return moneyString;
    }

    @Override
    public String getResultLore() {
        return "Need Money: " + getMoneyString();
    }

    public boolean checkMoney(double money) {
        boolean isValid;

        isValid = money >= minMoney;

        if (isValid && setBoth) {
            isValid = money <= maxMoney;
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
    public boolean onParse(String value) {
        if (!Econ.getInstance().isEnabled()) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " does nothing because no Vault-supported economy plugin was detected.");
        }

        setBoth = false;

        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(RMCUtil.trimExactQuotes(split[1]));
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        try {
            setMinMoney(Double.valueOf(value));
            setMaxMoney(getMinMoney());
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid min required money number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                setMaxMoney(Double.valueOf(value));
                setBoth = true;
            } catch (NumberFormatException e) {
                ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid max required money number: " + value);
                return false;
            }
        }

        if ((getMinMoney() <= 0 && getMaxMoney() <= 0) || getMaxMoney() < getMinMoney()) {
            ErrorReporter.getInstance().error("The " + getFlagType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!Econ.getInstance().isEnabled()) {
            return;
        }

        if (!a.hasPlayerUUID() || !checkMoney(Econ.getInstance().getMoney(a.playerUUID()))) {
            a.addReason("flag.needmoney", failMessage, "{money}", getMoneyString());
        }
    }
}
