package haveric.recipeManager;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class TestMetaEnchantedBook extends TestMetaItem implements EnchantmentStorageMeta {
    private Map<Enchantment, Integer> enchantments;

    TestMetaEnchantedBook(TestMetaItem meta) {
        super(meta);

        if (!(meta instanceof TestMetaEnchantedBook)) {
            return;
        }

        TestMetaEnchantedBook that = (TestMetaEnchantedBook) meta;

        if (that.hasEnchants()) {
            this.enchantments = new HashMap<>(that.enchantments);
        }
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case ENCHANTED_BOOK:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isEnchantedEmpty();
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof TestMetaEnchantedBook) {
            TestMetaEnchantedBook that = (TestMetaEnchantedBook) meta;

            return (hasStoredEnchants() ? that.hasStoredEnchants() && this.enchantments.equals(that.enchantments) : !that.hasStoredEnchants());
        }
        return true;
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaEnchantedBook || isEnchantedEmpty());
    }

    @Override
    public TestMetaEnchantedBook clone() {
        TestMetaEnchantedBook meta = (TestMetaEnchantedBook) super.clone();

        if (this.enchantments != null) {
            meta.enchantments = new HashMap<>(this.enchantments);
        }

        return meta;
    }

    boolean isEnchantedEmpty() {
        return !hasStoredEnchants();
    }

    public boolean hasStoredEnchant(Enchantment ench) {
        return hasStoredEnchants() && enchantments.containsKey(ench);
    }

    public int getStoredEnchantLevel(Enchantment ench) {
        Integer level = hasStoredEnchants() ? enchantments.get(ench) : null;
        if (level == null) {
            return 0;
        }
        return level;
    }

    public Map<Enchantment, Integer> getStoredEnchants() {
        return hasStoredEnchants() ? ImmutableMap.copyOf(enchantments) : ImmutableMap.<Enchantment, Integer>of();
    }

    public boolean addStoredEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
        if (enchantments == null) {
            enchantments = new HashMap<>(4);
        }

        if (ignoreRestrictions || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            Integer old = enchantments.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    public boolean removeStoredEnchant(Enchantment ench) {
        return hasStoredEnchants() && enchantments.remove(ench) != null;
    }

    public boolean hasStoredEnchants() {
        return !(enchantments == null || enchantments.isEmpty());
    }

    public boolean hasConflictingStoredEnchant(Enchantment ench) {
        return checkConflictingEnchants(enchantments, ench);
    }
}
