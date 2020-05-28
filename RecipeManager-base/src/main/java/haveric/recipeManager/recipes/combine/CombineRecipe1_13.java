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
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class CombineRecipe1_13 extends BaseCombineRecipe {
    private String choiceShape = "";
    private Map<Character, RecipeChoice> ingredientsChoiceMap = new HashMap<>();

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

            choiceShape = r.choiceShape;

            if (!r.ingredientsChoiceMap.isEmpty()) {
                ingredientsChoiceMap.putAll(r.ingredientsChoiceMap);
            }
        }
    }

    public CombineRecipe1_13(Flags flags) {
        super(flags);
    }

    public void setChoiceShape(String shape) {
        choiceShape = shape;
    }

    public String getChoiceShape() {
        return choiceShape;
    }

    private void setIngredientsChoiceMap(ShapedRecipe recipe) {
        ingredientsChoiceMap.clear();
        ingredientsChoiceMap.putAll(recipe.getChoiceMap());

        updateHash();
    }

    public void setIngredientsRecipeChoiceMap(Map<Character, RecipeChoice> newIngredientsChoiceMap) {
        ingredientsChoiceMap.clear();
        ingredientsChoiceMap.putAll(newIngredientsChoiceMap);

        updateHash();
    }

    public void setIngredientsChoiceMap(Map<Character, List<Material>> newIngredientsChoiceMap) {
        ingredientsChoiceMap.clear();

        for (Map.Entry<Character, List<Material>> entry : newIngredientsChoiceMap.entrySet()) {
            List<Material> materials = entry.getValue();

            if (materials.size() == 1 && materials.get(0) == Material.AIR) {
                ingredientsChoiceMap.put(entry.getKey(), null);
            } else {
                RecipeChoice.MaterialChoice newMaterialList = new RecipeChoice.MaterialChoice(entry.getValue());
                ingredientsChoiceMap.put(entry.getKey(), newMaterialList);
            }
        }

        updateHash();
    }

    public Map<Character, RecipeChoice> getIngredientsChoiceMap() {
        return ingredientsChoiceMap;
    }

    public void setIngredientChoiceList(List<RecipeChoice> recipeChoices) {
        char letter = 'a';
        String shape = "";

        for (RecipeChoice recipeChoice : recipeChoices) {
            ingredientsChoiceMap.put(letter, recipeChoice);
            shape += letter;
            letter ++;
        }

        choiceShape = shape;
        updateHash();
    }

    public List<RecipeChoice> getIngredientChoiceList() {
        List<RecipeChoice> ingredientChoiceList = new ArrayList<>();

        for (char c : choiceShape.toCharArray()) {
            ingredientChoiceList.add(ingredientsChoiceMap.get(c));
        }

        return ingredientChoiceList;
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("combine");

        for (RecipeChoice choice : getIngredientChoiceList()) {
            str.append(" ").append(ToolsItem.getRecipeChoiceHash(choice));
        }

        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("combine");
        s.append(" (");

        for (RecipeChoice choice : getIngredientChoiceList()) {
            s.append(" ").append(ToolsItem.getRecipeChoiceName(choice));
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

        for (RecipeChoice choice : getIngredientChoiceList()) {
            bukkitRecipe.addIngredient(choice);
        }

        return bukkitRecipe;
    }

    public boolean hasIngredientChoices() {
        return !ingredientsChoiceMap.isEmpty();
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
        StringBuilder s = getHeaderResult("combine", result);

        s.append(Messages.getInstance().parse("recipebook.header.shape")).append('\n');
        s.append(RMCChatColor.GRAY).append(choiceShape).append('\n');

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (Map.Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            s.append('\n').append(RMCChatColor.DARK_PURPLE).append(entry.getKey()).append(RMCChatColor.GRAY).append(": ");

            ToolsItem.printRecipeChoice(entry.getValue(), RMCChatColor.BLACK, RMCChatColor.BLACK);
        }

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        for (Map.Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            RecipeChoice choice = entry.getValue();

            int num = ToolsItem.getNumMaterialsInRecipeChoice(type, choice);
            if (num > 0) {
                found += num;
                break;
            }
        }

        return found;
    }
}
