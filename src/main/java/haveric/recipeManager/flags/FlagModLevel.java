package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.entity.Player;

public class FlagModLevel extends Flag {

    @Override
    protected String getFlagType() {
        return FlagType.MOD_LEVEL;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [modifier]<number> | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Modifies crafter's level.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The '[modifier]' argument can be nothing at all or you can use",
            "  + (which is the same as nothing, to add)",
            "  - (to subtract)",
            "  = (to set)",
            "",
            "The '<number>' argument must be the amount of levels to modify.",
            "The '[fail message]' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.",
            "For the fail message you can use the following arguments:",
            "  {amount}       = amount defined in the flag, never has modifier prefix.",
            "  {modifier}     = the modifier prefix.",
            "  {actualamount} = (only works for - modifier) the actual amount lost.",
            "",
            "NOTE: This is for experience levels, for experience points use " + FlagType.MOD_EXP,
            "NOTE: This flag does not check if player has enough levels when subtracting! Use in combination with " + FlagType.NEED_LEVEL + " if you want to check.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 1 // gives 1 level to crafter",
            "{flag} +1 // exactly the same as above",
            "{flag} -2 | <red>You lost {amount} levels.  // takes at most 2 levels from crafter, if he does not have that amount it will be set to 0.",
            "{flag} = 0 | <red>You've been set to level 0!  // sets crafter's level to 0, that space is valid there too.", };
    }


    private char mod = '+';
    private int amount = 0;
    private String failMessage;

    public FlagModLevel() {
    }

    public FlagModLevel(FlagModLevel flag) {
        mod = flag.mod;
        amount = flag.amount;
        failMessage = flag.failMessage;
    }

    @Override
    public FlagModLevel clone() {
        return new FlagModLevel((FlagModLevel) super.clone());
    }

    /**
     * @return type of modifier (+, -, =)
     */
    public char getModifier() {
        return mod;
    }

    /**
     * The amount modified.<br>
     * Use {@link #getModifier()} to get the type of modifier (+, -, =).
     *
     * @return amount
     */
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
            setAmount('-', amount);
        } else {
            setAmount('+', amount);
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

    public void setFailMessage(String newFailMessage) {
        failMessage = newFailMessage;
    }

    @Override
    public String getResultLore() {
        return "Mod Level: " + getModifier() + " " + getAmount();
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("\\|");

        if (split.length > 1) {
            setFailMessage(split[1].trim());
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
            return ErrorReporter.getInstance().error("The " + getFlagType() + " flag has level value that is too long: " + value, "Value for integers can be between " + RMCUtil.printNumber(Integer.MIN_VALUE) + " and " + RMCUtil.printNumber(Integer.MAX_VALUE) + ".");
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
    protected void onCrafted(Args a) {
        if (mod != '=' && amount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-'!");
        }

        if (!a.hasPlayer()) {
            a.addCustomReason("Need a player!");
            return;
        }

        Player p = a.player();

        switch (mod) {
            case '+':
                p.giveExpLevels(amount);

                a.addEffect("flag.modlevel.add", failMessage, "{amount}", amount, "{modifier}", mod);

                break;
            case '-':
                int level = Math.max(p.getLevel() - amount, 0);

                p.setLevel(level);

                a.addEffect("flag.modlevel.sub", failMessage, "{amount}", amount, "{modifier}", mod, "{actualamount}", level);

                break;
            case '=':
                p.setLevel(amount);

                a.addEffect("flag.modlevel.set", failMessage, "{amount}", amount, "{modifier}", mod);

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
                list.add(MessagesOld.FLAG_MODLEVEL_ADD.get("{amount}", amount, "{modifier}", mod));
                break;
            case '-':
                list.add(MessagesOld.FLAG_MODLEVEL_SUB.get("{amount}", amount, "{modifier}", mod, "{actualamount}", amount));
                break;
            case '=':
                list.add(MessagesOld.FLAG_MODLEVEL_SET.get("{amount}", amount, "{modifier}", mod));
                break;
        }

        return list;
    }
    */
}
