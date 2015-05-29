package haveric.recipeManager.recipes;

import java.util.Iterator;

import haveric.recipeManager.Vanilla;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class BrewRecipe extends MultiResultRecipe {
    private ItemStack ingredient;
    private ItemStack potion;

    public BrewRecipe() {

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

        s.append(ingredient.getTypeId());

        if (ingredient.getDurability() != Vanilla.DATA_WILDCARD) {
            s.append(':').append(ingredient.getDurability());
        }

        s.append(" + ");

        s.append(potion.getTypeId());

        if (potion.getDurability() != Vanilla.DATA_WILDCARD) {
            s.append(':').append(potion.getDurability());
        }

        Iterator<ItemResult> iter = getResults().iterator();
        while (iter.hasNext()) {
            ItemResult result = iter.next();
            s.append(" = ").append(result.getTypeId());
            if (result.getDurability() != Vanilla.DATA_WILDCARD) {
                s.append(':').append(result.getDurability());
            }
        }


        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return ingredient != null && potion != null && hasResults();
    }

    @Override
    public RecipeType getType() {
        return RecipeType.BREW;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;
    }

    public String getIndexString() {
        String indexString = "" + ingredient.getTypeId();

        if (ingredient.getDurability() != Vanilla.DATA_WILDCARD) {
            indexString += ":" + ingredient.getDurability();
        } else {
            indexString += ":" + Vanilla.DATA_WILDCARD;
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
