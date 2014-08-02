package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.ErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.MultiResultRecipe;
import ro.thehunters.digi.recipeManager.recipes.SingleResultRecipe;

public class FlagFailMessage extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.FAILMESSAGE;

        A = new String[] { "{flag} [message or false]", };

        D = new String[] { "Changes the message when recipe fails due to failure chance.", "Using this flag more than once will overwrite the previous message.", "", "The message supports colors (<red>, &3, etc).", "", "You can also use the following variables inside the message:", "  {failchance}    = recipe's chance of failure as a number.", "  {successchance} = recipe's chance of success as a number.", "  {resultchance}  = result's chance of success as a number.", "", "The same effect can be achieved by using " + FlagType.MESSAGE + " on the fail result item.", };

        E = new String[] { "{flag} <red>YOU FAILED, MWaHahahah!", };
    }

    // Flag code

    private String message;

    public FlagFailMessage() {
    }

    public FlagFailMessage(FlagFailMessage flag) {
        message = flag.message;
    }

    @Override
    public FlagFailMessage clone() {
        return new FlagFailMessage(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message == null) {
            remove();
        } else {
            this.message = Tools.parseColors(message.replaceAll("\\n", "\n"), false);
        }
    }

    @Override
    protected boolean onValidate() {
        BaseRecipe recipe = getRecipe();

        if (recipe instanceof MultiResultRecipe == false && recipe instanceof SingleResultRecipe == false) {
            ErrorReporter.error("Flag " + getType() + " can only be used on recipes that support failure chance.");
            return false;
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        setMessage(value);
        return true;
    }

    @Override
    protected void onFailed(Args a) {
        if (!a.hasResult() || !a.hasRecipe() || (a.recipe() instanceof MultiResultRecipe == false && a.recipe() instanceof SingleResultRecipe == false)) {
            a.addCustomReason("Needs fail-supporting recipe and result!");
            return;
        }

        float resultChance = a.result().getChance();
        float failChance = 0;
        float successChance = 0;

        if (a.recipe() instanceof SingleResultRecipe) {
            SingleResultRecipe recipe = (SingleResultRecipe) a.recipe();

            successChance = recipe.getResult().getChance();
            failChance = 100 - successChance;
        } else if (a.recipe() instanceof MultiResultRecipe) {
            MultiResultRecipe recipe = (MultiResultRecipe) a.recipe();

            for (ItemResult r : recipe.getResults()) {
                if (r.getTypeId() == 0) {
                    failChance = r.getChance();
                } else {
                    successChance += r.getChance();
                }
            }
        }

        a.addCustomEffect(Tools.replaceVariables(message, "{failchance}", failChance, "{successchance}", successChance, "{resultchance}", resultChance));
    }
}
