package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;

public class BaseCombineRecipe extends PreparableResultRecipe {
    public BaseCombineRecipe() {
        super();
    }

    public BaseCombineRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public BaseCombineRecipe(Flags flags) {
        super(flags);
    }
}
