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
import haveric.recipeManager.recipes.PreparableResultRecipe;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombineRecipe extends PreparableResultRecipe {
    private String choicePattern = "";
    private Map<Character, RecipeChoice> ingredientsChoiceMap = new HashMap<>();

    private String group;
    private String category;

    public CombineRecipe() {
    }

    public CombineRecipe(ShapelessRecipe recipe) {
        setIngredientChoiceList(recipe.getChoiceList());

        setResult(recipe.getResult());
    }

    public CombineRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CombineRecipe) {
            CombineRecipe r = (CombineRecipe) recipe;

            choicePattern = r.choicePattern;

            if (!r.ingredientsChoiceMap.isEmpty()) {
                ingredientsChoiceMap.putAll(r.ingredientsChoiceMap);
            }

            group = r.group;
            category = r.category;
        }
    }

    public CombineRecipe(Flags flags) {
        super(flags);
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.COMBINE;
    }

    public void setChoicePattern(String pattern) {
        choicePattern = pattern;
    }

    public String getChoicePattern() {
        return choicePattern;
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

        choicePattern = shape;
        updateHash();
    }

    public List<RecipeChoice> getIngredientChoiceList() {
        List<RecipeChoice> ingredientChoiceList = new ArrayList<>();

        for (char c : choicePattern.toCharArray()) {
            ingredientChoiceList.add(ingredientsChoiceMap.get(c));
        }

        return ingredientChoiceList;
    }

    public boolean hasGroup() {
        return group != null;
    }

    public void setGroup(String newGroup) {
        group = newGroup;
    }

    public String getGroup() {
        return group;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
    }

    public String getCategory() {
        return category;
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("combine");

        for (RecipeChoice choice : getIngredientChoiceList()) {
            str.append(" ").append(ToolsRecipeChoice.getRecipeChoiceHash(choice));
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
            s.append(" ").append(ToolsRecipeChoice.getRecipeChoiceName(choice));
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

            ItemStack result;
            if (requiresRecipeManagerModification()) {
                result = Tools.createItemRecipeId(a.result(), hashCode());
            } else {
                result = a.result();
            }

            bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), result);
        }

        if (hasGroup()) {
            bukkitRecipe.setGroup(group);
        }

        if (Supports.categories() && hasCategory()) {
            bukkitRecipe.setCategory(CraftingBookCategory.valueOf(category));
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
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("combine", result);

        s.append(Messages.getInstance().parse("recipebook.header.pattern")).append('\n');
        s.append(RMCChatColor.GRAY).append(choicePattern).append('\n');

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (Map.Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            s.append('\n').append(RMCChatColor.DARK_PURPLE).append(entry.getKey()).append(RMCChatColor.GRAY).append(": ");

            s.append(ToolsRecipeChoice.printRecipeChoice(entry.getValue(), RMCChatColor.BLACK, RMCChatColor.BLACK));
        }

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        for (Map.Entry<Character, RecipeChoice> entry : ingredientsChoiceMap.entrySet()) {
            RecipeChoice choice = entry.getValue();

            int num = ToolsRecipeChoice.getNumMaterialsInRecipeChoice(type, choice);
            if (num > 0) {
                found += num;
                break;
            }
        }

        return found;
    }

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (result != null) {
            recipeIndexes.add(Tools.getRecipeIdFromItem(result));
        }

        return recipeIndexes;
    }

    @Override
    public int getIngredientMatchQuality(List<ItemStack> ingredients) {
        boolean checkExact = true;
        if (hasFlag(FlagType.INGREDIENT_CONDITION)) {
            checkExact = false;
        } else {
            for (ItemResult result : getResults()) {
                if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                    checkExact = false;
                    break;
                }
            }
        }

        int totalQuality = 0;
        for (ItemStack ingredient : ingredients) {
            if (ingredient.getType() != Material.AIR) {
                int quality = 0;
                for (Character c : choicePattern.toCharArray()) {
                    RecipeChoice ingredientChoice = ingredientsChoiceMap.get(c);
                    int newQuality = ToolsRecipeChoice.getIngredientMatchQuality(ingredient, ingredientChoice, checkExact);
                    if (newQuality > quality) {
                        quality = newQuality;
                        totalQuality += quality;
                    }
                }

                if (quality == 0) {
                    totalQuality = 0;
                    break;
                }
            }
        }

        return totalQuality;
    }
}
