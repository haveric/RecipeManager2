package haveric.recipeManager.flags;

public class FlagNoResult extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag}", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Prevents the result item from being crafted.",
            "",
            "Useful when giving items through " + FlagType.COMMAND + " or providing non-item results, such as " + FlagType.MODEXP + "."};
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}", };
    }

    public FlagNoResult() {
    }

    public FlagNoResult(FlagNoResult flag) {
    }

    @Override
    public FlagNoResult clone() {
        return new FlagNoResult((FlagNoResult) super.clone());
    }

    @Override
    protected boolean onParse(String value) {
        return true;
    }
}
