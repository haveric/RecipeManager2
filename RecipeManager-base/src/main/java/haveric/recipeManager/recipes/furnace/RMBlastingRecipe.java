package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.RecipeChoice;

public class RMBlastingRecipe extends RMBaseFurnaceRecipe1_13 {
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
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        return new BlastingRecipe(getNamespacedKey(), getResult(), new RecipeChoice.MaterialChoice(getIngredientChoice()), 0, getCookTicks());

    }

    @Override
    public boolean hasCustomTime() {
        return getMinTime() != Vanilla.BLASTING_RECIPE_TIME;
    }
}
