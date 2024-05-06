package haveric.recipeManager.recipes.cooking.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.Supports;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;

public class RMFurnaceRecipe extends RMBaseFurnaceRecipe {
    public RMFurnaceRecipe() {
        minTime = Vanilla.FURNACE_RECIPE_TIME;
    }

    public RMFurnaceRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public RMFurnaceRecipe(Flags flags) {
        super(flags);

        minTime = Vanilla.FURNACE_RECIPE_TIME;
    }

    public RMFurnaceRecipe(FurnaceRecipe recipe) {
        super(recipe);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.SMELT;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof RMFurnaceRecipe && hashCode() == obj.hashCode();
    }

    @Override
    public FurnaceRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        FurnaceRecipe bukkitRecipe = new FurnaceRecipe(getNamespacedKey(), a.result(), getIngredientChoice(), experience, getCookTicks());
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
        return minTime != Vanilla.FURNACE_RECIPE_TIME;
    }
}
