package haveric.recipeManager.recipes.cooking.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.Supports;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;

public class RMSmokingRecipe extends RMBaseFurnaceRecipe1_13 {
    public RMSmokingRecipe() {
        minTime = Vanilla.SMOKER_RECIPE_TIME;
    }

    public RMSmokingRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public RMSmokingRecipe(Flags flags) {
        super(flags);

        minTime = Vanilla.SMOKER_RECIPE_TIME;
    }

    public RMSmokingRecipe(SmokingRecipe recipe) {
        super(recipe);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.SMOKING;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof RMSmokingRecipe && hashCode() == obj.hashCode();
    }

    @Override
    public SmokingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        SmokingRecipe bukkitRecipe = new SmokingRecipe(getNamespacedKey(), a.result(), getIngredientChoice(), experience, getCookTicks());
        if (hasGroup()) {
            bukkitRecipe.setGroup(getGroup());
        }

        if (Supports.categories() && hasCategory()) {
            bukkitRecipe.setCategory(CookingBookCategory.valueOf(getCategory()));
        }

        return bukkitRecipe;
    }

    @Override
    public boolean hasCustomTime() {
        return getMinTime() != Vanilla.SMOKER_RECIPE_TIME;
    }
}
