package haveric.recipeManager.recipes.craft;

import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.flag.flags.any.FlagIngredientCondition;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.RMBukkitTools;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsItem;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CraftRecipe extends BaseCraftRecipe {
    private ItemStack[] ingredients;
    private int width;
    private int height;
    private boolean mirror = false;

    public CraftRecipe() {
    }

    public CraftRecipe(ShapedRecipe recipe) {
        setBukkitRecipe(recipe);
        setIngredients(RMBukkitTools.convertShapedRecipeToItemMatrix(recipe));
        setResult(recipe.getResult());
    }

    public CraftRecipe(BaseRecipe recipe) {
        super(recipe);

        if (recipe instanceof CraftRecipe) {
            CraftRecipe r = (CraftRecipe) recipe;

            if (r.getIngredients() != null) {
                ingredients = r.getIngredients();
            }

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
            int ingredientsLength = ingredients.length;
            ItemStack[] items = new ItemStack[ingredientsLength];

            for (int i = 0; i < ingredientsLength; i++) {
                if (ingredients[i] == null) {
                    items[i] = null;
                } else {
                    items[i] = ingredients[i].clone();
                }
            }

            return items;
        }

        return null;
    }

    /**
     * Set the ingredients matrix. <br>
     * This also calculates the width and height of the shape matrix.<br>
     * <b>NOTE: Array must have exactly 9 elements, use null for empty slots.</b>
     *
     * @param newIngredients
     *            ingredients matrix, this also defines the shape, width and height.
     */
    public void setIngredients(ItemStack[] newIngredients) {
        if (newIngredients.length != 9) {
            throw new IllegalArgumentException("Recipe " + this.name + " must have exactly 9 items, use null to specify empty slots!");
        }

        ingredients = newIngredients.clone();
        calculate();
    }

    /**
     * Sets an ingredient slot to material with wildcard data value.<br>
     * Slots are like:<br>
     * <code>
     * | 0 1 2 |<br>
     * | 3 4 5 |<br>
     * | 6 7 8 |</code> <br>
     * Null slots are ignored and allow the recipe to be
     * used in a smaller grid (inventory's 2x2 for example)<br> <br>
     * <b>NOTE: always start with index 0!</b> Then you can use whatever index you want up to 8.<br>
     * This is required because ingredients are shifted to top-left corner of the 2D matrix on each call of this method.
     *
     * @param slot
     *            start with 0, then use any index from 1 to 8
     * @param type
     */
    public void setIngredient(int slot, Material type) {
        setIngredient(slot, type, RMCVanilla.DATA_WILDCARD);
    }

    /**
     * Sets an ingredient slot to material with specific data value.<br>
     * Slots are like:<br>
     * <code>
     * | 0 1 2 |<br>
     * | 3 4 5 |<br>
     * | 6 7 8 |</code> <br>
     * Null slots are ignored and allow the recipe to be
     * used in a smaller grid (inventory's 2x2 for example)<br> <br>
     * <b>NOTE: always start with index 0!</b> Then you can use whatever index you want up to 8.<br>
     * This is required because ingredients are shifted to top-left corner of the 2D matrix on each call of this method.
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

        if (slot != 0 && ingredients[0] == null) {
            throw new IllegalArgumentException("A plugin is using setIngredient() with index NOT starting at 0, shape is corrupted!!!");
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
     * Mirror the ingredients shape.<br>
     * Useful for matching recipes, no other real effect.<br>
     * This triggers a hashCode recalculation.
     *
     * @param newMirror
     */
    public void setMirrorShape(boolean newMirror) {
        mirror = newMirror;
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
            RMBukkitTools.trimItemMatrix(ingredients);
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

                    str.append(item.getType());
                    if (!Version.has1_13BasicSupport() || item instanceof Damageable) {
                        str.append(':').append(item.getDurability());
                    }

                    if (!item.getEnchantments().isEmpty()) {
                        for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                            str.append("enchant:").append(entry.getKey().getName()).append(':').append(entry.getValue());
                        }
                    }
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

        s.append("shaped ").append(width).append('x').append(height);

        s.append(" (");

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                ItemStack item = ingredients[(h * 3) + w];

                if (item == null) {
                    s.append("air");
                } else {
                    s.append(item.getType().toString().toLowerCase());

                    if (!Version.has1_13BasicSupport() || item instanceof Damageable) {
                        if (item.getDurability() != RMCVanilla.DATA_WILDCARD) {
                            s.append(':').append(item.getDurability());
                        }
                    }
                }

                if (w < (width - 1)) {
                    s.append(' ');
                }
            }

            if (h < (height - 1)) {
                s.append(" / ");
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
    public ShapedRecipe toBukkitRecipe(boolean vanilla) {
        if (!hasIngredients() || !hasResults()) {
            return null;
        }


        ItemStack result;
        if (vanilla) {
            result = getFirstResultItemStack();
        } else {
            ItemResult firstResult = getFirstResult();

            Args a = ArgBuilder.create().result(firstResult).build();
            getFlags().sendPrepare(a, true);
            firstResult.getFlags().sendPrepare(a, true);

            if (requiresRecipeManagerModification()) {
                result = Tools.createItemRecipeId(a.result().getItemStack(), hashCode());
            } else {
                result = a.result().getItemStack();
            }
        }

        ShapedRecipe bukkitRecipe = new ShapedRecipe(getNamespacedKey(), result);

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
                        break;
                    default:
                        break;
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
                        break;
                    default:
                        break;
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
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        ItemStack item;
        char key = 'a';

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                item = ingredients[(h * 3) + w];

                if (item != null) {
                    if (Version.has1_13BasicSupport()) {
                        if (item.getItemMeta() instanceof Damageable) {
                            bukkitRecipe.setIngredient(key, item.getType(), ((Damageable) item.getItemMeta()).getDamage());
                        } else {
                            bukkitRecipe.setIngredient(key, item.getType());
                        }
                    } else {
                        bukkitRecipe.setIngredient(key, item.getType(), item.getDurability());
                    }
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
    public String getInvalidErrorMessage() {
        return super.getInvalidErrorMessage() + " Needs at least one result and exactly 9 ingredient slots, empty ones can be null.";
    }

    @Override
    public String printBookResult(ItemResult result) {
        StringBuilder s = getHeaderResult("craft", result);

        s.append(Messages.getInstance().parse("recipebook.header.shape")).append('\n');
        s.append(RMCChatColor.GRAY);

        Map<String, Integer> charItems = new LinkedHashMap<>();
        int num = 1;

        // If ingredients get mirrored at any point, display them as they were written
        ItemStack[] displayIngredients = ingredients;
        if (mirror) {
            displayIngredients = Tools.mirrorItemMatrix(ingredients);
        }

        int ingredientsLength = displayIngredients.length;
        for (int i = 0; i < ingredientsLength; i++) {
            int col = i % 3 + 1;
            int row = i / 3 + 1;

            if (col <= width && row <= height) {
                if (displayIngredients[i] == null) {
                    s.append('[').append(RMCChatColor.WHITE).append('_').append(RMCChatColor.GRAY).append(']');
                } else {
                    String print = "";
                    // TODO: Recipes can have ingredientcondition as well
                    if (result.hasFlag(FlagType.INGREDIENT_CONDITION)) {
                        FlagIngredientCondition flag = (FlagIngredientCondition) result.getFlag(FlagType.INGREDIENT_CONDITION);
                        List<ConditionsIngredient> conditions = flag.getIngredientConditions(displayIngredients[i]);

                        if (!conditions.isEmpty()) {
                            ConditionsIngredient condition = conditions.get(0);

                            if (condition.hasDisplayName()) {
                                print = RMCChatColor.BLACK + condition.getDisplayName();
                            } else if (condition.hasLore()) {
                                print = RMCChatColor.BLACK + "" + RMCChatColor.ITALIC + condition.getLores().get(0);
                            }
                        }
                    }

                    if (print.isEmpty()) {
                        print = ToolsItem.print(displayIngredients[i], RMCChatColor.BLACK, RMCChatColor.BLACK);
                    }

                    Integer get = charItems.get(print);

                    if (get == null) {
                        charItems.put(print, num);
                        get = num;
                        num++;
                    }

                    s.append('[').append(RMCChatColor.DARK_PURPLE).append(get).append(RMCChatColor.GRAY).append(']');
                }
            }

            if (col == width) {
                s.append('\n');
            }
        }

        s.append(Messages.getInstance().parse("recipebook.header.ingredients"));

        for (Entry<String, Integer> entry : charItems.entrySet()) {
            s.append('\n').append(RMCChatColor.DARK_PURPLE).append(entry.getValue()).append(RMCChatColor.GRAY).append(": ").append(entry.getKey());
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

    @Override
    public List<String> getRecipeIndexesForInput(List<ItemStack> ingredients, ItemStack result) {
        List<String> recipeIndexes = new ArrayList<>();
        if (result != null) {
            recipeIndexes.add(Tools.getRecipeIdFromItem(result));
        }

        return recipeIndexes;
    }
}
