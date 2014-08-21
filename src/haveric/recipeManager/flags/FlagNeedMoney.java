package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;

public class FlagNeedMoney extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.NEEDMONEY;

        A = new String[] { "{flag} <min or min-max> | [fail message]", };

        D = new String[] { "Checks if crafter has at least 'min' money and optionally at most 'max' money.",
                           "Using this flag more than once will overwrite the previous one.",
                           "",
                           "Optionally you can overwrite the fail message or you can use 'false' to hide it.",
                           "In the message the following variables can be used:",
                           "  {money}      = money or money range; formatted with currency.",
                           "",
                           "NOTE: Vault with a supported economy plugin is required for this flag to work.", };

        E = new String[] { "{flag} 0.25",
                           "{flag} 0.1 - 1000 | <red>Need {money}!", };
    }

    // Flag code

    private double minMoney;
    private double maxMoney;
    private String failMessage;

    public FlagNeedMoney() {
    }

    public FlagNeedMoney(FlagNeedMoney flag) {
        minMoney = flag.minMoney;
        maxMoney = flag.maxMoney;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagNeedMoney clone() {
        return new FlagNeedMoney(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
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
        if (!RecipeManager.getEconomy().isEnabled()) {
            moneyString = null;
        } else {
            moneyString = RecipeManager.getEconomy().getFormat(getMinMoney());

            if (getMaxMoney() > getMinMoney()) {
                moneyString += " - " + RecipeManager.getEconomy().getFormat(getMaxMoney());
            }
        }

        return moneyString;
    }

    public boolean checkMoney(double money) {
        return (money >= minMoney && money <= maxMoney);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    protected boolean onParse(String value) {
        if (!RecipeManager.getEconomy().isEnabled()) {
            ErrorReporter.warning("Flag " + getType() + " does nothing because no Vault-supported economy plugin was detected.");
        }

        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(split[1].trim());
        }

        split = split[0].split("-", 2);
        value = split[0].trim();

        try {
            setMinMoney(Double.valueOf(value));
            setMaxMoney(getMinMoney());
        } catch (NumberFormatException e) {
            ErrorReporter.error("The " + getType() + " flag has invalid min required money number: " + value);
            return false;
        }

        if (split.length > 1) {
            value = split[1].trim();

            try {
                setMaxMoney(Double.valueOf(value));
            } catch (NumberFormatException e) {
                ErrorReporter.error("The " + getType() + " flag has invalid max required money number: " + value);
                return false;
            }
        }

        if ((getMinMoney() <= 0 && getMaxMoney() <= 0) || getMaxMoney() < getMinMoney()) {
            ErrorReporter.error("The " + getType() + " flag needs min or max higher than 0 and max higher than min.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!RecipeManager.getEconomy().isEnabled()) {
            return;
        }

        if (!a.hasPlayerName() || !checkMoney(RecipeManager.getEconomy().getMoney(a.playerName()))) {
            a.addReason(Messages.FLAG_NEEDMONEY, failMessage, "{money}", getMoneyString());
        }
    }
}
