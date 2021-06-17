package haveric.recipeManager.recipes.craft;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;

public abstract class BaseCraftRecipe extends PreparableResultRecipe {
    public BaseCraftRecipe() {
        super();
    }

    public BaseCraftRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public BaseCraftRecipe(Flags flags) {
        super(flags);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.CRAFT;
    }
}
