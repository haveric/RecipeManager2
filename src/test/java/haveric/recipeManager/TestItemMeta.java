package haveric.recipeManager;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TestItemMeta implements ItemMeta {
    private String displayName;
    private List<String> lores = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> flags = new HashSet<>();

    public TestItemMeta() {}

    public TestItemMeta(ItemMeta meta) {
        setDisplayName(meta.getDisplayName());
        setLore(meta.getLore());

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            addEnchant(entry.getKey(), entry.getValue(), true);
        }

        for (ItemFlag flag : meta.getItemFlags()) {
            addItemFlags(flag);
        }
    }

    @Override
    public boolean hasDisplayName() {
        return displayName != null;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public boolean hasLore() {
        return !lores.isEmpty();
    }

    @Override
    public List<String> getLore() {
        ArrayList<String> loresCopy = new ArrayList<>();
        loresCopy.addAll(lores);
        return loresCopy;
    }

    @Override
    public void setLore(List<String> lore) {
        lores.clear();
        lores.addAll(lore);
    }

    @Override
    public boolean hasEnchants() {
        return !enchantments.isEmpty();
    }

    @Override
    public boolean hasEnchant(Enchantment ench) {
        return enchantments.containsKey(ench);
    }

    @Override
    public int getEnchantLevel(Enchantment ench) {
        return enchantments.get(ench);
    }

    @Override
    public Map<Enchantment, Integer> getEnchants() {
        return enchantments;
    }

    @Override
    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        boolean changed = false;
        if (ignoreLevelRestriction || level <= ench.getMaxLevel()) {
            enchantments.put(ench, level);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean removeEnchant(Enchantment ench) {
        boolean changed = false;
        if (enchantments.containsKey(ench)) {
            enchantments.remove(ench);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean hasConflictingEnchant(Enchantment ench) {
        boolean conflicts = false;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (entry.getKey().conflictsWith(ench)) {
                conflicts = true;
                break;
            }
        }
        return conflicts;
    }

    @Override
    public void addItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            flags.add(itemFlag);
        }
    }

    @Override
    public void removeItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            flags.remove(itemFlag);
        }
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return flags;
    }

    @Override
    public boolean hasItemFlag(ItemFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public ItemMeta clone() {
        return new TestItemMeta(this);
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
