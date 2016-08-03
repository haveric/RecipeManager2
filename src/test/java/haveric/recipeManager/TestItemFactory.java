package haveric.recipeManager;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TestItemFactory implements ItemFactory {

    public TestItemFactory() { }

    @Override
    public ItemMeta getItemMeta(Material material) {
        // TODO: Handle materials differently?
        return new TestItemMeta();
    }

    @Override
    public boolean isApplicable(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        // TODO: Update
        return true;
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material material) throws IllegalArgumentException {
        // TODO: Update
        return true;
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) throws IllegalArgumentException {
        // TODO: Update?
        return meta1 != null && meta2 != null;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        // TODO: Update
        return meta;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
        // TODO: Update
        return meta;
    }

    @Override
    public Color getDefaultLeatherColor() {
        return Color.fromRGB(160, 101, 64);
    }
}
