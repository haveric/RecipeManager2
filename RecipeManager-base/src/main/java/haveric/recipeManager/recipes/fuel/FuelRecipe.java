package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuelRecipe extends BaseFuelRecipe {
    private ItemStack ingredient;

    public FuelRecipe() {

    }

    public FuelRecipe(Material type, float burnTime) {
        setIngredient(new ItemStack(type, 1, RMCVanilla.DATA_WILDCARD));
        minTime = burnTime;
    }

    public FuelRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof FuelRecipe) {
            FuelRecipe r = (FuelRecipe) recipe;

            if (r.ingredient == null) {
                ingredient = null;
            } else {
                ingredient = r.ingredient.clone();
            }
        }
    }

    public FuelRecipe(Flags flags) {
        super(flags);
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack newIngredient) {
        ingredient = newIngredient;

        hash = ("fuel" + newIngredient.getType() + ":" + newIngredient.getDurability()).hashCode();
    }

    @Override
    public List<String> getIndexes() {
        String indexString = "" + ingredient.getType();

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            indexString += ":" + ingredient.getDurability();
        }

        return Collections.singletonList(indexString);
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("fuel ");

        s.append(ingredient.getType().toString().toLowerCase());

        if (ingredient.getDurability() != RMCVanilla.DATA_WILDCARD) {
            s.append(':').append(ingredient.getDurability());
        }

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    public boolean hasIngredient() {
        return ingredient != null;
    }

    @Override
    public boolean isValid() {
        return hasIngredient();
    }

    @Override
    public List<String> printBookIndices() {
        List<String> print = new ArrayList<>();

        if (hasCustomName()) {
            print.add(RMCChatColor.ITALIC + getName());
        } else {
            print.add(ToolsItem.getName(ingredient) + " Fuel");
        }

        return print;
    }

    @Override
    public String printBookResult(ItemResult result) {
        return getPrintForIngredient(ToolsItem.print(ingredient, RMCChatColor.BLACK, RMCChatColor.BLACK));
    }


}
