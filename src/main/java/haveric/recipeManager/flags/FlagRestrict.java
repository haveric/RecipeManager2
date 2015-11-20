package haveric.recipeManager.flags;

import haveric.recipeManager.Messages;

public class FlagRestrict extends Flag {
    private static final FlagType TYPE = FlagType.RESTRICT;

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Restricts the recipe for everybody.",
            "This is the player-friendly version of @remove because crafter gets a message when trying to craft the recipe.",
            "",
            "Optionally you can overwrite the default restrict message.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}",
            "{flag} <red>Access denied!", };
    }


    private String message;

    public FlagRestrict() {
    }

    public FlagRestrict(FlagRestrict flag) {
        message = flag.message;
    }

    @Override
    public FlagRestrict clone() {
        super.clone();
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
