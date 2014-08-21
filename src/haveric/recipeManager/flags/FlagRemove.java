package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.RecipeInfo.RecipeStatus;

public class FlagRemove extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.REMOVE;

        A = new String[] { "{flag} [true or false]", };

        D = new String[] { "Removes an existing recipe that was added by vanilla Minecraft or other plugins/mods. ",
                           "The recipe definition must have the exact ingredients of the recipe you want to overwrite.",
                           "",
                           "Results and smelt time will be ignored and you don't have to delete them if you want to keep them for later.",
                           "",
                           "If you don't know the exact ingredients you can use 'rmextract' command to extract all existing recipes in RecipeManager format.",
                           "Value is optional, if value is not specified it will just be enabled.",
                           "",
                           "This can't be used along with " + FlagType.OVERRIDE + " flag.", };

        E = new String[] { "{flag}", };
    }

    // Flag code

    public FlagRemove() {
    }

    public FlagRemove(FlagRemove flag) {
    }

    @Override
    public FlagRemove clone() {
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
            Messages.error(null, new IllegalAccessError(), "ERROR: invalid recipe pointer: " + recipe);
            remove();
        } else {
            recipe.getInfo().setStatus(RecipeStatus.REMOVED);
        }
    }
}
