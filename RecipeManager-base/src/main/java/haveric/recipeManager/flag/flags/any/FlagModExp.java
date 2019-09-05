package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.ToolsExp;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.entity.Player;

public class FlagModExp extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MOD_EXP;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [modifier]<amount> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Modifies crafter's experience points.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The '[modifier]' argument can be nothing at all or you can use",
            "  + (which is the same as nothing, to add)",
            "  - (to subtract)",
            "  = (to set)",
            "",
            "The '<amount>' argument must be the amount of experience to modify.",
            "The '[fail message]' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.", "For the fail message you can use the following arguments:",
            "  {amount}       = amount defined in the flag, never has modifier prefix.",
            "  {modifier}     = the modifier prefix.",
            "  {actualamount} = (only works for - modifier) the actual amount lost.",
            "",
            "NOTE: This is for total experience points, for experience levels use " + FlagType.MOD_LEVEL,
            "NOTE: This flag does not check if player has enough experience when subtracting! Use in combination with " + FlagType.NEED_EXP + " if you want to check.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 25 // gives 25 experience to crafter",
            "{flag} +25 // exactly the same as above",
            "{flag} -50 | <red>You lost {amount} exp!  // takes at most 50 experience from crafter, if he does not have that amount it will be set to 0.",
            "{flag} = 0 | <red>You lost all your experience!  // sets crafter experience to 0, that space is valid there too.", };
    }


    private char mod = '+';
    private int amount = 0;
    private String failMessage = null;

    public FlagModExp() {
    }

    public FlagModExp(FlagModExp flag) {
        mod = flag.mod;
        amount = flag.amount;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagModExp clone() {
        return new FlagModExp((FlagModExp) super.clone());
    }

    public char getModifier() {
        return mod;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Set the amount, can be negative.
     *
     * @param newAmount
     */
    public void setAmount(int newAmount) {
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
    public void setAmount(char newMod, int newAmount) {
        switch (newMod) {
            case '-':
            case '=':
            case '+':
                break;
            default:
                throw new IllegalArgumentException("mod can only be '+', '-', '=' !");
        }

        if (newMod != '=' && newAmount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-' !");
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

        int newAmount;

        try {
            newAmount = Integer.parseInt(value);
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
            addResultLore(a, "Mod Exp: " + getModifier() + " " + getAmount());
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (mod != '=' && amount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-' !");
        }

        if (!a.hasPlayer()) {
            a.addCustomReason("Need a player!");
            return;
        }

        Player p = a.player();
        int exp = 0;

        switch (mod) {
            case '+':
                exp = ToolsExp.getTotalExperience(p) + amount;

                a.addEffect("flag.modexp.add", failMessage, "{amount}", amount, "{modifier}", mod);

                break;
            case '-':
                exp = Math.max(ToolsExp.getTotalExperience(p) - amount, 0);

                a.addEffect("flag.modexp.sub", failMessage, "{amount}", amount, "{modifier}", mod, "{actualamount}", exp);

                break;
            case '=':
                exp = Math.max(amount, 0);

                a.addEffect("flag.modexp.set", failMessage, "{amount}", amount, "{modifier}", mod);

                break;
            default:
                break;
        }

        ToolsExp.setTotalExperience(p, exp);
    }

    /*
    @Override
    public List<String> information() {
        List<String> list = new ArrayList<String>(1);

        switch(mod) {
           case '+':
               list.add(MessagesOld.FLAG_MODEXP_ADD.get("{amount}", amount, "{modifier}", mod));
               break;
           case '-':
               list.add(MessagesOld.FLAG_MODEXP_SUB.get("{amount}", amount, "{modifier}", mod, "{actualamount}", amount));
               break;
           case '=':
               list.add(MessagesOld.FLAG_MODEXP_SET.get("{amount}", amount, "{modifier}", mod));
               break;
        }

        return list; }
    */

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "mod: " + mod;
        toHash += "amount: " + amount;
        toHash += "failMessage: " + failMessage;

        return toHash.hashCode();
    }
}
