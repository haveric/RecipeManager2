package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;

public class FlagNoResult extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.NO_RESULT;
    }

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
            "Useful when giving items through " + FlagType.COMMAND + " or providing non-item results, such as " + FlagType.MOD_EXP + ".", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}", };
    }

    public FlagNoResult() {
    }

    public FlagNoResult(FlagNoResult flag) {
        super(flag);
    }

    @Override
    public FlagNoResult clone() {
        return new FlagNoResult((FlagNoResult) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        return true;
    }
}
