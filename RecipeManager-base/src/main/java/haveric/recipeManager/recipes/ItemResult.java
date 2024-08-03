package haveric.recipeManager.recipes;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.Flaggable;
import haveric.recipeManager.flag.Flags;
import haveric.recipeManager.flag.args.Args;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Map;

public class ItemResult implements Flaggable {
    private ItemStack itemStack;
    private Flags flags;
    private float chance = 100;
    private BaseRecipe recipe;

    public ItemResult() {
    }

    public ItemResult(ItemStack item) {
        itemStack = item.clone();
    }

    public ItemResult(ItemResult result, boolean cloneFlags) {
        itemStack = result.itemStack.clone();

        if (result.hasFlags()) {
            // don't clone, needs to be a reference to allow some flags (ex: FlagCooldown) to work
            flags = result.getFlags();

            if (cloneFlags) {
                flags = flags.clone();
            }
        } else {
            flags = null;
        }

        chance = result.chance;
        recipe = result.recipe; // don't clone, needs to be a reference
    }

    public ItemResult(ItemStack item, float newChance) {
        itemStack = item.clone();

        chance = newChance;
    }

    public ItemResult(Material type, int amount, int data, float newChance) {
        itemStack = new ItemStack(type, amount, (short) data);

        chance = newChance;
    }

    public ItemResult(ItemStack item, Flags newFlags) {
        itemStack = item.clone();

        flags = newFlags.clone(this);
    }

    @Override
    public ItemResult clone() {
        return new ItemResult(this, false);
    }

    public void setItemStack(ItemStack item) {
        itemStack = item.clone();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setChance(float newChance) {
        chance = newChance;
    }

    public float getChance() {
        return chance;
    }

    public BaseRecipe getRecipe() {
        return recipe;
    }

    public ItemResult setRecipe(BaseRecipe newRecipe) {
        recipe = newRecipe;
        return this;
    }

    // From Flaggable interface

    public boolean hasFlag(String type) {
        boolean hasFlag = false;

        if (flags != null) {
            hasFlag = flags.hasFlag(type);
        }

        return hasFlag;
    }

    public boolean hasFlags() {
        return flags != null;
    }

    public Flag getFlag(String type) {
        return flags.getFlag(type);
    }

    public Flags getFlags() {
        if (flags == null) {
            flags = new Flags(this);
        }

        return flags;
    }

    public void clearFlags() {
        flags = null;
    }

    public void addFlag(Flag flag) {
        flags.addFlag(flag, FlagBit.RESULT);
    }

    public boolean checkFlags(Args a) {
        boolean checkFlags = true;

        if (flags != null) {
            checkFlags = flags.checkFlags(a);
        }

        return checkFlags;
    }

    public boolean sendCrafted(Args a) {
        boolean sendCrafted = true;

        if (flags != null) {
            sendCrafted = flags.sendCrafted(a);
        }

        return sendCrafted;
    }

    public boolean sendPrepare(Args a) {
        boolean sendPrepare = true;

        if (flags != null) {
            sendPrepare = flags.sendPrepare(a);
        }

        return sendPrepare;
    }

    @Override
    public boolean sendFuelRandom(Args a) {
        boolean sendRandom = true;

        if (flags != null) {
            sendRandom = flags.sendFuelRandom(a);
        }

        return sendRandom;
    }

    @Override
    public boolean sendFuelEnd(Args a) {
        boolean sendEnd = true;

        if (flags != null) {
            sendEnd = flags.sendFuelEnd(a);
        }

        return sendEnd;
    }

    @Override
    public int hashCode() {
        String toHash = String.valueOf(super.hashCode()); // Get ItemStack's hash

        if (hasFlags() && flags.hasFlags()) {
            toHash += flags.hashCode();
        }

        toHash += "chance: " + chance;

        return toHash.hashCode();
    }

    // ItemStack shortcut methods

    public ItemMeta getItemMeta() {
        return itemStack.getItemMeta();
    }

    public void setItemMeta(ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
    }

    public void clearMetadata() {
        setItemMeta(null);
    }

    public Material getType() {
        return itemStack.getType();
    }

    public void setType(Material type) {
        itemStack.setType(type);
    }

    public boolean isAir() {
        return getType() == Material.AIR;
    }

    public MaterialData getData() {
        return itemStack.getData();
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public void setAmount(int amount) {
        itemStack.setAmount(amount);
    }

    public short getDurability() {
        return itemStack.getDurability();
    }

    public void setDurability(short durability) {
        itemStack.setDurability(durability);
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return itemStack.getEnchantments();
    }

    public void addUnsafeEnchantment(Enchantment ench, int level) {
        itemStack.addUnsafeEnchantment(ench, level);
    }

    public int removeEnchantment(Enchantment ench) {
        return itemStack.removeEnchantment(ench);
    }
}
