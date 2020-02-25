package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);
            recipeIndexes.add(ingredient.getType().toString() + ":" + ingredient.getDurability());
            recipeIndexes.add(ingredient.getType().toString() + ":" + RMCVanilla.DATA_WILDCARD);
        }

        return recipeIndexes;
    }
}
