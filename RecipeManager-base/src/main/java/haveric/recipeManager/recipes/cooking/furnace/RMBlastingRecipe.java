package haveric.recipeManager.recipes.cooking.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.Supports;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;

public class RMBlastingRecipe extends RMBaseFurnaceRecipe {
    public RMBlastingRecipe() {
        minTime = Vanilla.BLASTING_RECIPE_TIME;
    }

    public RMBlastingRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public RMBlastingRecipe(Flags flags) {
        super(flags);

        minTime = Vanilla.BLASTING_RECIPE_TIME;
    }

    public RMBlastingRecipe(BlastingRecipe recipe) {
        super(recipe);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.BLASTING;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof RMBlastingRecipe && hashCode() == obj.hashCode();
    }

    @Override
    public BlastingRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        BlastingRecipe bukkitRecipe = new BlastingRecipe(getNamespacedKey(), a.result(), getIngredientChoice(), experience, getCookTicks());
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
        return getMinTime() != Vanilla.BLASTING_RECIPE_TIME;
    }
}
