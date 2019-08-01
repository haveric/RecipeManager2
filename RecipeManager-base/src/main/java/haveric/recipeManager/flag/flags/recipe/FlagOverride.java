package haveric.recipeManager.flag.flags.recipe;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo.RecipeStatus;

public class FlagOverride extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.OVERRIDE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [true or false]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Overwrites an existing recipe from vanilla Minecraft or other plugins/mods.",
            "The recipe definition must have the exact ingredients of the recipe you want to overwrite.",
            "",
            "You may set whatever result(s) you want and add any other flags, this flag allows RecipeManager to take control over that recipe.",
            "If you don't know the exact ingredients you can use 'rmextract' command to extract all existing recipes in RecipeManager format.",
            "",
            "Value is optional, if value is not specified it will just be enabled.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag}", };
    }


    public FlagOverride() {
    }

    public FlagOverride(FlagOverride flag) {
    }

    @Override
    public FlagOverride clone() {
        return new FlagOverride((FlagOverride) super.clone());
    }

    @Override
    public boolean onValidate() {
        if (getFlagsContainer().hasFlag(FlagType.REMOVE)) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " can't work with @remove flag!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value) {
        return true;
    }

    @Override
    public void onRegistered() {
        BaseRecipe recipe = getRecipe();

        if (recipe == null) {
            MessageSender.getInstance().debug("ERROR: invalid recipe pointer");
            remove();
        } else {
            recipe.getInfo().setStatus(RecipeStatus.OVERRIDDEN);
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(1);
     *
     * list.add(MessagesOld.FLAG_OVERRIDE.get());
     *
     * return list; }
     */
}
