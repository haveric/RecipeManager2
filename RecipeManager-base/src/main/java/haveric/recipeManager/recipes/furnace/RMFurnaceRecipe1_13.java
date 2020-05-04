package haveric.recipeManager.recipes.furnace;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.BaseRecipe;
import org.bukkit.inventory.FurnaceRecipe;

public class RMFurnaceRecipe1_13 extends RMBaseFurnaceRecipe1_13 {
    public RMFurnaceRecipe1_13() {
    }

    public RMFurnaceRecipe1_13(BaseRecipe recipe) {
        super(recipe);
    }

    public RMFurnaceRecipe1_13(Flags flags) {
        super(flags);
    }

    public RMFurnaceRecipe1_13(FurnaceRecipe recipe) {
        super(recipe);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.SMELT;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof RMFurnaceRecipe1_13 && hashCode() == obj.hashCode();
    }

    @Override
    public FurnaceRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoice() || !hasResult()) {
            return null;
        }

        Args a = ArgBuilder.create().result(getResult()).build();
        getFlags().sendPrepare(a, true);
        getResult().getFlags().sendPrepare(a, true);

        return new FurnaceRecipe(getNamespacedKey(), a.result(), getIngredientChoice(), 0, getCookTicks());
    }
}
