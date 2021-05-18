package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.Econ;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;

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
            "  {money}      = money or money range",
            "  {minmoney}   = defined min money range.",
            "  {maxmoney}   = defined max money range.",
            "  {fmoney}      = money or money range; formatted with currency.",
            "  {fminmoney}  = defined min money range; formatted with currency.",
            "  {fmaxmoney}  = defined max money range; formatted with currency.",
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
        super(flag);
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
            moneyString = "" + minMoney;

            if (maxMoney > minMoney) {
                moneyString += " - " + maxMoney;
            }
        } else {
            moneyString = null;
        }

        return moneyString;
    }

    public String getFormattedMoneyString() {
        String moneyString;
        if (Econ.getInstance().isEnabled()) {
            moneyString = getFormattedMinMoney();

            if (maxMoney > minMoney) {
                moneyString += " - " + getFormattedMaxMoney();
            }
        } else {
            moneyString = null;
        }

        return moneyString;
    }

    public String getFormattedMinMoney() {
        return Econ.getInstance().getFormat(minMoney);
    }

    public String getFormattedMaxMoney() {
        return Econ.getInstance().getFormat(maxMoney);
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
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (!Econ.getInstance().isEnabled()) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " does nothing because no Vault-supported economy plugin was detected.");
        }

        setBoth = false;

        String[] split = value.split("\\|");

        if (split.length > 1) {
            failMessage = RMCUtil.trimExactQuotes(split[1]);
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        try {
            minMoney = Double.parseDouble(value);
            maxMoney = minMoney;
        } catch (NumberFormatException e) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid min required money number: " + value);
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                maxMoney = Double.parseDouble(value);
                setBoth = true;
            } catch (NumberFormatException e) {
                return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid max required money number: " + value);
            }
        }

        if ((minMoney <= 0 && maxMoney <= 0) || maxMoney < minMoney) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag needs min or max higher than 0 and max higher than min.");
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!Econ.getInstance().isEnabled()) {
            return;
        }

        if (!a.hasPlayerUUID() || !checkMoney(Econ.getInstance().getMoney(a.playerUUID()))) {
            a.addReason("flag.needmoney.checkmessage", failMessage, "{money}", getMoneyString(), "{fmoney}", getFormattedMoneyString(), "{minmoney}", minMoney, "{maxmoney}", maxMoney, "{fminmoney}", getFormattedMinMoney(), "{fmaxmoney}", getFormattedMaxMoney());
        }
    }

    @Override
    public void onPrepare(Args a) {
        if (canAddMeta(a)) {
            String message = "flag.needmoney.preparelore.";
            if (setBoth) {
                message += "exact";
            } else {
                message += "default";
            }

            if (maxMoney > minMoney) {
                message += "both";
            }

            addResultLore(a, Messages.getInstance().parse(message, "{money}", getMoneyString(), "{fmoney}", getFormattedMoneyString(), "{minmoney}", minMoney, "{maxmoney}", maxMoney, "{fminmoney}", getFormattedMinMoney(), "{fmaxmoney}", getFormattedMaxMoney()));
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "minMoney: " + minMoney;
        toHash += "maxMoney: " + maxMoney;
        toHash += "failMessage: " + failMessage;
        toHash += "setBoth: " + setBoth;

        return toHash.hashCode();
    }
}
