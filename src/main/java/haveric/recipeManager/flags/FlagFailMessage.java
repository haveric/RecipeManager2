package haveric.recipeManager.flags;

import org.bukkit.Material;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.MultiResultRecipe;
import haveric.recipeManager.recipes.SingleResultRecipe;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagFailMessage extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [message or false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes the message when recipe fails due to failure chance.",
            "Using this flag more than once will overwrite the previous message.",
            "",
            "The message supports colors (<red>, &3, etc).",
            "",
            "You can also use the following variables inside the message:",
            "  {failchance}    = recipe's chance of failure as a number.",
            "  {successchance} = recipe's chance of success as a number.",
            "  {resultchance}  = result's chance of success as a number.",
            "",
            "The same effect can be achieved by using " + FlagType.MESSAGE + " on the fail result item.", };
    }

    protected String[] getExamples() {
        return new String[] {
            "{flag} <red>YOU FAILED, MWaHahahah!", };
    }


    private String message;

    public FlagFailMessage() {
    }

    public FlagFailMessage(FlagFailMessage flag) {
        message = flag.message;
    }

    @Override
    public FlagFailMessage clone() {
        return new FlagFailMessage((FlagFailMessage) super.clone());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        if (newMessage == null) {
            remove();
        } else {
            message = RMCUtil.parseColors(newMessage.replaceAll("\\n", "\n"), false);
        }
    }

    @Override
    protected boolean onValidate() {
        BaseRecipe recipe = getRecipe();

        if (!(recipe instanceof MultiResultRecipe) && !(recipe instanceof SingleResultRecipe)) {
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
        if (!a.hasResult() || !a.hasRecipe() || (!(a.recipe() instanceof MultiResultRecipe) && !(a.recipe() instanceof SingleResultRecipe))) {
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
                if (r.getType() == Material.AIR) {
                    failChance = r.getChance();
                } else {
                    successChance += r.getChance();
                }
            }
        }

        a.addCustomEffect(RMCUtil.replaceVariables(message, "{failchance}", failChance, "{successchance}", successChance, "{resultchance}", resultChance));
    }
}
