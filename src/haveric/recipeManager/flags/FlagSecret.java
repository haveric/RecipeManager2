package haveric.recipeManager.flags;

public class FlagSecret extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.SECRET;

        A = new String[] { "{flag} [true or false]", };

        D = new String[] { "Hides the recipe or result from common info sources.",
                           "Recipes are hidden from commands, books, etc.",
                           "Results are also hidden from commands, books and most importantly from multiresult item display.",
                           "This also means recipes/results won't give out any fail craft reasons!", };

        E = new String[] { "{flag}", };
    }

    // Flag code

    public FlagSecret() {
    }

    public FlagSecret(FlagSecret flag) {
    }

    @Override
    public FlagSecret clone() {
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
