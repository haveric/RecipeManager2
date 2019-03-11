package haveric.recipeManager;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;

import java.util.*;

public class TestMetaItem implements ItemMeta, Damageable, Repairable {
    private String displayName;
    private String locName;
    private List<String> lores = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> flags = new HashSet<>();
    private int repairCost;
    private int hideFlag;
    private boolean unbreakable;
    private int damage;

    public TestMetaItem(TestMetaItem meta) {
        if (meta == null) {
            return;
        }
        setDisplayName(meta.getDisplayName());
        this.locName = meta.locName;

        setLore(meta.getLore());

        this.enchantments = new HashMap<>(meta.getEnchants());

        for (ItemFlag flag : meta.getItemFlags()) {
            addItemFlags(flag);
        }

        this.repairCost = meta.getRepairCost();
        this.hideFlag = meta.hideFlag;
        this.unbreakable = meta.unbreakable;
        this.damage = meta.damage;
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
    public String getLocalizedName() {
        return locName;
    }

    @Override
    public void setLocalizedName(String name) {
        this.locName = name;
    }

    @Override
    public boolean hasLocalizedName() {
        return !Strings.isNullOrEmpty(locName);
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
    public boolean hasDamage() {
        return damage > 0;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public TestMetaItem clone() {
        try {
            TestMetaItem clone = (TestMetaItem) super.clone();
            if (this.lores != null) {
                clone.lores = new ArrayList<String>(this.lores);
            }
            if (this.enchantments != null) {
                clone.enchantments = new HashMap<Enchantment, Integer>(this.enchantments);
            }
            clone.hideFlag = this.hideFlag;
            clone.unbreakable = this.unbreakable;
            clone.damage = this.damage;
            return clone;
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
        return !(hasDisplayName() || hasLocalizedName() || hasEnchants() || hasLore() || hasRepairCost() || hideFlag != 0 || isUnbreakable() || hasDamage());
    }

    @Override
    public boolean isUnbreakable() {
        return unbreakable;
    }

    @Override
    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    @Override
    public boolean hasAttributeModifiers() {
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        return null;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return null;
    }

    @Override
    public Collection<AttributeModifier> getAttributeModifiers(Attribute attribute) {
        return null;
    }

    @Override
    public boolean addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        return false;
    }

    @Override
    public void setAttributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {

    }

    @Override
    public boolean removeAttributeModifier(Attribute attribute) {
        return false;
    }

    @Override
    public boolean removeAttributeModifier(EquipmentSlot slot) {
        return false;
    }

    @Override
    public boolean removeAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        return false;
    }

    @Override
    public CustomItemTagContainer getCustomTagContainer() {
        return null;
    }

    boolean equalsCommon(TestMetaItem that) {
        return ((this.hasDisplayName() ? that.hasDisplayName() && this.displayName.equals(that.displayName) : !that.hasDisplayName()))
                && (this.hasLocalizedName()? that.hasLocalizedName()&& this.locName.equals(that.locName) : !that.hasLocalizedName())
                && (this.hasEnchants() ? that.hasEnchants() && this.enchantments.equals(that.enchantments) : !that.hasEnchants())
                && (this.hasLore() ? that.hasLore() && this.lores.equals(that.lores) : !that.hasLore())
                && (this.hasRepairCost() ? that.hasRepairCost() && this.repairCost == that.repairCost : !that.hasRepairCost())
                && (this.hideFlag == that.hideFlag)
                && (this.isUnbreakable() == that.isUnbreakable())
                && (this.hasDamage() ? that.hasDamage() && this.damage == that.damage : !that.hasDamage());
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
