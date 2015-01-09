package haveric.recipeManager.recipes;

import haveric.recipeManager.Messages;
import haveric.recipeManager.Vanilla;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;


public class CombineRecipe extends WorkbenchRecipe {
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

            ingredients = r.getIngredients();
        }
    }

    public CombineRecipe(Flags flags) {
        super(flags);
    }

    /**
     * @return clone of ingredients list
     */
    public List<ItemStack> getIngredients() {
        List<ItemStack> clone = new ArrayList<ItemStack>();

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
            ingredients = new ArrayList<ItemStack>();
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
        ingredients = new ArrayList<ItemStack>();
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

    private void sort() {
        // sort by type and data
        Tools.sortIngredientList(ingredients);

        // build hashCode
        StringBuilder str = new StringBuilder("combine");

        for (ItemStack item : ingredients) {
            str.append(item.getTypeId()).append(':').append(item.getDurability()).append(';');
        }

        hash = str.toString().hashCode();
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
                s.append('0');
            } else {
                s.append(item.getTypeId());

                if (item.getDurability() != Vanilla.DATA_WILDCARD) {
                    s.append(':').append(item.getDurability());
                }
            }

            if (i < (size - 1)) {
                s.append(' ');
            }
        }

        s.append(") ");

        if (removed) {
            s.append("removed recipe");
        } else {
            s.append(getResultsString());
        }

        name = s.toString();
        customName = false;
    }

    @Override
    public ShapelessRecipe toBukkitRecipe() {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(Tools.createItemRecipeId(getFirstResult(), getIndex()));

        for (ItemStack item : ingredients) {
            bukkitRecipe.addIngredient(item.getAmount(), item.getType(), item.getDurability());
        }

        return bukkitRecipe;
    }

    public boolean hasIngredients() {
        return ingredients != null && !ingredients.isEmpty();
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public RecipeType getType() {
        return RecipeType.COMBINE;
    }

    @Override
    public String printBookIndex() {
        String print;

        if (hasCustomName()) {
            print = ChatColor.ITALIC + getName();
        } else {
            print = ToolsItem.getName(getFirstResult());
        }

        return print;
    }

    @Override
    public String printBook() {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.RECIPEBOOK_HEADER_SHAPELESS.get());

        if (hasCustomName()) {
            s.append('\n').append(ChatColor.DARK_BLUE).append(getName()).append(ChatColor.BLACK);
        }

        s.append('\n').append(ChatColor.GRAY).append('=').append(ChatColor.BLACK).append(ChatColor.BOLD).append(ToolsItem.print(getFirstResult(), ChatColor.DARK_GREEN, null, true));

        if (isMultiResult()) {
            s.append('\n').append(Messages.RECIPEBOOK_MORERESULTS.get("{amount}", (getResults().size() - 1)));
        }

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_INGREDIENTS.get()).append(ChatColor.BLACK);

        Map<ItemStack, MutableInt> items = new HashMap<ItemStack, MutableInt>();

        for (ItemStack item : ingredients) {
            MutableInt i = items.get(item);

            if (i == null) {
                i = new MutableInt();
                items.put(item, i);
            }

            i.add(item.getAmount());
        }

        for (Entry<ItemStack, MutableInt> e : items.entrySet()) {
            ItemStack item = e.getKey();
            item.setAmount(e.getValue().intValue());
            s.append('\n').append(ToolsItem.print(item, ChatColor.RED, ChatColor.BLACK, false));
        }

        return s.toString();
    }
}
