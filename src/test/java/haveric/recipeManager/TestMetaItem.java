package haveric.recipeManager;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.*;

public class TestMetaItem implements ItemMeta, Repairable {
    private String displayName;
    private List<String> lores = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> flags = new HashSet<>();
    private int repairCost;

    public TestMetaItem(TestMetaItem meta) {
        if (meta == null) {
            return;
        }
        setDisplayName(meta.getDisplayName());
        setLore(meta.getLore());

        this.enchantments = new HashMap<>(meta.getEnchants());

        for (ItemFlag flag : meta.getItemFlags()) {
            addItemFlags(flag);
        }

        this.repairCost = meta.getRepairCost();
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
        return new ArrayList<>(lores);
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
        if (ignoreLevelRestriction || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
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
        Collections.addAll(flags, itemFlags);
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
    public TestMetaItem clone() {
        try {
            return (TestMetaItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public boolean hasRepairCost() {
        return repairCost > 0;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int cost) {
        repairCost = cost;
    }

    boolean applicableTo(Material type) {
        return type != Material.AIR;
    }

    boolean isEmpty() {
        return !(hasDisplayName() || hasEnchants() || hasLore());
    }

    boolean equalsCommon(TestMetaItem that) {
        return ((this.hasDisplayName() ? that.hasDisplayName() && this.displayName.equals(that.displayName) : !that.hasDisplayName()))
                && (this.hasEnchants() ? that.hasEnchants() && this.enchantments.equals(that.enchantments) : !that.hasEnchants())
                && (this.hasLore() ? that.hasLore() && this.lores.equals(that.lores) : !that.hasLore())
                && (this.hasRepairCost() ? that.hasRepairCost() && this.repairCost == that.repairCost : !that.hasRepairCost());
    }

    boolean notUncommon(TestMetaItem meta) {
        return true;
    }

    static boolean checkConflictingEnchants(Map<Enchantment, Integer> enchantments, Enchantment ench) {
        if (enchantments == null || enchantments.isEmpty()) {
            return false;
        }

        for (Enchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(ench)) {
                return true;
            }
        }

        return false;
    }
}
