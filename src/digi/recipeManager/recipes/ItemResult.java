package digi.recipeManager.recipes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import digi.recipeManager.recipes.flags.ItemFlags;

public class ItemResult extends ItemStack
{
    private ItemFlags flags;
    private int       chance = -1;
    
    public ItemResult()
    {
    }
    
    public ItemResult(ItemStack item)
    {
        super(item);
    }
    
    public ItemResult(ItemStack item, int chance)
    {
        super(item);
        
        setChance(chance);
    }
    
    public ItemResult(Material type, int amount, int data, int chance)
    {
        super(type, amount, (short)data);
        
        setChance(chance);
    }
    
    public ItemResult(ItemStack item, ItemFlags flags)
    {
        super(item);
        
        this.flags = flags;
    }
    
    public void setItemStack(ItemStack item)
    {
        setTypeId(item.getTypeId());
        setDurability(item.getDurability());
        setAmount(item.getAmount());
        setItemMeta(item.getItemMeta());
    }
    
    public ItemFlags getFlags()
    {
        if(flags == null)
            flags = new ItemFlags();
        
        return flags;
    }
    
    public boolean checkFlags(Player player, String playerName, Location location, List<String> reasons)
    {
        return (flags == null ? true : flags.checkFlags(player, playerName, location, null, this, reasons));
    }
    
    public boolean applyFlags(Player player, String playerName, Location location, List<String> reasons)
    {
        return (flags == null ? true : flags.applyFlags(player, playerName, location, null, this, reasons));
    }
    
    public void setChance(int chance)
    {
        this.chance = chance;
    }
    
    public int getChance()
    {
        return chance;
    }
    
    public String print()
    {
        return String.format("%s%s%s%s", (getEnchantments().size() > 0 ? ChatColor.AQUA : ChatColor.WHITE), getType().toString(), (getDurability() > 0 ? ":" + getDurability() : ""), (getAmount() > 1 ? " x " + getAmount() : ""));
    }
}