package haveric.recipeManager.recipes.fuel;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
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
    public List<String> printBookRecipes() {
        List<String> recipes = new ArrayList<>();

        recipes.add(printBookResult());

        return recipes;
    }

    public String printBookResult() {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.getInstance().parse("recipebook.header.fuel"));

        if (hasCustomName()) {
            s.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.ITALIC).append(getName()).append(RMCChatColor.BLACK);
        }

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.ingredient")).append(RMCChatColor.BLACK);
        s.append('\n').append(ToolsItem.print(ingredient, RMCChatColor.BLACK, RMCChatColor.BLACK));

        s.append("\n\n");
        s.append(Messages.getInstance().parse("recipebook.header.burntime")).append(RMCChatColor.BLACK);
        s.append('\n');

        if (maxTime > minTime) {
            s.append(Messages.getInstance().parse("recipebook.fuel.time.random", "{min}", RMCUtil.printNumber(minTime), "{max}", RMCUtil.printNumber(maxTime)));
        } else {
            s.append(Messages.getInstance().parse("recipebook.fuel.time.fixed", "{time}", RMCUtil.printNumber(minTime)));
        }

        return s.toString();
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (ingredients.size() == 1) {
            ItemStack ingredient = ingredients.get(0);
            recipeIndexes.add(ingredient.getType().toString());
            recipeIndexes.add(ingredient.getType() + ":" + ingredient.getDurability());
        }

        return recipeIndexes;
    }
}
