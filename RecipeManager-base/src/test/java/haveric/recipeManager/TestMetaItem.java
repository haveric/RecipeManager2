package haveric.recipeManager;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class TestMetaItem implements ItemMeta, Damageable, Repairable, BlockDataMeta {
    private String displayName;
    private String locName;
    private List<String> lores = new ArrayList<>();
    private Integer customModelData;
    private String blockData;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);
    private int repairCost;
    private int hideFlag;
    private boolean unbreakable;
    private int damage;

    private int version = 0; // Internal use only

    public TestMetaItem(TestMetaItem meta) {
        if (meta == null) {
            return;
        }
        displayName = meta.displayName;
        this.locName = meta.locName;

        setLore(meta.getLore());

        this.customModelData = meta.customModelData;
        this.blockData = meta.blockData;

        this.enchantments = new HashMap<>(meta.enchantments);

        for (ItemFlag flag : meta.flags) {
            addItemFlags(flag);
        }

        this.repairCost = meta.repairCost;
        this.hideFlag = meta.hideFlag;
        this.unbreakable = meta.unbreakable;
        this.damage = meta.damage;

        this.version = meta.version;
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
    public boolean hasItemName() {
        return false;
    }

    @Override
    public String getItemName() {
        return "";
    }

    @Override
    public void setItemName(String s) {

    }

    @SuppressWarnings("removal")
    @Override
    public String getLocalizedName() {
        return locName;
    }

    @SuppressWarnings("removal")
    @Override
    public void setLocalizedName(String name) {
        this.locName = name;
    }

    @SuppressWarnings("removal")
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
    public boolean hasCustomModelData() {
        return customModelData != null;
    }

    @Override
    public int getCustomModelData() {
        Preconditions.checkState(hasCustomModelData(), "We don't have CustomModelData! Check hasCustomModelData first!");
        return customModelData;
    }

    @Override
    public void setCustomModelData(Integer data) {
        this.customModelData = data;
    }

    @Override
    public boolean hasBlockData() {
        return this.blockData != null;
    }

    @Override
    public BlockData getBlockData(Material material) {
        return null;//CraftBlockData.newData(material, '[' + blockData + ']');
    }

    @Override
    public void setBlockData(BlockData blockData) {
        //this.blockData = (blockData == null) ? null : ((CraftBlockData) blockData).toStates();
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
    public void removeEnchantments() {

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
    public boolean isHideTooltip() {
        return false;
    }

    @Override
    public void setHideTooltip(boolean b) {

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
    public boolean hasMaxDamage() {
        return false;
    }

    @Override
    public int getMaxDamage() {
        return 0;
    }

    @Override
    public void setMaxDamage(Integer integer) {

    }

    @Override
    public TestMetaItem clone() {
        try {
            TestMetaItem clone = (TestMetaItem) super.clone();
            if (this.lores != null) {
                clone.lores = new ArrayList<>(this.lores);
            }
            if (this.enchantments != null) {
                clone.enchantments = new HashMap<>(this.enchantments);
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
        return !(hasDisplayName() || hasLocalizedName() || hasEnchants() || hasLore() || hasCustomModelData() || hasBlockData() || hasRepairCost() || hideFlag != 0 || unbreakable || hasDamage());
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
    public boolean hasEnchantmentGlintOverride() {
        return false;
    }

    @Override
    public Boolean getEnchantmentGlintOverride() {
        return null;
    }

    @Override
    public void setEnchantmentGlintOverride(Boolean aBoolean) {

    }

    @Override
    public boolean isFireResistant() {
        return false;
    }

    @Override
    public void setFireResistant(boolean b) {

    }

    @Override
    public boolean hasMaxStackSize() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(Integer integer) {

    }

    @Override
    public boolean hasRarity() {
        return false;
    }

    @Override
    public ItemRarity getRarity() {
        return null;
    }

    @Override
    public void setRarity(ItemRarity itemRarity) {

    }

    @Override
    public boolean hasFood() {
        return false;
    }

    @Override
    public FoodComponent getFood() {
        return null;
    }

    @Override
    public void setFood(FoodComponent foodComponent) {

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
    public String getAsString() {
        return null;
    }

    @Override
    public String getAsComponentString() {
        return "";
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
                && (this.hasCustomModelData() ? that.hasCustomModelData() && this.customModelData.equals(that.customModelData) : !that.hasCustomModelData())
                && (this.hasBlockData() ? that.hasBlockData() && this.blockData.equals(that.blockData) : !that.hasBlockData())
                && (this.hasRepairCost() ? that.hasRepairCost() && this.repairCost == that.repairCost : !that.hasRepairCost())
                && (this.hideFlag == that.hideFlag)
                && (this.unbreakable == that.unbreakable)
                && (this.hasDamage() ? that.hasDamage() && this.damage == that.damage : !that.hasDamage())
                && (this.version == that.version);
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

    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return null;
    }
}
