package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.messages.Messages;
import org.bukkit.entity.Player;

public class FlagModLevel extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.MOD_LEVEL;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [modifier]<number> | [craft message]", };
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
            "The '[craft message]' argument is optional and can be used to overwrite the default message or you can set it to false to hide it. Message will be printed in chat.",
            "For the craft message you can use the following arguments:",
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
    private String craftMessage;

    public FlagModLevel() {
    }

    public FlagModLevel(FlagModLevel flag) {
        super(flag);
        mod = flag.mod;
        amount = flag.amount;
        craftMessage = flag.craftMessage;
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
            setAmount('-', Math.abs(newAmount));
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
                throw new IllegalArgumentException("mod can only be '+', '-', '='!");
        }

        if (newMod != '=' && newAmount == 0) {
            throw new IllegalArgumentException("The amount can not be 0 while mod is '+' or '-'!");
        }

        mod = newMod;
        amount = Math.abs(newAmount);
    }

    public String getCraftMessage() {
        return craftMessage;
    }

    public void setCraftMessage(String newCraftMessage) {
        craftMessage = newCraftMessage;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|");

        if (split.length > 1) {
            craftMessage = RMCUtil.trimExactQuotes(split[1]);
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
    public void onPrepare(Args a) {
        if (canAddMeta(a)) {
            String resultLore = "";
            switch (mod) {
                case '+':
                    resultLore = Messages.getInstance().parse("flag.modlevel.preparelore.add", "{amount}", amount, "{modifier}", mod);
                    break;
                case '-':
                    resultLore = Messages.getInstance().parse("flag.modlevel.preparelore.sub", "{amount}", amount, "{modifier}", mod);
                    break;
                case '=':
                    resultLore = Messages.getInstance().parse("flag.modlevel.preparelore.set", "{amount}", amount, "{modifier}", mod);
                    break;
                default:
                    break;
            }
            addResultLore(a, resultLore);
        }
    }

    @Override
    public void onCrafted(Args a) {
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

                a.addEffect("flag.modlevel.craftmessage.add", craftMessage, "{amount}", amount, "{modifier}", mod);

                break;
            case '-':
                int level = Math.max(p.getLevel() - amount, 0);

                p.setLevel(level);

                a.addEffect("flag.modlevel.craftmessage.sub", craftMessage, "{amount}", amount, "{modifier}", mod, "{actualamount}", level);

                break;
            case '=':
                p.setLevel(amount);

                a.addEffect("flag.modlevel.craftmessage.set", craftMessage, "{amount}", amount, "{modifier}", mod);

                break;
            default:
                break;
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "mod: " + mod;
        toHash += "amount: " + amount;
        toHash += "craftMessage: " + craftMessage;

        return toHash.hashCode();
    }
}
