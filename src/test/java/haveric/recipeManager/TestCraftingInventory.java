package haveric.recipeManager;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class TestCraftingInventory extends TestCraftInventory implements CraftingInventory {
    ItemStack result;
    ItemStack[] matrix;

    public TestCraftingInventory() {
        super();
        matrix = new ItemStack[0];
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    @Override
    public ItemStack[] getMatrix() {
        return matrix;
    }

    @Override
    public void setResult(ItemStack newResult) {
        result = newResult;
    }

    @Override
    public void setMatrix(ItemStack[] contents) {
        matrix = contents;
    }

    @Override
    public Recipe getRecipe() {
        return null;
    }
}
