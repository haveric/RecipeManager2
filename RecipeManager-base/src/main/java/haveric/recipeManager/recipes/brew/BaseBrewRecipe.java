package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.MultiResultRecipe;

public class BaseBrewRecipe extends MultiResultRecipe {
    public BaseBrewRecipe() {

    }

    public BaseBrewRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public BaseBrewRecipe(Flags flags) {
        super(flags);
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and ingredient!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.BREW;
    }
}
