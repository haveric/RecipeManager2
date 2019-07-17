package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;

public class RMFurnaceRecipe extends RMBaseFurnaceRecipe {
    public RMFurnaceRecipe() {
    }

    public RMFurnaceRecipe(BaseRecipe recipe) {
        super(recipe);
    }

    public RMFurnaceRecipe(Flags flags) {
        super(flags);
    }

    public RMFurnaceRecipe(FurnaceRecipe recipe) {
        super(recipe);
    }

    @Override
    public String getRecipeBaseHash() {
        return "smelt";
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
        if (Version.has1_13Support()) {
            if (!hasIngredientChoice() || !hasResult()) {
                return null;
            }
        } else {
            if (!hasIngredient() || !hasResult()) {
                return null;
            }
        }

        FurnaceRecipe recipe;
        if (Version.has1_13Support()) {
            recipe = new FurnaceRecipe(getNamespacedKey(), getResult(), new RecipeChoice.MaterialChoice(getIngredientChoice()), 0, getCookTicks());
        } else {
            recipe = new FurnaceRecipe(getResult(), getIngredient().getType(), getIngredient().getDurability());
        }

        return recipe;
    }
}
