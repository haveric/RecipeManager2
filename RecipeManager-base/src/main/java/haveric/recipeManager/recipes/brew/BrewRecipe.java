package haveric.recipeManager.recipes.brew;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrewRecipe extends BaseBrewRecipe {
    private ItemStack ingredient;
    private ItemStack potion;

    public BrewRecipe() {

    }

    public BrewRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof BrewRecipe) {
            BrewRecipe r = (BrewRecipe) recipe;

            setIngredient(r.ingredient.clone());
            potion = r.potion.clone();
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

        s.append(" to ").append(getResultsString());

        name = s.toString();
        customName = false;
    }

    @Override
    public boolean isValid() {
        return hasIngredient() && hasPotion() && hasResults();
    }

    @Override
    public void onRegister() {
        if (hasIngredient()) {
            BrewInventoryUtil.addIngredient(ingredient.getType());
        }
        if (hasPotion()) {
            BrewInventoryUtil.addPotion(potion.getType());
        }
    }

    public boolean hasIngredient() {
        return ingredient != null;
    }

    public boolean hasPotion() {
        return potion != null;
    }
    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;

        // build hashCode
        hash = ("brew" + ingredient.getType() + ':' + ingredient.getDurability() + ';').hashCode();
    }

    @Override
    public List<String> getIndexes() {
        String indexString = ingredient.getType().toString();

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            indexString += ":" + ingredient.getDurability();
        } else {
            indexString += ":" + RMCVanilla.DATA_WILDCARD;
        }

        return Collections.singletonList(indexString);
    }

    public ItemStack getPotion() {
        return potion;
    }

    public void setPotion(ItemStack potion) {
        this.potion = potion;
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        if (ingredient.getType() == type && (data == null || data == RMCVanilla.DATA_WILDCARD || ingredient.getDurability() == data)) {
            found++;
        }

        if (potion.getType() == type && (data == null || data == RMCVanilla.DATA_WILDCARD || potion.getDurability() == data)) {
            found++;
        }

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);
            recipeIndexes.add(ingredient.getType() + ":" + ingredient.getDurability());
            recipeIndexes.add(ingredient.getType() + ":" + RMCVanilla.DATA_WILDCARD);
        }

        return recipeIndexes;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("brewing", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredient"));
        s.append('\n').append(ToolsItem.print(ingredient, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.potion"));
        s.append('\n').append(ToolsItem.print(potion, RMCChatColor.BLACK, RMCChatColor.BLACK));

        return s.toString();
    }
}
