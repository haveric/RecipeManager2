package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.Econ;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagModMoney extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MOD_MONEY;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [modifier]<float number> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Modifies crafter's money.", "Using this flag more than once will overwrite the previous one.",
            "",
            "The '[modifier]' argument can be nothing at all or you can use",
            "  + (which is the same as nothing, to add)",
            "  - (to subtract)",
            "  = (to set)",
            "",
            "The '<number>' argument must be the amount of money to modify.",
            "The '[fail message]' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.",
            "For the fail message you can use the following arguments:",
            "  {amount}       = amount defined in the flag, never has modifier prefix.",
            "  {modifier}     = the modifier prefix.",
            "",
            "NOTE: Vault with a supported economy plugin is required for this flag to work.",
            "NOTE: This flag does not check if player has enough money when subtracting! Use in combination with " + FlagType.NEED_MONEY + " if you want to check.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 0.5 // gives 0.5 currency or 50 minor currency money to crafter",
            "{flag} +0.5 // exactly the same as above",
            "{flag} -2.5 | <red>You lost {money}!  // takes at most 2.5 currency from crafter, if he does not have that amount it will be set to 0.",
            "{flag} = 0 | <red>You lost all your money!  // sets crafter's money to 0, that space is valid there too.", };
    }


    private char mod = '+';
    private float amount = 0.0f;
    private String failMessage = null;

    public FlagModMoney() {
    }

    public FlagModMoney(FlagModMoney flag) {
        mod = flag.mod;
        amount = flag.amount;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagModMoney clone() {
        return new FlagModMoney((FlagModMoney) super.clone());
    }

    public char getModifier() {
        return mod;
    }

    public float getAmount() {
        return amount;
    }

    /**
     * Set the amount, can be negative.
     *
     * @param newAmount
     */
    public void setAmount(float newAmount) {
        if (newAmount < 0) {
            setAmount('-', newAmount);
        } else {
            setAmount('+', newAmount);
        }
    }

    /**
     * @param newMod
     *            can be '+', '-', '='
     * @param newAmount
     *            the amount, forced as positive number
     */
    public void setAmount(char newMod, float newAmount) {
        switch (newMod) {
            case '-':
            case '=':
            case '+':
                break;
            default:
                throw new IllegalArgumentException("mod can only be '+', '-', '='!");
        }

        if (newMod != '=' && newAmount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-'!");
        }

        mod = newMod;
        amount = Math.abs(newAmount);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        failMessage = message;
    }

    @Override
    public boolean onParse(String value) {
        if (!Econ.getInstance().isEnabled()) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " does nothing because no Vault-supported economy plugin was detected.");
        }

        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(RMCUtil.trimExactQuotes(split[1]));
        }

        value = split[0].trim();
        char newMod = value.charAt(0);

        switch (newMod) {
            case '-':
            case '=':
            case '+':
                value = value.substring(1).trim(); // remove modifier from string
                break;
            default:
                newMod = '+'; // set default modifier if it's not defined
        }

        if (value.length() > String.valueOf(Integer.MAX_VALUE).length()) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has exp value that is too long: " + value, "Value for integers can be between " + RMCUtil.printNumber(Integer.MIN_VALUE) + " and " + RMCUtil.printNumber(Integer.MAX_VALUE) + ".");
        }

        float newAmount;

        try {
            newAmount = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has invalid number: " + value);
        }

        if (newMod != '=' && newAmount == 0) {
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag can only have 0 amount for = modifier, not for + or -");
        }

        setAmount(newMod, newAmount);

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (canAddMeta(a)) {
            addResultLore(a, "Mod Money: " + getModifier() + " " + getAmount());
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (mod != '=' && amount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-'!");
        }

        if (!Econ.getInstance().isEnabled()) {
            return;
        }

        if (!a.hasPlayerUUID()) {
            a.addCustomReason("Need a player UUID!");
            return;
        }

        switch (mod) {
            case '+':
                Econ.getInstance().modMoney(a.playerUUID(), amount);

                a.addEffect("flag.modmoney.add", failMessage, "{money}", Econ.getInstance().getFormat(amount), "{amount}", amount, "{modifier}", mod);

                break;
            case '-':
                Econ.getInstance().modMoney(a.playerUUID(), -amount);

                a.addEffect("flag.modmoney.sub", failMessage, "{money}", Econ.getInstance().getFormat(amount), "{amount}", amount, "{modifier}", mod);

                break;
            case '=':
                double money = Econ.getInstance().getMoney(a.playerUUID());

                Econ.getInstance().modMoney(a.playerUUID(), -money);

                if (amount > 0) {
                    Econ.getInstance().modMoney(a.playerUUID(), amount);
                }

                a.addEffect("flag.modmoney.set", failMessage, "{money}", Econ.getInstance().getFormat(amount), "{amount}", amount, "{modifier}", mod);

                break;
            default:
                break;
        }
    }

    /*
    @Override
    public List<String> information() {
        List<String> list = new ArrayList<String>(1);

        switch(mod) {
            case '+':
                list.add(MessagesOld.FLAG_MODMONEY_ADD.get("{amount}", amount, "{modifier}", mod));
                break;
            case '-':
                list.add(MessagesOld.FLAG_MODMONEY_SUB.get("{amount}", amount, "{modifier}", mod));
                break;
            case '=':
                list.add(MessagesOld.FLAG_MODMONEY_SET.get("{amount}", amount, "{modifier}", mod));
                break;
        }

        return list;
    }
    */
}
