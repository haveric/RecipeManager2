package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.MultiResultRecipe;
import haveric.recipeManagerCommon.RMCVanilla;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class BrewRecipe extends MultiResultRecipe {
    private ItemStack ingredient;
    private ItemStack potion;

    public BrewRecipe() {

    }

    public BrewRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof BrewRecipe) {
            BrewRecipe r = (BrewRecipe) recipe;

            ingredient = r.ingredient;
            potion = r.potion;
        }
    }

    public BrewRecipe(Flags flags) {
        super(flags);
    }

    @Override
    public Recipe getBukkitRecipe(boolean vanilla) {
        return null;
    }

    @Override
    public void setBukkitRecipe(Recipe newRecipe) {

    }

    @Override
    public Recipe toBukkitRecipe(boolean vanilla) {
        return null;
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();

        s.append(ingredient.getType().toString().toLowerCase());

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            s.append(':').append(ingredient.getDurability());
        }

        s.append(" + ");

        s.append(potion.getType().toString().toLowerCase());

        if (potion.getDurability() != RMCVanilla.DATA_WILDCARD) {
            s.append(':').append(potion.getDurability());
        }

        s.append(" = ").append(getResultsString());

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return ingredient != null && potion != null && hasResults();
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.BREW;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;

        // build hashCode
        hash = ("brew" + ingredient.getType().toString() + ':' + ingredient.getDurability() + ';').hashCode();
    }

    public String getIndexString() {
        String indexString = "" + ingredient.getType().toString();

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            indexString += ":" + ingredient.getDurability();
        } else {
            indexString += ":" + RMCVanilla.DATA_WILDCARD;
        }

        return indexString;
    }

    public ItemStack getPotion() {
        return potion;
    }

    public void setPotion(ItemStack potion) {
        this.potion = potion;
    }
}
