package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeStatus;

public class FlagRemove extends Flag {
    private static final FlagType TYPE = FlagType.REMOVE;

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
        super.clone();
        return new FlagRemove(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    @Override
    protected boolean onValidate() {
        if (getFlagsContainer().hasFlag(FlagType.OVERRIDE)) {
            return ErrorReporter.error("Flag " + getType() + " can't work with " + FlagType.OVERRIDE + " flag!");
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        return true;
    }

    @Override
    protected void onRegistered() {
        BaseRecipe recipe = getRecipe();

        if (recipe == null) {
            Messages.error(null, new IllegalAccessError(), "ERROR: invalid recipe pointer");
            remove();
        } else {
            recipe.getInfo().setStatus(RecipeStatus.REMOVED);
        }
    }
}
