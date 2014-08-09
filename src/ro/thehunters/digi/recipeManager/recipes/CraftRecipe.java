package ro.thehunters.digi.recipeManager.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Vanilla;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.flags.Flags;

public class CraftRecipe extends WorkbenchRecipe {
    private ItemStack[] ingredients;
    private int width;
    private int height;
    private boolean mirror = false;

    public CraftRecipe() {
    }

    public CraftRecipe(ShapedRecipe recipe) {
        setBukkitRecipe(recipe);
        setIngredients(Tools.convertShapedRecipeToItemMatrix(recipe));
        setResult(recipe.getResult());
    }

    public CraftRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CraftRecipe) {
            CraftRecipe r = (CraftRecipe) recipe;

            ingredients = r.getIngredients();
            width = r.width;
            height = r.height;
            mirror = r.mirror;
        }
    }

    public CraftRecipe(Flags flags) {
        super(flags);
    }

    /**
     * @return clone of ingredients array's elements
     */
    public ItemStack[] getIngredients() {
        if (ingredients != null) {
            ItemStack[] items = new ItemStack[ingredients.length];

            for (int i = 0; i < ingredients.length; i++) {
                items[i] = (ingredients[i] == null ? null : ingredients[i].clone());
            }

            return items;
        }

        return null;
    }

    /**
     * Set the ingredients matrix. <br> This also calculates the width and height of the shape matrix.<br> <b>NOTE: Array must have exactly 9 elements, use null for empty slots.</b>
     *
     * @param ingredients
     *            ingredients matrix, this also defines the shape, width and height.
     */
    public void setIngredients(ItemStack[] ingredients) {
        if (ingredients.length != 9) {
            throw new IllegalArgumentException("Recipe must have exactly 9 items, use null to specify empty slots!");
        }

        this.ingredients = ingredients;
        calculate();
    }

    /**
     * Sets an ingredient slot to material with wildcard data value.<br> Slots are like:<br> <code>| 0 1 2 |<br> | 3 4 5 |<br> | 6 7 8 |</code> <br> Null slots are ignored and allow the recipe to be
     * used in a smaller grid (inventory's 2x2 for example)<br> <br> <b>NOTE: always start with index 0 !</b> Then you can use whatever index you want up to 8.<br> This is required because ingredients
     * are shifted to top-left corner of the 2D matrix on each call of this method.
     *
     * @param slot
     *            start with 0, then use any index from 1 to 8
     * @param type
     */
    public void setIngredient(int slot, Material type) {
        setIngredient(slot, type, Vanilla.DATA_WILDCARD);
    }

    /**
     * Sets an ingredient slot to material with specific data value.<br> Slots are like:<br> <code>| 0 1 2 |<br> | 3 4 5 |<br> | 6 7 8 |</code> <br> Null slots are ignored and allow the recipe to be
     * used in a smaller grid (inventory's 2x2 for example)<br> <br> <b>NOTE: always start with index 0 !</b> Then you can use whatever index you want up to 8.<br> This is required because ingredients
     * are shifted to top-left corner of the 2D matrix on each call of this method.
     *
     * @param slot
     *            start with 0, then use any index from 1 to 8
     * @param type
     * @param data
     */
    public void setIngredient(int slot, Material type, int data) {
        if (ingredients == null) {
            ingredients = new ItemStack[9];
        }

        // TODO remember WHY is this required
        if (slot != 0 && ingredients[0] == null) {
            throw new IllegalArgumentException("A plugin is using setIngredient() with index NOT starting at 0, shape is corupted!!!");
        }

        if (type == null) {
            ingredients[slot] = null;
        } else {
            ingredients[slot] = new ItemStack(type, 1, (short) data);
        }

        calculate();
    }

    /**
     * @return true if shape was mirrored, usually false.
     */
    public boolean isMirrorShape() {
        return mirror;
    }

    /**
     * Mirror the ingredients shape.<br> Useful for matching recipes, no other real effect.<br> This triggers a hashCode recalculation.
     *
     * @param mirror
     */
    public void setMirrorShape(boolean mirror) {
        this.mirror = mirror;
        calculate();
    }

    private void calculate() {
        if (ingredients == null) {
            return;
        }

        StringBuilder str = new StringBuilder("craft");

        if (mirror) {
            // Mirror the ingredients shape and trim the item matrix, shift ingredients to top-left corner
            ingredients = Tools.mirrorItemMatrix(ingredients);
        } else {
            // Trim the item matrix, shift ingredients to top-left corner
            Tools.trimItemMatrix(ingredients);
        }

        width = 0;
        height = 0;

        // Calculate width and height of the shape and build the ingredient string for hashing
        for (int h = 0; h < 3; h++) {
            for (int w = 0; w < 3; w++) {
                ItemStack item = ingredients[(h * 3) + w];

                if (item != null) {
                    width = Math.max(width, w);
                    height = Math.max(height, h);

                    str.append(item.getTypeId()).append(':').append(item.getDurability());
                }

                str.append(';');
            }
        }

        width++;
        height++;
        hash = str.toString().hashCode();
    }

    @Override
    public void resetName() {
        StringBuilder s = new StringBuilder();
        boolean removed = hasFlag(FlagType.REMOVE);

        s.append("shaped ").append(getWidth()).append("x").append(getHeight());

        s.append(" (");

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                ItemStack item = ingredients[(h * 3) + w];

                if (item != null) {
                    s.append(item.getTypeId()); // item.getType().toString().toLowerCase());

                    if (item.getDurability() != Vanilla.DATA_WILDCARD) {
                        s.append(":").append(item.getDurability());
                    }
                } else {
                    s.append("0");
                }

                if (w < (width - 1)) {
                    s.append(" ");
                }
            }

            if (h < (height - 1)) {
                s.append(" / ");
            }
        }

        s.append(") ");

        if (!removed) {
            s.append(getResultsString());
        } else {
            s.append("removed recipe");
        }

        name = s.toString();
        customName = false;
    }

    /**
     * @return Shape width, 1 to 3
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Shape height, 1 to 3
     */
    public int getHeight() {
        return height;
    }

    @Override
    public ShapedRecipe toBukkitRecipe() {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }

        ShapedRecipe bukkitRecipe = new ShapedRecipe(Tools.createItemRecipeId(getFirstResult(), getIndex()));

        switch (height) {
            case 1:
                switch (width) {
                    case 1:
                        bukkitRecipe.shape("a");
                        break;

                    case 2:
                        bukkitRecipe.shape("ab");
                        break;

                    case 3:
                        bukkitRecipe.shape("abc");
                }

                break;
            case 2:
                switch (width) {
                    case 1:
                        bukkitRecipe.shape("a", "b");
                        break;

                    case 2:
                        bukkitRecipe.shape("ab", "cd");
                        break;

                    case 3:
                        bukkitRecipe.shape("abc", "def");
                }
                break;
            case 3:
                switch (width) {
                    case 1:
                        bukkitRecipe.shape("a", "b", "c");
                        break;

                    case 2:
                        bukkitRecipe.shape("ab", "cd", "ef");
                        break;

                    case 3:
                        bukkitRecipe.shape("abc", "def", "ghi");
                }
        }

        ItemStack item;
        char key = 'a';

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                item = ingredients[(h * 3) + w];

                if (item != null) {
                    bukkitRecipe.setIngredient(key, item.getType(), item.getDurability());
                }

                key++;
            }
        }

        return bukkitRecipe;
    }

    public boolean hasIngredients() {
        return ingredients != null && ingredients.length == 9;
    }

    @Override
    public boolean isValid() {
        return hasIngredients() && (hasFlag(FlagType.REMOVE) || hasFlag(FlagType.RESTRICT) || hasResults());
    }

    @Override
    public RecipeType getType() {
        return RecipeType.CRAFT;
    }

    @Override
    public String printBookIndex() {
        return hasCustomName() ? ChatColor.ITALIC + getName() : Tools.Item.getName(getFirstResult());
    }

    @Override
    public String printBook() {
        StringBuilder s = new StringBuilder(256);

        s.append(Messages.RECIPEBOOK_HEADER_SHAPED.get());

        if (hasCustomName()) {
            s.append('\n').append(ChatColor.DARK_BLUE).append(getName()).append(ChatColor.BLACK);
        }

        s.append('\n').append(ChatColor.GRAY).append('=').append(ChatColor.BLACK).append(ChatColor.BOLD).append(Tools.Item.print(getFirstResult(), ChatColor.DARK_GREEN, null, true));

        if (isMultiResult()) {
            s.append('\n').append(Messages.RECIPEBOOK_MORERESULTS.get("{amount}", (getResults().size() - 1)));
        }

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_SHAPE.get()).append(ChatColor.GRAY);

        Map<String, Integer> charItems = new LinkedHashMap<String, Integer>();
        int num = 1;
        boolean smallGrid = getWidth() <= 2 && getHeight() <= 2;

        for (int i = 0; i < ingredients.length; i++) {
            if (smallGrid && (i == 2 || i >= 5)) {
                continue;
            }

            if (i == 0 || i == 3 || i == 6) {
                s.append('\n');
            }

            if (ingredients[i] == null) {
                s.append('[').append(ChatColor.WHITE).append('_').append(ChatColor.GRAY).append(']');
            } else {
                String print = Tools.Item.print(ingredients[i], ChatColor.RED, ChatColor.BLACK, false);
                Integer get = charItems.get(print);

                if (get == null) {
                    charItems.put(print, num);
                    get = num;
                    num++;
                }

                s.append('[').append(ChatColor.DARK_PURPLE).append(get).append(ChatColor.GRAY).append(']');
            }
        }

        s.append('\n');
        s.append('\n').append(Messages.RECIPEBOOK_HEADER_INGREDIENTS.get()).append(ChatColor.GRAY);

        for (Entry<String, Integer> entry : charItems.entrySet()) {
            s.append('\n').append(ChatColor.DARK_PURPLE).append(entry.getValue()).append(ChatColor.GRAY).append(": ").append(entry.getKey());
        }

        return s.toString();
    }
}
