package haveric.recipeManager.flag.flags.recipe;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeStatus;

public class FlagRemove extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.REMOVE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [true or false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Removes an existing recipe that was added by vanilla Minecraft or other plugins/mods. ",
            "The recipe definition must have the exact ingredients of the recipe you want to overwrite.",
            "",
            "Results and smelt time will be ignored and you don't have to delete them if you want to keep them for later.",
            "",
            "If you don't know the exact ingredients you can use 'rmextract' command to extract all existing recipes in RecipeManager format.",
            "Value is optional, if value is not specified it will just be enabled.",
            "",
            "This can't be used along with " + FlagType.OVERRIDE + " flag.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}", };
    }


    public FlagRemove() {
    }

    public FlagRemove(FlagRemove flag) {
    }

    @Override
    public FlagRemove clone() {
        return new FlagRemove((FlagRemove) super.clone());
    }

    @Override
    public boolean onValidate() {
        if (getFlagsContainer().hasFlag(FlagType.OVERRIDE)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " can't work with " + FlagType.OVERRIDE + " flag!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        return true;
    }

    @Override
    public void onRegistered() {
        BaseRecipe recipe = getRecipe();

        if (recipe == null) {
            MessageSender.getInstance().error(null, new IllegalAccessError(), "ERROR: invalid recipe pointer");
            remove();
        } else {
            recipe.getInfo().setStatus(RecipeStatus.REMOVED);
        }
    }
}
