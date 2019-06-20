package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.inventory.BlastingRecipe;

public class RMBlastingRecipe extends RMBaseFurnaceRecipe {
    public RMBlastingRecipe() {
        setMinTime(Vanilla.BLASTING_RECIPE_TIME);
    }

    public RMBlastingRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public RMBlastingRecipe(Flags flags) {
        super(flags);

        setMinTime(Vanilla.BLASTING_RECIPE_TIME);
    }

    public RMBlastingRecipe(BlastingRecipe recipe) {
        super(recipe);

        setMinTime(Vanilla.BLASTING_RECIPE_TIME);
    }

    @Override
    public String getRecipeBaseHash() {
        return "blasting";
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
        if (!hasIngredient() || !hasResult()) {
            return null;
        }

        return new BlastingRecipe(getNamespacedKey(), getResult(), getIngredient().getType(), 0, getCookTicks());
    }

    @Override
    public boolean hasCustomTime() {
        return getMinTime() != Vanilla.BLASTING_RECIPE_TIME;
    }
}
