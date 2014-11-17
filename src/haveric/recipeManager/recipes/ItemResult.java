package haveric.recipeManager.recipes;

import haveric.recipeManager.flags.Args;
import haveric.recipeManager.flags.Flag;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flaggable;
import haveric.recipeManager.flags.Flags;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ItemResult extends ItemStack implements Flaggable {
    private Flags flags;
    private float chance = 100;
    private BaseRecipe recipe;

    public ItemResult() {
    }

    public ItemResult(ItemStack item) {
        super(item);
    }

    public ItemResult(ItemResult result) {
        super(result);

        if (result.hasFlags()) {
            flags = result.getFlags().clone(this);
        } else {
            flags = null;
        }

        chance = result.chance;
        recipe = result.recipe; // don't clone, needs to be a pointer
    }

    public ItemResult(ItemStack item, float newChance) {
        super(item);

        setChance(newChance);
    }

    public ItemResult(Material type, int amount, int data, float newChance) {
        super(type, amount, (short) data);

        setChance(newChance);
    }

    public ItemResult(ItemStack item, Flags newFlags) {
        super(item);

        flags = newFlags.clone(this);
    }

    @Override
    public ItemResult clone() {
        super.clone();
        return new ItemResult(this);
    }

    public void setItemStack(ItemStack item) {
        setType(item.getType());
        setDurability(item.getDurability());
        setAmount(item.getAmount());
        setItemMeta(item.getItemMeta());
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

    @Override
    public boolean hasFlag(FlagType type) {
        boolean hasFlag = false;

        if (flags != null) {
            hasFlag = flags.hasFlag(type);
        }

        return hasFlag;
    }

    @Override
    public boolean hasFlags() {
        return flags != null;
    }

    @Override
    public boolean hasNoShiftBit() {
        boolean hasNoShiftBit = true;

        if (flags != null) {
            hasNoShiftBit = flags.hasNoShiftBit();
        }

        return hasNoShiftBit;
    }

    @Override
    public Flag getFlag(FlagType type) {
        return flags.getFlag(type);
    }

    @Override
    public <T extends Flag> T getFlag(Class<T> flagClass) {
        return flags.getFlag(flagClass);
    }

    @Override
    public Flags getFlags() {
        if (flags == null) {
            flags = new Flags(this);
        }

        return flags;
    }

    public void clearFlags() {
        flags = null;
    }

    @Override
    public void addFlag(Flag flag) {
        flags.addFlag(flag);
    }

    @Override
    public boolean checkFlags(Args a) {
        boolean checkFlags = true;

        if (flags != null) {
            checkFlags = flags.checkFlags(a);
        }

        return checkFlags;
    }

    @Override
    public boolean sendCrafted(Args a) {
        boolean sendCrafted = true;

        if (flags != null) {
            sendCrafted = flags.sendCrafted(a);
        }

        return sendCrafted;
    }

    @Override
    public boolean sendPrepare(Args a) {
        boolean sendPrepare = true;

        if (flags != null) {
            sendPrepare = flags.sendPrepare(a);
        }

        return sendPrepare;
    }
}
