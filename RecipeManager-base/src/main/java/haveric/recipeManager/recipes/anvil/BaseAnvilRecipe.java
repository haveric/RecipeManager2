package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;

public abstract class BaseAnvilRecipe extends PreparableResultRecipe {
    protected int repairCost = 0;
    protected boolean renamingAllowed = false;
    protected double anvilDamageChance = 12;

    public BaseAnvilRecipe() {

    }

    public BaseAnvilRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof BaseAnvilRecipe) {
            BaseAnvilRecipe r = (BaseAnvilRecipe) recipe;
            repairCost = r.repairCost;

            renamingAllowed = r.renamingAllowed;
            anvilDamageChance = r.anvilDamageChance;
        }
    }

    public BaseAnvilRecipe(Flags flags) {
        super(flags);
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int newCost) {
        repairCost = newCost;
    }

    public boolean isRenamingAllowed() {
        return renamingAllowed;
    }

    public void setRenamingAllowed(boolean allowRenaming) {
        this.renamingAllowed = allowRenaming;
    }

    public double getAnvilDamageChance() {
        return anvilDamageChance;
    }

    public void setAnvilDamageChance(double anvilDamageChance) {
        this.anvilDamageChance = anvilDamageChance;
    }

    public boolean isValidBlockMaterial(Material material) {
        boolean valid = material == Material.ANVIL;

        if (!valid && Version.has1_13BasicSupport()) {
            valid = material == Material.CHIPPED_ANVIL;

            if (!valid) {
                valid = material == Material.DAMAGED_ANVIL;
            }
        }

        return valid;
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.ANVIL;
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs a result and two ingredients!";
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return true;
    }
}
