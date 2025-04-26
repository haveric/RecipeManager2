package haveric.recipeManager;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestEnchantmentUnbreaking extends Enchantment {
    private final Enchantment target;
    public TestEnchantmentUnbreaking(Enchantment target) {
        super();
        this.target = target;
    }

    @Override
    public String getName() {
        return "unbreaking";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public NamespacedKey getKey() {
        return null;
    }

    @Override
    public String getTranslationKey() {
        return null;
    }

    @NotNull
    @Override
    public NamespacedKey getKeyOrThrow() {
        return null;
    }

    @Nullable
    @Override
    public NamespacedKey getKeyOrNull() {
        return null;
    }

    @Override
    public boolean isRegistered() {
        return false;
    }
}
