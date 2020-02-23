package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;

public class FlagItemNBT extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.ITEM_NAME;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <nbtRaw>",
            "{flag} <nbtRaw> | display",
            "{flag} <nbtRaw> | result", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Sets raw nbt data on the result.",
            "  WARNING: This exists only to support features that may not exist in the Bukkit/Spigot API yet. Support is NOT GUARANTEED, especially across future versions.",
            "    If you find you need to use this flag a lot, please consider creating a ticket about adding support for the features you are using. I wll do my best to support what I can in better ways.",
            "  WARNING: There is NO VALIDATION on <nbtRaw> values. Test all outputs carefully before adding to a live server.",
            "",
            "Format should include outer brackets: '{}'",
            "",
            "Optional Arguments:",
            "  display          = only show on the displayed item when preparing to craft (only relevant to craft/combine recipes)",
            "  result           = only show on the result, but hide from the prepared result",
            "    Default behavior with neither of these arguments is to display in both locations", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} {display:{Name: '{\"text\":\"CUSTOM NAME\"}'}} // Basic example, but should use " + FlagType.ITEM_NAME + " instead.", };
    }


    private String displayNBT;
    private String resultNBT;

    public FlagItemNBT() {
    }

    public FlagItemNBT(FlagItemNBT flag) {
        displayNBT = flag.displayNBT;
        resultNBT = flag.resultNBT;
    }

    @Override
    public FlagItemNBT clone() {
        return new FlagItemNBT((FlagItemNBT) super.clone());
    }

    public String getDisplayNBT() {
        return displayNBT;
    }

    public void setDisplayNBT(String nbt) {
        displayNBT = nbt;
    }

    public String getResultNBT() {
        return resultNBT;
    }

    public void setResultNBT(String nbt) {
        resultNBT = nbt;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        // Match on single pipes '|', but not double '||'
        String[] args = value.split("(?<!\\|)\\|(?!\\|)");
        String nbt = args[0];

        // Replace double pipes with single pipe: || -> |
        nbt = nbt.replaceAll("\\|\\|", "|");

        if (args.length > 1) {
            String display = args[1].trim().toLowerCase();
            if (display.equals("display")) {
                displayNBT = nbt;
            } else if (display.equals("result")) {
                resultNBT = nbt;
            } else {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid argument: " + args[1] + ". Defaulting to set nbt in both locations.");
                displayNBT = nbt;
                resultNBT = nbt;
            }
        } else {
            displayNBT = nbt;
            resultNBT = nbt;
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (canAddMeta(a)) {
            addNBTRaw(a, displayNBT);
        }
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            addNBTRaw(a, resultNBT);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "displayNBT: " + displayNBT;
        toHash += "resultNBT: " + resultNBT;

        return toHash.hashCode();
    }
}
