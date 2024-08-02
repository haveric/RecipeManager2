package haveric.recipeManager.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class AirChoice implements RecipeChoice {
    public AirChoice() { }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public RecipeChoice clone() {
        try {
            return (AirChoice) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.isSimilar(new ItemStack(Material.AIR));
    }
}
