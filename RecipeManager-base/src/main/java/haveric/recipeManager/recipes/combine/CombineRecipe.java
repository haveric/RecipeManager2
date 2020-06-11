package haveric.recipeManager.recipes.combine;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.recipes.RMCRecipeType;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.RMBukkitTools;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CombineRecipe extends BaseCombineRecipe {
    private List<List<Material>> ingredientChoiceList = new ArrayList<>();
    private List<ItemStack> ingredients;

    public CombineRecipe() {
    }

    public CombineRecipe(ShapelessRecipe recipe) {
        setIngredients(recipe.getIngredientList());
        setResult(recipe.getResult());
    }

    public CombineRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CombineRecipe) {
            CombineRecipe r = (CombineRecipe) recipe;

            if (!r.ingredients.isEmpty()) {
                ingredients.addAll(r.getIngredients());
            }

            if (!r.ingredientChoiceList.isEmpty()) {
                for (List<Material> ingredientChoice : r.ingredientChoiceList) {
                    ArrayList<Material> cloneList = new ArrayList<>(ingredientChoice);
                    ingredientChoiceList.add(cloneList);
                }
            }
        }
    }

    public CombineRecipe(Flags flags) {
        super(flags);
    }

    /**
     * @return clone of ingredients list
     */
    public List<ItemStack> getIngredients() {
        List<ItemStack> clone = new ArrayList<>();

        for (ItemStack i : ingredients) {
            clone.add(i.clone());
        }

        return clone;
    }

    public void addIngredient(Material type) {
        addIngredient(1, type, (short) -1);
    }

    public void addIngredient(Material type, short data) {
        addIngredient(1, type, data);
    }

    public void addIngredient(ItemStack ingredient) {
        addIngredient(ingredient.getAmount(), ingredient.getType(), ingredient.getDurability());
    }

    public void addIngredient(int amount, Material type, short data) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }

        if ((ingredients.size() + amount) > 9) { // check if they're more than they should...
            throw new IllegalArgumentException("Recipe can't have more than 9 ingredients!");
        }

        while (amount-- > 0) {
            ingredients.add(new ItemStack(type, 1, data));
        }

        sort();
    }

    public void setIngredients(List<ItemStack> newIngredients) {
        // unstack ingredients
        ingredients = new ArrayList<>();
        int amount;

        for (ItemStack ingredient : newIngredients) {
            amount = ingredient.getAmount();

            while (amount-- > 0) {
                ingredients.add(new ItemStack(ingredient.getType(), 1, ingredient.getDurability()));
            }
        }

        if (ingredients.size() > 9) { // check if they're more than they should...
            throw new IllegalArgumentException("Recipe can't have more than 9 ingredients!");
        }

        sort();
    }

    public List<List<Material>> getIngredientChoiceList() {
        return ingredientChoiceList;
    }

    public void setIngredientChoiceList(List<List<Material>> recipeChoices) {
        ingredientChoiceList.clear();

        ingredientChoiceList.addAll(recipeChoices);

        updateHash();
    }

    private void updateHash() {
        StringBuilder str = new StringBuilder("combine");

        for (ItemStack item : ingredients) {
            str.append(item.getType().toString()).append(':').append(item.getDurability()).append(';');
        }

        hash = str.toString().hashCode();
    }

    private void sort() {
        // sort by type and data
        RMBukkitTools.sortIngredientList(ingredients);

        updateHash();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("shapeless");
        s.append(" (");

        int size = ingredients.size();

        for (int i = 0; i < size; i++) {
            ItemStack item = ingredients.get(i);

            if (item == null) {
                s.append("air");
            } else {
                s.append(item.getType().toString().toLowerCase());

                if (item.getDurability() != RMCVanilla.DATA_WILDCARD) {
                    s.append(':').append(item.getDurability());
                }
            }

            if (i < (size - 1)) {
                s.append(' ');
            }
        }

        s.append(") ");

        if (removed) {
            s.append("[removed recipe]");
        } else {
            s.append("to ").append(getResultsString());
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public ShapelessRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        ShapelessRecipe bukkitRecipe;
        if (Version.has1_12Support()) {
            if (vanilla) {
                bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), getFirstResult());
            } else {
                bukkitRecipe = new ShapelessRecipe(getNamespacedKey(), Tools.createItemRecipeId(getFirstResult(), hashCode()));
            }
        } else {
            if (vanilla) {
                bukkitRecipe = new ShapelessRecipe(getFirstResult());
            } else {
                bukkitRecipe = new ShapelessRecipe(Tools.createItemRecipeId(getFirstResult(), hashCode()));
            }
        }

        for (ItemStack item : ingredients) {
            bukkitRecipe.addIngredient(item.getAmount(), item.getType(), item.getDurability());
        }

        return bukkitRecipe;
    }

    public boolean hasIngredients() {
        return ingredients != null && !ingredients.isEmpty();
    }

    public boolean hasIngredientChoices() {
        return ingredientChoiceList != null && !ingredientChoiceList.isEmpty();
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs at least one result and ingredient!";
    }

    @Override
    public RMCRecipeType getType() {
        return RMCRecipeType.COMBINE;
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("shapeless", result);

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        Map<ItemStack, MutableInt> items = new HashMap<>();

        for (ItemStack item : ingredients) {
            MutableInt i = items.get(item);

            if (i == null) {
                i = new MutableInt();
                items.put(item.clone(), i);
            }

            i.add(item.getAmount());
        }

        for (Entry<ItemStack, MutableInt> e : items.entrySet()) {
            ItemStack item = e.getKey();
            item.setAmount(e.getValue().intValue());

            String print = "";
            // TODO: Recipes can have ingredientcondition as well
            if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
                List<ConditionsIngredient> conditions = flag.getIngredientConditions(item);

                if (conditions.size() > 0) {
                    ConditionsIngredient condition = conditions.get(0);

                    if (condition.hasName()) {
                        print = RMCChatColor.BLACK + condition.getName();
                    } else if (condition.hasLore()) {
                        print = RMCChatColor.BLACK + "" + RMCChatColor.ITALIC + condition.getLores().get(0);
                    }
                }
            }

            if (print.isEmpty()) {
                print = ToolsItem.print(item, RMCChatColor.RESET, RMCChatColor.BLACK);
            }

            s.append('\n').append(print);
        }

        return s.toString();
    }

    @Override
    public int findItemInIngredients(Material type, Short data) {
        int found = 0;

        for (ItemStack i : getIngredients()) {
            if (i == null) {
                continue;
            }

            if (i.getType() == type && (data == null || data == RMCVanilla.DATA_WILDCARD || i.getDurability() == data)) {
                found++;
            }
        }

        return found;
    }
}
