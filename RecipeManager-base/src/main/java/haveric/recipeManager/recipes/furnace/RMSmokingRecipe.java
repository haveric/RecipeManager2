package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

public class RMSmokingRecipe extends RMBaseFurnaceRecipe {
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
    public String getRecipeBaseHash() {
        return "smoking";
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
        if (Version.has1_13Support()) {
            if (!hasIngredientChoice() || !hasResult()) {
                return null;
            }
        } else {
            if (!hasIngredient() || !hasResult()) {
                return null;
            }
        }

        SmokingRecipe recipe;
        if (Version.has1_13Support()) {
            recipe = new SmokingRecipe(getNamespacedKey(), getResult(), new RecipeChoice.MaterialChoice(getIngredientChoice()), 0, getCookTicks());
        } else {
            recipe = new SmokingRecipe(getNamespacedKey(), getResult(), getIngredient().getType(), 0, getCookTicks());
        }
        return recipe;
    }

    @Override
    public boolean hasCustomTime() {
        return getMinTime() != Vanilla.SMOKER_RECIPE_TIME;
    }
}
