package haveric.recipeManager.recipes.anvil;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.PreparableResultRecipe;

public class BaseAnvilRecipe extends PreparableResultRecipe {
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

}
