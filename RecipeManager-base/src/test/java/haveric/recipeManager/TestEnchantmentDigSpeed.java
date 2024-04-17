package haveric.recipeManager;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class TestEnchantmentDigSpeed extends Enchantment {
    private final Enchantment target;
    public TestEnchantmentDigSpeed(Enchantment target) {
        super();
        this.target = target;
    }

    @Override
    public String getName() {
        return "digspeed";
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
}
