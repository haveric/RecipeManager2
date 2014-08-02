package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeManager;
import ro.thehunters.digi.recipeManager.Tools;

public class FlagModMoney extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.MODMONEY;

        A = new String[] { "{flag} [modifier]<float number> | [fail message]", };

        D = new String[] { "Modifies crafter's money.", "Using this flag more than once will overwrite the previous one.", "", "The '[modifier]' argument can be nothing at all or you can use + (which is the same as nothing, to add), - (to subtract) or = (to set).", "The '<number>' argument must be the amount of money to modify.", "The '[fail message]' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.", "For the fail message you can use the following arguments:", "  {amount}       = amount defined in the flag, never has modifier prefix.", "  {modifier}     = the modifier prefix.", "", "NOTE: Vault with a supported economy plugin is required for this flag to work.", "NOTE: This flag does not check if player has enough money when subtracting! Use in combination with " + FlagType.NEEDMONEY.toString() + " if you want to check.", };

        E = new String[] { "{flag} 0.5 // gives 0.5 currency or 50 minor currency money to crafter", "{flag} +0.5 // exacly the same as above", "{flag} -2.5 | <red>You lost {money}!  // takes at most 2.5 currency from crafter, if he does not have that amount it will be set to 0.", "{flag} = 0 | <red>You lost all your money!  // sets crafter's money to 0, that space is valid there too.", };
    }

    // Flag code

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
        return new FlagModMoney(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
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
     * @param amount
     */
    public void setAmount(float amount) {
        setAmount(amount < 0 ? '-' : '+', amount);
    }

    /**
     * @param mod
     *            can be '+', '-', '='
     * @param amount
     *            the amount, forced as positive number
     */
    public void setAmount(char mod, float amount) {
        switch (mod) {
            case '-':
            case '=':
            case '+': {
                break;
            }

            default: {
                throw new IllegalArgumentException("mod can only be '+', '-', '=' !");
            }
        }

        if (mod != '=' && amount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-' !");
        }

        this.mod = mod;
        this.amount = Math.abs(amount);
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String message) {
        this.failMessage = message;
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

        value = split[0].trim();
        char mod = value.charAt(0);

        switch (mod) {
            case '-':
            case '=':
            case '+': {
                value = value.substring(1).trim(); // remove modifier from string
                break;
            }

            default: {
                mod = '+'; // set default modifier if it's not defined
            }
        }

        if (value.length() > String.valueOf(Integer.MAX_VALUE).length()) {
            return ErrorReporter.error("The " + getType() + " flag has exp value that is too long: " + value, "Value for integers can be between " + Tools.printNumber(Integer.MIN_VALUE) + " and " + Tools.printNumber(Integer.MAX_VALUE) + ".");
        }

        float amount = 0;

        try {
            amount = Float.valueOf(value);
        } catch (NumberFormatException e) {
            return ErrorReporter.error("The " + getType() + " flag has invalid number: " + value);
        }

        if (mod != '=' && amount == 0) {
            return ErrorReporter.error("The " + getType() + " flag can only have 0 amount for = modifier, not for + or -");
        }

        setAmount(mod, amount);

        return true;
    }

    @Override
    protected void onCrafted(Args a) {
        if (mod != '=' && amount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-' !");
        }

        if (!RecipeManager.getEconomy().isEnabled()) {
            return;
        }

        if (!a.hasPlayerName()) {
            a.addCustomReason("Need a player name!");
            return;
        }

        switch (mod) {
            case '+': {
                RecipeManager.getEconomy().modMoney(a.playerName(), amount);

                a.addEffect(Messages.FLAG_MODMONEY_ADD, failMessage, "{money}", RecipeManager.getEconomy().getFormat(amount), "{amount}", amount, "{modifier}", mod);

                break;
            }

            case '-': {
                RecipeManager.getEconomy().modMoney(a.playerName(), -amount);

                a.addEffect(Messages.FLAG_MODMONEY_SUB, failMessage, "{money}", RecipeManager.getEconomy().getFormat(amount), "{amount}", amount, "{modifier}", mod);

                break;
            }

            case '=': {
                double money = RecipeManager.getEconomy().getMoney(a.playerName());

                RecipeManager.getEconomy().modMoney(a.playerName(), -money);

                if (amount > 0) {
                    RecipeManager.getEconomy().modMoney(a.playerName(), amount);
                }

                a.addEffect(Messages.FLAG_MODMONEY_SET, failMessage, "{money}", RecipeManager.getEconomy().getFormat(amount), "{amount}", amount, "{modifier}", mod);

                break;
            }
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(1);
     * 
     * switch(mod) { case '+': list.add(Messages.FLAG_MODMONEY_ADD.get("{amount}", amount, "{modifier}", mod)); break; case '-': list.add(Messages.FLAG_MODMONEY_SUB.get("{amount}", amount,
     * "{modifier}", mod)); break; case '=': list.add(Messages.FLAG_MODMONEY_SET.get("{amount}", amount, "{modifier}", mod)); break; }
     * 
     * return list; }
     */
}
