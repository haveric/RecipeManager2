package haveric.recipeManager.flags;

public class FlagSecret extends Flag {
    private static final FlagType TYPE = FlagType.SECRET;

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [true or false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Hides the recipe or result from common info sources.",
            "Recipes are hidden from commands, books, etc.",
            "Results are also hidden from commands, books and most importantly from multiresult item display.",
            "This also means recipes/results won't give out any fail craft reasons!", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}", };
    }


    public FlagSecret() {
    }

    public FlagSecret(FlagSecret flag) {
    }

    @Override
    public FlagSecret clone() {
        super.clone();
        return new FlagSecret(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    @Override
    protected boolean onParse(String value) {
        return true;
    }
}
