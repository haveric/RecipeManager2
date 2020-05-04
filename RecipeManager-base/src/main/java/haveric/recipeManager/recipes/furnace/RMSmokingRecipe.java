package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import org.bukkit.inventory.SmokingRecipe;

public class RMSmokingRecipe extends RMBaseFurnaceRecipe1_13 {
    public RMSmokingRecipe() {
        setMinTime(Vanilla.SMOKER_RECIPE_TIME);
    }

    public RMSmokingRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public RMSmokingRecipe(Flags flags) {
        super(flags);

        setMinTime(Vanilla.SMOKER_RECIPE_TIME);
    }

    public RMSmokingRecipe(SmokingRecipe recipe) {
        super(recipe);

        setMinTime(Vanilla.SMOKER_RECIPE_TIME);
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

        return new SmokingRecipe(getNamespacedKey(), a.result(), getIngredientChoice(), 0, getCookTicks());
    }

    @Override
    public boolean hasCustomTime() {
        return getMinTime() != Vanilla.SMOKER_RECIPE_TIME;
    }
}
