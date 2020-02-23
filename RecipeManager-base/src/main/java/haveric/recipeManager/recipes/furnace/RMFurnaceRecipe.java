package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import org.bukkit.inventory.FurnaceRecipe;

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
        if (!hasIngredient() || !hasResult()) {
            return null;
        }

        return new FurnaceRecipe(getResult(), getIngredient().getType(), getIngredient().getDurability());
    }
}
