package digi.recipeManager.recipes;

import org.bukkit.ChatColor;
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
        if(flags == null) // TODO
        {
//            Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "ItemFlags were null!");
            
            flags = new ItemFlags();
        }
        
        return flags;
    }
    
    public void setChance(int chance)
    {
        this.chance = chance;
    }
    
    public int getChance()
    {
        return chance;
    }
    
    public String[] canCraftResult(Player player)
    {
        if(player == null)
            return null;
        
        // TODO check permissions and stuff
        
        if(flags.test)
            return new String[] { "Just because :P" };
        
        return null;
    }
    
    public String print()
    {
        return String.format("%s%s%s%s", (getEnchantments().size() > 0 ? ChatColor.AQUA : ChatColor.WHITE), getType().toString(), (getDurability() > 0 ? ":" + getDurability() : ""), (getAmount() > 1 ? " x " + getAmount() : ""));
    }
}