package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class CombineRecipe1_13 extends BaseCombineRecipe {
    private String shape = "";
    private List<RecipeChoice> ingredientChoiceList = new ArrayList<>();

    public CombineRecipe1_13() {
    }

    public CombineRecipe1_13(ShapelessRecipe recipe) {
        setIngredientChoiceList(recipe.getChoiceList());

        setResult(recipe.getResult());
    }

    public CombineRecipe1_13(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CombineRecipe1_13) {
            CombineRecipe1_13 r = (CombineRecipe1_13) recipe;

            shape = r.shape;

            if (!r.ingredientChoiceList.isEmpty()) {
                for (RecipeChoice ingredientChoice : r.ingredientChoiceList) {
                    ingredientChoiceList.add(ingredientChoice.clone());
                }
            }
        }
    }

    public CombineRecipe1_13(Flags flags) {
        super(flags);
    }

    public void setShape(String newShape) {
        shape = newShape;
    }

    public List<RecipeChoice> getIngredientChoiceList() {
        return ingredientChoiceList;
    }

    public void setIngredientChoiceList(List<RecipeChoice> recipeChoices) {
        ingredientChoiceList.clear();

        ingredientChoiceList.addAll(recipeChoices);

        updateHash();
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("combine");

        for (RecipeChoice choice : ingredientChoiceList) {
            str.append(" ");

            str.append(ToolsItem.getRecipeChoiceHash(choice));
        }

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("shapeless");
        s.append(" (");

        int ingredientChoiceListSize = ingredientChoiceList.size();
        for (int j = 0; j < ingredientChoiceListSize; j++) {
            RecipeChoice choice = ingredientChoiceList.get(j);

            if (j > 0) {
                s.append(" ");
            }

            s.append(ToolsItem.getRecipeChoiceName(choice));
        }

        s.append(") to ");
        s.append(getResultsString());

        if (removed) {
            s.append(" [removed recipe]");
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public ShapelessRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredientChoices() || !hasResults()) {
            return null;
        }

        ShapelessRecipe bukkitRecipe;
        if (vanilla) {
            bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), getFirstResult());
        } else {
            ItemResult firstResult = getFirstResult();

            Args a = ArgBuilder.create().result(firstResult).build();
            getFlags().sendPrepare(a, true);
            firstResult.getFlags().sendPrepare(a, true);

            ItemStack result = Tools.createItemRecipeId(a.result(), hashCode());

            bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), result);
        }

        for (RecipeChoice choice : ingredientChoiceList) {
            bukkitRecipe.addIngredient(choice);
        }

        return bukkitRecipe;
    }

    public boolean hasIngredientChoices() {
        return ingredientChoiceList != null && !ingredientChoiceList.isEmpty();
    }

    @Override
    public boolean isValid() {
        return hasIngredientChoices() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.COMBINE;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("shapeless", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (RecipeChoice choice : ingredientChoiceList) {
            s.append('\n').append(ToolsItem.printRecipeChoice(choice, RMCChatColor.BLACK, RMCChatColor.BLACK));
        }

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        for (RecipeChoice choice : ingredientChoiceList) {
            int num = ToolsItem.getNumMaterialsInRecipeChoice(type, choice);
            if (num > 0) {
                found += num;
                break;
            }
        }

        return found;
    }
}
