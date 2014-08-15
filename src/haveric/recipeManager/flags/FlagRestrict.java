package haveric.recipeManager.flags;

import haveric.recipeManager.Messages;

public class FlagRestrict extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.RESTRICT;

        A = new String[] { "{flag} [fail message]", };

        D = new String[] { "Restricts the recipe to everybody.", "This is the player-friendly version of @remove because crafter gets a message when trying to craft the recipe.", "", "Optionally you can overwrite the default restrict message.", };

        E = new String[] { "{flag}", "{flag} <red>Access denied!", };
    }

    // Flag code

    private String message;

    public FlagRestrict() {
    }

    public FlagRestrict(FlagRestrict flag) {
        message = flag.message;
    }

    @Override
    public FlagRestrict clone() {
        return new FlagRestrict(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        message = newMessage;
    }

    @Override
    protected boolean onParse(String value) {
        setMessage(value);
        return true;
    }

    @Override
    protected void onCheck(Args a) {
        a.addReason(Messages.FLAG_RESTRICT, message);
    }
}
